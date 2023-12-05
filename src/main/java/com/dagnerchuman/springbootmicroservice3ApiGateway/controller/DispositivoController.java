package com.dagnerchuman.springbootmicroservice3ApiGateway.controller;

import com.dagnerchuman.springbootmicroservice3ApiGateway.request.DispositivoServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/gateway/dispositivo")
@CrossOrigin(origins = "http://api-gateway:5200")
public class DispositivoController {

    @Autowired
    private DispositivoServiceRequest dispositivoServiceRequest;

    // Guardar dispositivo
    @PostMapping("/saveDevice")
    public ResponseEntity<Object> saveDevice(@RequestBody Object dispositivo) {
        try {
            // Llama al servicio de dispositivos a través de FeignClient
            Object savedDispositivo = dispositivoServiceRequest.saveDevice(dispositivo);

            // Retorno de ejemplo (ajústalo según tus necesidades)
            return new ResponseEntity<>(savedDispositivo, HttpStatus.CREATED);
        } catch (Exception e) {
            // Manejo de excepciones, puedes personalizar según tus necesidades
            e.printStackTrace();
            return new ResponseEntity<>("Error al guardar el dispositivo.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Enviar notificación
    @PostMapping("/sendNotification/{deviceId}")
    public ResponseEntity<String> sendNotification(
            @PathVariable("deviceId") int deviceId,
            @RequestBody Object notification) {
        try {
            // Llama al servicio de dispositivos a través de FeignClient
            dispositivoServiceRequest.sendNotification(deviceId, notification);

            // Retorno de ejemplo (ajústalo según tus necesidades)
            return new ResponseEntity<>("Notificación enviada correctamente.", HttpStatus.OK);
        } catch (Exception e) {
            // Manejo de excepciones, puedes personalizar según tus necesidades
            e.printStackTrace();
            return new ResponseEntity<>("Error al enviar la notificación.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener todos los dispositivos
    @GetMapping("/getAllDevices")
    public ResponseEntity<List<Object>> getAllDevices() {
        try {
            // Llama al servicio de dispositivos a través de FeignClient
            List<Object> dispositivos = dispositivoServiceRequest.getAllDevices();

            // Retorno de ejemplo (ajústalo según tus necesidades)
            return new ResponseEntity<>(dispositivos, HttpStatus.OK);
        } catch (Exception e) {
            // Manejo de excepciones, puedes personalizar según tus necesidades
            e.printStackTrace();
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Enviar notificación a todos los dispositivos
    @PostMapping("/sendNotification")
    public ResponseEntity<String> sendNotification(@RequestBody Object notification) {
        try {
            // Llama al servicio de dispositivos a través de FeignClient
            dispositivoServiceRequest.sendNotificationToAll(notification);

            // Retorno de ejemplo (ajústalo según tus necesidades)
            return new ResponseEntity<>("Notificaciones enviadas correctamente.", HttpStatus.OK);
        } catch (Exception e) {
            // Manejo de excepciones, puedes personalizar según tus necesidades
            e.printStackTrace();
            return new ResponseEntity<>("Error al enviar las notificaciones.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Enviar notificación a dispositivos asociados a un negocio específico
    @PostMapping("/sendNotificationToBusiness/{negocioId}")
    public ResponseEntity<String> sendNotificationToBusiness(
            @PathVariable Long negocioId,
            @RequestBody Object notification) {
        try {
            // Llama al servicio de dispositivos a través de FeignClient
            dispositivoServiceRequest.sendNotificationToBusiness(notification, negocioId);

            // Retorno de ejemplo (ajústalo según tus necesidades)
            return new ResponseEntity<>("Notificaciones enviadas correctamente.", HttpStatus.OK);
        } catch (Exception e) {
            // Manejo de excepciones, puedes personalizar según tus necesidades
            e.printStackTrace();
            return new ResponseEntity<>("Error al enviar las notificaciones.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    private ResponseEntity<Object> handleException(Exception e, String errorMessage) {
        e.printStackTrace();
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    // Actualizar dispositivo con valores parciales
    @PutMapping("/updateDevice/{deviceId}")
    public ResponseEntity<Object> updateDevice(
            @PathVariable int deviceId,
            @RequestBody Object partialDispositivo) {
        try {
            // Llama al servicio de dispositivos a través de FeignClient
            ResponseEntity<Object> updatedDispositivo = dispositivoServiceRequest.updateDevice(deviceId, partialDispositivo);

            // Retorno de ejemplo (ajústalo según tus necesidades)
            return new ResponseEntity<>(updatedDispositivo.getBody(), updatedDispositivo.getStatusCode());
        } catch (Exception e) {
            // Manejo de excepciones, puedes personalizar según tus necesidades
            return handleException(e, "Error al actualizar el dispositivo.");
        }
    }


    @GetMapping("/getDevice/{deviceId}")
    public ResponseEntity<Object> getDeviceByDeviceId(@PathVariable String deviceId) {
        try {
            // Llama al servicio de dispositivos a través de FeignClient
            Object dispositivo = dispositivoServiceRequest.getDeviceByDeviceId(deviceId);

            // Retorno de ejemplo (ajústalo según tus necesidades)
            return new ResponseEntity<>(dispositivo, HttpStatus.OK);
        } catch (Exception e) {
            // Manejo de excepciones, puedes personalizar según tus necesidades
            e.printStackTrace();
            return new ResponseEntity<>("Error al recuperar el dispositivo.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}




