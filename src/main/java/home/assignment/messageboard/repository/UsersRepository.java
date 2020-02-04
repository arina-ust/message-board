package home.assignment.messageboard.repository;

import home.assignment.messageboard.model.User;
import org.jooq.DSLContext;
import org.jooq.generated.db.tables.Users;
import org.jooq.generated.db.tables.records.UsersRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UsersRepository {

    private final static Logger logger = LoggerFactory.getLogger(UsersRepository.class);

    @Autowired
    private DSLContext dslContext;

    private static Users usersTable = Users.USERS;

    @Transactional
    public void createUser(User user) {
        dslContext.insertInto(usersTable)
                .set(usersTable.USERNAME, user.getUserName())
                .set(usersTable.PASSWORDHASH, user.getPasswordHash())
                .execute();

        logger.info("Created new user {}", user);
    }

    public User getUser(String username) {
        return dslContext.selectFrom(usersTable)
                .where(usersTable.USERNAME.eq(username))
                .fetchOneInto(User.class);
    }

    public boolean exists(String username) {
        UsersRecord record = dslContext.selectFrom(usersTable)
                .where(usersTable.USERNAME.eq(username))
                .fetchAny();
        return record != null;
    }

}
