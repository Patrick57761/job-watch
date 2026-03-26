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

    public AuthResponseDTO register(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("An account with that email already exists.");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);
        userRepository.save(user);

        String token = jwtUtil.generateToken(email);
        return new AuthResponseDTO(token, DtoMapper.toUserDTO(user));
    }

    public AuthResponseDTO login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with that email."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password.");
        }

        String token = jwtUtil.generateToken(email);
        return new AuthResponseDTO(token, DtoMapper.toUserDTO(user));
    }
}