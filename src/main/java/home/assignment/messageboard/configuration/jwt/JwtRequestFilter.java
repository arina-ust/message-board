package home.assignment.messageboard.configuration.jwt;

import home.assignment.messageboard.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String PREFIX = "Bearer";

    private CustomUserDetailsService jwtUserDetailsService;

    private JwtTokenUtil jwtTokenUtil;

    public JwtRequestFilter(CustomUserDetailsService jwtUserDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestTokenHeader = request.getHeader("Authorization");

        if (requestTokenHeader == null || !requestTokenHeader.startsWith(PREFIX)) {
            return;
        }

        String token = requestTokenHeader.substring(PREFIX.length() + 1);

        String username;
        try {
            username = jwtTokenUtil.getUsernameFromToken(token);
        } catch (ExpiredJwtException e) {
            return;
        }

        if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken = getUsernamePasswordAuthenticationToken(request, userDetails);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        WebAuthenticationDetails details = new WebAuthenticationDetailsSource().buildDetails(request);
        authenticationToken.setDetails(details);
        return authenticationToken;
    }
}