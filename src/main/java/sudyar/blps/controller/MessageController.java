package sudyar.blps.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sudyar.blps.dto.request.MessageRequest;
import sudyar.blps.dto.response.InfoResponse;
import sudyar.blps.service.MessageService;
import sudyar.blps.service.UserService;

@RestController
@RequestMapping("/messenger")
public class MessageController {

    final private Integer DIFF_TIME = 1000000;
    final private Long DIFF_DATE = 2592000000L; // 30 дней
    final private Long DIFF_MINUTE = 120000L;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/sendMessage")
    public InfoResponse sendMessage(@RequestBody MessageRequest messageRequest) {
        String login = messageRequest.getToUser();
        if (userService.exitsUserLogin(login)) {
            messageService.createMessage(messageRequest, SecurityContextHolder.getContext().getAuthentication().getName());
            return new InfoResponse("Сообщение отправлено", 0);
        } else return new InfoResponse("Пользователя с логином '" + login + "' нет", 1);
    }
    @DeleteMapping("/deleteMessage")
    public void deleteMessage(@RequestBody int idMessage){
        messageService.deleteMessage(idMessage, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping("/readMessages")
    public ResponseEntity<?> readMessages(){
        return ResponseEntity.ok(messageService.readMessages(SecurityContextHolder.getContext().getAuthentication().getName(), DIFF_MINUTE));
    }
}
