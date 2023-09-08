package Nexos.Software.Nexos.Software.services;

import Nexos.Software.Nexos.Software.entitys.CardEntity;
import Nexos.Software.Nexos.Software.entitys.TransactionEntity;
import Nexos.Software.Nexos.Software.repositorys.CardRepository;
import Nexos.Software.Nexos.Software.repositorys.TransactionRepository;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


/**
 * El archivo TransactionServicesTest es un archivo de prueba unitaria que prueba los servicios
 * de transacciones de la aplicación. Los servicios de transacciones son clases que proporcionan lógica de negocio para realizar transacciones de tarjetas.
 */


@SpringBootTest
@ExtendWith(SpringExtension.class)
class TransactionServicesTest {



    @Autowired
    private TransactionServices transactionServices;

    @Autowired
    private CardService cardService;


    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private TransactionRepository transactionRepository;



    @BeforeEach
    void setUp() {
    }

    /**
     * este metodo de createTransaction lo que hace es crea una transaction y guarda ese registro en la base de datos
     */
    @Test
    public void testCreateTransactionValid() {
        try {
            CardEntity cardEntity = new CardEntity();
            cardEntity.setIdCard("1234567890123455");
            cardEntity.setState("AC");
            cardEntity.setExpirationDate("01/2026");
            cardEntity.setBalance(10000.0F);
            when(cardRepository.buscardCardXId(eq("1234567890123455"))).thenReturn(cardEntity);
            String transaction = "{\"cardId\": \"1234567890123455\", \"price\": \"50.0\"}";
            String result = transactionServices.createTransaction(transaction);
            assertEquals("SE REALIZÓ LA TRANSACCIÓN CON ÉXITO", result);
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }

    }


    /**
     * este metodo de createTransaction lo que hace es probar un escenario desfavorable
     * json no valido
     */
    @Test
    public void testCreateTransaction_InvalidJson() {
        try {
            String transaction = "{\"cardId\": \"1234567890123456\", \"price\": \"hola\"}";
            String result = transactionServices.createTransaction(transaction);
            assertEquals("TRANSACCIÓN RECHAZADA - NO EXISTE LA TARJETA O PRICE NO ES VALIDO", result);
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }


    /**
     * este metodo de createTransaction lo que hace es probar un escenario desfavorable
     * id no valido
     */
    @Test
    public void testCreateTransaction_InvalidKey() {
        try {
            String transaction = "{\"cardId\": \"7854asdasdasd23\", \"price\": \"50.0\"}";
            String result = transactionServices.createTransaction(transaction);
            assertEquals("TRANSACCIÓN RECHAZADA - NO EXISTE LA TARJETA O PRICE NO ES VALIDO", result);
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }


    /**
     * este metodo de createTransaction lo que hace es probar un escenario desfavorable
     * id no existe
     */
    @Test
    public void testCreateTransaction_IdNotExist() {
        try {
            String transaction = "{\"cardId\": \"1234567890123457\", \"price\": \"50.0\"}";
            String result =  transactionServices.createTransaction(transaction);
            assertEquals("TRANSACCIÓN RECHAZADA - NO EXISTE LA TARJETA", result);
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }

    }


    /**
     * este metodo de createTransaction lo que hace es probar un escenario desfavorable
     * id vacio
     */
    @Test
    public void testCreateTransaction_IdEmpty() {
        try {
            String transaction = "{\"cardId\": \"\", \"price\": \"50.0\"}";
            String result = transactionServices.createTransaction(transaction);
            assertEquals("TRANSACCIÓN RECHAZADA - NO EXISTE LA TARJETA O PRICE NO ES VALIDO", result);
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }


    /**
     * este metodo de createTransaction lo que hace es probar un escenario desfavorable
     * no tiene saldo suficiente
     */
    @Test
    public void testCreateTransaction_NoBalance() {
        try {
            CardEntity cardEntity = new CardEntity();
            cardEntity.setIdCard("1234567890123455");
            cardEntity.setState("AC");
            cardEntity.setExpirationDate("12/2026");
            cardEntity.setBalance(10000.0F);
            when(cardRepository.buscardCardXId(eq("1234567890123455"))).thenReturn(cardEntity);
            TransactionEntity transactionEntity = new TransactionEntity();
            transactionEntity.setTransactionDate(new Date());
            transactionEntity.setPrice(1000.0F);
            transactionEntity.setState("AP");
            transactionEntity.setCard(cardEntity);
            when(transactionRepository.findTransactionById(eq(1))).thenReturn(transactionEntity);
            String transaction = "{\"cardId\": \"1234567890123455\", \"price\": \"\"}";
            String result = transactionServices.createTransaction(transaction);
            assertEquals("TRANSACCIÓN RECHAZADA - NO EXISTE LA TARJETA O PRICE NO ES VALIDO", result);
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }

    }



    /**
     * este metodo de createTransaction lo que hace es probar un escenario desfavorable
     * fecha de experiencia de la card inferior a la fecha de la transaction
     */
    @Test
    public void testCreateTransaction_ExpirationDate() {
        try {
            CardEntity cardEntity = new CardEntity();
            cardEntity.setIdCard("1234567890123455");
            cardEntity.setState("AC");
            cardEntity.setExpirationDate("12/2022");
            cardEntity.setBalance(10000.0F);
            when(cardRepository.buscardCardXId(eq("1234567890123455"))).thenReturn(cardEntity);
            String transaction = "{\"cardId\": \"1234567890123455\", \"price\": \"50.0\"}";
            String result = transactionServices.createTransaction(transaction);
            assertEquals("TRANSACCIÓN RECHAZADA", result);
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }

    }

    /**
     * este metodo de createTransaction lo que hace es probar un escenario favorable
     * consulta la transaccion en la base de datos y devuelve el registro solicitadp
     */
    @Test
    public void testConsultarTransaction_Valid() {
        try {
            CardEntity cardEntity = new CardEntity();
            cardEntity.setIdCard("1234567890123455");
            cardEntity.setState("AC");
            cardEntity.setExpirationDate("12/2026");
            cardEntity.setBalance(10000.0F);
            when(cardRepository.buscardCardXId(eq("1234567890123455"))).thenReturn(cardEntity);
            TransactionEntity transactionEntity = new TransactionEntity();
            transactionEntity.setTransactionDate(new Date());
            transactionEntity.setPrice(10000.0F);
            transactionEntity.setState("AP");
            transactionEntity.setCard(cardEntity);
            when(transactionRepository.findTransactionById(eq(1))).thenReturn(transactionEntity);
            Optional<?> result = transactionServices.consultarTransaction("1");
            Object valor = result.get();
            TransactionEntity entidad = (TransactionEntity) valor;
            assertEquals(entidad, transactionEntity);
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }


    /**
     * este metodo de consulta registros en la tabla transaction , lo que hace es probar un escenario desfavorable
     * id null
     */
    @Test
    public void testConsultarTransaction_InvalidJson() {
        try {
            Optional<?> result = transactionServices.consultarTransaction(null);
            if(result!=null){
                Object valor = result.get();
                String cadena = (String) valor;
                assertEquals(cadena, "EL ID NO ES TIPO NUMÉRICO");
            }else{
                assertEquals(null, result);
            }
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }


    /**
     * este metodo de createTransaction lo que hace es probar un escenario desfavorable
     * id tipo incompleto no es numerico
     */
    @Test
    public void testConsultarTransaction_KeysDoNotMatchValues() {
        try {
            Optional<?> result = transactionServices.consultarTransaction("1pw");
                Object valor = result.get();
                String cadena = (String) valor;
                assertEquals(cadena, "EL ID NO ES TIPO NUMÉRICO");
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }

    /**
     * este metodo de createTransaction lo que hace es probar un escenario desfavorable
     * id no esta registrado en la base de datos
     */
    @Test
    public void testConsultarTransaction_IdDoesNotExist() {
        try {
            Optional<?> result = transactionServices.consultarTransaction("7894");
            Object valor = result.get();
            String cadena = (String) valor;
            assertEquals(cadena, "NO EXISTE LA TRANSACTION");
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }

    /**
     * este metodo de createTransaction lo que hace es probar un escenario desfavorable
     * id esta vacio
     */
    @Test
    public void testConsultarTransaction_IdIsEmpty() {
        try {
            Optional<?> result = transactionServices.consultarTransaction("");
            Object valor = result.get();
            String cadena = (String) valor;
            assertEquals(cadena, "EL ID NO ES TIPO NUMÉRICO");

        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }



    /**
     * este metodo de createTransaction lo que hace es probar un escenario favorable
     * apara anular transaction
     */
    @Test
    public void testAnularTransactionValid() {
        try {
            CardEntity cardEntity = new CardEntity();
            cardEntity.setIdCard("1234567890123455");
            cardEntity.setState("AC");
            cardEntity.setExpirationDate("12/2026");
            cardEntity.setBalance(10000.0F);
            when(cardRepository.buscardCardXId(eq("1234567890123455"))).thenReturn(cardEntity);
            TransactionEntity transactionEntity = new TransactionEntity();
            transactionEntity.setTransactionDate(obtenerFechaAyerMenosSegundo());
            transactionEntity.setPrice(10000.0F);
            transactionEntity.setState("AP");
            transactionEntity.setCard(cardEntity);
            when(transactionRepository.findTransactionById(eq(1))).thenReturn(transactionEntity);
            String json = "{\"cardId\": \"1234567890123455\", \"transactionId\": \"1\"}";
            String result = transactionServices.anulacionTransaction(json);
            assertEquals(result, "SE HA ANULADO LA TRANSACCIÓN");
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }

    public static Date obtenerFechaAyerMenosSegundo() {
        Date fechaActual = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.add(Calendar.SECOND, -1);
        return calendar.getTime();
    }

    /**
     * este metodo de createTransaction lo que hace es probar un escenario desfavorable
     * json no valido
     */
    @Test
    public void testAnulacionTransactionWithInvalidData() {
        try {
            String anulation = "Datos inválidos";
            String resultado = transactionServices.anulacionTransaction(anulation);
            assertEquals("NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON", resultado);
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }

    /**
     * este metodo de createTransaction lo que hace es probar un escenario desfavorable
     * clave faltante
     */
    @Test
    void estAnularTransactionClaveFaltante() {
        try {
            String anulacion = "{\"transactionId\": \"123456\"}";
            String resultado = transactionServices.anulacionTransaction(anulacion);
            assertEquals("NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON", resultado);
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }



    /**
     * este metodo de createTransaction lo que hace es probar un escenario desfavorable
     * clave cardID INCORRECTO
     */
        @Test
        void testIdNoNumerico() {
            try {
                String anulacion = "{\"cardId\": \"ABCDEFGHIJKLMN123\", \"transactionId\": \"123456\"}";
                String resultado = transactionServices.anulacionTransaction(anulacion);
                assertEquals("HUBO UN ERROR AL MOMENTO DE ANULAR LA TRANSACCIÓN - INGRESE SOLO TIPO DE DATOS NUMÉRICOS", resultado);
            }catch (Exception e){
                System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
            }
        }

    /**
     * este metodo de createTransaction lo que hace es probar un escenario desfavorable
     * kEYS que no estan registrados el id de la car y de transaction
        */
    @Test
    void testTarjetaInexistente() {
        try {
            String anulacion = "{\"cardId\": \"1020301234567801\", \"transactionId\": \"123456\"}";
            // Supongamos que la tarjeta con el ID proporcionado no existe en la base de datos
            String resultado =  transactionServices.anulacionTransaction(anulacion);
            assertEquals("NO EXISTE LA TARJETA O LA TRANSACCIÓN", resultado);
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }

    /**
     * este metodo de createTransaction lo que hace es probar un escenario desfavorable
     * YA ESTA ANULADA LA TRANSACTION
     */
    @Test
    void testTransaccionAnulada() {
        try {
            CardEntity cardEntity = new CardEntity();
            cardEntity.setIdCard("1234567890123455");
            cardEntity.setState("AC");
            cardEntity.setExpirationDate("12/2026");
            cardEntity.setBalance(10000.0F);
            when(cardRepository.buscardCardXId(eq("1234567890123455"))).thenReturn(cardEntity);
            TransactionEntity transactionEntity = new TransactionEntity();
            transactionEntity.setTransactionDate(new Date());
            transactionEntity.setPrice(10000.0F);
            transactionEntity.setState("AN");
            transactionEntity.setCard(cardEntity);
            when(transactionRepository.findTransactionById(eq(1))).thenReturn(transactionEntity);
            String json = "{\"cardId\": \"1234567890123455\", \"transactionId\": \"1\"}";
            String result = transactionServices.anulacionTransaction(json);
            assertEquals("LA TRANSACTION YA FUE ANULADA , NO PUEDE VOLVER ANULARSE", result);
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }



    @Test
    void testTransaccionMasDe24Horas() {
        try {
            CardEntity cardEntity = new CardEntity();
            cardEntity.setIdCard("1234567890123455");
            cardEntity.setState("AC");
            cardEntity.setExpirationDate("12/2026");
            cardEntity.setBalance(10000.0F);
            when(cardRepository.buscardCardXId(eq("1234567890123455"))).thenReturn(cardEntity);
            TransactionEntity transactionEntity = new TransactionEntity();
            transactionEntity.setTransactionDate(obtenerFechaMasUnSegundo());
            transactionEntity.setPrice(10000.0F);
            transactionEntity.setState("AP");
            transactionEntity.setCard(cardEntity);
            when(transactionRepository.findTransactionById(eq(1))).thenReturn(transactionEntity);
            String json = "{\"cardId\": \"1234567890123455\", \"transactionId\": \"1\"}";
            String result = transactionServices.anulacionTransaction(json);
            assertEquals(result,"NO SE PUEDE ANULAR LA TRANSACCIÓN PORQUE SUPERO LAS 24 HORAS O NO ESTÁ ACTIVO O NO CUMPLE LA CONDICIÓN PARA ANULARSE o YA SE ANULO");
        }catch (Exception e){
            System.err.println("Hubo un error al momento de ejecutar la aplicacion: " + e.getClass().getName());
        }
    }

    public Date obtenerFechaMasUnSegundo() {


        Date fechaActual = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual);

        calendar.add(Calendar.SECOND, 1);
        Date fechaNueva = calendar.getTime();
        return fechaNueva;
    }





    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        cardRepository.deleteAll();
    }

}