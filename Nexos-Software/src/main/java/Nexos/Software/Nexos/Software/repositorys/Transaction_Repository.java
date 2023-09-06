package Nexos.Software.Nexos.Software.repositorys;

import Nexos.Software.Nexos.Software.entitys.Transaction_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


/**
 * Este archivo Es una interfaz que se utiliza en el contexto de Spring Data JPA
 * para interactuar con una base de datos relacionada con la entidad Transaction_Entity
 * Este archivo juega un papel importante en la capa de persistencia de una aplicación Spring
 */





/**
 * es una interfaz que extiende JpaRepository Esta
 * interfaz define métodos para realizar operaciones de lectura
 * y escritura en la base de datos relacionada con la entidad Transaction_Entity
 */
@Repository
public interface Transaction_Repository extends JpaRepository<Transaction_Entity,String>{


    /**
     * este método hace una consulta nativa en la tabla transaction donde
     * guardará un nuevo registro en la tabla TRANSACTION
     * @param transactionDate // la fecha de la transacton
     * @param price // el valor de la transacción
     * @param state // el estado de la transacción
     * @param cardIdCard // el id de la tarjeta que se está haciendo la transacción.
     */
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO TRANSACTION (transaction_date, price, state, card_id_card) " +
            "VALUES (:transactionDate, :price, :state, :cardIdCard)", nativeQuery = true)
    void insertTransaction(
            @Param("transactionDate") Date transactionDate,
            @Param("price") float price,
            @Param("state") String state,
            @Param("cardIdCard") String cardIdCard
    );


    /**
     * este método hace una consulta nativa en la tabla transaction donde me va a devolver
     * el registro que sea igual con el id ingresado por el nombre de la columna id_transaction
     * @param idTransaction este es el id que se pasa por parámetro que servirá para buscar el registro en la base de datos
     * @return me devuelve el registro que dio como respuesta la base de datos que coincide con la condición
     */
    @Query(value = "select * from transaction where id_transaction=:idTransaction", nativeQuery = true)
    Transaction_Entity findTransactionById(@Param("idTransaction") int idTransaction);





    @Transactional
    @Modifying
    @Query(value = "INSERT INTO TRANSACTION (id_transaction, transaction_date, price, state, card_id_card) " +
            "VALUES (:id_transaction, :transactionDate, :price, :state, :cardIdCard)", nativeQuery = true)
    void insertTransactionTest(
            @Param("id_transaction") int idTransaction,
            @Param("transactionDate") Date transactionDate,
            @Param("price") float price,
            @Param("state") String state,
            @Param("cardIdCard") String cardIdCard
    );



}
