package Nexos.Software.Nexos.Software.controllers;

import Nexos.Software.Nexos.Software.entitys.Card_Entity;
import Nexos.Software.Nexos.Software.repositorys.Card_Repository;
import Nexos.Software.Nexos.Software.services.Card_Service;
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




class Card_ControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private Card_Controller cardController;
    
    private Card_Entity cardEntity = new Card_Entity();

    @Mock
    private Card_Service cardService;


    @Autowired
    private  Card_Repository cardRepository;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateCard() {
        String productId = "123456";
        Card_Entity cardEntity = new Card_Entity();
        cardEntity.setIdCard("CARD123");
        try {
            when(cardService.createCard(productId)).thenReturn(cardEntity);
            ResponseEntity<?> response = cardController.createCard(productId);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(cardEntity, response.getBody());
            verify(cardService, times(1)).createCard(productId);
        } catch (Exception e) {
            fail("Se produjo una excepción inesperada: " + e.getMessage());
        }
    }



    @Test
    public void testCardActive() {
        try{
            String validCardJson = "{\"idCard\":\"1234567890123456\"}";
            String expectedStatus = "ACTIVATED";
            when(cardService.activeCard(validCardJson)).thenReturn(expectedStatus);
            ResponseEntity<String> response = cardController.cardActive(validCardJson);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedStatus, response.getBody());
            verify(cardService, times(1)).activeCard(validCardJson); // Verifica que se llamó al servicio
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SE ACTUALIZÓ LA TARJETA", e);
        }
    }


    @Test
    public void testDeleteCardFavorable() {
        try {
            // Configura el comportamiento del mock cardService
            String cardId = "123456";
            when(cardService.bloqueoCard(cardId)).thenReturn("TARJETA BLOQUEADA");

            // Llama al método deleteCard del controlador
            ResponseEntity<String> response = cardController.deleteCard(cardId);

            // Verifica que se llamó al método cardService.bloqueoCard con el cardId
            verify(cardService, times(1)).bloqueoCard(cardId);

            // Verifica que la respuesta HTTP sea OK (200) y que el estado sea "TARJETA BLOQUEADA"
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("TARJETA BLOQUEADA", response.getBody());
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SE BLOQUEÓ LA TARJETA", e);
        }

    }




    @Test
    public void testRecargarBalanceFavorable() {
        try{
            // Configura el comportamiento del mock cardService
            String cardBalance = "{\"idCard\": \"123456\", \"balance\": 100}";
            when(cardService.recargarBalance(cardBalance)).thenReturn("RECARGA EXITOSA");

            // Llama al método recargarBalance del controlador
            ResponseEntity<String> response = cardController.recargarBalance(cardBalance);

            // Verifica que se llamó al método cardService.recargarBalance con el cardBalance especificado
            verify(cardService, times(1)).recargarBalance(cardBalance);

            // Verifica que la respuesta HTTP sea OK (200) y que el estado sea "RECARGA EXITOSA"
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("RECARGA EXITOSA", response.getBody());
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SE BLOQUEÓ LA TARJETA", e);
        }
    }


    @Test
    public void testConsultarBalanceFavorable() {
        try {
            // Configura el comportamiento del mock cardService
            String validCardId = "1234567890123456";
            Card_Entity validCard = new Card_Entity();
            validCard.setIdCard("1234567890123456");
            validCard.setState("AC"); // Tarjeta activa
            validCard.setBalance(100.0f);
            // Crea un arreglo de objetos con la información válida
            Object[] consulta = new Object[] { validCard.getBalance(), validCard.getState(), validCard.getIdCard() };
            // Simula una respuesta válida del servicio
            when(cardService.consultarBalance(validCardId)).thenReturn(consulta);
            // Llama al método consultarBalance del controlador
            ResponseEntity<?> response = cardController.consultarBalance(validCardId);
            // Verifica que se llamó al método cardService.consultarBalance con el cardId especificado
            verify(cardService, times(1)).consultarBalance(validCardId);
            // Verifica el contenido de la respuesta HTTP
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SE ENCONTRÓ EL REGISTRO", e);
        }

    }













@Test
    void consultarBalance() {
    }
}