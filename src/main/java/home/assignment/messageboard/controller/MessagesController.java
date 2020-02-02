package home.assignment.messageboard.controller;

import home.assignment.messageboard.api.V1ApiDelegate;
import home.assignment.messageboard.model.MessageListDTO;
import home.assignment.messageboard.service.MessagesService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class MessagesController implements V1ApiDelegate {

    private MessagesService messagesService;

    public MessagesController(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Override
    public ResponseEntity<MessageListDTO> getAllMessages() {
        MessageListDTO messagesDTO = new MessageListDTO();
        messagesDTO.setMessages(messagesService.getAllMessages());
        return ResponseEntity.ok(messagesDTO);
    }
}
