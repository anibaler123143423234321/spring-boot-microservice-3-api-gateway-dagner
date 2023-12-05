package com.dagnerchuman.springbootmicroservice3ApiGateway.controller;

import com.dagnerchuman.springbootmicroservice3ApiGateway.Dto.Mensaje;
import com.dagnerchuman.springbootmicroservice3ApiGateway.model.Role;
import com.dagnerchuman.springbootmicroservice3ApiGateway.model.User;
import com.dagnerchuman.springbootmicroservice3ApiGateway.security.UserPrincipal;
import com.dagnerchuman.springbootmicroservice3ApiGateway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://api-gateway:5200") // Esto permite solicitudes desde http://localhost:5200
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("listar")
    public List<User> listUsers() {
        return userService.findAllUsers();
    }


    @PutMapping("change/{role}")
    public ResponseEntity<?> changeRole(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Role role) {
        User user = userService.findByUsername(userPrincipal.getUsername()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (role == Role.SUPERADMIN && user.getRole() == Role.ADMIN) {
            // Actualiza el rol a SUPERADMIN si es un ADMIN
            userService.changeRole(Role.SUPERADMIN, userPrincipal.getUsername());
            return ResponseEntity.ok(true);
        } else {
            // Maneja otros cambios de roles aquí si es necesario
            userService.changeRole(role, userPrincipal.getUsername());
            return ResponseEntity.ok(true);
        }
    }


    @GetMapping()
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        return new ResponseEntity<>(userService.findByUsernameReturnToken(userPrincipal.getUsername()), HttpStatus.OK);
    }

    // http://locahost:5555/gateway/usuario/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long userId) {
        try {
            User usuario = userService.findUserById(userId);
            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("deleteAll")
    public ResponseEntity<?> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.ok("Todos los usuarios han sido eliminados.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updateUser) {
        try {
            User updatedUser = userService.updateUser(id, updateUser);

            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el usuario: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok("Usuario eliminado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el usuario: " + e.getMessage());
        }
    }


    @PostMapping("/schedule-delete")
    public ResponseEntity<?> scheduleDelete(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return new ResponseEntity<>(new Mensaje("El campo 'email' es requerido"), HttpStatus.BAD_REQUEST);
        }

        Optional<User> userOpt = userService.findByEmail(email);
        if (!userOpt.isPresent()) {
            return new ResponseEntity<>(new Mensaje("Usuario no encontrado"), HttpStatus.NOT_FOUND);
        }

        User user = userOpt.get();
        // Establecer el tiempo de eliminación a 7 días desde ahora
        LocalDateTime deletionTime = LocalDateTime.now().plusDays(7);
        userService.updateDeletionTime(user.getId(), deletionTime);

        return new ResponseEntity<>(new Mensaje("Eliminación programada con éxito"), HttpStatus.OK);
    }


    @PostMapping("/cancel-delete")
    public ResponseEntity<?> cancelDelete(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return new ResponseEntity<>(new Mensaje("El campo 'email' es requerido"), HttpStatus.BAD_REQUEST);
        }

        Optional<User> userOpt = userService.findByEmail(email);
        if (!userOpt.isPresent()) {
            return new ResponseEntity<>(new Mensaje("Usuario no encontrado"), HttpStatus.NOT_FOUND);
        }

        User user = userOpt.get();
        // Cancelar la eliminación estableciendo el tiempo de eliminación a null
        userService.updateDeletionTime(user.getId(), null);

        return new ResponseEntity<>(new Mensaje("Eliminación cancelada con éxito"), HttpStatus.OK);
    }




}
