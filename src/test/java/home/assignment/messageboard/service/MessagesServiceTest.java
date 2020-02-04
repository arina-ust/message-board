package home.assignment.messageboard.service;

import home.assignment.messageboard.model.MessageDTO;
import org.jooq.DSLContext;
import org.jooq.generated.db.tables.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql("/truncate_tables.sql")
public class MessagesServiceTest {

    @Autowired
    private MessagesService messagesService;
    @Autowired
    private DSLContext dslContext;

    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "user2";

    @BeforeEach
    public void setUp() {
        dslContext.insertInto(Users.USERS)
                .set(Users.USERS.USERNAME, USERNAME_1)
                .set(Users.USERS.PASSWORDHASH, "somehash")
                .execute();
        dslContext.insertInto(Users.USERS)
                .set(Users.USERS.USERNAME, USERNAME_2)
                .set(Users.USERS.PASSWORDHASH, "somehash2")
                .execute();
    }

    @Test
    public void createMessageTest() {
        String title1 = "Title of the message 1";
        String text1 = "Text of the message 1";

        MessageDTO messageDTO1 = createMessageDTO(title1, text1, USERNAME_1);

        messagesService.createMessage(messageDTO1);

        List<MessageDTO> messages = messagesService.getMessagesForUser(USERNAME_1, null, null);
        assertEquals(1, messages.size());
        MessageDTO messageFound = messages.get(0);
        assertEquals(title1, messageFound.getTitle());
        assertEquals(text1, messageFound.getText());
        assertTrue(OffsetDateTime.now().isAfter(OffsetDateTime.parse(messageFound.getCreatedAt())));


        MessageDTO messageDTO2 = createMessageDTO("Title of the message 2", "Text of the message 2", USERNAME_1);

        messagesService.createMessage(messageDTO2);

        assertEquals(2, messagesService.getAllMessages(null, null).size());
        assertEquals(2, messagesService.getMessagesForUser(USERNAME_1, null, null).size());


        MessageDTO messageDTO3 = createMessageDTO("Title of the message 3", "Text of the message 3", USERNAME_2);

        messagesService.createMessage(messageDTO3);

        assertEquals(3, messagesService.getAllMessages(null, null).size());
        assertEquals(1, messagesService.getMessagesForUser(USERNAME_2, null, null).size());
        assertEquals(2, messagesService.getMessagesForUser(USERNAME_1, null, null).size());
    }

    private MessageDTO createMessageDTO(String title, String text, String username) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setTitle(title);
        messageDTO.setText(text);
        messageDTO.setUsername(username);
        return messageDTO;
    }

    @Test
    public void deleteMessageTest() {
        MessageDTO messageDTO1 = createMessageDTO("Title of the message 1", "Text of the message 1", USERNAME_1);
        MessageDTO messageDTO2 = createMessageDTO("Title of the message 2", "Text of the message 2", USERNAME_1);
        MessageDTO messageDTO3 = createMessageDTO("Title of the message 3", "Text of the message 3", USERNAME_2);

        messagesService.createMessage(messageDTO1);
        messagesService.createMessage(messageDTO2);
        messagesService.createMessage(messageDTO3);

        assertEquals(2, messagesService.getMessagesForUser(USERNAME_1, null, null).size());
        assertEquals(1, messagesService.getMessagesForUser(USERNAME_2, null, null).size());
        assertEquals(3, messagesService.getAllMessages(null, null).size());

        MessageDTO messageByUser1 = messagesService.getMessagesForUser(USERNAME_1, null, null).get(0);
        messagesService.deleteMessage(messageByUser1.getId(), USERNAME_1);

        assertEquals(1, messagesService.getMessagesForUser(USERNAME_1, null, null).size());
        assertEquals(2, messagesService.getAllMessages(null, null).size());

        List<MessageDTO> messagesByUser2 = messagesService.getMessagesForUser(USERNAME_2, null, null);
        assertEquals(1, messagesByUser2.size());

        assertThrows(IllegalArgumentException.class, () ->
                messagesService.deleteMessage(messagesByUser2.get(0).getId(), USERNAME_1));

        assertEquals(2, messagesService.getAllMessages(null, null).size());
    }

    @Test
    public void updateTest() {
        String title1 = "Title of the message 1";
        String text1 = "Text of the message 1";
        MessageDTO messageDTO1 = createMessageDTO(title1, text1, USERNAME_1);

        messagesService.createMessage(messageDTO1);

        List<MessageDTO> messages = messagesService.getMessagesForUser(USERNAME_1, null, null);
        assertEquals(1, messages.size());

        MessageDTO firstMessage = messages.get(0);
        long differenceInCreatedAndUpdated = OffsetDateTime.parse(firstMessage.getCreatedAt()).getNano() -
                OffsetDateTime.parse(firstMessage.getUpdatedAt()).getNano();


        String title2 = "Updated Title of the message 1";
        String text2 = "Updated Text of the message 1";
        MessageDTO updatedMessageDTO = createMessageDTO(title2, text2, USERNAME_1);
        updatedMessageDTO.setId(firstMessage.getId());
        updatedMessageDTO.setUsername(firstMessage.getUsername());

        messagesService.updateMessage(updatedMessageDTO);

        List<MessageDTO> messagesAgain = messagesService.getMessagesForUser(USERNAME_1, null, null);
        assertEquals(1, messagesAgain.size());

        MessageDTO messageUpdated = messagesAgain.get(0);
        assertEquals(title2, messageUpdated.getTitle());
        assertEquals(text2, messageUpdated.getText());
        assertNotEquals(differenceInCreatedAndUpdated, OffsetDateTime.parse(messageUpdated.getCreatedAt()).getNano() -
                OffsetDateTime.parse(messageUpdated.getUpdatedAt()).getNano());

        MessageDTO incorrectDTO = createMessageDTO(title2, text2, USERNAME_2);
        incorrectDTO.setId(messageUpdated.getId());
        assertThrows(IllegalArgumentException.class, () -> messagesService.updateMessage(incorrectDTO));
    }

    @Test
    public void getAllMessagesTest() {
        String title1 = "Title of the message 1";
        String text1 = "Text of the message 1";
        MessageDTO messageDTO1 = createMessageDTO(title1, text1, USERNAME_1);

        messagesService.createMessage(messageDTO1);

        List<MessageDTO> allMessages = messagesService.getAllMessages(null, null);

        assertEquals(1, allMessages.size());

        MessageDTO messageDTO1constructed = allMessages.get(0);

        assertEquals(title1, messageDTO1constructed.getTitle());
        assertEquals(text1, messageDTO1constructed.getText());
        assertEquals(USERNAME_1, messageDTO1constructed.getUsername());

        try {
            OffsetDateTime.parse(messageDTO1constructed.getCreatedAt());
            OffsetDateTime.parse(messageDTO1constructed.getUpdatedAt());
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void getAllMessagesOrderTest() {
        MessageDTO messageDTO1 = createMessageDTO("Title of the message 1", "Text of the message 1", USERNAME_1);
        MessageDTO messageDTO2 = createMessageDTO("Title of the message 2", "Text of the message 2",  USERNAME_1);
        MessageDTO messageDTO3 = createMessageDTO("Title of the message 3", "Text of the message 3", USERNAME_2);

        messagesService.createMessage(messageDTO1);
        messagesService.createMessage(messageDTO2);
        messagesService.createMessage(messageDTO3);

        List<MessageDTO> allMessagesAgain = messagesService.getAllMessages(null, null);

        assertEquals(3, allMessagesAgain.size());

        OffsetDateTime message1CreatedAt = OffsetDateTime.parse(allMessagesAgain.get(0).getCreatedAt());
        OffsetDateTime message3CreatedAt = OffsetDateTime.parse(allMessagesAgain.get(2).getCreatedAt());
        assertTrue(message1CreatedAt.isAfter(message3CreatedAt));
    }

    @Test
    public void getAllMessagesPaginationTest() {
        MessageDTO messageDTO1 = createMessageDTO("Title of the message 1", "Text of the message 1", USERNAME_1);
        String title2 = "Title of the message 2";
        MessageDTO messageDTO2 = createMessageDTO(title2, "Text of the message 2", USERNAME_1);
        String title3 = "Title of the message 3";
        MessageDTO messageDTO3 = createMessageDTO(title3, "Text of the message 3", USERNAME_2);

        messagesService.createMessage(messageDTO1);
        messagesService.createMessage(messageDTO2);
        messagesService.createMessage(messageDTO3);

        List<MessageDTO> allMessages = messagesService.getAllMessages(0, 2);

        assertEquals(2, allMessages.size());
        assertEquals(title2, allMessages.get(1).getTitle());

        List<MessageDTO> allMessagesWithOffset = messagesService.getAllMessages(1, null);

        assertEquals(2, allMessagesWithOffset.size());
        assertEquals(title2, allMessagesWithOffset.get(0).getTitle());

        List<MessageDTO> allMessagesWithLimit = messagesService.getAllMessages(null, 1);

        assertEquals(1, allMessagesWithLimit.size());
        assertEquals(title3, allMessagesWithLimit.get(0).getTitle());

        List<MessageDTO> allMessagesEmpty = messagesService.getAllMessages(4, 100);

        assertTrue(allMessagesEmpty.isEmpty());
    }

}
