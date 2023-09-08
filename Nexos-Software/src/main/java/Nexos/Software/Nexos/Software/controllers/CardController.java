package Nexos.Software.Nexos.Software.controllers;


import Nexos.Software.Nexos.Software.entitys.CardEntity;
import Nexos.Software.Nexos.Software.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


/**
 * Controlador (Controller) de la entidad "Card_Entity".
 * Este controlador maneja las solicitudes relacionadas con la entidad "Card_Entity",
 * incluyendo la exposición de API REST para operaciones CRUD y la gestión de la
 * lógica de negocio relacionada.
 */

@RestController
@RequestMapping("/card")
public class CardController {
    /**
     * Crea una nueva instancia (objeto) de la clase Card_Service
     * y asigna esta instancia a la variable cardService
     */
    CardService cardService = new CardService();

    CardEntity cardEntity = new CardEntity();


    /**
     * Constructor de la clase Card_Controller.
     *
     * @param cardService El servicio de tarjetas que será inyectado automáticamente por Spring.
     */
    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }


    /**
     * Crea una nueva tarjeta.
     *
     * @param productId Identificador del producto asociado a la tarjeta.
     * @return Una respuesta con la nueva tarjeta creada o un mensaje de error.
     */
    @GetMapping("/{productId}/number")
    public ResponseEntity<?> createCard(@PathVariable String productId){
        try {
            Optional<?> respuesta = Optional.empty();
            respuesta  = cardService.CreateCard(productId);
            if (respuesta.isPresent()) {
                Object valor = respuesta.get();
                if (valor instanceof String) {
                    String cadena = (String) valor;
                    return ResponseEntity.ok(cadena);
                } else if (valor instanceof CardEntity) {
                    CardEntity entidad = (CardEntity) valor;
                    return ResponseEntity.ok(entidad);
                }
            } else {
                return ResponseEntity.ok("NO SE PUDO HACER LA OPERACION");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SE CREÓ LA TARJETA - HUBO UN ERROR VERIFIQUE LA INFORMACIÓN INGRESADA", e);
        }
        return null;
    }


    /**
     * Activa una tarjeta.
     *
     * @param cardId Número de la tarjeta a activar.
     * @return Una respuesta con el estado de la activación o un mensaje de error.
     */
    @PostMapping("/enroll")
    public ResponseEntity<String> cardActive(@RequestBody String cardId){
        String estado = "";
        try {
            estado = cardService.activeCard(cardId);
            return ResponseEntity.ok(estado);
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "HUBO UN ERROR Y NO SE ACTIVO LA TARJETA", e);
        }
    }




    /**
     * Bloquea una tarjeta.
     *
     * @param cardId Número de la tarjeta a bloquear.
     * @return Una respuesta con el estado del bloqueo o un mensaje de error.
     */
    @DeleteMapping("/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable String cardId){
        String estado = "";
        try {
            estado = cardService.bloqueoCard(cardId);
            return ResponseEntity.ok(estado);
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SE BLOQUEÓ LA TARJETA", e);
        }
    }



    /**
     * Recarga el saldo de una tarjeta.
     *
     * @param cardBalance Información de la recarga de saldo.
     * @return Una respuesta con el estado de la recarga o un mensaje de error.
     */
    @PostMapping("/{balance}")
    public ResponseEntity<String> recargarBalance(@RequestBody String cardBalance ){
        String estado = "";
        try {
            estado = cardService.recargarBalance(cardBalance);
            return ResponseEntity.ok(estado);
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SE RECARGÓ LA TARJETA", e);
        }
    }

    /**
     * Consulta el saldo de una tarjeta.
     *
     * @param cardId Número de la tarjeta a consultar.
     * @return Una respuesta con la información del saldo o un mensaje de error.
     */
    @GetMapping("/balance/{cardId}")
    public ResponseEntity<?> consultarBalance(@PathVariable String cardId){
        Optional<?> respuesta = Optional.empty();
        try {
            respuesta  = cardService.consultarBalance(cardId);
            Object valor = respuesta.get();
            if (respuesta.isPresent()) {
                if (valor instanceof String) {
                    String cadena = (String) valor;
                    return ResponseEntity.ok(cadena);
                }if(valor instanceof CardEntity){
                    CardEntity entidad = (CardEntity) valor;
                    return ResponseEntity.ok(entidad);
                }
            }else{
                return ResponseEntity.ok("NO SE PUDO RETORNAR UN VALOR");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SE ENCONTRÓ EL REGISTRO", e);
        }
        return null;
    }




}