package com.dockerino.demo.config.security;

import com.dockerino.demo.model.AppUser;
import com.dockerino.demo.service.ProviderService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Component
public class UserSyncFilter extends OncePerRequestFilter {

    private final ProviderService providerService;

    public UserSyncFilter(ProviderService providerService) {
        this.providerService = providerService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();

            String sub = jwt.getSubject();

            AppUser user = providerService.getOrCreate(sub);

            List<GrantedAuthority> authorities = Stream.concat(
                    user.roles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name().toUpperCase())),
                            jwtAuth.getAuthorities().stream()
                    )
                    .distinct()
                    .toList();

            JwtAuthenticationToken enhancedJwtAuthToken = new JwtAuthenticationToken(jwt, authorities, sub);
            SecurityContextHolder.getContext().setAuthentication(enhancedJwtAuthToken);
        }

        filterChain.doFilter(request, response);
    }

}
