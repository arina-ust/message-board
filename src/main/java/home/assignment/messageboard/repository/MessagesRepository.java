package home.assignment.messageboard.repository;

import home.assignment.messageboard.model.Message;
import org.jooq.DSLContext;
import org.jooq.generated.flyway.db.h2.tables.Messages;
import org.jooq.generated.flyway.db.h2.tables.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class MessagesRepository {

    @Autowired
    private DSLContext dslContext;

    private static Messages messagesTable = Messages.MESSAGES;
    private static Users usersTable = Users.USERS;

    @Transactional
    public void createMessage(Message message) {
        dslContext.insertInto(messagesTable)
                .set(messagesTable.TITLE, message.getTitle())
                .set(messagesTable.TEXT, message.getText())
                .set(messagesTable.USER_ID, message.getUserId())
                .set(messagesTable.CREATED_AT, OffsetDateTime.now())
                .set(messagesTable.UPDATED_AT, OffsetDateTime.now())
                .execute();
    }

    public List<Message> getMessagesForUser(String username) {
        return dslContext.select(messagesTable.ID, messagesTable.USER_ID, messagesTable.TITLE, messagesTable.TEXT,
                messagesTable.CREATED_AT, messagesTable.UPDATED_AT)
                .from(messagesTable)
                .join(usersTable).on(usersTable.ID.eq(messagesTable.USER_ID))
                .where(usersTable.USERNAME.eq(username))
                .fetchInto(Message.class);

    }

    public List<Message> getAllMessages() {
        return dslContext.selectFrom(messagesTable)
                .fetchInto(Message.class);
    }

    @Transactional
    public void updateMessage(Message message) {
        int messageId = message.getId();

        if (!exists(messageId)) {
            throw new NoSuchElementException("Message with id = " + messageId + " does not exist. Cannot update it!");
        }

        dslContext.update(messagesTable)
                .set(messagesTable.TITLE, message.getTitle())
                .set(messagesTable.TEXT, message.getText())
                .set(messagesTable.UPDATED_AT, OffsetDateTime.now())
                .where(messagesTable.ID.eq(messageId))
                .execute();
    }

    private boolean exists(int messageId) {
        return dslContext.selectFrom(messagesTable).where(messagesTable.ID.eq(messageId)).fetchAny() != null;
    }

    @Transactional
    public void deleteMessage(int messageId) {
        dslContext.deleteFrom(messagesTable).where(messagesTable.ID.eq(messageId)).execute();
    }
}
