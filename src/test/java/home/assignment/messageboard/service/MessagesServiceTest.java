package home.assignment.messageboard.service;

import home.assignment.messageboard.model.Message;
import home.assignment.messageboard.model.MessageDTO;
import org.jooq.DSLContext;
import org.jooq.generated.flyway.db.h2.tables.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        MessageDTO messageDTO1 = createMessageDTO(title1, text1, 1);

        messagesService.createMessage(messageDTO1);

        List<Message> messages = messagesService.getMessagesForUser(USERNAME_1);
        assertEquals(1, messages.size());
        Message messageFound = messages.get(0);
        assertEquals(title1, messageFound.getTitle());
        assertEquals(text1, messageFound.getText());
        assertTrue(OffsetDateTime.now().isAfter(messageFound.getCreatedAt()));


        MessageDTO messageDTO2 = createMessageDTO("Title of the message 2", "Text of the message 2", 1);

        messagesService.createMessage(messageDTO2);

        assertEquals(2, messagesService.getAllMessages().size());
        assertEquals(2, messagesService.getMessagesForUser(USERNAME_1).size());


        MessageDTO messageDTO3 = createMessageDTO("Title of the message 3", "Text of the message 3", 2);

        messagesService.createMessage(messageDTO3);

        assertEquals(3, messagesService.getAllMessages().size());
        assertEquals(1, messagesService.getMessagesForUser(USERNAME_2).size());
        assertEquals(2, messagesService.getMessagesForUser(USERNAME_1).size());
    }

    private MessageDTO createMessageDTO(String title, String text, int userId) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setTitle(title);
        messageDTO.setText(text);
        messageDTO.setUserId(userId);
        return messageDTO;
    }

    @Test
    public void deleteMessageTest() {
        MessageDTO messageDTO1 = createMessageDTO("Title of the message 1", "Text of the message 1", 1);

        MessageDTO messageDTO2 = createMessageDTO("Title of the message 2", "Text of the message 2", 1);

        MessageDTO messageDTO3 = createMessageDTO("Title of the message 3", "Text of the message 3", 2);

        messagesService.createMessage(messageDTO1);
        messagesService.createMessage(messageDTO2);
        messagesService.createMessage(messageDTO3);

        assertEquals(2, messagesService.getMessagesForUser(USERNAME_1).size());
        assertEquals(1, messagesService.getMessagesForUser(USERNAME_2).size());
        assertEquals(3, messagesService.getAllMessages().size());

        Message message = messagesService.getMessagesForUser(USERNAME_1).get(0);
        messagesService.deleteMessage(message.getId());

        assertEquals(1, messagesService.getMessagesForUser(USERNAME_1).size());
        assertEquals(1, messagesService.getMessagesForUser(USERNAME_2).size());
        assertEquals(2, messagesService.getAllMessages().size());
    }
}
