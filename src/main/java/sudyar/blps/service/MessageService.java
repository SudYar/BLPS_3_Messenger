package sudyar.blps.service;

import bitronix.tm.BitronixTransactionManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Queue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sudyar.blps.controller.MessageController;
import sudyar.blps.dto.request.MessageRequest;
import sudyar.blps.dto.response.MessageResponse;
import sudyar.blps.entity.Message;
import sudyar.blps.etc.Note;
import sudyar.blps.repo.MessageRepository;

import javax.transaction.SystemException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MessageService {
    final private long DIFF_MINUTE = 20000L;


    private final BitronixTransactionManager bitronixTransactionManager;

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Queue queue;


    public MessageResponse readMessages(String login, Long DIFF_DATE){
        final var res = messageRepository.findByToOrderByCreatedDate(login);
        List<Message> list = res.stream().toList();
        for (int i = 0; i<list.size(); i++){
            Message element = list.get(i);
            if (! element.isRead()) {
                Date ldt = new Date(element.getCreatedDate().getTime() + DIFF_DATE);
                Date ldtNow = new Date();
                if (ldt.before(ldtNow)) messageRepository.delete(element);
                else {
                    element.setIsRead(true);
                    messageRepository.save(element);
                }
            }
        }
        return new MessageResponse(res);
    }

    @Scheduled(fixedRate = DIFF_MINUTE)
    public void deleteOverdueMessages(){
        Long ldtNow = new Date().getTime();
        Long ldt = ldtNow - DIFF_MINUTE;
        List<Message> messages = messageRepository.findByIsReadAndCreatedDateIsLessThan(true, new Timestamp(ldt));
        System.out.println(new Timestamp(ldt));
        System.out.println(messages);
        messageRepository.deleteAll(messages);
    }

    public MessageResponse getAllFromUser(String login){
        final var res = messageRepository.findByFrom(login).stream().toList();
        return new MessageResponse(res);
    }


    public void createMessage(@NonNull MessageRequest messageRequest, String fromUser){
        String login = messageRequest.getToUser();
        Message message = new Message();
        message.setTo(login);
        message.setFrom(fromUser);
        message.setMessage(messageRequest.getDescription());
        message.setIsRead(false);
        messageRepository.save(message);
    }
    public void createMessage(String toUser, String fromUser, String message){
        Message mes = new Message();
        mes.setTo(toUser);
        mes.setFrom(fromUser);
        mes.setMessage(message);
        mes.setIsRead(false);
        messageRepository.save(mes);
    }

    public void deleteMessage(int idMessage, String toUser){
        try{
            bitronixTransactionManager.begin();
            Optional<Message> optionalMessage = messageRepository.findById(idMessage);
            if (optionalMessage.isPresent()){
                Message message = optionalMessage.get();
                if (toUser.equals(message.getTo())) messageRepository.delete(message);
            }
            bitronixTransactionManager.commit();
        } catch (Exception ex){
            try {
                bitronixTransactionManager.rollback();
            } catch (SystemException e) {
                e.printStackTrace();
            }
        }

    }

    public void deleteAllMessages(String toUser){
        try {
            bitronixTransactionManager.begin();
            List<Message> messages =  messageRepository.findByToOrderByCreatedDate(toUser);
            messageRepository.deleteAll(messages);
            bitronixTransactionManager.commit();
        }catch (Exception ex) {

            try {
                bitronixTransactionManager.rollback();
            } catch (SystemException e) {
                e.printStackTrace();
            }
        }

    }



    @JmsListener(destination = "netsurfingzone-queue")
    public void consumeMessage(String jsonMessage) {
        Note note = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
//            logger.info(jsonMessage);
            note = mapper.readValue(jsonMessage, Note.class);
            Message mes = new Message();
            mes.setTo(note.getTo());
            mes.setFrom(note.getFrom());
            mes.setMessage(note.getMessage());
            mes.setIsRead(false);
            messageRepository.save(mes);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }
}
