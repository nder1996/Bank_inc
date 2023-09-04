package Nexos.Software.Nexos.Software.services;


import Nexos.Software.Nexos.Software.entitys.Card_Entity;
import Nexos.Software.Nexos.Software.entitys.Transaction_Entity;
import Nexos.Software.Nexos.Software.repositorys.Card_Repository;
import Nexos.Software.Nexos.Software.repositorys.Transaction_Repository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;



/**
 *
 Este archivo de servicios en Java JPA (Java Persistence API) se utiliza para definir lógica de
 negocio y operaciones relacionadas con la base de datos. Los servicios encapsulan
 la funcionalidad que se debe realizar en las entidades JPA, como crear, leer, actualizar
 y eliminar registros, así como otras operaciones específicas de la aplicación.
 */


@Service
public class Transaction_Services {




    /**
     *  es una referencia a un objeto de tipo Card_Repository
     */
    @Autowired
    private Card_Repository cardRepository;

    /**
     *  es una referencia a un objeto de tipo transactionRepository
     */
    @Autowired
    private Transaction_Repository transactionRepository;



    /**
     * EntityManager Es una interfaz que forma parte de Java Persistence API (JPA) y
     *se utiliza para interactuar con la base de datos en aplicaciones que utilizan JPA para el acceso a datos.
     */
    @PersistenceContext
    private EntityManager entityManager;
    /**
     EntityManager es una interfaz de Java Persistence API (JPA)
     que se utiliza para interactuar con la base de datos. Esta interfaz proporciona métodos
     para realizar operaciones de persistencia, como guardar y recuperar datos de la base de datos
     */





    /**
     * Constructor de la clase Transaction_Services
     * Este constructor crea una instancia de la clase Transaction_Services.
     * Puede ser utilizado para inicializar objetos de esta clase.
     */
    public  Transaction_Services(){}




    /**
     *este metodo crea un nuevo registro en la tabla transaction llamando el metodo del interface repository , teniendo en cuenta las validaciones
     * @param transaction dato que se ingresa por parametro donde se obtendra el valor de la transaccion y el id de la tarjeta que se va sacar la transaccion
     * @return retorna el mensaje del estado de la transaccion si se realizo o no
     */
    public String createTransaction(String transaction){
        String estadoTransaction = "";


        Card_Entity card = new Card_Entity();
        Transaction_Entity transactionEntity = new Transaction_Entity();
     try {
         //ESTADO DE LA TRANSACCION APROBADA (AP) - ANULADA (AN)

         Gson gson = new Gson(); // Crear una instancia de Gson para procesar datos JSON
         JsonObject object = gson.fromJson(transaction, JsonObject.class); // Parsear la cadena JSON "cardBalance" a un objeto JsonObject

         if(object.get("price").getAsString().matches("\\d+") && object.get("cardId").getAsString().length()==16){ // verifica si la informacion que se paso por parametro cumple con las condiciones para crear la transaccion
             String idCard = object.get("cardId").getAsString(); // obtiene el id de la tarjeta
             float valorCosto =  Float.valueOf(object.get("price").getAsString()); // obtiene el valor de la transaccion
             card = cardRepository.buscardCardBalanceXId(idCard); // obtiene el registro de la tarjeta que se obtuvo al pasarle el id  de la tarjeta
             float balanceCard = card.getBalance(); // es el saldo de la tarjeta del cliente
             Date fechaTransaction = new Date(); // obtiene la fecha de hoy
             SimpleDateFormat formatoFecha = new SimpleDateFormat("MM/yyyy"); // formato para convertir la fecha
             if(card.getState().equals("AC")  && compararFechas(formatoFecha.parse(card.getExpirationDate()), fechaTransaction) >0 && card.getBalance() >= valorCosto ){ //verifica si la tarjeta esta activada , si la fecha de vencimiento es mayor a la fecha acual , si la tarjeta tiene el saldo para realizar la compra
                 transactionEntity.setTransactionDate(fechaTransaction); // guarda la fecha actual en la entitad de transaccion
                 transactionEntity.setState("AP"); // aprueba la transaccion
                 transactionEntity.setCard(card); // guarda el registro de la card ya que la relacion es uno a muchos
                 transactionEntity.setPrice(valorCosto); // el costo de la transaccion
                 float Total = balanceCard - valorCosto ; // el total de la tarjeta de credito
                 card.setBalance(Total); // guarda el saldo restante de la tarjeta de credito
                 cardRepository.saveAndFlush(card); // guarda la entitad modificada con el nuevo saldo del registro
                 transactionRepository.insertTransaction(transactionEntity.getTransactionDate() , transactionEntity.getPrice()
                         , transactionEntity.getState() ,transactionEntity.getCard().getIdCard()
                 ); /// guarda por nativequery el nuevo registro en la tabla transaccion
                 estadoTransaction = "SE REALIZÓ LA TRANSACCIÓN CON ÉXITO"; // mensaje de exito
                 }else{
                         estadoTransaction = "TRANSACCIÓN RECHAZADA"; // no se realizo la trasacion
                 }
         }else{
             estadoTransaction = "TRANSACCIÓN RECHAZADA - POR FAVOR INGRESE LA INFORMACIÓN DE FORMA CORRECTA"; //
         }
     }catch (Exception e){
         e.printStackTrace();
         System.out.println("NO SE PUDO REALIZAR LA TRANSACCIÓN : "+e.getMessage());
     }

     return estadoTransaction;

    }

    /**
     * Este metodo devuelve el registro de la transaccion  que devuelve la base de datos
     * @param transactionId el id de la transaccion
     * @return devuelve la respuesta de la base de datos en una entity
     */
    public Transaction_Entity consultarTransaction(int transactionId){
        Transaction_Entity transactionEntity = new Transaction_Entity();
        try {
            transactionEntity = transactionRepository.findTransactionById(transactionId);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("HUBO UN ERROR AL MOMENTO DE CONSULTAR LA TRANSACCIÓN : "+e.getMessage());
        }
        return transactionEntity;
    }


    /**
     *Este metodo anula la transaccion
     * @param anulation // se pasa por parametro un string que tiene los datos del id de la tarjeta y de la transaccion
     * @return retorna el estado de la anulacion si lo hizo o no
     */
    public String anulacionTransaction(String anulation){
        String estadoAnulacion = "";
        Card_Entity card_entity = new Card_Entity();
        Transaction_Entity transactionEntity = new Transaction_Entity();
        Gson gson = new Gson();
        JsonObject object = gson.fromJson(anulation, JsonObject.class);
        try {
            if(object.get("transactionId").getAsString().matches("\\d+") && object.get("cardId").getAsString().matches("\\d+") && object.get("cardId").getAsString().length()==16){ //cumprueba si es tipo numerico
                String idCard = object.get("cardId").getAsString();
                int idTransaction = Integer.parseInt(object.get("transactionId").getAsString());
                card_entity = cardRepository.findById(idCard).orElse(null);
                transactionEntity = transactionRepository.findTransactionById(idTransaction);
                boolean fecha24H = tieneMasDe24Horas(transactionEntity.getTransactionDate());
                if(fecha24H==false && transactionEntity.getState().equals("AN")){ // verifica si ya la transaccion ha sido anulada y si es mayor a 4 horas
                    transactionEntity.setState("AN"); // cambia de esto
                    float nuevoSaldo = card_entity.getBalance()+transactionEntity.getPrice(); // nuevo saldo de la tarjeta
                    card_entity.setBalance(nuevoSaldo);
                    cardRepository.saveAndFlush(card_entity);
                    transactionRepository.saveAndFlush(transactionEntity);
                    estadoAnulacion = "SE HA ANULADO LA TRANSACCIÓN";
                }else{
                    estadoAnulacion = "NO SE PUEDE ANULAR LA TRANSACCIÓN";
                }
            }else{
                estadoAnulacion = "NO SE PUEDE ANULAR LA TRANSACCIÓN";
            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("HUBO UN ERROR AL MOMENTO DE ANULAR LA CONSULTA : "+e.getMessage());
        }

        return estadoAnulacion;
    }


    /**
     *Este metodo compara cual de las dos fechas es mayor si la fecha de vencimiento de la tarjeta o la fecha actual
     * @param fecha1 fecha de vencimiento de la tarjeta
     * @param fecha2 fecha actual de la consulta
     * @return devuelve un entero si es mayor que cero es mayor sino el valor contrario
     */
    public static int compararFechas(Date fecha1, Date fecha2) {
        return fecha1.compareTo(fecha2);
    }


    /**
     *Este metodo verifica si la fecha de la transaccion supera los 24 horas despues de realizarse
     * @param fecha es la fecha del registro de la transaccion
     * @return devuelve al respuesta si el registro es superior a las 24 horas
     */
    public static boolean tieneMasDe24Horas(Date fecha) {

        Date fechaActual = new Date();

        long diferenciaEnMilisegundos = fechaActual.getTime() - fecha.getTime();

        long diferenciaEnSegundos = diferenciaEnMilisegundos / 1000;

        return diferenciaEnSegundos > 86400;
    }


}
