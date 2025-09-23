package com.finbridge.user.service;

import com.finbridge.user.dto.LoginRequest;
import com.finbridge.user.dto.LoginResponse;
import com.finbridge.user.dto.RegisterRequest;
import com.finbridge.user.dto.UserDto;
import com.finbridge.user.entity.User;
import com.finbridge.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public UserDto registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setActive(true);
        User saved = userRepository.save(user);
        return new UserDto(saved);
    }

    public LoginResponse loginUser(LoginRequest request) {
        User user = userRepository.findByEmailAndActive(request.getEmail(), true)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        String token = jwtService.generateToken(user.getEmail());
        return new LoginResponse(token, new UserDto(user));
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmailAndActive(email, true)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDto(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndActive(username, true)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        // Используем реализацию UserDetails из вашей DTO или Entity
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("USER")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getActive())
                .build();
    }
}
