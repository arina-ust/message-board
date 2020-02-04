package home.assignment.messageboard.service;

import home.assignment.messageboard.model.UserDTO;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.generated.db.tables.Users;
import org.jooq.generated.db.tables.records.UsersRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.management.InstanceAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql("/truncate_tables.sql")
public class UsersServiceTest {

    @Autowired
    private UsersService usersService;
    @Autowired
    private DSLContext dslContext;


    @Test
    public void createUserTest() {
        String username1 = "user1@email.com";
        String password1 = "123";

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username1);
        userDTO.setPassword(password1);

        createUserOrFail(userDTO);

        Result<UsersRecord> foundUsers = dslContext.selectFrom(Users.USERS)
                .where(Users.USERS.USERNAME.eq(username1))
                .fetch();

        assertEquals(1, foundUsers.size());

        assertEquals(username1, foundUsers.get(0).getUsername());
        assertEquals(1, foundUsers.get(0).getId());


        UserDTO userDTO2 = new UserDTO();
        userDTO2.setUsername("user2@email.com");
        userDTO2.setPassword("12");

        createUserOrFail(userDTO2);

        assertEquals(2, dslContext.fetchCount(Users.USERS));
    }

    private void createUserOrFail(UserDTO userDTO) {
        try {
            usersService.createUser(userDTO);
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void createSameUserTest() {
        String username1 = "user1@email.com";
        String password1 = "123";

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username1);
        userDTO.setPassword(password1);

        createUserOrFail(userDTO);

        assertThrows(InstanceAlreadyExistsException.class, () -> usersService.createUser(userDTO));
    }

}
