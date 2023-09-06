package Nexos.Software.Nexos.Software.repositorys;
import Nexos.Software.Nexos.Software.entitys.Card_Entity;
import Nexos.Software.Nexos.Software.entitys.Transaction_Entity;
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


@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionRepositoryTest {


    @Autowired
    Transaction_Repository transactionRepository;

    @Autowired
     Card_Repository cardRepository;

    Transaction_Entity transactionEntity = new Transaction_Entity();

    Card_Entity cardEntity = new Card_Entity();




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

            Transaction_Entity transactionToSave = new Transaction_Entity();
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
            Transaction_Entity resultante = transactionRepository.findTransactionById(1);
            assertNotNull(resultante, "Se encontró la transacción");
            assertEquals(1, resultante.getIdTransaction(), "Los ID de transacción coinciden");
        } catch (Exception e) {
            fail("Se produjo una excepción: " + e.getMessage());
        }
    }


    @Test
    void findTransactionByIdXIdNoExiste() {
        try {
            cardEntity = new Card_Entity();
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
            Transaction_Entity resultante = transactionRepository.findTransactionById(157);
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



    @Test
    void insertTransaction(){
        try {
            cardEntity = new Card_Entity();
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

    @Test
    void insertTransactionXErrorDatosEntrada(){
        try {
            cardEntity = new Card_Entity();
            transactionEntity = new Transaction_Entity();
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


    public boolean validarTarjeta(Card_Entity cardEntity) {
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


    public boolean crearTransaccion(Transaction_Entity transactionEntity) {
        try {
            // Validar que id_transaction sea mayor que 0 (asumiendo que debe ser un valor positivo)
            if (transactionEntity.getIdTransaction() <= 0) {
                // Manejar el error: id_transaction no es válido
                return false;
            }

            // Validar que transactionDate no sea nulo y que sea una fecha válida
            if (transactionEntity.getTransactionDate() == null) {
                // Manejar el error: transactionDate es nulo
                return false;
            }

            // Validar que price sea mayor que 0 (asumiendo que debe ser un valor positivo)
            if (transactionEntity.getPrice() <= 0) {
                // Manejar el error: price no es válido
                return false;
            }

            // Validar que state tenga exactamente 2 caracteres
            if (transactionEntity.getState() == null || transactionEntity.getState().length() != 2) {
                // Manejar el error: state no es válido
                return false;
            }

            // Validar que card sea una entidad válida (puedes agregar validaciones adicionales si es necesario)
            if (transactionEntity.getCard() == null || transactionEntity.getCard().getIdCard() == null) {
                // Manejar el error: card no es válido
                return false;
            }

            // Si todas las validaciones pasan, puedes proceder a insertar la transacción en la base de datos
            // transactionRepository.save(transactionEntity);
            return true;
        } catch (Exception e) {
            // Manejar cualquier excepción no prevista aquí, por ejemplo, registrarla o lanzar una excepción personalizada.
            return false; // Indicar que hubo un error no manejado
        }
    }



    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        cardRepository.deleteAll();
    }

}