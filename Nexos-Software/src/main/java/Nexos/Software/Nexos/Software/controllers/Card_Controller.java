package Nexos.Software.Nexos.Software.controllers;


import Nexos.Software.Nexos.Software.entitys.Card_Entity;
import Nexos.Software.Nexos.Software.services.Card_Service;
import com.fasterxml.jackson.core.JsonParser;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


/**
 * Controlador (Controller) de la entidad "Card_Entity".
 * Este controlador maneja las solicitudes relacionadas con la entidad "Card_Entity",
 * incluyendo la exposición de API REST para operaciones CRUD y la gestión de la
 * lógica de negocio relacionada.
 */

@RestController
@RequestMapping("/card")
public class Card_Controller {


    /**
     * Crea una nueva instancia (objeto) de la clase Card_Service
     * y asigna esta instancia a la variable cardService
     */
    Card_Service cardService = new Card_Service();


    /**
     * Constructor de la clase Card_Controller.
     *
     * @param cardService El servicio de tarjetas que será inyectado automáticamente por Spring.
     */
    @Autowired
    public Card_Controller(Card_Service cardService) {
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
            Card_Entity newCard = new Card_Entity();
            newCard = cardService.createCard(productId);
            if (newCard.getIdCard() != null) {
                return ResponseEntity.ok(newCard);
            } else {
                return ResponseEntity.ok("NO SE CREÓ LA TARJETA -  VERIFIQUE LA INFORMACIÓN INGRESADA (DEBEN SER 6 DÍGITOS)");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SE CREÓ LA TARJETA - HUBO UN ERROR VERIFIQUE LA INFORMACIÓN INGRESADA", e);
        }

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
        Card_Entity newCard = new Card_Entity();
        //String numeros = cardId.replaceAll("[^0-9]", "");
        try {
            estado = cardService.activeCard(cardId);
            return ResponseEntity.ok(estado);
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SE ACTUALIZÓ LA TARJETA", e);
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
        Card_Entity newCard = new Card_Entity();
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
        Card_Entity card = new Card_Entity();
        Object[] consulta = new Object[3];
        try {
            consulta = cardService.consultarBalance(cardId);
            if(consulta[1].toString().equals("")){
                return ResponseEntity.ok(consulta[0]);
            }else{
                return ResponseEntity.ok(consulta);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SE ENCONTRÓ EL REGISTRO", e);
        }


    }



}
