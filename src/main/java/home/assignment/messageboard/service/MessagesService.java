package home.assignment.messageboard.service;

import home.assignment.messageboard.model.Message;
import home.assignment.messageboard.model.MessageDTO;
import home.assignment.messageboard.repository.MessagesRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessagesService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private MessagesRepository messagesRepository;

    public MessagesService(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    public List<MessageDTO> getAllMessages(Integer offset, Integer limit) {
        return messagesRepository.getAllMessages(offset, limit)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private MessageDTO mapToDto(Message message) {
        return new MessageDTO()
                .id(message.getId())
                .title(message.getTitle())
                .text(message.getText())
                .username(message.getAuthor())
                .createdAt(message.getCreatedAt().format(DATE_TIME_FORMATTER))
                .updatedAt(message.getUpdatedAt().format(DATE_TIME_FORMATTER));
    }

    public List<MessageDTO> getMessagesForUser(String username, Integer offset, Integer limit) {
        return messagesRepository.getMessagesForUser(username, offset, limit)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void createMessage(MessageDTO messageDTO) {
        messagesRepository.createMessage(mapFromDto(messageDTO));
    }

    private Message mapFromDto(MessageDTO messageDTO) {
        String createdAt = messageDTO.getCreatedAt();
        String updatedAt = messageDTO.getUpdatedAt();

        OffsetDateTime parsedCreateAt = createdAt == null ? null : OffsetDateTime.parse(createdAt);
        OffsetDateTime parsedUpdatedAt = updatedAt == null ? null : OffsetDateTime.parse(updatedAt);

        return new Message(
                messageDTO.getId(),
                messageDTO.getUsername(),
                messageDTO.getTitle(),
                messageDTO.getText(),
                parsedCreateAt,
                parsedUpdatedAt
        );
    }

    public void deleteMessage(int messageId, String username) {
        messagesRepository.deleteMessage(messageId, username);
    }

    public void updateMessage(MessageDTO messageDTO) {
        messagesRepository.updateMessage(mapFromDto(messageDTO));
    }
}
