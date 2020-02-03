package home.assignment.messageboard.service;

import home.assignment.messageboard.repository.UsersRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UsersRepository usersRepository;

    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        home.assignment.messageboard.model.User user = usersRepository.getUser(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(user.getUserName(), user.getPasswordHash(), Collections.emptyList());
    }
}
