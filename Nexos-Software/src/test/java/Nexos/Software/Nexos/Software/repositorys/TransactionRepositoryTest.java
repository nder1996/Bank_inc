package Nexos.Software.Nexos.Software.repositorys;
import Nexos.Software.Nexos.Software.entitys.CardEntity;
import Nexos.Software.Nexos.Software.entitys.TransactionEntity;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import static org.junit.jupiter.api.Assertions.*;


/**
 * a clase TransactionRepositoryTest es una clase de prueba unitaria que prueba el repositorio de transacciones de la aplicación.
 * El repositorio de transacciones es una clase que proporciona acceso a las transacciones de la base de datos.
 */


@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionRepositoryTest {


    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    CardRepository cardRepository;

    TransactionEntity transactionEntity = new TransactionEntity();

    CardEntity cardEntity = new CardEntity();


    /**
     * este metodo busca un registro de una transaction en la base de datos , escenario favorable si existe el registro
     */
    @Test
    public void testFindTransactionById() {
        try {

            Date fechActual = new Date();
            cardEntity.setIdCard("1234567890123456");
            cardEntity.setExpirationDate("01/2022");
            cardEntity.setBalance(10000.0F);
            cardEntity.setOwnerName("PEPITO PEREZ");
            cardEntity.setState("AC");
            cardRepository.saveAndFlush(cardEntity);

            TransactionEntity transactionToSave = new TransactionEntity();
            transactionToSave.setIdTransaction(1);
            transactionEntity.setState("AP");
            transactionEntity.setTransactionDate(fechActual);
            transactionEntity.setCard(cardEntity);
            transactionEntity.setPrice(500F);
            transactionRepository.insertTransaction(
                    transactionEntity.getTransactionDate(),
                    transactionEntity.getPrice(),
                    transactionEntity.getState(),
                    transactionEntity.getCard().getIdCard());
            TransactionEntity resultante = transactionRepository.findTransactionById(1);
            assertNotNull(resultante, "Se encontró la transacción");
            assertEquals(1, resultante.getIdTransaction(), "Los ID de transacción coinciden");
        } catch (Exception e) {
            fail("Se produjo una excepción: " + e.getMessage());
        }
    }

    /**
     * este metodo  busca un registro de una transaction en la base de datos , escenario desfavorable no existe el registro
     */
    @Test
    void findTransactionByIdXIdNoExiste() {
        try {
            cardEntity = new CardEntity();
            Date fechActual = new Date();
            cardEntity.setIdCard("1234567890123456");
            cardEntity.setExpirationDate("01/2022");
            cardEntity.setBalance(10000.0F);
            cardEntity.setOwnerName("PEPITO PEREZ");
            cardEntity.setState("AC");
            cardRepository.saveAndFlush(cardEntity);
            transactionEntity.setIdTransaction(1);
            transactionEntity.setState("AP");
            transactionEntity.setTransactionDate(fechActual);
            transactionEntity.setCard(cardEntity);
            transactionEntity.setPrice(500F);
            transactionRepository.insertTransaction(
                    transactionEntity.getTransactionDate(),
                    transactionEntity.getPrice(),
                    transactionEntity.getState(),
                    transactionEntity.getCard().getIdCard());
            TransactionEntity resultante = transactionRepository.findTransactionById(157);
            assertNotNull(resultante, "La base de datos no encontró el registro");
        }catch (DataIntegrityViolationException e) {
            System.err.println("Error de violación de integridad: " + e.getMessage());
            e.printStackTrace();
        }catch (AssertionError e) {
            System.err.println("Error en la prueba: " + e.getMessage());
        } catch (EmptyResultDataAccessException e) {
            System.err.println("No se encontró la tarjeta en la base de datos: " + e.getMessage());
        }
        catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }



    /**
     * este metodo inserta un registro de una transaction en la base de datos , escenario favorable
     */
    @Test
    void insertTransaction(){
        try {
            cardEntity = new CardEntity();
            Date fechActual = new Date();
            cardEntity.setIdCard("1234567890123456");
            cardEntity.setExpirationDate("01/2022");
            cardEntity.setBalance(10000.0F);
            cardEntity.setOwnerName("PEPITO PEREZ");
            cardEntity.setState("AC");
            cardRepository.saveAndFlush(cardEntity);
            transactionEntity.setIdTransaction(1);
            transactionEntity.setState("AP");
            transactionEntity.setTransactionDate(fechActual);
            transactionEntity.setCard(cardEntity);
            transactionEntity.setPrice(500F);
            transactionRepository.insertTransaction(
                    transactionEntity.getTransactionDate(),
                    transactionEntity.getPrice(),
                    transactionEntity.getState(),
                    transactionEntity.getCard().getIdCard());
            System.out.println("test insertar nueva transaccion se realizado con exito");

        }catch (Exception e){
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }




    /**
     * este metodo inserta un registro de una transaction en la base de datos , escenario desfavorable
     */
    @Test
    void insertTransactionXErrorDatosEntrada(){
        try {
            cardEntity = new CardEntity();
            transactionEntity = new TransactionEntity();
            Date fechActual = new Date();
            cardEntity.setIdCard("123456789012D213456");
            cardEntity.setExpirationDate("01/20222");
            cardEntity.setBalance(10000.0F);
            cardEntity.setOwnerName("PEPITO PEREZ");
            cardEntity.setState("AC2");
            if(validarTarjeta(cardEntity)==true){
                cardRepository.saveAndFlush(cardEntity);
                transactionEntity.setIdTransaction(1);
                transactionEntity.setState("AP1");
                transactionEntity.setTransactionDate(fechActual);
                transactionEntity.setCard(cardEntity);
                transactionEntity.setPrice(500F);
                if(crearTransaccion(transactionEntity)==true){
                    transactionRepository.insertTransaction(
                            transactionEntity.getTransactionDate(),
                            transactionEntity.getPrice(),
                            transactionEntity.getState(),
                            transactionEntity.getCard().getIdCard());
                }else{
                    System.err.println("Datos que ingreso son incorrectos en el entity transaction, por favor verifique: ");

                }
            }else{
                System.err.println("Datos que ingreso son incorrectos en el entity card, por favor verifique: ");
            }

        }catch (ConstraintViolationException e) {
            System.err.println("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al guardar en la base de datos - Revise la información que ingreso: " + e.getMessage());
        }
    }

    /**
     * este metodo compureba si el id de la tarjeta cumple con las condicciones necesarios
     * @param cardEntity recibe por parametro un entity
     * @return retorna un true si todo el registro contiene el formato correcto de lo contrario devolverla un false
     */
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


    public boolean crearTransaccion(TransactionEntity transactionEntity) {
        try {
            if (transactionEntity.getIdTransaction() <= 0) {
                // Manejar el error: id_transaction no es válido
                return false;
            }
            if (transactionEntity.getTransactionDate() == null) {
                return false;
            }
            if (transactionEntity.getPrice() <= 0) {
                return false;
            }
            if (transactionEntity.getState() == null || transactionEntity.getState().length() != 2) {
                return false;
            }
            if (transactionEntity.getCard() == null || transactionEntity.getCard().getIdCard() == null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        cardRepository.deleteAll();
    }

}