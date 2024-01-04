package com.dagnerchuman.springbootmicroservice3ApiGateway.service;

import com.dagnerchuman.springbootmicroservice3ApiGateway.model.Role;
import com.dagnerchuman.springbootmicroservice3ApiGateway.model.User;
import com.dagnerchuman.springbootmicroservice3ApiGateway.repository.UserRepository;
import com.dagnerchuman.springbootmicroservice3ApiGateway.security.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;  // Agrega esta línea

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        // Inicializa manualmente el objeto UserService con los mocks
        userService = new UserServiceImpl(userRepository, passwordEncoder, jwtProvider);

        user = new User();
        user.setNombre(new String());
        user.setEmail(new String());
        user.setApellido(new String());
        user.setTelefono(new String());
        user.setId(11L);
        user.setRole(Role.USER);
        user.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void findAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        assertNotNull(userService.findAllUsers());
    }

    @Test
    void saveUser() {
        when(userRepository.save(any())).thenReturn(user);
        when(jwtProvider.generateToken(any(User.class))).thenReturn("dummyToken");

        User savedUser = userService.saveUser(user);

        assertNotNull(savedUser);
        assertEquals("dummyToken", savedUser.getToken());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void updateUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        User updatedUser = userService.updateUser(11L, user);

        assertNotNull(updatedUser);
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getByTokenPassword() {
        String tokenPassword = "dummyTokenPassword";
        when(userRepository.findByTokenPassword(tokenPassword)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getByTokenPassword(tokenPassword);

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
        verify(userRepository, times(1)).findByTokenPassword(tokenPassword);
    }
}
