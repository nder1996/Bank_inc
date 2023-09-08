package Nexos.Software.Nexos.Software.controllers;

import Nexos.Software.Nexos.Software.entitys.CardEntity;
import Nexos.Software.Nexos.Software.repositorys.CardRepository;
import Nexos.Software.Nexos.Software.services.CardService;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * El archivo CardControllerTest es un archivo de prueba unitaria que prueba el controlador de Card de la aplicación.
 * El controlador de Card es una clase que proporciona una API REST para crear, leer, actualizar y eliminar tarjetas.
 */

class CardControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private CardController cardController;

    private CardEntity cardEntity = new CardEntity();

    @Mock
    private CardService cardService;


    @Autowired
    private CardRepository cardRepository;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    /**
     *este metodo testea el metodo de crear una tarjeta en un escenario favorable
     */
    @Test
    public void testCreateCardSuccess() {
        try {
            CardService cardService = mock(CardService.class);
            CardEntity tarjetaSimulada = new CardEntity("1234567890123456", "12/2026", 0.0F, "John Doe", "IN");
            when(cardService.createCard(eq("123456"))).thenAnswer(invocation -> {
                return Optional.of(tarjetaSimulada);
            });
            CardController cardController = new CardController(cardService);
            ResponseEntity<?> response = cardController.createCard("123456");
            verify(cardService).createCard("123456");
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(tarjetaSimulada, response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            // Manejar cualquier excepción que ocurra durante la ejecución de la prueba
            fail("La prueba falló debido a una excepción: " + e.getMessage());
        }

    }






    /**
     *este metodo testea el metodo activa una tarjeta ya registrada en un escenario favorable
     */
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


    /**
     *este metodo testea el metodo bloqueo la tarjeta  en un escenario favorable
     */
    @Test
    public void testDeleteCardFavorable() {
        try {
            String cardId = "123456";
            when(cardService.bloqueoCard(cardId)).thenReturn("TARJETA BLOQUEADA");
            ResponseEntity<String> response = cardController.deleteCard(cardId);
            verify(cardService, times(1)).bloqueoCard(cardId);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("TARJETA BLOQUEADA", response.getBody());
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SE BLOQUEÓ LA TARJETA", e);
        }

    }



    /**
     *este metodo testea la recarga la tarjeta en un escenario favorable
     */
    @Test
    public void testRecargarBalanceFavorable() {
        try{
            String cardBalance = "{\"idCard\": \"1234567890123456\", \"balance\": 100}";
            when(cardService.recargarBalance(cardBalance)).thenReturn("SU TARJETA SE HA RECARGADO");
            ResponseEntity<String> response = cardController.recargarBalance(cardBalance);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("SU TARJETA SE HA RECARGADO", response.getBody());
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO SE BLOQUEÓ LA TARJETA", e);
        }
    }


    /**
     *este metodo testea la consulta del saldo de la tarjeta en un escenario favorable
     */
   /* @Test
    public void testConsultarBalanceSuccess() {
        String cardId = "1234567890123456";
        CardEntity cardEntity = new CardEntity("1234567890123456", "12/2026", 100.0F, "John Doe", "IN");
        when(cardService.createCard(eq("123456"))).thenAnswer(invocation -> {
            return Optional.of(cardEntity);
        });
        ResponseEntity<?> response = cardController.consultarBalance(cardId);
        verify(cardService).consultarBalance(cardId);y
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof CardEntity);
        CardEntity entidadRespuesta = (CardEntity) response.getBody();
        assertEquals(cardEntity, entidadRespuesta);
    }
}*/


}