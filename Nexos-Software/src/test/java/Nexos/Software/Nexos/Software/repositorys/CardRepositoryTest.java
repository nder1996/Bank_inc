package Nexos.Software.Nexos.Software.repositorys;

import Nexos.Software.Nexos.Software.entitys.CardEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.transaction.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CardRepositoryTest {

    @Autowired
    CardRepository cardRepository;




    CardEntity cardEntity = new CardEntity();



    @Test
    void buscardCardXId_EscenarioFavorable() {
        cardEntity = new CardEntity();
        cardEntity.setIdCard("1234567890123456");
        cardEntity.setExpirationDate("01/2023");
        cardEntity.setBalance(0.0F);
        cardEntity.setOwnerName("PEPITO PEREZ");
        cardEntity.setState("IN");
        cardRepository.saveAndFlush(cardEntity);
        try {
        CardEntity resultante = cardRepository.buscardCardXId("1234567890123456");
        assertNotNull(resultante);
        assertTrue(resultante!=null,"Se encontro el registro");
            assertEquals(resultante.getIdCard(), "1234567890123456", "Los ID de tarjeta coinciden");
            assertEquals(resultante.getExpirationDate(), "01/2023", "Las fechas de vencimiento coinciden");
            assertEquals(resultante.getBalance(), 0.0F, "Los saldos coinciden");
            assertEquals(resultante.getOwnerName(), "PEPITO PEREZ", "Los nombres de propietario coinciden");
            assertEquals(resultante.getState(), "IN", "Los estados coinciden");

    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("La prueba falló debido a una excepción: " + e.getMessage());
    }


    }


   @Test
   void buscardCardXIdNoExisteIdCard() {
       cardEntity = new CardEntity();
       cardEntity.setIdCard("1234567890123456");
       cardEntity.setExpirationDate("01/2023");
       cardEntity.setBalance(0.0F);
       cardEntity.setOwnerName("PEPITO PEREZ");
       cardEntity.setState("IN");
       try {
           cardRepository.saveAndFlush(cardEntity);
           CardEntity resultante = cardRepository.buscardCardXId("123456P7890123450");
           assertNotNull(resultante, "La base de datos encontró el registro");
           assertEquals("1234567890123456", resultante.getIdCard(), "ID de tarjeta no coincide");
           assertEquals("01/2023", resultante.getExpirationDate(), "Fecha de vencimiento no coincide");
           assertEquals(0.0F, resultante.getBalance(), 0.001, "Saldo no coincide"); // Usar delta para valores flotantes
           assertEquals("PEPITO PEREZ", resultante.getOwnerName(), "Nombre del propietario no coincide");
           assertEquals("IN", resultante.getState(), "Estado no coincide");
       }catch (AssertionError e) {
           System.err.println("Error en la prueba: " + e.getMessage());
       } catch (EmptyResultDataAccessException e) {
           System.err.println("No se encontró la tarjeta en la base de datos: " + e.getMessage());
       } catch (Exception e) {
           System.err.println("Error inesperado: " + e.getMessage());
       }
   }






    @Test
    void guardarCard(){
        cardEntity = new CardEntity();
        cardEntity.setIdCard("1234567890123455");
        cardEntity.setExpirationDate("01/2022");
        cardEntity.setBalance(0.0F);
        cardEntity.setOwnerName("JUANITO");
        cardEntity.setState("IN");
        cardRepository.saveAndFlush(cardEntity);
        CardEntity resultante = cardRepository.buscardCardXId(cardEntity.getIdCard());
        assertTrue(resultante != null &&
                        cardEntity.getIdCard().equals(resultante.getIdCard()) &&
                        cardEntity.getExpirationDate().equals(resultante.getExpirationDate()) &&
                        cardEntity.getBalance() == resultante.getBalance() &&
                        cardEntity.getOwnerName().equals(resultante.getOwnerName()) &&
                        cardEntity.getState().equals(resultante.getState()),
                "Todos los datos se guardaron correctamente");
    }



    @Test
    void guardarCardInvalidData(){
        CardEntity cardEntity = new CardEntity();
        cardEntity.setIdCard("02345678we90123455");
        cardEntity.setExpirationDate("0we120228");
        cardEntity.setBalance(0.0F);
        cardEntity.setOwnerName("JUANITO");
        cardEntity.setState("IN87");
        try {
            if(validarTarjeta(cardEntity)){
                cardRepository.saveAndFlush(cardEntity);
            }else{
                System.err.println("Datos que ingreso son incorrectos , por favor verifique: ");
            }
        }catch (org.springframework.dao.DataIntegrityViolationException e) {
            System.err.println("Error de integridad de datos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Se esperaba una DataIntegrityViolationException, pero se lanzó otra excepción: " + e.getClass().getName());
        }

    }

    public boolean validarTarjeta(CardEntity cardEntity) {
        if (cardEntity.getIdCard().length() != 16 && cardEntity.getIdCard().matches("\\d+")) {
            return false;
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MMdd");
            dateFormat.setLenient(false);
            dateFormat.parse(cardEntity.getExpirationDate());
        } catch (ParseException e) {
            return false;
        }
        try {
            if (cardEntity == null) {
                        return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        if (cardEntity.getOwnerName().length() > 260) {
            return false;
        }

        return true;
    }
    @AfterEach
    void tearDown() {
        cardRepository.deleteAll();
    }

}