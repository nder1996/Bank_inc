package Nexos.Software.Nexos.Software.services;


import Nexos.Software.Nexos.Software.entitys.Card_Entity;
import Nexos.Software.Nexos.Software.entitys.Transaction_Entity;
import Nexos.Software.Nexos.Software.repositorys.Card_Repository;
import Nexos.Software.Nexos.Software.repositorys.Transaction_Repository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class Transaction_Services {


    @Autowired
    private Card_Repository cardRepository;

    @Autowired
    private Transaction_Repository transactionRepository;


    @PersistenceContext
    private EntityManager entityManager;



    public String createTransaction(String transaction){

        String estadoTransaction = "";


        Card_Entity card = new Card_Entity();
        Transaction_Entity transactionEntity = new Transaction_Entity();
     try {
         //ESTADO DE LA TRANSACCION APROBADA (AP) - ANULADA (AN)

         Gson gson = new Gson();
         JsonObject object = gson.fromJson(transaction, JsonObject.class);

         String idCard = object.get("cardId").getAsString();
         float valorCosto =  Float.valueOf(object.get("price").getAsString());

         card = cardRepository.buscardCardBalanceXId(idCard);
         float balanceCard = card.getBalance();


         Date fechaTransaction = new Date();
         SimpleDateFormat formatoFecha = new SimpleDateFormat("MM/yyyy");
         formatoFecha.parse(card.getExpirationDate());
         if(card.getState()=="AC"  && compararFechas(formatoFecha.parse(card.getExpirationDate()), fechaTransaction) >0 && card.getBalance()>=valorCosto ){
             transactionEntity.setTransactionDate(fechaTransaction);
             transactionEntity.setState("AP");
             transactionEntity.setCard(card);
             transactionEntity.setPrice(valorCosto);
             float Total = balanceCard - valorCosto ;
             card.setBalance(Total);
             cardRepository.saveAndFlush(card);
             transactionRepository.saveAndFlush(transactionEntity);
             estadoTransaction = "SE REALIZO LA TRANSACCION CON EXITO";
         }else{
             estadoTransaction = "TRANSACCION RECHAZADA";
         }
     }catch (Exception e){
         e.printStackTrace();
         System.out.println("No se pudo realizar la transaccion : "+e.getMessage());
     }

     return estadoTransaction;

    }




    public static int compararFechas(Date fecha1, Date fecha2) {
        return fecha1.compareTo(fecha2);
    }


}
