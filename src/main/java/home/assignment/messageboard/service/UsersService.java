package home.assignment.messageboard.service;

import home.assignment.messageboard.model.User;
import home.assignment.messageboard.model.UserDTO;
import home.assignment.messageboard.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;

@Service
public class UsersService {

    private UsersRepository usersRepository;
    private PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(UserDTO userDto) throws InstanceAlreadyExistsException {
        if (usersRepository.exists(userDto.getUsername())) {
            // TODO: implement custom exception
            throw new InstanceAlreadyExistsException("User with username " + userDto.getUsername() + " already exists");
        }

        String password = passwordEncoder.encode(userDto.getPassword());
        User user = new User(userDto.getUsername(), password);

        usersRepository.createUser(user);
    }

    public boolean isPasswordVerified(UserDTO userDto) {
        User user = usersRepository.getUser(userDto.getUsername());
        return passwordEncoder.matches(userDto.getPassword(), user.getPasswordHash());
    }

}
