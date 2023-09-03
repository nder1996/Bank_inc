package Nexos.Software.Nexos.Software.entitys;


import javax.persistence.*;
import java.util.Date;

import Nexos.Software.Nexos.Software.entitys.Card_Entity;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "transaction")
public class Transaction_Entity {


    @Id
    @Column(name = "id_transaction")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private int idTransaction;

    @Column(name = "transaction_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date transactionDate;

    @Column(name = "state", length = 2, nullable = false)
    @Getter
    @Setter
    private String state;


    @Getter
    @Setter
    @Column(name = "price")
    private float price;

    @ManyToOne
    @JoinColumn(name = "card_id_card", referencedColumnName = "id_card", nullable = false)
    @Getter
    @Setter
    private Card_Entity card;

    public Transaction_Entity(){}

    public Transaction_Entity(int idTransaction , Date transactionDate , String state , Card_Entity card , float price){
      this.idTransaction = idTransaction;
      this.transactionDate = transactionDate;
      this.state = state ;
      this.card = card;
      this.price = price;
    }

}
