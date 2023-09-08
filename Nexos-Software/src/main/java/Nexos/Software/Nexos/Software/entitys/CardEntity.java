package Nexos.Software.Nexos.Software.entitys;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


/**
 * Clase que representa la entidad "card" en la base de datos.
 */
@Entity
@Table(name = "card")
public class CardEntity {


    /**
     * Identificador único.
     */
    @Id
    @Column(name = "id_card", length = 16)
    @Getter
    @Setter
    private String idCard;

    /**
     * Fecha de vencimiento de la tarjeta (formato MM/yyyy).
     */
    @Column(name = "expiration_date", nullable = false ,  length = 7)
    @Getter
    @Setter
    private String expirationDate;

    /**
     * Estado de la tarjeta (por ejemplo, activa o inactiva).
     */
    @Column(name = "state", nullable = false ,  length = 2)
    @Getter
    @Setter
    private String state;


    /**
     * Saldo actual de la tarjeta.
     */
    @Column(name = "balance", precision = 15, scale = 2, nullable = false)
    @Getter
    @Setter
    private float balance;

    /**
     * Nombre del propietario de la tarjeta.
     */
    @Column(name = "owner_name", length = 260)
    @Getter
    @Setter
    private String ownerName;

    /**
     * Constructor vacío de la entidad Card_Entity.
     */
    public CardEntity() {}

    /**
     * Constructor de la entidad Card_Entity con parámetros.
     *
     * @param idCard        Identificador único de la tarjeta.
     * @param expirationDate Fecha de vencimiento de la tarjeta (formato MM/yyyy).
     * @param balance       Saldo actual de la tarjeta.
     * @param ownerName     Nombre del propietario de la tarjeta.
     * @param state         Estado de la tarjeta.
     */
    public CardEntity(String idCard , String expirationDate , float balance , String ownerName , String state ){
        this.idCard = idCard;
        this.expirationDate = expirationDate;
        this.balance = balance;
        this.ownerName = ownerName;
        this.state = state;
    }


}
