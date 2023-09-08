package Nexos.Software.Nexos.Software.controllers;

import Nexos.Software.Nexos.Software.entitys.CardEntity;
import Nexos.Software.Nexos.Software.entitys.TransactionEntity;
import Nexos.Software.Nexos.Software.repositorys.CardRepository;
import Nexos.Software.Nexos.Software.repositorys.TransactionRepository;
import Nexos.Software.Nexos.Software.services.CardService;
import Nexos.Software.Nexos.Software.services.TransactionServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 *
 El archivo TransactionControllerTest es un archivo de prueba unitaria que prueba el controlador de transacciones de la aplicación.
 El controlador de transacciones es una clase que proporciona una API REST para realizar transacciones de tarjetas.
 *
 */


class TransactionControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private CardController cardController;

    @InjectMocks
    private TransactionController transactionController;

    private CardEntity cardEntity = new CardEntity();

    @Mock
    private CardService cardService;

    @Mock
    private TransactionServices transactionServices;


    @Autowired
    private CardRepository cardRepository;


    @Autowired
    private TransactionRepository transactionRepository;


    @Autowired
    private TransactionEntity transactionEntity = new TransactionEntity();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    /**
     * este prueba el metodo del controller crear transaction en un escencario favorable
     */
    @Test
    public void testCreateTransactionFavorable() {
        try {
            String transaction = "{\"cardId\": \"1234567890123457\", \"price\": \"50.0\"}";
            when(transactionServices.createTransaction(transaction)).thenReturn("Transacción creada exitosamente");
            ResponseEntity<String> response = transactionController.createTransaction(transaction);
            verify(transactionServices, times(1)).createTransaction(transaction);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Transacción creada exitosamente", response.getBody());
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se actualizo la tarjeta", e);
        }

    }

    /**
     * este metodo testea en un evento favorable la consulta de una transaction
     */
    @Test
    public void testConsultarTransactionFavorable() {
        try {
            String transactionId = "123";
            TransactionEntity transactionEntity = new TransactionEntity();
            transactionEntity.setIdTransaction(1);
            transactionEntity.setState("COMPLETADA");
            when(transactionServices.consultarTransaction(eq(transactionId))).thenAnswer(invocation -> {
                return Optional.of(transactionEntity);
            });
            ResponseEntity<?> response = transactionController.consultarTransaction(transactionId);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(transactionEntity, response.getBody());
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "HUBO UN ERROR AL MOMENTO DE HACER LA CONSULTAR EL REGISTRO EN LA BASE DE DATOS", e);
        }
    }


    /**
     * este prueba el metodo del controller anula transaction en un escencario favorable
     */
    @Test
    public void testAnularTransactionFavorable() {
    try {
        String anulation = "{\"transactionId\":\"123\", \"reason\":\"Anulación de prueba\"}";
        String estado = "ANULADA";
        when(transactionServices.anulacionTransaction(anulation)).thenReturn(estado);
        ResponseEntity<String> response = transactionController.anularTransaction(anulation);
        verify(transactionServices, times(1)).anulacionTransaction(anulation);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(estado, response.getBody());
    }catch (Exception e){
        e.printStackTrace();
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hubo un error al momento de hacer la anulacion", e);
    }
    }


}