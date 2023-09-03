package Nexos.Software.Nexos.Software.controllers;


import Nexos.Software.Nexos.Software.entitys.Card_Entity;
import Nexos.Software.Nexos.Software.entitys.Transaction_Entity;
import Nexos.Software.Nexos.Software.services.Card_Service;
import Nexos.Software.Nexos.Software.services.Transaction_Services;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/transaction")
public class Transaction_Controller {


    Transaction_Services transactionServices = new Transaction_Services();

    Transaction_Entity transactionEntity = new Transaction_Entity();

    @Autowired
    public Transaction_Controller(Transaction_Services transactionServices) {
        this.transactionServices = transactionServices;
    }

    @PostMapping("/purchase")
    public ResponseEntity<String> createTransaction(@RequestBody String transaction){
        String estado = "";
        transactionEntity = new Transaction_Entity();
        try {
            estado = transactionServices.createTransaction(transaction);
            return ResponseEntity.ok(estado);
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se actualizo la tarjeta", e);
        }
    }



}
