package home.assignment.messageboard.repository;

import home.assignment.messageboard.model.Message;
import org.jooq.DSLContext;
import org.jooq.generated.flyway.db.h2.tables.Messages;
import org.jooq.generated.flyway.db.h2.tables.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class MessagesRepository {

    private final static Logger logger = LoggerFactory.getLogger(MessagesRepository.class);

    @Autowired
    private DSLContext dslContext;

    private static Messages messagesTable = Messages.MESSAGES;
    private static Users usersTable = Users.USERS;

    @Transactional
    public void createMessage(Message message) {
        dslContext.insertInto(messagesTable)
                .set(messagesTable.TITLE, message.getTitle())
                .set(messagesTable.TEXT, message.getText())
                .set(messagesTable.AUTHOR, message.getAuthor())
                .set(messagesTable.CREATED_AT, OffsetDateTime.now())
                .set(messagesTable.UPDATED_AT, OffsetDateTime.now())
                .execute();
        logger.info("Created new message {}", message);
    }

    public List<Message> getMessagesForUser(String username, Integer offset, Integer limit) {
        return dslContext.selectFrom(messagesTable)
                .where(messagesTable.AUTHOR.eq(username))
                .orderBy(messagesTable.UPDATED_AT.desc())
                .offset(offset)
                .limit(limit)
                .fetchInto(Message.class);

    }

    public List<Message> getAllMessages(Integer offset, Integer limit) {
        return dslContext.selectFrom(messagesTable)
                .orderBy(messagesTable.CREATED_AT.desc())
                .offset(offset)
                .limit(limit)
                .fetchInto(Message.class);
    }

    @Transactional
    public void updateMessage(Message message) {
        int messageId = message.getId();

        int rowsAffected = dslContext.update(messagesTable)
                .set(messagesTable.TITLE, message.getTitle())
                .set(messagesTable.TEXT, message.getText())
                .set(messagesTable.UPDATED_AT, OffsetDateTime.now())
                .where(messagesTable.ID.eq(messageId))
                .and(messagesTable.AUTHOR.eq(message.getAuthor()))
                .execute();

        if (rowsAffected == 0) {
            logger.error("Failed to update. Either there is no message with id {} or it was created by another user" +
                    ", not {}", messageId, message.getAuthor());
            throw new IllegalArgumentException("Message with id = " + messageId + " not found or " +
                    "it was created by another user. Cannot update it!");
        } else {
            logger.info("Updated message {}", message);
        }
    }

    @Transactional
    public void deleteMessage(int messageId, String username) {
        int rowsAffected = dslContext.deleteFrom(messagesTable)
                .where(messagesTable.ID.eq(messageId))
                .and(messagesTable.AUTHOR.eq(username))
                .execute();

        if (rowsAffected == 0) {
            logger.error("Did not find message to delete. Either there is no message with id {} " +
                    "or it was created by another user, not {}", messageId, username);
            throw new IllegalArgumentException("Message with id = " + messageId + " not found or " +
                    "it was created by another user. Cannot delete it!");
        } else {
            logger.info("Deleted message  with id {}", messageId);
        }
    }
}
