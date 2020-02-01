package home.assignment.messageboard.repository;

import home.assignment.messageboard.model.User;
import org.jooq.DSLContext;
import org.jooq.generated.flyway.db.h2.tables.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UsersRepository {

    @Autowired
    private DSLContext dslContext;

    private static Users usersTable = Users.USERS;

    @Transactional
    public void createUser(User user) {
        dslContext.insertInto(usersTable)
                .set(usersTable.USERNAME, user.getUserName())
                .set(usersTable.PASSWORDHASH, user.getPasswordHash())
                .execute();
    }

    public User getUser(String username) {
        return dslContext.selectFrom(usersTable)
                .where(usersTable.USERNAME.eq(username))
                .fetchOneInto(User.class);
    }

}
