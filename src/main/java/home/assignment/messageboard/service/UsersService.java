package home.assignment.messageboard.service;

import home.assignment.messageboard.model.User;
import home.assignment.messageboard.model.UserDTO;
import home.assignment.messageboard.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;

@Service
public class UsersService {

    private final static Logger logger = LoggerFactory.getLogger(UsersService.class);

    private UsersRepository usersRepository;
    private PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(UserDTO userDto) throws InstanceAlreadyExistsException {
        if (usersRepository.exists(userDto.getUsername())) {
            // TODO: implement custom exception
            logger.error("Username {} was not found", userDto.getUsername());
            throw new InstanceAlreadyExistsException("User with username " + userDto.getUsername() + " already exists");
        }

        String password = passwordEncoder.encode(userDto.getPassword());
        User user = new User(userDto.getUsername(), password);

        usersRepository.createUser(user);
    }

}
