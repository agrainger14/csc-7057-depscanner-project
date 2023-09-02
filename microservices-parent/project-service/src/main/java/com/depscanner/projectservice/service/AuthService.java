package com.depscanner.projectservice.service;

import com.depscanner.projectservice.exception.UserNotAuthorisedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Service class for handling user authentication.
 */
@Service
public class AuthService {

    /**
     * Retrieves the authenticated user's email from the JWT token.
     *
     * @return The email of the authenticated user.
     * @throws UserNotAuthorisedException If the user is not authorized.
     */
    public String getAuthEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("email");
        }

        throw new UserNotAuthorisedException("User not authorised!");
    }
}
