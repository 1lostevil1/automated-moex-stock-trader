package org.example.middleware;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.JwtTokenUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Component
public class BearerAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken;

        final String bearerStarting = "Bearer ";

        if (authHeader != null && authHeader.startsWith(bearerStarting)) {
            jwtToken = authHeader;
            try {
                username = jwtTokenUtils.getUsername(jwtToken);
            } catch (ExpiredJwtException e) {
                log.info("время жизни токена {} вышло", jwtToken);
            }
            catch (MalformedJwtException e) {
                log.info("токен {} поврежден", jwtToken);
            }
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
