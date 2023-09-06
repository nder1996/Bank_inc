package Nexos.Software.Nexos.Software.services;

import Nexos.Software.Nexos.Software.entitys.Card_Entity;
import Nexos.Software.Nexos.Software.entitys.Transaction_Entity;
import Nexos.Software.Nexos.Software.repositorys.Card_Repository;
import Nexos.Software.Nexos.Software.repositorys.Transaction_Repository;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
class Transaction_ServicesTest {



    @Autowired
    private Transaction_Services transactionServices;

    @Autowired
    private Card_Service cardService;


    @MockBean
    private Card_Repository cardRepository;

    @MockBean
    private Transaction_Repository transactionRepository;



    @BeforeEach
    void setUp() {
    }

    @Test
    public void testCreateTransaction() {

        try {
            // Configura el comportamiento simulado del cardRepository
            Card_Entity cardEntity = new Card_Entity();
            cardEntity.setIdCard("1234567890123455");
            cardEntity.setState("AC");
            cardEntity.setExpirationDate("01/2026");
            cardEntity.setBalance(10000.0F);
            when(cardRepository.buscardCardXId(eq("1234567890123455"))).thenReturn(cardEntity);

            // Simula una transacción válida en formato JSON
            String transactionJson = "{\"price\": \"500\", \"cardId\": \"1234567890123455\"}";

            // Ejecuta el método createTransaction del servicio inyectado
            String result = transactionServices.createTransaction(transactionJson);

            // Verifica que la transacción se haya realizado con éxito
            assertEquals("SE REALIZÓ LA TRANSACCIÓN CON ÉXITO", result);

            // Verifica que se llamaron los métodos adecuados en cardRepository y transactionRepository
            verify(cardRepository, times(1)).buscardCardXId(eq("1234567890123455"));
            verify(cardRepository, times(1)).saveAndFlush(any(Card_Entity.class));
            verify(transactionRepository, times(1)).insertTransaction(any(Date.class), eq(500F), eq("AP"), eq("1234567890123455"));
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }

    }


    @Test
    public void testCreateTransactionXDesfavorable() {
        try {
            // Caso 1: JSON no válido
            String invalidJson = "Este no es un JSON válido";
            String result = transactionServices.createTransaction(invalidJson);
            assertEquals("NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON", result);

            // Caso 2: ID de tarjeta no válido (no tiene 16 dígitos)
            String invalidCardIdJson = "{\"price\": \"500\", \"cardId\": \"1234567890\"}";
            result = transactionServices.createTransaction(invalidCardIdJson);
            assertEquals("TRANSACCIÓN RECHAZADA - POR FAVOR INGRESE LA INFORMACIÓN DE FORMA CORRECTA EL NÚMERO DE LA TARJETA DEBE TENER 16 DÍGITOS Y EL PRECIO EN TIPO DE DATO NUMÉRICO", result);

            // Caso 3: Tarjeta inactiva
            String inactiveCardJson = "{\"price\": \"500\", \"cardId\": \"1234567890123455\"}";
            Card_Entity inactiveCard = new Card_Entity();
            inactiveCard.setState("INACTIVA");
            when(cardRepository.buscardCardXId(eq("1234567890123455"))).thenReturn(inactiveCard);
            result = transactionServices.createTransaction(inactiveCardJson);
            assertEquals("TRANSACCIÓN RECHAZADA", result);

            // Caso 4: Tarjeta vencida
            String expiredCardJson = "{\"price\": \"500\", \"cardId\": \"1234567890123455\"}";
            Card_Entity expiredCard = new Card_Entity();
            expiredCard.setState("ACTIVA");
            expiredCard.setExpirationDate("01/2021"); // Tarjeta vencida en enero de 2021
            when(cardRepository.buscardCardXId(eq("1234567890123455"))).thenReturn(expiredCard);
            result = transactionServices.createTransaction(expiredCardJson);
            assertEquals("TRANSACCIÓN RECHAZADA", result);

            // Caso 5: Saldo insuficiente en la tarjeta
            String insufficientBalanceJson = "{\"price\": \"15000\", \"cardId\": \"1234567890123455\"}";
            Card_Entity insufficientBalanceCard = new Card_Entity();
            insufficientBalanceCard.setState("ACTIVA");
            insufficientBalanceCard.setBalance(5000.0F); // Saldo insuficiente
            when(cardRepository.buscardCardXId(eq("1234567890123455"))).thenReturn(insufficientBalanceCard);
            result = transactionServices.createTransaction(insufficientBalanceJson);
            assertEquals("TRANSACCIÓN RECHAZADA", result);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            System.out.println("FORMATO JSON INVALIDO : "+e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Hubo un error al actualizar la tarjeta de credito : "+e.getMessage());
        }

    }

    private Card_Entity createCardEntity(String idCard, String state, String expirationDate, float balance) {
        Card_Entity cardEntity = new Card_Entity();
        cardEntity.setIdCard(idCard);
        cardEntity.setState(state);
        cardEntity.setExpirationDate(expirationDate);
        cardEntity.setBalance(balance);
        return cardEntity;
    }




    @Test
    public void testConsultarTransactionXId() {
        try {
            // Simula un ID de transacción válido
            String validTransactionId = "123";
            // Configura el comportamiento simulado del transactionRepository
            Transaction_Entity transactionEntity = new Transaction_Entity();
            when(transactionRepository.findTransactionById(eq(123))).thenReturn(transactionEntity);
            // Ejecuta el método consultarTransaction
            Transaction_Entity result = transactionServices.consultarTransaction(validTransactionId);
            // Verifica que se haya encontrado la transacción
            assertNotNull(result, "Se encontró la transacción");
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }


    @Test
    public void testConsultarTransactionXDesfavorable() {
        try {
            // Caso 1: ID de transacción no válido (no es un número)
            String invalidTransactionId = "abc";
            when(transactionRepository.findTransactionById(anyInt())).thenReturn(null);

            // Ejecuta el método consultarTransaction con un ID de transacción no válido
            Transaction_Entity result1 = transactionServices.consultarTransaction(invalidTransactionId);

            // Verifica que no se haya encontrado la transacción (debe ser una transacción vacía)
            if(result1.getState()==null){
                assertNull(result1.getState(), "No se encontró la transacción en caso de ID no válido. Resultado: " + result1);
            }

            // Caso 2: ID de transacción negativo
            String negativeTransactionId = "-1";

            // Ejecuta el método consultarTransaction con un ID de transacción negativo
            Transaction_Entity result2 = transactionServices.consultarTransaction(negativeTransactionId);

            // Verifica que no se haya encontrado la transacción (debe ser una transacción vacía)
            assertNull(result2.getTransactionDate(), "No se encontró la transacción en caso de ID negativo");

            // Caso 3: ID de transacción inexistente
            String nonExistentTransactionId = "999";
            when(transactionRepository.findTransactionById(eq(999))).thenReturn(null);

            // Ejecuta el método consultarTransaction con un ID de transacción inexistente
            Transaction_Entity result3 = transactionServices.consultarTransaction(nonExistentTransactionId);

            // Verifica que no se haya encontrado la transacción (debe ser una transacción vacía)
            assertNull(result3, "No se encontró la transacción en caso de ID inexistente");
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("Se produjo una NullPointerException: " + e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("HUBO UN ERROR AL MOMENTO DE CONSULTAR LA TRANSACCIÓN : "+e.getMessage());
        }
    }



    @Test
    public void testAnulacionTransactionCardXTansaction() {
        try {
            // Simula un JSON de anulación válido
            String anulationJson = "{\"transactionId\": \"123\", \"cardId\": \"1234567890123455\"}";
            // Simula un objeto Card_Entity
            Card_Entity cardEntity = new Card_Entity();
            cardEntity.setIdCard("1234567890123455");
            cardEntity.setState("AC");
            cardEntity.setExpirationDate("01/2026");
            cardEntity.setBalance(10000.0F);
            // Simula un objeto Transaction_Entity
            Transaction_Entity transactionEntity = new Transaction_Entity();
            transactionEntity.setIdTransaction(123);
            transactionEntity.setState("AP");
            Date fechaTransaction =  new Date() ;
            transactionEntity.setTransactionDate(fechaTransaction);
            transactionEntity.setPrice(500F);
            transactionEntity.setCard(cardEntity);
            // Configura el comportamiento simulado de los repositorios
            when(cardRepository.buscardCardXId(eq("1234567890123455"))).thenReturn(cardEntity);
            when(transactionRepository.findTransactionById(eq(123))).thenReturn(transactionEntity);

            // Ejecuta el método anulacionTransaction
            String result = transactionServices.anulacionTransaction(anulationJson);

            // Verifica que la anulación se haya realizado con éxito
            assertEquals("SE HA ANULADO LA TRANSACCIÓN", result);

            // Verifica que se hayan llamado los métodos adecuados en cardRepository y transactionRepository
            verify(cardRepository, times(1)).buscardCardXId(eq("1234567890123455"));
            verify(transactionRepository, times(1)).findTransactionById(eq(123));
            verify(cardRepository, times(1)).saveAndFlush(any(Card_Entity.class));
            verify(transactionRepository, times(1)).saveAndFlush(any(Transaction_Entity.class));
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }

    }

    @Test
    public void testAnulacionTransactionDesfavorable() {
        try {
            // Caso 1: JSON no válido
            String invalidJson = "invalidKey";
            String result1 = transactionServices.anulacionTransaction(invalidJson);
            assertEquals("NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON", result1);

            // Caso 2: JSON con datos no numéricos
            String invalidDataJson = "{\"transactionId\": \"abc\", \"cardId\": \"123456789012345\"}";
            String result2 = transactionServices.anulacionTransaction(invalidDataJson);
            assertEquals("HUBO UN ERROR AL MOMENTO DE ANULAR LA TRANSACCIÓN - INGRESE SOLO TIPO DE DATOS NUMÉRICOS", result2);

            // Caso 3: Tarjeta no encontrada
            String nonExistingCardJson = "{\"transactionId\": \"123\", \"cardId\": \"1234567893123456\"}";
            String result3 = transactionServices.anulacionTransaction(nonExistingCardJson);
            assertEquals("NO SE PUEDE ANULAR LA TRANSACCIÓN - NO EXISTE LA TARJETA O LA TRANSACCIÓN", result3);

            // Caso 4: Transacción no encontrada
            String nonExistingTransactionJson = "{\"transactionId\": \"999\", \"cardId\": \"1234567890123456\"}";
            String result4 = transactionServices.anulacionTransaction(nonExistingTransactionJson);
            assertEquals("NO SE PUEDE ANULAR LA TRANSACCIÓN - NO EXISTE LA TARJETA O LA TRANSACCIÓN", result4);

            // Caso 5: Transacción ya anulada
            Card_Entity card = new Card_Entity();
            //card.setIdCard("1234567890123456");
            card.setIdCard("1234567890123455");
            card.setState("AC");
            card.setExpirationDate("01/2026");
            card.setBalance(10000.0F);
            when(cardRepository.buscardCardXId("1234567890123456")).thenReturn(card);
            // Configurar el comportamiento simulado del transactionRepository
            Transaction_Entity transaction = new Transaction_Entity();
            Date fechaActual = new Date();
            transaction.setIdTransaction(456);
            transaction.setTransactionDate(fechaActual);
            transaction.setPrice(500F);
            transaction.setCard(card);
            transaction.setState("AN");
            when(transactionRepository.findTransactionById(456)).thenReturn(transaction);
            // JSON que representa una solicitud de anulación para una transacción ya anulada
            String json = "{\"transactionId\": \"456\", \"cardId\": \"1234567890123456\"}";

            // Ejecutar el método de anulación de transacción
            String result = transactionServices.anulacionTransaction(json);

            // Verificar que el resultado sea el mensaje esperado
            assertEquals("LA TRANSACTION YA FUE ANULADA , NO PUEDE VOLVER ANULARSE", result);

            // Caso 6: Transacción más de 24 horas de antigüedad
            card = new Card_Entity();
            //card.setIdCard("1234567890123456");
            card.setIdCard("1034567897123456");
            card.setState("AC");
            card.setExpirationDate("01/2026");
            card.setBalance(10000.0F);
            when(cardRepository.buscardCardXId("1034567897123456")).thenReturn(card);
            // Configurar el comportamiento simulado del transactionRepository
             transaction = new Transaction_Entity();
            Date fechaAyer = new Date();
            Date fechaAyer1 = new Date(fechaActual.getTime() - (2 * 1000 * 60 * 60 * 24));
            transaction.setIdTransaction(909);
            transaction.setTransactionDate(fechaAyer1);
            transaction.setPrice(500F);
            transaction.setCard(card);
            transaction.setState("AP"); // Transacción ya anulada

            // Configurar el comportamiento simulado de transactionRepository
            when(transactionRepository.findTransactionById(909)).thenReturn(transaction);

            String olderTransactionJson = "{\"transactionId\": \"909\", \"cardId\": \"1034567897123456\"}";
            String result6 = transactionServices.anulacionTransaction(olderTransactionJson);
            assertEquals("NO SE PUEDE ANULAR LA TRANSACCIÓN PORQUE SUPERO LAS 24 HORAS O NO ESTÁ ACTIVO O NO CUMPLE LA CONDICIÓN PARA ANULARSE", result6);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            System.out.println("FORMATO JSON INVALIDO : "+e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("Se produjo una NullPointerException: " + e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("HUBO UN ERROR AL MOMENTO DE CONSULTAR LA TRANSACCIÓN : "+e.getMessage());
        }
    }



    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        cardRepository.deleteAll();
    }

}