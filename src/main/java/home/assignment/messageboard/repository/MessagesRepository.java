package home.assignment.messageboard.repository;

import home.assignment.messageboard.model.Message;
import org.jooq.DSLContext;
import org.jooq.generated.flyway.db.h2.tables.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class MessagesRepository {

    @Autowired
    private DSLContext dslContext;

    private static Messages messagesTable = Messages.MESSAGES;

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

    public List<Message> getMessagesForUser(int userId) {
        return dslContext.selectFrom(messagesTable)
                .where(messagesTable.USER_ID.eq(userId))
                .fetchInto(Message.class);

    }

    public List<Message> getAllMessages() {
        return dslContext.selectFrom(messagesTable)
                .fetchInto(Message.class);
    }

    @Transactional
    public void updateMessage(int messageId, Message newMessage) {
        throw new UnsupportedOperationException();
    }

    @Transactional
    public void deleteMessage(int messageId) {
        throw new UnsupportedOperationException();
    }
}
