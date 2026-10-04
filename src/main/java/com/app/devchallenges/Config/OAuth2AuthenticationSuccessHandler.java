package com.app.devchallenges.Config;

import com.app.devchallenges.Entity.User;
import com.app.devchallenges.Security.AuthenticatedUser;
import com.app.devchallenges.Security.GoogleOAuth2Service;
import com.app.devchallenges.Security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    private final GoogleOAuth2Service googleOAuth2Service;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauthUser =
                (OAuth2User) authentication.getPrincipal();

        User user =
                googleOAuth2Service.processGoogleUser(oauthUser);

        String token =
                jwtService.generateToken(
                        new AuthenticatedUser(user)
                );

        String username = user.getUsername();

        response.sendRedirect(
                "https://devchallenges.in/oauth2/success"
                        + "?token=" + token
                        + "&username=" + username
        );
    }
}
