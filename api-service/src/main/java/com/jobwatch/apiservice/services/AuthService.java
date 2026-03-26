package com.jobwatch.apiservice.services;

import com.jobwatch.apiservice.dto.AuthResponseDTO;
import com.jobwatch.apiservice.dto.DtoMapper;
import com.jobwatch.apiservice.models.User;
import com.jobwatch.apiservice.repositories.UserRepository;
import com.jobwatch.apiservice.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponseDTO register(String username, String password) {
        if (username == null || username.contains(" ") || username.length() < 3) {
            throw new RuntimeException("Username must be at least 3 characters and contain no spaces.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("That username is already taken.");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        userRepository.save(user);

        String token = jwtUtil.generateToken(username);
        return new AuthResponseDTO(token, DtoMapper.toUserDTO(user));
    }

    public AuthResponseDTO login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No account found with that username."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password.");
        }

        String token = jwtUtil.generateToken(username);
        return new AuthResponseDTO(token, DtoMapper.toUserDTO(user));
    }
}