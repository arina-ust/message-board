package home.assignment.messageboard.controller;

import home.assignment.messageboard.api.V1ApiDelegate;
import home.assignment.messageboard.configuration.jwt.JwtTokenUtil;
import home.assignment.messageboard.model.MessageDTO;
import home.assignment.messageboard.model.MessageListDTO;
import home.assignment.messageboard.model.UserDTO;
import home.assignment.messageboard.service.CustomUserDetailsService;
import home.assignment.messageboard.service.MessagesService;
import home.assignment.messageboard.service.UsersService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import javax.management.InstanceAlreadyExistsException;
import java.net.URI;
import java.util.NoSuchElementException;

@Controller
public class BoardController implements V1ApiDelegate {

    private MessagesService messagesService;
    private UsersService usersService;

    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
    private CustomUserDetailsService userDetailsService;

    public BoardController(MessagesService messagesService,
                           UsersService usersService,
                           AuthenticationManager authenticationManager,
                           JwtTokenUtil jwtTokenUtil,
                           CustomUserDetailsService userDetailsService) {
        this.messagesService = messagesService;
        this.usersService = usersService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public ResponseEntity<MessageListDTO> getAllMessages(Integer offset, Integer limit) {
        MessageListDTO messagesDTO = new MessageListDTO();
        messagesDTO.setMessages(messagesService.getAllMessages(offset, limit));
        return ResponseEntity.ok(messagesDTO);
    }

    @Override
    public ResponseEntity<MessageListDTO> getMessagesForUser(Integer offset, Integer limit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        MessageListDTO messagesDTO = new MessageListDTO();
        messagesDTO.setMessages(messagesService.getMessagesForUser(username, offset, limit));
        return ResponseEntity.ok(messagesDTO);
    }

    @Override
    public ResponseEntity<Void> createMessage(MessageDTO body) {
        messagesService.createMessage(body);
        return ResponseEntity.created(URI.create("/messages/")).build();
    }

    @Override
    public ResponseEntity<Void> deleteMessage(Integer id) {
        messagesService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> updateMessage(MessageDTO body, Integer id) {
        try {
            messagesService.updateMessage(body);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> signIn(UserDTO body) {
        try {
            authenticate(body.getUsername(), body.getPassword());
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().build();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(body.getUsername());

        String token = jwtTokenUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.noContent()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Override
    public ResponseEntity<Void> signUp(UserDTO body) {
        try {
            usersService.createUser(body);
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        String token = jwtTokenUtil.generateToken(body.getUsername());
        return ResponseEntity.created(URI.create("/users/"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
    }
}
