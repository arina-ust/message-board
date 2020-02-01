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

    private static String USERNAME_1 = "user1";

    @BeforeEach
    public void setUp() {
        dslContext.insertInto(Users.USERS)
                .set(Users.USERS.USERNAME, USERNAME_1)
                .set(Users.USERS.PASSWORDHASH, "somehash")
                .execute();
    }

    @Test
    public void createMessageTest() {
        String title1 = "Title of the message 1";
        String text1 = "Text of the message 1";

        MessageDTO messageDTO1 = new MessageDTO();
        messageDTO1.setTitle(title1);
        messageDTO1.setText(text1);
        messageDTO1.setUserId(1);

        messagesService.createMessage(messageDTO1);

        List<Message> messages = messagesService.getMessagesForUser(USERNAME_1);
        assertEquals(1, messages.size());
        Message messageFound = messages.get(0);
        assertEquals(title1, messageFound.getTitle());
        assertEquals(text1, messageFound.getText());
        assertTrue(OffsetDateTime.now().isAfter(messageFound.getCreatedAt()));

        MessageDTO messageDTO2 = new MessageDTO();
        messageDTO2.setTitle("Title of the message 2");
        messageDTO2.setText("Text of the message 2");
        messageDTO2.setUserId(1);

        messagesService.createMessage(messageDTO2);

        assertEquals(2, messagesService.getAllMessages().size());
        assertEquals(2, messagesService.getMessagesForUser(USERNAME_1).size());

        String username2 = "user2";
        dslContext.insertInto(Users.USERS)
                .set(Users.USERS.USERNAME, username2)
                .set(Users.USERS.PASSWORDHASH, "somehash2")
                .execute();

        MessageDTO messageDTO3 = new MessageDTO();
        messageDTO3.setTitle("Title of the message 3");
        messageDTO3.setText("Text of the message 3");
        messageDTO3.setUserId(2);

        messagesService.createMessage(messageDTO3);

        assertEquals(3, messagesService.getAllMessages().size());
        assertEquals(1, messagesService.getMessagesForUser(username2).size());
        assertEquals(2, messagesService.getMessagesForUser(USERNAME_1).size());
    }
}
