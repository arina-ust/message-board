package home.assignment.messageboard.controller;

import home.assignment.messageboard.configuration.jwt.JwtTokenUtil;
import home.assignment.messageboard.model.UserDTO;
import home.assignment.messageboard.service.CustomUserDetailsService;
import home.assignment.messageboard.service.UsersService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import javax.management.InstanceAlreadyExistsException;
import java.net.URI;

public class UserController {

    private UsersService usersService;

    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
    private CustomUserDetailsService userDetailsService;

    public UserController(UsersService usersService, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, CustomUserDetailsService userDetailsService) {
        this.usersService = usersService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }


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
