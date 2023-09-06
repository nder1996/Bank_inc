package Nexos.Software.Nexos.Software.services;

import Nexos.Software.Nexos.Software.entitys.Card_Entity;
import Nexos.Software.Nexos.Software.repositorys.Card_Repository;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class Card_ServiceTest {


    @Autowired
    private Card_Service cardService; // Se inyecta automáticamente el servicio real

    @MockBean
    private Card_Repository cardRepository; // Se utiliza una simulación del repositorio



    @Test
    public void CreateCard() {
        try {
            Card_Entity cardEntity = new Card_Entity();
            cardEntity.setIdCard("123456");
            cardEntity.setState("IN");
            cardEntity.setBalance(0.0F);
            Mockito.doReturn(cardEntity).when(cardRepository).saveAndFlush(any(Card_Entity.class));
            String numProducto = "123456";
            Card_Entity result = cardService.createCard(numProducto);
            assertNotNull(result);
            assertEquals("IN", result.getState());
            assertEquals(0.0F, result.getBalance());
            Mockito.verify(cardRepository, times(1)).saveAndFlush(any(Card_Entity.class));
            Mockito.verifyNoMoreInteractions(cardRepository);
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }


    @Test
    public void createCardXIdInvalido() {
        try {
            String numProducto = "12345";
            Card_Entity result = cardService.createCard(numProducto);
            if (result == null) {
                System.err.println("Ingrese número correcto - Solo 6 dígitos");
            } else {
                // Si el resultado no es nulo, verifica las interacciones del repositorio aquí si es necesario.
                Mockito.verifyNoMoreInteractions(cardRepository);
            }
        } catch (Exception e) {
            System.err.println("Hubo un error al momento de ejecutar la aplicación: " + e.getClass().getName());
        }
    }


    @Test
    public void testActiveCard() {
        try {
            cardService = new Card_Service();
            Card_Repository cardRepository = mock(Card_Repository.class);
            Card_Service cardService = new Card_Service(cardRepository);
            Card_Entity existingCard = new Card_Entity();
            existingCard.setIdCard("1234567890123456");
            existingCard.setState("IN"); // Tarjeta inactiva
            when(cardRepository.buscardCardXId(anyString())).thenReturn(existingCard);
            // Caso 1: Activar una tarjeta válida e inactiva
            String validCardJson = "{\"idCard\":\"1234567890123456\"}";
            String result1 = cardService.activeCard(validCardJson);
            assertEquals("TARJETA ACTIVADA", result1);
            // Caso 2: Intentar activar una tarjeta que ya está activa
            existingCard.setState("AC"); // Tarjeta activa
            when(cardRepository.buscardCardXId(anyString())).thenReturn(existingCard);
            String result2 = cardService.activeCard(validCardJson);
            assertEquals("LA TARJETA YA SE ENCUENTRA ACTIVADA", result2);
            // Caso 3: Intentar activar una tarjeta que no existe
            when(cardRepository.buscardCardXId(anyString())).thenReturn(null);
            String result3 = cardService.activeCard(validCardJson);
            assertEquals("NO EXISTE EL ID DE LA TARJETA EN LA BASE DE DATOS", result3);
            // Caso 4: Intentar activar una tarjeta con un formato de JSON inválido
            String invalidCardJson = "invalid_json_format";
            String result4 = cardService.activeCard(invalidCardJson);
            assertEquals("NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON", result4);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            System.out.println("FORMATO JSON INVALIDO : "+e.getMessage());
        }catch(Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }

    @Test
    public void testActiveCardXCasosDesfavoravle() {
        try {
            // Configuración inicial
            cardService = new Card_Service();
            Card_Repository cardRepository = mock(Card_Repository.class);
            Card_Service cardService = new Card_Service(cardRepository);

            // Caso 1: JSON inválido
            String invalidJson = "invalid_json_format";
            String result1 = cardService.activeCard(invalidJson);
            assertEquals("NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON", result1);

            // Caso 2: ID de tarjeta no válido
            String invalidCardId = "{\"idCard\":\"12345\"}";
            String result2 = cardService.activeCard(invalidCardId);
            assertEquals("EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, SOLO TIPO NUMÉRICO O NO INGRESO LOS 16 DÍGITOS DE LA TARJETA", result2);

            // Caso 3: Tarjeta no existe en la base de datos
            String validCardJson = "{\"idCard\":\"1234567890123456\"}";
            when(cardRepository.buscardCardXId(anyString())).thenReturn(null);
            String result3 = cardService.activeCard(validCardJson);
            assertEquals("NO EXISTE EL ID DE LA TARJETA EN LA BASE DE DATOS", result3);

            // Caso 4: Tarjeta ya está activada
            Card_Entity existingCard = new Card_Entity();
            existingCard.setIdCard("1234567890123456");
            existingCard.setState("AC"); // Tarjeta activa
            when(cardRepository.buscardCardXId(anyString())).thenReturn(existingCard);
            String result4 = cardService.activeCard(validCardJson);
            assertEquals("LA TARJETA YA SE ENCUENTRA ACTIVADA", result4);

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            System.out.println("FORMATO JSON INVALIDO : "+e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Se ha producido una NullPointerException: " + e.getMessage());
        } catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicación: " + e.getClass().getName());
        }

    }




    @Test
    public void testBloqueoCard() {
        String cambio;
        try {
            // Caso 1: Tarjeta válida e inactiva
            Card_Entity validCard = new Card_Entity();
            validCard.setIdCard("1234567890123456");
            validCard.setState("IN"); // Tarjeta inactiva
            when(cardRepository.buscardCardXId("1234567890123456")).thenReturn(validCard);
            String validCardJson = "{\"idCard\":\"1234567890123456\"}";
            String result = cardService.bloqueoCard(validCardJson);

            assertEquals("TARJETA BLOQUEADA", result);

            // Caso 2: Tarjeta válida pero ya bloqueada
            Card_Entity blockedCard = new Card_Entity();
            blockedCard.setIdCard("1234567890123457");
            blockedCard.setState("BL");
            when(cardRepository.buscardCardXId("1234567890123457")).thenReturn(blockedCard);
            String blockedCardJson = "{\"idCard\":\"1234567890123457\"}";
            cambio = cardService.bloqueoCard(blockedCardJson);
            assertEquals("YA SE ENCUENTRA BLOQUEADA ESTA TARJETA", cambio);

            // Caso 3: Tarjeta con formato inválido
            String invalidCardJson = "invalid_json_format";
            cambio = cardService.bloqueoCard(invalidCardJson);
            assertEquals("NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON", cambio);

            // Caso 4: Tarjeta inexistente en la base de datos
            when(cardRepository.buscardCardXId("9999999999999999")).thenReturn(null);
            String nonExistentCardJson = "{\"idCard\":\"9999999999999999\"}";
            cambio = cardService.bloqueoCard(nonExistentCardJson);
            assertEquals("NO EXISTE EL ID DE LA TARJETA EN LA BASE DE DATOS", cambio);



        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            cambio = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, SOLO TIPO NUMÉRICO O NO INGRESO LOS 16 DÍGITOS DE LA TARJETA";
            System.out.println("FORMATO JSON INVALIDO : "+e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            cambio = "HUBO UN ERROR AL BLOQUEAR LA TARJETA DE CRÉDITO";
            System.out.println("Hubo un error al bloquear la tarjeta de credito : "+e.getMessage());
        }
    }


    @Test
    public void bloqueoCardXCasosXDesfavoraBle() {
        try {
            // Caso 1: Bloqueo exitoso de una tarjeta activa
            String validJson1 = "{\"idCard\":\"1234567890123456\"}";
            Card_Entity activeCard = new Card_Entity();
            activeCard.setState("AC"); // Tarjeta activa
            Card_Repository cardRepository1 = mock(Card_Repository.class);
            Card_Service cardService1 = new Card_Service(cardRepository1);
            when(cardRepository1.buscardCardXId(anyString())).thenReturn(activeCard);
            String result1 = cardService1.bloqueoCard(validJson1);
            assertEquals("TARJETA BLOQUEADA", result1);
            verify(cardRepository1, times(1)).buscardCardXId(anyString());
            verify(cardRepository1, times(1)).saveAndFlush(any(Card_Entity.class));
            Mockito.verifyNoMoreInteractions(cardRepository1);

            // Caso 2: Intento de bloqueo en una tarjeta que ya está bloqueada
            String validJson2 = "{\"idCard\":\"1234567890123456\"}";
            Card_Entity blockedCard = new Card_Entity();
            blockedCard.setState("BL"); // Tarjeta bloqueada
            Card_Repository cardRepository2 = mock(Card_Repository.class);
            Card_Service cardService2 = new Card_Service(cardRepository2);
            when(cardRepository2.buscardCardXId(anyString())).thenReturn(blockedCard);
            String result2 = cardService2.bloqueoCard(validJson2);
            assertEquals("YA SE ENCUENTRA BLOQUEADA ESTA TARJETA", result2);
            verify(cardRepository2, times(1)).buscardCardXId(anyString());
            Mockito.verifyNoMoreInteractions(cardRepository2);

            // Caso 3: Intento de bloqueo en una tarjeta que no existe
            String validJson3 = "{\"idCard\":\"1234567890123456\"}";
            Card_Repository cardRepository3 = mock(Card_Repository.class);
            Card_Service cardService3 = new Card_Service(cardRepository3);
            when(cardRepository3.buscardCardXId(anyString())).thenReturn(null);
            String result3 = cardService3.bloqueoCard(validJson3);
            assertEquals("NO EXISTE EL ID DE LA TARJETA EN LA BASE DE DATOS", result3);
            verify(cardRepository3, times(1)).buscardCardXId(anyString());
            Mockito.verifyNoMoreInteractions(cardRepository3);

            // Caso 4: Intento de bloqueo con JSON inválido
            String invalidJson = "invalid_json_format";
            Card_Repository cardRepository4 = mock(Card_Repository.class);
            Card_Service cardService4 = new Card_Service(cardRepository4);
            String result4 = cardService4.bloqueoCard(invalidJson);
            assertEquals("NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON", result4);
            Mockito.verifyNoInteractions(cardRepository4);
        } catch (Exception e) {
            System.err.println("Hubo un error al ejecutar la prueba: " + e.getClass().getName());
        }
    }



    @Test
    public void testRecargarBalance() {
        try {

            // Caso 1: JSON válido y datos válidos
            Card_Entity  validCard = new Card_Entity();
            validCard.setIdCard("1234567890123456");
            validCard.setState("AC"); // Tarjeta activa
            validCard.setBalance(100.0f);
            when(cardRepository.buscardCardXId("1234567890123456")).thenReturn(validCard);

            String validJson = "{\"idCard\":\"1234567890123456\", \"balance\":\"50.0\"}";
            String result = cardService.recargarBalance(validJson);
            assertEquals("SU TARJETA SE HA RECARGADO", result);
            assertEquals(150.0f, validCard.getBalance()); // Verifica que el saldo se haya actualizado correctamente

            // Caso 2: Tarjeta bloqueada
            Card_Entity blockedCard = new Card_Entity();
            blockedCard.setIdCard("1234567890123457");
            blockedCard.setState("BL"); // Tarjeta bloqueada
            when(cardRepository.buscardCardXId("1234567890123457")).thenReturn(blockedCard);

            String blockedCardJson = "{\"idCard\":\"1234567890123457\", \"balance\":\"50.0\"}";
            String resultBlocked = cardService.recargarBalance(blockedCardJson);
            assertEquals("TARJETA BLOQUEADA O INACTIVA", resultBlocked);

            // Caso 3: JSON inválido
            String invalidJson = "invalid_json_format";
            String resultInvalidJson = cardService.recargarBalance(invalidJson);
            assertEquals("NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON", resultInvalidJson);

            // Caso 4: Datos inválidos
            String invalidDataJson = "{\"idCard\":\"1234567890123458\", \"balance\":\"invalid_balance\"}";
            String resultInvalidData = cardService.recargarBalance(invalidDataJson);
            assertEquals("EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, DEBE INGRESAR SOLO 16 DÍGITOS PARA EL ID DE LA TARJETA", resultInvalidData);

            // Caso 5: Tarjeta no existe en la base de datos
            when(cardRepository.buscardCardXId("9999999999999999")).thenReturn(null);

            String nonExistentCardJson = "{\"idCard\":\"9999999999999999\", \"balance\":\"50.0\"}";
            String resultNonExistentCard = cardService.recargarBalance(nonExistentCardJson);
            assertEquals("TARJETA NO EXISTE EN LA BASE DE DATOS", resultNonExistentCard);

        }catch (JsonSyntaxException e) {
            e.printStackTrace();
            System.out.println("FORMATO JSON INVALIDO : "+e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Hubo un error al momento de recargar su tarjeta de credito : "+e.getMessage());
        }

    }

    @Test
    public void testRecargarBalanceEscenarioDesfavorable() {
        try {
            // Caso 1: JSON no válido
            String invalidJson = "invalid_json_string";
            Card_Repository cardRepository1 = mock(Card_Repository.class);
            Card_Service cardService1 = new Card_Service(cardRepository1);

            Object[] result1 = new Object[]{cardService1.recargarBalance(invalidJson)};
            Object[] expectedResult1 = {"NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON", "", ""};
            assertEquals(expectedResult1[0], result1[0]);

            // Verificar que no se haya interactuado con cardRepository1
            Mockito.verifyNoInteractions(cardRepository1);

            // Caso 2: ID de tarjeta inválido (menos de 16 dígitos)
            String validJson = "{\"idCard\":\"12345\",\"balance\":\"100.0\"}";
            Card_Repository cardRepository2 = mock(Card_Repository.class);
            Card_Service cardService2 = new Card_Service(cardRepository2);

            Object[] result2 = new Object[]{cardService2.recargarBalance(validJson)};
            Object[] expectedResult2 = {"EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, DEBE INGRESAR SOLO 16 DÍGITOS PARA EL ID DE LA TARJETA", "", ""};
            assertEquals(expectedResult2[0], result2[0]);

            // Verificar que no se haya interactuado con cardRepository2
            Mockito.verifyNoInteractions(cardRepository2);

            // Caso 3: Tarjeta no encontrada en el repositorio
            String validJson3 = "{\"idCard\":\"1234567890123456\",\"balance\":\"100.0\"}";
            Card_Repository cardRepository3 = mock(Card_Repository.class);
            Card_Service cardService3 = new Card_Service(cardRepository3);

            when(cardRepository3.buscardCardXId(anyString())).thenReturn(null);

            Object[] result3 =new Object[]{cardService3.recargarBalance(validJson3)};
            Object[] expectedResult3 = {"TARJETA NO EXISTE EN LA BASE DE DATOS", "", ""};
            assertEquals(expectedResult3[0], result3[0]);

            // Verificar que se llamó a buscardCardXId con el ID de tarjeta válido
            verify(cardRepository3, times(1)).buscardCardXId(eq("1234567890123456"));
            Mockito.verifyNoMoreInteractions(cardRepository3);

            // Caso 4: Tarjeta bloqueada o inactiva
            String validJson4 = "{\"idCard\":\"1234567890123456\",\"balance\":\"100.0\"}";
            Card_Repository cardRepository4 = mock(Card_Repository.class);
            Card_Service cardService4 = new Card_Service(cardRepository4);
            Card_Entity blockedCard = new Card_Entity();
            blockedCard.setState("BL");

            when(cardRepository4.buscardCardXId(eq("1234567890123456"))).thenReturn(blockedCard);

            Object[] result4 = new String[]{cardService4.recargarBalance(validJson4)};
            Object[] expectedResult4 = {"TARJETA BLOQUEADA O INACTIVA", "", ""};
            assertEquals(expectedResult4[0], result4[0]);

            // Verificar que se llamó a buscardCardXId con el ID de tarjeta válido
            verify(cardRepository4, times(1)).buscardCardXId(eq("1234567890123456"));
            Mockito.verifyNoMoreInteractions(cardRepository4);
        } catch (Exception e) {
            System.err.println("Hubo un error al ejecutar la prueba: " + e.getClass().getName());
        }
    }



    @Test
    public void testConsultarBalance() {
        // Caso 1: Tarjeta válida y activa
        Card_Entity activeCard = new Card_Entity();
        activeCard.setIdCard("1234567890123456");
        activeCard.setState("AC"); // Tarjeta activa
        activeCard.setBalance(100.0f);
        when(cardRepository.buscardCardXId("1234567890123456")).thenReturn(activeCard);

        Object[] resultActive = cardService.consultarBalance("1234567890123456");
        assertEquals("cardId : 1234567890123456", resultActive[0]);
        assertEquals("balance : 100.0", resultActive[1]);
        assertEquals("ESTADO DE TARJETA : ACTIVO", resultActive[2]);

        // Caso 2: Tarjeta válida e inactiva
        Card_Entity inactiveCard = new Card_Entity();
        inactiveCard.setIdCard("1234567890123457");
        inactiveCard.setState("IN"); // Tarjeta inactiva
        inactiveCard.setBalance(50.0f);
        when(cardRepository.buscardCardXId("1234567890123457")).thenReturn(inactiveCard);

        Object[] resultInactive = cardService.consultarBalance("1234567890123457");
        assertEquals("cardId : 1234567890123457", resultInactive[0]);
        assertEquals("balance : 50.0", resultInactive[1]);
        assertEquals("ESTADO DE TARJETA : INACTIVO", resultInactive[2]);

        // Caso 3: Tarjeta válida y bloqueada
        Card_Entity blockedCard = new Card_Entity();
        blockedCard.setIdCard("1234567890123458");
        blockedCard.setState("BL"); // Tarjeta bloqueada
        blockedCard.setBalance(0.0f);
        when(cardRepository.buscardCardXId("1234567890123458")).thenReturn(blockedCard);

        Object[] resultBlocked = cardService.consultarBalance("1234567890123458");
        assertEquals("cardId : 1234567890123458", resultBlocked[0]);
        assertEquals("balance : 0.0", resultBlocked[1]);
        assertEquals("ESTADO DE TARJETA : BLOQUEADO", resultBlocked[2]);

        // Caso 4: Tarjeta no existe en la base de datos
        when(cardRepository.buscardCardXId("9999999999999999")).thenReturn(null);

        Object[] resultNonExistentCard = cardService.consultarBalance("9999999999999999");
        assertEquals("NO EXISTE LA TARJETA", resultNonExistentCard[0]);
        assertEquals("", resultNonExistentCard[1]);
        assertEquals("", resultNonExistentCard[2]);

        // Caso 5: Tarjeta con formato de ID inválido
        Object[] resultInvalidFormat = cardService.consultarBalance("invalid_id_format");
        assertEquals("EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, DEBE INGRESAR SOLO 16 DÍGITOS PARA EL ID DE LA TARJETA", resultInvalidFormat[0]);
        assertEquals("", resultInvalidFormat[1]);
        assertEquals("", resultInvalidFormat[2]);
    }


    @Test
    public void testConsultarBalanceXDesfavorable() {
        try {
            // Caso 1: ID de tarjeta inválido (menos de 16 dígitos)
            String invalidIdCard1 = "12345";
            Card_Repository cardRepository1 = mock(Card_Repository.class);
            Card_Service cardService1 = new Card_Service(cardRepository1);

            Object[] result1 = cardService1.consultarBalance(invalidIdCard1);
            Object[] expectedResult1 = {
                    "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, DEBE INGRESAR SOLO 16 DÍGITOS PARA EL ID DE LA TARJETA",
                    "",
                    ""
            };
            assertArrayEquals(expectedResult1, result1);

            // Verificar que no se haya interactuado con cardRepository1
            Mockito.verifyNoInteractions(cardRepository1);

            // Caso 2: Tarjeta no encontrada en el repositorio
            String validIdCard = "1234567890123456"; // Supongamos que esta tarjeta existe
            Card_Repository cardRepository2 = mock(Card_Repository.class);
            Card_Service cardService2 = new Card_Service(cardRepository2);

            when(cardRepository2.buscardCardXId(eq(validIdCard))).thenReturn(null);

            Object[] result2 = cardService2.consultarBalance(validIdCard);
            Object[] expectedResult2 = {"NO EXISTE LA TARJETA", "", ""};
            assertArrayEquals(expectedResult2, result2);

            // Verificar que se llamó a buscardCardXId con el ID de tarjeta válido
            verify(cardRepository2, times(1)).buscardCardXId(eq(validIdCard));
            Mockito.verifyNoMoreInteractions(cardRepository2);
        } catch (Exception e) {
            System.err.println("Hubo un error al ejecutar la prueba: " + e.getClass().getName());
        }
    }



    @AfterEach
    void tearDown() {
        cardRepository.deleteAll();
    }

}
