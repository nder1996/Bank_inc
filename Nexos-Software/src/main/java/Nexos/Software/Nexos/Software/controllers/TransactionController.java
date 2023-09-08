package Nexos.Software.Nexos.Software.controllers;


import Nexos.Software.Nexos.Software.entitys.CardEntity;
import Nexos.Software.Nexos.Software.entitys.TransactionEntity;

import Nexos.Software.Nexos.Software.services.TransactionServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


/**
 * Controlador (Controller) de la entidad "Transaction".
 * Este controlador maneja las solicitudes relacionadas con la entidad "Transaction",
 * incluyendo la exposición de API REST para operaciones de compra, consulta y anulación.
 */
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    /**
     * Crea una nueva instancia (objeto) de la clase Transaction_Services
     * y asigna esta instancia a la variable transactionServices
     */
    TransactionServices transactionServices = new TransactionServices();

    /**
     * Crea una nueva instancia (objeto) de la clase Transaction_Entity
     * y asigna esta instancia a la variable transactionEntity
     */
    TransactionEntity transactionEntity = new TransactionEntity();




    /**
     * Constructor de la clase Card_Controller.
     *
     * @param transactionServices El servicio de transacciones que será inyectado automáticamente por Spring.
     */
    @Autowired
    public TransactionController(TransactionServices transactionServices) {
        this.transactionServices = transactionServices;
    }




    /**
     * Crea una nueva transacción de compra.
     *
     * @param transaction Datos de la transacción de compra en formato JSON.
     * @return Una respuesta con el estado de la creación de la transacción o un mensaje de error.
     */
    @PostMapping("/purchase")
    public ResponseEntity<String> createTransaction(@RequestBody String transaction){
        String estado = "";
        transactionEntity = new TransactionEntity();
        try {
            estado = transactionServices.createTransaction(transaction);
            return ResponseEntity.ok(estado);
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se actualizo la tarjeta", e);
        }
    }

    /**
     * Consulta una transacción por su ID.
     *
     * @param transactionId El ID de la transacción a consultar.
     * @return Una respuesta con la información de la transacción consultada o un mensaje de error.
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<?> consultarTransaction(@PathVariable String transactionId){
        try {
            Optional<?> respuesta = Optional.empty();
            respuesta = transactionServices.consultarTransaction(transactionId);
            TransactionEntity transaction = new TransactionEntity();
            if (respuesta.isPresent()) {
                Object valor = respuesta.get();
                if (valor instanceof String) {
                    String cadena = (String) valor;
                    return ResponseEntity.ok(cadena);
                } else if (valor instanceof TransactionEntity) {
                    TransactionEntity entity = (TransactionEntity) valor;
                    return ResponseEntity.ok(entity);
                }
            } else {
                return ResponseEntity.ok("NO SE PUEDO REALIZAR LA SOLICITUD");
            }

        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "HUBO UN ERROR AL MOMENTO DE HACER LA CONSULTAR EL REGISTRO EN LA BASE DE DATOS", e);
        }
        return null;
    }

    /**
     * Anula una transacción existente.
     *
     * @param anulation Datos de la anulación de la transacción en formato JSON.
     * @return Una respuesta con el estado de la anulación de la transacción o un mensaje de error.
     */
    @PostMapping("/anulation")
    public ResponseEntity<String> anularTransaction(@RequestBody String anulation){
        String estado = "";
        try {
            estado  = transactionServices.anulacionTransaction(anulation);
            return ResponseEntity.ok(estado);
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hubo un error al momento de hacer la anulacion", e);
        }
    }



}
