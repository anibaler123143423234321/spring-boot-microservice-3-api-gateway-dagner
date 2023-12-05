package com.dagnerchuman.springbootmicroservice3ApiGateway.controller;

import com.dagnerchuman.springbootmicroservice3ApiGateway.Dto.ChangePasswordDTO;
import com.dagnerchuman.springbootmicroservice3ApiGateway.Dto.EmailValuesDto;
import com.dagnerchuman.springbootmicroservice3ApiGateway.Dto.Mensaje;
import com.dagnerchuman.springbootmicroservice3ApiGateway.model.User;
import com.dagnerchuman.springbootmicroservice3ApiGateway.service.EmailService;
import com.dagnerchuman.springbootmicroservice3ApiGateway.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("gateway/email")
@CrossOrigin
public class EmailController {
    @Autowired
    EmailService emailService;

    @Autowired
    UserServiceImpl userService;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String subject = "Cambio de contraseña";

/*
    @GetMapping("/send")
    public ResponseEntity<?> sendEmail(){
        emailService.sendEmail();
        return new ResponseEntity("Correo enviado con exito", HttpStatus.OK);
    }
*/
    @PostMapping("/send-html")
    public ResponseEntity<?> sendEmailTemplate(@RequestBody EmailValuesDto dto){
        Optional<User> userOpt = userService.getdByUsernameOrEmail(dto.getMailTo());
        if (!userOpt.isPresent())
            return new ResponseEntity(new Mensaje("No existe ningun usuario con esas credenciales"), HttpStatus.NOT_FOUND);

        User user = userOpt.get();
        dto.setMailFrom(mailFrom);
        dto.setMailTo(user.getEmail());
        dto.setSubject(subject);
        dto.setUserName(user.getUsername());
        UUID uuid = UUID.randomUUID();
        String tokenPassword = uuid.toString();
        dto.setTokenPassword(tokenPassword);
        user.setTokenPassword(tokenPassword);
        userService.updateTokenPassword(user, user.getTokenPassword());
        emailService.sendEmail(dto);
        return new ResponseEntity(new Mensaje("Correo con plantilla enviado con exito"), HttpStatus.OK);
    }



    @PostMapping("/change-password")
    public ResponseEntity<?> changePasswordByTokenPassword(
            @Valid @RequestBody ChangePasswordDTO dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new Mensaje("Campos mal puestos"), HttpStatus.BAD_REQUEST);
        }

        // Buscar al usuario por tokenPassword
        Optional<User> userOpt = userService.getByTokenPassword(dto.getTokenPassword());
        if (!userOpt.isPresent()) {
            return new ResponseEntity<>(new Mensaje("No existe ningún usuario con ese token"), HttpStatus.NOT_FOUND);
        }

        User user = userOpt.get();
        String newPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(newPassword);
        user.setTokenPassword(null);
        userService.updateTokenPassword(user, user.getTokenPassword());

        return new ResponseEntity<>(new Mensaje("Contraseña actualizada"), HttpStatus.OK);
    }


    @PostMapping("/send-htmluser")
    public ResponseEntity<?> sendEmailDeleteUser(@RequestBody EmailValuesDto dto) {
        Optional<User> userOpt = userService.getdByUsernameOrEmail(dto.getMailTo());
        if (!userOpt.isPresent())
            return new ResponseEntity(new Mensaje("No existe ningún usuario con esas credenciales"), HttpStatus.NOT_FOUND);

        User user = userOpt.get();
        dto.setMailFrom(mailFrom);
        dto.setMailTo(user.getEmail());
        dto.setSubject(subject);
        dto.setUserName(user.getUsername());

        // Establecer el tiempo de eliminación a 7 días desde ahora
        LocalDateTime deletionTime = LocalDateTime.now().plusDays(7);

        UUID uuid = UUID.randomUUID();
        String tokenPassword = uuid.toString();
        dto.setTokenPassword(tokenPassword);

        // Almacenar tanto el tokenPassword como el deletionTime en el usuario
        user.setTokenPassword(tokenPassword);
        user.setDeletionTime(deletionTime);

        userService.updateUser(user.getId(), user);

        // Llamar al servicio de correo electrónico correspondiente
        emailService.sendEmailDelete(dto);

        return new ResponseEntity(new Mensaje("Correo con plantilla enviado con éxito"), HttpStatus.OK);
    }


}