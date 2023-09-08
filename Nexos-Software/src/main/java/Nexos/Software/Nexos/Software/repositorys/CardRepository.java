package Nexos.Software.Nexos.Software.repositorys;

import Nexos.Software.Nexos.Software.entitys.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Este archivo Es una interfaz que se utiliza en el contexto de Spring Data JPA
 * para interactuar con una base de datos relacionada con la entidad Card_Entity
 * Este archivo juega un papel importante en la capa de persistencia de una aplicación Spring
 */



/**
 * es una interfaz que extiende JpaRepository Esta
 * interfaz define métodos para realizar operaciones de lectura
 * y escritura en la base de datos relacionada con la entidad Card_Entity
 */
@Repository
public interface CardRepository extends  JpaRepository<CardEntity,String>{


    /**
     * Este método hace una consulta nativa en la tabla card donde me va a devolver
     * el registro que devuelva la base de datos en un entity
     * @param idCard el ID del registro que se va a buscar
     * @return retorna el entity que guarda la respuesta de la base de datos
     */
    @Query(value = "select * from card where id_card=:idCard", nativeQuery = true)
    CardEntity buscardCardXId(@Param("idCard") String idCard);


}
