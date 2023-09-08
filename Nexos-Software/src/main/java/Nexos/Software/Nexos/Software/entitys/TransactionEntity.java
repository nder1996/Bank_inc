package Nexos.Software.Nexos.Software.entitys;


import javax.persistence.*;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "transaction")
public class TransactionEntity {

    /**
     * Identificador único de la transacción.
     */
    @Id
    @Column(name = "id_transaction")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private int idTransaction;

    /**
     * Fecha y hora de la transacción.
     */
    @Column(name = "transaction_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date transactionDate;

    /**
     * Estado de la transacción (por ejemplo, aprobada o rechazada).
     */
    @Column(name = "state", length = 2, nullable = false)
    @Getter
    @Setter
    private String state;

    /**
     * Precio de la transacción.
     */
    @Getter
    @Setter
    @Column(name = "price")
    private float price;

    /**
     * Tarjeta asociada a la transacción.
     */
    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "id_card", nullable = false)
    @Getter
    @Setter
    private CardEntity card;


    /**
     * Constructor vacío de la entidad Transaction_Entity.
     */
    public TransactionEntity(){}


    /**
     * Constructor de la entidad Transaction_Entity con parámetros.
     *
     * @param idTransaction   Identificador único de la transacción.
     * @param transactionDate Fecha y hora de la transacción.
     * @param state           Estado de la transacción.
     * @param card            Tarjeta asociada a la transacción.
     * @param price           Precio de la transacción.
     */
    public TransactionEntity(int idTransaction , Date transactionDate , String state , CardEntity card , float price){
      this.idTransaction = idTransaction;
      this.transactionDate = transactionDate;
      this.state = state ;
      this.card = card;
      this.price = price;
    }

}
