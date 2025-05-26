package com.dockerino.demo.security;

import com.dockerino.demo.model.AuthProvider;
import com.dockerino.demo.model.User;
import com.dockerino.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerType = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = AuthProvider.valueOf(providerType.toUpperCase());

        String providerId;
        String email;
        String name;

        if (provider.equals(AuthProvider.GOOGLE)) {
            providerId = (String) attributes.get("sub"); // Google's unique ID
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else {
            // Potentially handle other providers or throw an exception
            throw new OAuth2AuthenticationException("Unsupported provider: " + providerType);
        }

        if (!StringUtils.hasText(email)) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getProvider().equals(provider)) {
                // User exists but signed up with a different provider
                // You might want to link accounts or throw an error
                 throw new OAuth2AuthenticationException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            // Update existing user's details if necessary (e.g., name)
            user.setUsername(name); // Google might update name
            user.setProviderId(providerId); // Ensure providerId is set if they logged in via email first
        } else {
            // Register new user
            user = new User();
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setEmail(email);
            user.setUsername(name);
            // Password can be null for OAuth2 users or provide a way for user to set one up
        }
        user = userRepository.save(user);
        return CustomUserDetails.create(user, attributes);
    }
}