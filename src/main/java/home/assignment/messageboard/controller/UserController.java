package home.assignment.messageboard.controller;

import home.assignment.messageboard.api.V1ApiDelegate;
import home.assignment.messageboard.model.UserDTO;
import home.assignment.messageboard.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.management.InstanceAlreadyExistsException;

@Controller
public class UserController implements V1ApiDelegate {

    private UsersService usersService;

    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public ResponseEntity<Void> signIn(UserDTO body) {
        // TODO issue token
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> signUp(UserDTO body) {
        try {
            usersService.createUser(body);
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        // TODO issue token
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
