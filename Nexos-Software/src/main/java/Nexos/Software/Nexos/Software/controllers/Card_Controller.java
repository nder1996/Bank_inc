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


//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;

@RestController
@RequestMapping("/card")
public class Card_Controller {


    Card_Service cardService = new Card_Service();

    @Autowired
    public Card_Controller(Card_Service cardService) {
        this.cardService = cardService;
    }


    @GetMapping("/{productId}/number")
    public ResponseEntity<Card_Entity> createCard(@PathVariable String productId){
        try {
            Card_Entity newCard = new Card_Entity();
            newCard = cardService.createCard(productId);
            if (newCard.getIdCard() != null) {
                return ResponseEntity.ok(newCard);
            } else {
                return ResponseEntity.notFound().build();
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se creo la nueva tarjeta", e);
        }

    }

    @PostMapping("/enroll")
    public ResponseEntity<String> cardActive(@RequestBody String cardId){
        String estado = "";
        Card_Entity newCard = new Card_Entity();
        String numeros = cardId.replaceAll("[^0-9]", "");
        try {
            estado = cardService.activeCard(numeros);
            if(estado=="tarjeta activada"){
                return ResponseEntity.ok("tarjeta activada");
            }else {
                return ResponseEntity.ok(estado);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se actualizo la tarjeta", e);
        }
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable String cardId){
        String estado = "";
        Card_Entity newCard = new Card_Entity();
        String numeros = cardId.replaceAll("[^0-9]", "");
        try {
            estado = cardService.bloqueoCard(numeros);
            if (estado == "tarjeta bloqueada") {
                return ResponseEntity.ok("tarjeta bloqueada");
            } else {
                return ResponseEntity.ok(estado);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se bloqueo la tarjeta", e);
        }
    }


    @PostMapping("/{balance}")
    public ResponseEntity<String> recargarBalance(@RequestBody String cardBalance ){
        String estado = "";
        try {
            estado = cardService.recargarBalance(cardBalance);
            if (estado == "Su tarjeta se ha recargado") {
                return ResponseEntity.ok("Su tarjeta se ha recargado");
            } else {
                return ResponseEntity.ok(estado);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se recargado la tarjeta", e);
        }
    }


    @GetMapping("/balance/{cardId}")
    public ResponseEntity<Object[]> consultarBalance(@PathVariable String cardId){
        Card_Entity card = new Card_Entity();
        Object[] consulta = new Object[2];
        try {
            consulta = cardService.consultarBalance(cardId);
            return ResponseEntity.ok(consulta);
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se pudo traer el registro al buscar en la base de datos", e);
        }


    }



}
