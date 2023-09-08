package Nexos.Software.Nexos.Software.services;

import Nexos.Software.Nexos.Software.entitys.CardEntity;
import Nexos.Software.Nexos.Software.repositorys.CardRepository;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.function.Try;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class CardServiceTest {


    @Autowired
    private CardService cardService; // Se inyecta automáticamente el servicio real

    @MockBean
    private CardRepository cardRepository; // Se utiliza una simulación del repositorio


    /**
     * este metodo de test createCard  sirve para hacer una prueba que el metodo del services funciona correctamente
     */
    @Test
    public void testCreateCardXValid() {
        try {
            String numProducto = "123456";
            CardEntity cardEntity = new CardEntity();
            cardEntity.setIdCard("7894561230123456");
            cardEntity.setState("IN");
            cardEntity.setBalance(0.0F);
            Mockito.doReturn(cardEntity).when(cardRepository).saveAndFlush(any(CardEntity.class));
            Optional<?> respuesta = Optional.empty();
            respuesta = cardService.CreateCard(numProducto);
            if (respuesta.isPresent()) {
                Object valor = respuesta.get();
                CardEntity entidad = (CardEntity) valor;
                assertEquals(cardEntity, entidad, "Las instancias de CardEntity deben ser iguales");
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("HUBO UN ERROR AL CREAR LA TARJETA DE CREDITO : "+e.getMessage());
        }
    }

    /**
     *este metodo de test createCard   sirve para simular el escenario que el usuario ingrese un input que contiene una letra
     */
    @Test
    public void testCreateCardXInvalidInputLetra() {
        String numProducto = "1234a6";
        Optional<?> respuesta = Optional.empty();
        respuesta = cardService.CreateCard(numProducto);
        try {
            if (respuesta.isPresent()) {
                Object valor = respuesta.get();
                String cadena = (String) valor;
                assertEquals(cadena, "NO INGRESASTE DATO VáLIDO , SOLO NÚMEROS");
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("HUBO UN ERROR AL CREAR LA TARJETA DE CREDITO : "+e.getMessage());
        }
    }

    /**
     * este metodo de test createCard sirve para simular el escenario que el usuario ingrese un input que contiene uun espacio en blanco
     */
    @Test
    public void testCreateCardXNoIdProvided() {
        String numProducto = "";
        Optional<?> respuesta = Optional.empty();
        respuesta = cardService.CreateCard(numProducto);
        try {
            if (respuesta.isPresent()) {
                Object valor = respuesta.get();
                String cadena = (String) valor;
                assertEquals(cadena, "NO INGRESASTE NINGÚN DATO");
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("HUBO UN ERROR AL CREAR LA TARJETA : "+e.getMessage());
        }
    }


    /**
     * este metodo de test createCard sirve para simular el escenario que el usuario ingrese un input que solo 5 digitos
     */
    @Test
    public void testCreateCardXIdNotSixDigits() {
        String numProducto = "12345";
        Optional<?> respuesta = Optional.empty();
        respuesta = cardService.CreateCard(numProducto);
        try {
            if (respuesta.isPresent()) {
                Object valor = respuesta.get();
                String cadena = (String) valor;
                assertEquals(cadena, "LA CADENA NO TIENE 6 CARACTERES");
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("A CADENA NO TIENE 6 CARACTERES : "+e.getMessage());
        }
    }


    /**
     * este metodo de test createCard sirve para simular el escenario que el usuario ingrese un input null
     */
    @Test
    public void testCreateCardXNullId() {
        String numProducto = null;
        Optional<?> respuesta = Optional.empty();
        respuesta = cardService.CreateCard(numProducto);
        try {
            if (respuesta.isPresent()) {
                Object valor = respuesta.get();
                String cadena = (String) valor;
                assertEquals(cadena, "LA CADENA ES NULA");
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA CADENA ES NULA : "+e.getMessage());
        }
    }


    /**
     * este metodo de activar tarjeta verifica que en un escenario favorable se puede activar la tarjeta
     */
    @Test
    public void testActiveCardXValid() {
        try {
            cardService = new CardService();
            CardRepository cardRepository = mock(CardRepository.class);
            CardService cardService = new CardService(cardRepository);
            CardEntity existingCard = new CardEntity();
            existingCard.setIdCard("1234567890123456");
            existingCard.setState("IN");
            existingCard.setBalance(0.0F);
            existingCard.setExpirationDate("01/2026");
            when(cardRepository.buscardCardXId(anyString())).thenReturn(existingCard);
            String json = "{\"idCard\": \"1234567890123456\"}";
            String result = cardService.activeCard(json);
            assertEquals(result, "TARJETA ACTIVADA");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO ACTIVAR : "+e.getMessage());
        }
    }


    /**
     *   * este metodo de activar tarjeta verifica que en un escenario donde no existe json
     */
    @Test
    public void testActiveCard_NoJson() {
        try {
            String result = cardService.activeCard(null);
            assertEquals("NO INGRESASTE UN JSON O NO COLOCASTE BIEN EL KEY DEL JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON", result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO ACTIVAR : "+e.getMessage());
        }
    }


    /**
     *   * este metodo de activar tarjeta verifica que en un escenario donde ya esta activada la tarjeta
     *
     */
    @Test
    public void testActiveCard_CardAlreadyActive() {
        try {
            CardRepository cardRepository = mock(CardRepository.class);
            CardService cardService = new CardService(cardRepository);
            CardEntity existingCard = new CardEntity();
            existingCard.setIdCard("1234567890123456");
            existingCard.setState("AC");
            existingCard.setBalance(0.0F);
            existingCard.setExpirationDate("01/2026");
            when(cardRepository.buscardCardXId(anyString())).thenReturn(existingCard);
            // Arrange
            String json = "{\"idCard\": \"1234567890123456\"}";
            String result = cardService.activeCard(json);
            assertEquals("LA TARJETA YA SE ENCUENTRA ACTIVADA", result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO ACTIVAR : "+e.getMessage());
        }
    }


    /**
     *   * este metodo de activar tarjeta verifica que en un escenario donde no halla id
     */
    @Test
    public void testActiveCard_IdEmpty() {
        try {
            String json = "{\"idCard\": \"\"}";
            String result = cardService.activeCard(json);
            assertEquals("EL KEY DE idCard NO CUMPLE CON EL FORMATO , VUELVE A INGRESARLO NUEVAMENTE", result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO ACTIVAR : "+e.getMessage());
        }
    }

    /**
     * este metodo de activar tarjeta verifica que en un escenario donde no exista la tarjeta
     */
    @Test
    public void testActiveCard_IdNotExist() {
        try {
            String json = "{\"idCard\": \"1234567890123456\"}";
            String result = cardService.activeCard(json);
            assertEquals("NO EXISTE EL ID DE LA TARJETA EN LA BASE DE DATOS", result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO ACTIVAR : "+e.getMessage());
        }
    }

    /**
     * este metodo de activar tarjeta verifica que en un escenario donde la tarjeta no tenga el formato correspondiente
     */
    @Test
    public void testActiveCard_InvalidId() {
        try {
            String json = "{\"idCard\": \"123456789012345P\"}";
            String result = cardService.activeCard(json);
            assertEquals("EL KEY DE idCard NO CUMPLE CON EL FORMATO , VUELVE A INGRESARLO NUEVAMENTE", result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO ACTIVAR : "+e.getMessage());
        }
    }



    /**
     * este metodo de bloquear tarjeta verifica que en un escenario favorable se puede bloquear la tarjeta
     */
    @Test
    public void testBlockCardXValid() {
        try {
            cardService = new CardService();
            CardRepository cardRepository = mock(CardRepository.class);
            CardService cardService = new CardService(cardRepository);
            CardEntity existingCard = new CardEntity();
            existingCard.setIdCard("1234567890123456");
            existingCard.setState("IN");
            existingCard.setBalance(0.0F);
            existingCard.setExpirationDate("01/2026");
            when(cardRepository.buscardCardXId(anyString())).thenReturn(existingCard);
            String idCard = "1234567890123456";
            String result = cardService.bloqueoCard(idCard);
            assertEquals(result, "TARJETA BLOQUEADA");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO BLOQUEAR : "+e.getMessage());
        }
    }


    /**
     * este metodo de activar tarjeta verifica que en un escenario donde el id de la tarjeta no tenga formato valido
     */
    @Test
    public void testBloqueoCardXInvalidId() {
        try {
            String idCard = "1234a56789";
            String result = cardService.bloqueoCard(idCard);
            assertEquals("EL ID QUE INGRESASTE NO CUMPLE CON LAS CONDICCIONES , VUELVE INGRESARLO", result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO BLOQUEAR : "+e.getMessage());
        }

    }

    /**
     * este metodo de activar tarjeta verifica que en un escenario donde el id de la tarjeta no tenga formato vacio
     */
    @Test
    public void testBloqueoCardXIdEmpty() {
        try {
            String idCard = "";
            String result = cardService.bloqueoCard(idCard);
            assertEquals("EL ID QUE INGRESASTE NO CUMPLE CON LAS CONDICCIONES , VUELVE INGRESARLO", result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO BLOQUEAR : "+e.getMessage());
        }

    }


    /**
     * este metodo de activar tarjeta verifica que en un escenario donde el id no iene los 16 digitos
     */
    @Test
    public void testBloqueoCardXInvalidLengthId() {
        try {
            String idCard = "12345";
            String result = cardService.bloqueoCard(idCard);
            assertEquals("EL ID QUE INGRESASTE NO CUMPLE CON LAS CONDICCIONES , VUELVE INGRESARLO", result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO BLOQUEAR : "+e.getMessage());
        }
    }


    /**
     * este metodo de activar tarjeta verifica que en un escenario donde el id no iene los 16 digitos
     */
    @Test
    public void testBloqueoCardXIdNotExist() {
        try {
            String idCard = "1234567890123056";
            String result = cardService.bloqueoCard(idCard);
            assertEquals("NO EXISTE EL ID DE LA TARJETA EN LA BASE DE DATOS", result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO BLOQUEAR : "+e.getMessage());
        }
    }

    /**
     * este metodo de activar tarjeta verifica que en un escenario donde el la tarjeta ya esta bloqueada
     */
    @Test
    public void testBloqueoCard_CardAlreadyBlocked() {
        try {
            cardService = new CardService();
            CardRepository cardRepository = mock(CardRepository.class);
            CardService cardService = new CardService(cardRepository);
            CardEntity existingCard = new CardEntity();
            existingCard.setIdCard("1234567890123456");
            existingCard.setState("BL");
            existingCard.setBalance(0.0F);
            existingCard.setExpirationDate("01/2026");
            when(cardRepository.buscardCardXId(anyString())).thenReturn(existingCard);
            String idCard = "1234567890123456";
            String result = cardService.bloqueoCard(idCard);
            assertEquals(result, "YA SE ENCUENTRA BLOQUEADA ESTA TARJETA");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO BLOQUEAR : "+e.getMessage());
        }

    }


    /**
     * este metodo de activar tarjeta verifica que en un escenario donde el id de la tarjeta es null
     */
    @Test
    public void testBloqueoCard_NoId() {
        try {
            String result = cardService.bloqueoCard(null);
            assertEquals("EL ID QUE INGRESASTE NO CUMPLE CON LAS CONDICCIONES , VUELVE INGRESARLO", result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO BLOQUEAR : "+e.getMessage());
        }

    }


    /**
     * este metodo de recargar balance esta en un escenario favorable se verifica si se recarga la tarjeta
     */
    @Test
    public void testRecargarBalanceXValid() {
        try {
            cardService = new CardService();
            CardRepository cardRepository = mock(CardRepository.class);
            CardService cardService = new CardService(cardRepository);
            CardEntity existingCard = new CardEntity();
            existingCard.setIdCard("1234567890123456");
            existingCard.setState("AC");
            existingCard.setBalance(0.0F);
            existingCard.setExpirationDate("01/2026");
            when(cardRepository.buscardCardXId(anyString())).thenReturn(existingCard);
            String validJson = "{\"idCard\":\"1234567890123456\", \"balance\":\"50.0\"}";
            String result = cardService.recargarBalance(validJson);
            assertEquals(result, "SU TARJETA SE HA RECARGADO");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO RECARGAR : "+e.getMessage());
        }
    }


    /**
     * este metodo de recargar balance esta en un escenario se verifica si el json es incorrecto
     */
    @Test
    public void testRecargarBalance_InvalidKey() {
        try {
            cardService = new CardService();
            CardRepository cardRepository = mock(CardRepository.class);
            CardService cardService = new CardService(cardRepository);
            CardEntity existingCard = new CardEntity();
            existingCard.setIdCard("1234567890123456");
            existingCard.setState("AC");
            existingCard.setBalance(0.0F);
            existingCard.setExpirationDate("01/2026");
            when(cardRepository.buscardCardXId(anyString())).thenReturn(existingCard);
            String json = "{\"qwe\": \"1234567890123456\", \"nombre\": \"50.0\"}";
            String result = cardService.recargarBalance(json);
            assertEquals("NO INGRESASTE UN JSON O NO COLOCASTE BIEN EL KEY DEL JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON", result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO RECARGAR : "+e.getMessage());
        }
    }

    /**
     * este metodo de recargar balance esta en un escenario se verifica si id no existe de la tarjeta
     */
    @Test
    public void testRecargarBalance_IdNotExist() {
        try {
            String json = "{\"idCard\": \"1234567890123456\", \"balance\": \"50.0\"}";
            String result = cardService.recargarBalance(json);
            assertEquals("TARJETA NO EXISTE EN LA BASE DE DATOS", result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO RECARGAR : "+e.getMessage());
        }
    }


    /**
     * este metodo de recargar balance esta en un escenario se verifica si id esta vacio
     */
    @Test
    public void testRecargarBalance_IdEmpty() {
        try {
            String json = "{\"idCard\": \"\", \"balance\": \"50.0\"}";
            String result = cardService.recargarBalance(json);
            assertEquals("EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, DEBE INGRESAR SOLO 16 DÍGITOS PARA EL ID DE LA TARJETA", result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO RECARGAR : "+e.getMessage());
        }
    }


    /**
     * este metodo de recargar balance esta en un escenario se verifica si id es null
     */
    @Test
    public void testRecargarBalance_NoId() {
        try {
            String result = cardService.recargarBalance(null);
            assertEquals("NO INGRESASTE UN JSON O NO COLOCASTE BIEN EL KEY DEL JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON", result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE PUDO RECARGAR : "+e.getMessage());
        }
    }


    /**
     * este metodo de consulta el balance esta en un escenario favorable consulta su saldo
     */
    @Test
    public void testConsultarBalanceValid() {
        try {
            cardService = new CardService();
            CardRepository cardRepository = mock(CardRepository.class);
            CardService cardService = new CardService(cardRepository);
            CardEntity existingCard = new CardEntity();
            existingCard.setIdCard("1234567890123456");
            existingCard.setState("AC");
            existingCard.setBalance(0.0F);
            existingCard.setExpirationDate("01/2026");
            when(cardRepository.buscardCardXId(anyString())).thenReturn(existingCard);
            Optional<?> respuesta = Optional.empty();
            respuesta = cardService.consultarBalance("1234567890123456");
            if (respuesta.isPresent()) {
                Object valor = respuesta.get();
                CardEntity entidad = (CardEntity) valor;
                assertEquals(existingCard, entidad);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE ENCONTRO : "+e.getMessage());
        }
    }


    /**
     * este metodo de consulta el balance esta en un escenario desfavorable
     El id del producto no es valido , no es tipo numerico , contiene al menos una
    letra.
     */
    @Test
    public void testConsultarBalance_InvalidId() {
        try {
            Optional<?> respuesta = Optional.empty();
            respuesta = cardService.consultarBalance("123456789012345E");
            if (respuesta.isPresent()) {
                Object valor = respuesta.get();
                String cadena = (String) valor;
                assertEquals("EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, DEBE INGRESAR SOLO 16 DÍGITOS PARA EL ID DE LA TARJETA", cadena);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE ENCONTRO : "+e.getMessage());
        }

    }

    /**
     * este metodo de consulta el balance esta en un escenario desfavorable
     El id es numerico pero no tiene 6 digitos
     */
    @Test
    public void testConsultarBalance_InvalidLengthId() {
        try {
            String idCard = "12345";
            Optional<?> result = cardService.consultarBalance(idCard);
            assertTrue(result.isPresent());
            String estadoConsulta = (String) result.get();
            assertEquals("EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, DEBE INGRESAR SOLO 16 DÍGITOS PARA EL ID DE LA TARJETA", estadoConsulta);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE ENCONTRO : "+e.getMessage());
        }
    }

    /**
     * este metodo de consulta el balance esta en un escenario desfavorable
     El id ES NULLO
     */
    @Test
    public void testConsultarBalance_NoId() {
        try {
            Optional<?> result = cardService.consultarBalance(null);
            assertTrue(result.isPresent());
            String estadoConsulta = (String) result.get();
            assertEquals("EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, DEBE INGRESAR SOLO 16 DÍGITOS PARA EL ID DE LA TARJETA", estadoConsulta);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("LA TARJETA NO SE ENCONTRO : "+e.getMessage());
        }
    }



    @AfterEach
    void tearDown() {
        cardRepository.deleteAll();
    }

}
