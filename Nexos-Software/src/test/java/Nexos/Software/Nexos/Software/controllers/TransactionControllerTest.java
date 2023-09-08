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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    public void testCreateTransactionFavorable() {
        try {

            // Configura el comportamiento del mock transactionService
            String validTransactionJson = "{\"key\":\"value\"}"; // Coloca aquí un JSON válido
            when(transactionServices.createTransaction(validTransactionJson)).thenReturn("Transacción creada exitosamente");
            // Llama al método createTransaction del controlador
            ResponseEntity<String> response = transactionController.createTransaction(validTransactionJson);
            // Verifica que se llamó al método transactionService.createTransaction con el JSON especificado
            verify(transactionServices, times(1)).createTransaction(validTransactionJson);
            // Verifica el contenido de la respuesta HTTP
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Transacción creada exitosamente", response.getBody());
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se actualizo la tarjeta", e);
        }

    }

   /* @Test
    public void testConsultarTransactionFavorable() {
        try {
            // Define un valor de ejemplo para transactionId
            String transactionId = "123";

            // Simula el comportamiento de transactionService.consultarTransaction
            TransactionEntity transactionEntity = new TransactionEntity();
            transactionEntity.setIdTransaction(1);
            transactionEntity.setState("COMPLETADA");
            when(transactionServices.consultarTransaction(transactionId)).thenReturn(transactionEntity);

            // Llama al método consultarTransaction del controlador
            ResponseEntity<?> response = transactionController.consultarTransaction(transactionId);

            // Verifica que se llamó al método transactionService.consultarTransaction con el transactionId especificado
            verify(transactionServices, times(1)).consultarTransaction(transactionId);

            // Verifica el contenido de la respuesta HTTP
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(transactionEntity, response.getBody());
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "HUBO UN ERROR AL MOMENTO DE HACER LA CONSULTAR EL REGISTRO EN LA BASE DE DATOS", e);
        }


    }


    */

   /* @Test
    public void testAnularTransactionFavorable() {
    try {
        // Define un valor de ejemplo para la solicitud de anulación
        String anulation = "{\"transactionId\":\"123\", \"reason\":\"Anulación de prueba\"}";

        // Simula el comportamiento de transactionService.anulacionTransaction
        String estado = "ANULADA";
        when(transactionServices.anulacionTransaction(anulation)).thenReturn(estado);

        // Llama al método anularTransaction del controlador
        ResponseEntity<String> response = transactionController.anularTransaction(anulation);

        // Verifica que se llamó al método transactionService.anulacionTransaction con la solicitud de anulación especificada
        verify(transactionServices, times(1)).anulacionTransaction(anulation);

        // Verifica el contenido de la respuesta HTTP
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(estado, response.getBody());
    }catch (Exception e){
        e.printStackTrace();
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hubo un error al momento de hacer la anulacion", e);
    }
    }
*/

}