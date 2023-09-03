package Nexos.Software.Nexos.Software.repositorys;

import Nexos.Software.Nexos.Software.entitys.Card_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Card_Repository extends  JpaRepository<Card_Entity,String>{


    @Query(value = "select * from card where id_card=:idCard", nativeQuery = true)
    Card_Entity buscardCardBalanceXId(@Param("idCard") String idCard);

    //public interface Card_Repository extends JpaRepository<Card_Entity,String> {

        /*
          @Modifying
    @Query(value = "INSERT INTO movimiento (id, id_empresa, descripcion, bodega_origen_codigo, bodega_destino_codigo, fecha_creacion, fecha_entrega, estado) VALUES (:id, :id_empresa, :descripcion, :bodega_origen_codigo, :bodega_destino_codigo, :fecha_creacion, :fecha_entrega, :estado)", nativeQuery = true)
    void guardarMovimiento(@Param("id") Long id, @Param("id_empresa") Long id_empresa, @Param("descripcion") String descripcion, @Param("bodega_origen_codigo") String bodega_origen_codigo, @Param("bodega_destino_codigo") String bodega_destino_codigo, @Param("fecha_creacion") Date fecha_creacion, @Param("fecha_entrega") Date fecha_entrega, @Param("estado") String estado);

    @Modifying
    @Query(value = "UPDATE movimiento SET id_empresa = :id_empresa, descripcion = :descripcion, bodega_origen_codigo = :bodega_origen_codigo, bodega_destino_codigo = :bodega_destino_codigo, fecha_creacion = :fecha_creacion, fecha_entrega = :fecha_entrega, estado = :estado WHERE id = :id", nativeQuery = true)
    int actualizarMovimiento(@Param("id") Long id, @Param("id_empresa") Long id_empresa, @Param("descripcion") String descripcion, @Param("bodega_origen_codigo") String bodega_origen_codigo, @Param("bodega_destino_codigo") String bodega_destino_codigo, @Param("fecha_creacion") Date fecha_creacion, @Param("fecha_entrega") Date fecha_entrega, @Param("estado") String estado);

    @Modifying
    @Query(value = "DELETE FROM movimiento WHERE id = :id", nativeQuery = true)
    int eliminarMovimiento(@Param("id") Long id);
        * */

    //}
}
