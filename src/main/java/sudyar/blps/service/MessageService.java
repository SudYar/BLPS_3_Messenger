package sudyar.blps.service;

import bitronix.tm.BitronixTransactionManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sudyar.blps.dto.request.MessageRequest;
import sudyar.blps.dto.response.MessageResponse;
import sudyar.blps.entity.Message;
import sudyar.blps.repo.MessageRepository;

import javax.transaction.SystemException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final BitronixTransactionManager bitronixTransactionManager;

    @Autowired
    private MessageRepository messageRepository;

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

    public void deleteOverdueMessages(String login, Integer DIFF_DATE){
        Long ldtNow = new Date().getTime();
        Long ldt = ldtNow - DIFF_DATE;
        List<Message> messages = messageRepository.findByToAndCreatedDateIsLessThan(login, new Timestamp(ldt));
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
}
