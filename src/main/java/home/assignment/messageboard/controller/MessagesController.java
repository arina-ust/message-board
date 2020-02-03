package home.assignment.messageboard.controller;

import home.assignment.messageboard.api.V1ApiDelegate;
import home.assignment.messageboard.model.MessageDTO;
import home.assignment.messageboard.model.MessageListDTO;
import home.assignment.messageboard.model.User;
import home.assignment.messageboard.service.MessagesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.util.NoSuchElementException;

@Controller
public class MessagesController implements V1ApiDelegate {

    private MessagesService messagesService;

    public MessagesController(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Override
    public ResponseEntity<MessageListDTO> getAllMessages(Integer offset, Integer limit) {
        MessageListDTO messagesDTO = new MessageListDTO();
        messagesDTO.setMessages(messagesService.getAllMessages(offset, limit));
        return ResponseEntity.ok(messagesDTO);
    }

    // TODO: get user id or ursername from token
    @Override
    public ResponseEntity<MessageListDTO> getMessagesForUser(Integer offset, Integer limit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((User) authentication.getPrincipal()).getUserName();

        MessageListDTO messagesDTO = new MessageListDTO();
        messagesDTO.setMessages(messagesService.getMessagesForUser(username, offset, limit));
        return ResponseEntity.ok(messagesDTO);
    }

    @Override
    public ResponseEntity<Void> createMessage(MessageDTO body) {
        messagesService.createMessage(body);
        return ResponseEntity.created(URI.create("/messages/")).build();
    }

    @Override
    public ResponseEntity<Void> deleteMessage(Integer id) {
        messagesService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> updateMessage(MessageDTO body, Integer id) {
        try {
            messagesService.updateMessage(body);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
