package home.assignment.messageboard.service;

import org.jooq.DSLContext;
import org.jooq.generated.flyway.db.h2.tables.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql("/truncate_tables.sql")
public class CustomUserDetailsServiceTest {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private DSLContext dslContext;

    @Test
    public void loadUserTest() {
        String username = "username@email.com";
        String passwordHash = "somepasswordhash";
        insertUser(username, passwordHash);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
        assertEquals(passwordHash, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    private void insertUser(String username, String passwordHash) {
        dslContext.insertInto(Users.USERS)
                .set(Users.USERS.USERNAME, username)
                .set(Users.USERS.PASSWORDHASH, passwordHash)
                .execute();
    }

    @Test
    public void loadUserFailTest() {
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("some@mail.com"));
    }


}
