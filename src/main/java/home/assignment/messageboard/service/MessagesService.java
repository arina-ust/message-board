package home.assignment.messageboard.service;

import home.assignment.messageboard.model.Message;
import home.assignment.messageboard.model.MessageDTO;
import home.assignment.messageboard.repository.MessagesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessagesService {

    private MessagesRepository messagesRepository;

    public MessagesService(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    public List<Message> getAllMessages() {
        return messagesRepository.getAllMessages();
    }

    public List<Message> getMessagesForUser(String username) {
        return messagesRepository.getMessagesForUser(username);
    }

    public void createMessage(MessageDTO messageDTO) {
        messagesRepository.createMessage(mapFromDto(messageDTO));
    }

    // TODO get user id or username from token
    private Message mapFromDto(MessageDTO messageDTO) {
        return new Message(messageDTO.getTitle(), messageDTO.getText(), messageDTO.getUserId());
    }

    public void deleteMessage(int messageId) {
        messagesRepository.deleteMessage(messageId);
    }
}
