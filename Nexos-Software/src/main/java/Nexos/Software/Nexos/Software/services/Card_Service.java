package Nexos.Software.Nexos.Software.services;

import Nexos.Software.Nexos.Software.entitys.Card_Entity;
import Nexos.Software.Nexos.Software.repositorys.Card_Repository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Random;


@Service
public class Card_Service {



    @Autowired
    private Card_Repository cardRepository;


    @PersistenceContext
    private EntityManager entityManager;


     Card_Entity card_entity = new Card_Entity();


    public  Card_Service(){}


     /*
     * ESTADOS DE LA TARJETA ACTIVA (AC) - INACTIVA - (IN) - BLOQUEADA (BL)
     * */
     public Card_Entity createCard(String numProducto){
         Card_Entity newCard = new Card_Entity();
         Card_Entity  Card = new Card_Entity();
         Random random = new Random();
         long min = 1_000_000_000L;
         long max = 9_999_999_9L;
         long number = random.nextLong() % (max - min + 1) + min;
         try {
             String numberString = number + "";
             LocalDate today = LocalDate.now();
             String expirationDate = ""+today.getMonthValue()+"/"+(today.getYear() + 3);
             if(numberString.length()<10){
                 for (int i=0;i<(numberString.length()-10);i++){
                     numberString = numberString + "0";
                 }
             }
             String idCard = "" + numberString + numProducto;
             newCard.setIdCard(idCard);
             newCard.setExpirationDate(expirationDate);
             newCard.setState("IN");
             newCard.setBalance(0.0F);
             Card  = cardRepository.saveAndFlush(newCard);
         }catch (Exception e){
             e.printStackTrace();
             System.out.println("Hubo un error al crear la tarjeta de credito : "+e.getMessage());
         }
         return Card;
     }

    public String activeCard(String idCard){
         String cambio = "";
        Card_Entity newCard = new Card_Entity();
         try {
             newCard = cardRepository.findById(idCard).orElse(null);
             if (newCard.getIdCard()!=null) {
                 newCard.setState("AC");
                 cardRepository.saveAndFlush(newCard);
                 cambio = "tarjeta activada";
             } else {
                 cambio = "no existe el id de la tarjeta en la base de datos";
             }
         }catch (Exception e){
             e.printStackTrace();
             cambio = "Hubo un error al actualizar la tarjeta de credito";
             System.out.println("Hubo un error al actualizar la tarjeta de credito : "+e.getMessage());
         }
         return cambio;
    }




    public String bloqueoCard(String idCard){
        String cambio = "";
        Card_Entity newCard = new Card_Entity();
        try {
            newCard = cardRepository.findById(idCard).orElse(null);
            if (newCard.getIdCard()!=null) {
                newCard.setState("BL");
                cardRepository.saveAndFlush(newCard);
                cambio = "tarjeta bloqueada";
            } else {
                cambio = "no existe el id de la tarjeta en la base de datos";
            }
        }catch (Exception e){
            e.printStackTrace();
            cambio = "Hubo un error al bloquear la tarjeta de credito";
            System.out.println("Hubo un error al bloquear la tarjeta de credito : "+e.getMessage());
        }
        return cambio;
    }


    public String recargarBalance(String cardBalance){
        String cambio = "";
        Card_Entity newCard = new Card_Entity();
        try {
            Gson gson = new Gson();
            JsonObject object = gson.fromJson(cardBalance, JsonObject.class);
            String idCard = object.get("idCard").getAsString();
            String balance = object.get("balance").getAsString();
            newCard = cardRepository.findById(idCard).orElse(null);
            if (newCard.getIdCard()!=null) {
                newCard.setBalance(Float.parseFloat(balance));
                cardRepository.saveAndFlush(newCard);
                cambio = "Su tarjeta se ha recargado";
            } else {
                cambio = "no existe el id de la tarjeta en la base de datos";
            }
        }catch (Exception e){
            e.printStackTrace();
            cambio = "Hubo un error al momento de recargar su tarjeta de credito";
            System.out.println("Hubo un error al momento de recargar su tarjeta de credito : "+e.getMessage());
        }
        return cambio;
    }


    public Object[] consultarBalance(String idCard){
        Object[] consulta = new Object[2];
        Card_Entity cardConsultada = new Card_Entity();
        try {
            cardConsultada = cardRepository.buscardCardBalanceXId(idCard);
            if(cardConsultada.getIdCard()!=null){
                consulta[0] = "cardId : "+cardConsultada.getIdCard();
                consulta[1] = "balance : "+ cardConsultada.getBalance();
            }else{
                consulta[0] = "NO EXISTE LA TARJETA";
                consulta[1] = "NO EXISTE LA TARJETA";
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Hubo un error al buscar los datos de la areta de credito : "+e.getMessage());
        }
        return consulta;
    }


}
