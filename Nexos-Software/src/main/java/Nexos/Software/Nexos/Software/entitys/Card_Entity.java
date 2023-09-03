package Nexos.Software.Nexos.Software.entitys;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "card")
public class Card_Entity {


    @Id
    @Column(name = "id_card", length = 16)
    @Getter
    @Setter
    private String idCard;

    @Column(name = "expiration_date", nullable = false ,  length = 7)
    @Getter
    @Setter
    private String expirationDate;

    @Column(name = "state", nullable = false ,  length = 2)
    @Getter
    @Setter
    private String state;

    @Column(name = "balance", precision = 15, scale = 2, nullable = false)
    @Getter
    @Setter
    private float balance;

    @Column(name = "owner_name", length = 260)
    @Getter
    @Setter
    private String ownerName;


    public Card_Entity() {}

    public Card_Entity(String idCard ,String expirationDate , float balance ,String ownerName , String state ){
        this.idCard = idCard;
        this.expirationDate = expirationDate;
        this.balance = balance;
        this.ownerName = ownerName;
        this.state = state;
    }


}
