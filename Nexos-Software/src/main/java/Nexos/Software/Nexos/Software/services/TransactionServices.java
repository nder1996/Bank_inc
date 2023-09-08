package Nexos.Software.Nexos.Software.services;


import Nexos.Software.Nexos.Software.entitys.CardEntity;
import Nexos.Software.Nexos.Software.entitys.TransactionEntity;
import Nexos.Software.Nexos.Software.repositorys.CardRepository;
import Nexos.Software.Nexos.Software.repositorys.TransactionRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;


/**
 *
 Este archivo de servicios en Java JPA (Java Persistence API) se utiliza para definir lógica de
 negocio y operaciones relacionadas con la base de datos. Los servicios encapsulan
 la funcionalidad que se debe realizar en las entidades JPA, como crear, leer, actualizar
 y eliminar registros, así como otras operaciones específicas de la aplicación.
 */


@Service
public class TransactionServices {




    /**
     *  es una referencia a un objeto de tipo Card_Repository
     */
    @Autowired
    private CardRepository cardRepository;

    /**
     *  es una referencia a un objeto de tipo transactionRepository
     */
    @Autowired
    private TransactionRepository transactionRepository;



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
    public TransactionServices(){}




    /**
     *este metodo crea un nuevo registro en la tabla transaction llamando el metodo del interface repository , teniendo en cuenta las validaciones
     * @param transaction dato que se ingresa por parametro donde se obtendra el valor de la transaccion y el id de la tarjeta que se va sacar la transaccion
     * @return retorna el mensaje del estado de la transaccion si se realizo o no
     */
    public String createTransaction(String transaction){
        String estadoTransaction = "";
        CardEntity card = new CardEntity();
        TransactionEntity transactionEntity = new TransactionEntity();
     try {
         if(isValidDataJson(transaction , "cardId" , "price")==true){
             Gson gson = new Gson(); // Crear una instancia de Gson para procesar datos JSON
             JsonObject object = gson.fromJson(transaction, JsonObject.class); // Parsear la cadena JSON "cardBalance" a un objeto JsonObject

             String idCard = object.get("cardId").getAsString(); // Obtener el valor de "idCard" como una cadena
             String price = object.get("price").getAsString();// Obtener el valor de "balance" como una cadena

             if( !idCard.isEmpty() && idCard.trim().length() == 16 && idCard.matches("\\d+") &&
                     !price.isEmpty()  &&  esFloatValido(price)){
                 card = cardRepository.buscardCardXId(idCard); // obtiene el registro de la tarjeta que se obtuvo al pasarle el id  de la tarjeta
                 if(card!=null){
                     float balanceCard = card.getBalance(); // es el saldo de la tarjeta del cliente
                     Date fechaTransaction = new Date(); // obtiene la fecha de hoy
                     SimpleDateFormat formatoFecha = new SimpleDateFormat("MM/yyyy"); // formato para convertir la fecha
                     float saldoCard = card.getBalance();
                     float valorCosto = Float.parseFloat(price);
                     if(card.getState().equals("AC")  && compararFechas(formatoFecha.parse(card.getExpirationDate()), fechaTransaction) >0 && saldoCard >= valorCosto ){
                         transactionEntity.setTransactionDate(fechaTransaction); // guarda la fecha actual en la entitad de transaccion
                         transactionEntity.setState("AP"); // aprueba la transaccion
                        // guarda el registro de la card ya que la relacion es uno a muchos
                         transactionEntity.setPrice(valorCosto); // el costo de la transaccion
                         float Total = balanceCard - valorCosto ; // el total de la tarjeta de credito
                         card.setBalance(Total); // guarda el saldo restante de la tarjeta de credito
                         cardRepository.saveAndFlush(card); // guarda la entitad modificada con el nuevo saldo del registro
                         transactionEntity.setCard(card);
                         transactionRepository.insertTransaction(transactionEntity.getTransactionDate() ,
                                 transactionEntity.getPrice() , transactionEntity.getState() , transactionEntity.getCard().getIdCard());
                         estadoTransaction = "SE REALIZÓ LA TRANSACCIÓN CON ÉXITO"; // mensaje de exito
                     }else{
                         estadoTransaction = "TRANSACCIÓN RECHAZADA"; // no cumplio con alguna condiccion
                     }
                 }else{
                     estadoTransaction = "TRANSACCIÓN RECHAZADA - NO EXISTE LA TARJETA"; // no cumplio con alguna condiccion
                 }
             }else{
                 estadoTransaction = "TRANSACCIÓN RECHAZADA - NO EXISTE LA TARJETA O PRICE NO ES VALIDO"; // no cumplio con alguna condiccion
             }
         }else{
             estadoTransaction = "NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON"; //
         }
     } catch (JsonSyntaxException e) {
         e.printStackTrace();
         estadoTransaction = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, SOLO TIPO NUMÉRICO O NO INGRESO LOS 16 DÍGITOS DE LA TARJETA";
         System.out.println("FORMATO JSON INVALIDO : "+e.getMessage());
     }catch (Exception e){
         e.printStackTrace();
         estadoTransaction = "HUBO UN ERROR AL ACTUALIZAR LA TARJETA DE CREDITO";
         System.out.println("HUBO UN ERROR AL ACTUALIZAR LA TARJETA DE CREDITO : "+e.getMessage());
     }
     return estadoTransaction;
    }

    /**
     * Este metodo devuelve el registro de la transaccion  que devuelve la base de datos
     * @param transactionId el id de la transaccion
     * @return devuelve la respuesta de la base de datos en una entity
     */
    public Optional<?> consultarTransaction(String transactionId){
        TransactionEntity transactionEntity = new TransactionEntity();
        String estadoConsulta = "";
        try {
            if( transactionId!=null && !transactionId.isEmpty()  && transactionId.matches("\\d+")){
                int idTransaction= Integer.parseInt(transactionId);
                transactionEntity = transactionRepository.findTransactionById(idTransaction);
                if(transactionEntity!=null){
                    return Optional.of(transactionEntity);
                }else{
                    estadoConsulta = "NO EXISTE LA TRANSACTION";
                    return Optional.of(estadoConsulta);
                }
            }else{
                estadoConsulta = "EL ID NO ES TIPO NUMÉRICO";
                return Optional.of(estadoConsulta);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("Se produjo una NullPointerException: " + e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("HUBO UN ERROR AL MOMENTO DE CONSULTAR LA TRANSACCIÓN : "+e.getMessage());
        }
        return null;
    }


    /**
     *Este metodo anula la transaccion
     * @param anulation // se pasa por parametro un string que tiene los datos del id de la tarjeta y de la transaccion
     * @return retorna el estado de la anulacion si lo hizo o no
     */
    public String anulacionTransaction(String anulation){
        String estadoAnulacion = "";
        CardEntity card_entity = new CardEntity();
        TransactionEntity transactionEntity = new TransactionEntity();
        try {
            if(isValidDataJson(anulation , "cardId" , "transactionId")==true){
                Gson gson = new Gson();
                JsonObject object = gson.fromJson(anulation, JsonObject.class);
                if(object.get("transactionId").getAsString().matches("\\d+") && object.get("cardId").getAsString().matches("\\d+") && object.get("cardId").getAsString().length()==16){ //cumprueba si es tipo numerico
                    String idCard = object.get("cardId").getAsString();
                    String idTransaction = object.get("transactionId").getAsString();
                    if(idCard !=null && idCard.matches("\\d+") && !idCard.isEmpty() && idCard.trim().length() == 16 && idTransaction !=null && idTransaction.matches("\\d+") && !idTransaction.isEmpty()){
                        int numeroEntero = Integer.parseInt(idTransaction);
                        card_entity = cardRepository.buscardCardXId(idCard);
                        transactionEntity = transactionRepository.findTransactionById(numeroEntero);
                        if(card_entity!=null && transactionEntity!=null){
                            boolean fecha24H = esMayorQue24Horas(new Date(), transactionEntity.getTransactionDate());
                            String estadoTransaccion = transactionEntity.getState();
                            if(fecha24H==false && !transactionEntity.getState().equals("AN")){
                                transactionEntity.setState("AN"); // cambia de esto
                                float cardSaldo = card_entity.getBalance();
                                float transactionSaldo = transactionEntity.getPrice();
                                float nuevoSaldo = cardSaldo + transactionSaldo; // nuevo saldo de la tarjeta
                                card_entity.setBalance(nuevoSaldo);
                                cardRepository.saveAndFlush(card_entity);
                                transactionRepository.saveAndFlush(transactionEntity);
                                estadoAnulacion = "SE HA ANULADO LA TRANSACCIÓN";
                            }else{
                                estadoAnulacion = "NO SE PUEDE ANULAR LA TRANSACCIÓN PORQUE SUPERO LAS 24 HORAS O NO ESTÁ ACTIVO O NO CUMPLE LA CONDICIÓN PARA ANULARSE o YA SE ANULO";
                            }if(estadoTransaccion.equals("AN")){
                                estadoAnulacion = "LA TRANSACTION YA FUE ANULADA , NO PUEDE VOLVER ANULARSE";
                            }
                        }else{
                            estadoAnulacion = "NO EXISTE LA TARJETA O LA TRANSACCIÓN";
                        }

                    }else{
                        estadoAnulacion = "HUBO UN ERROR AL MOMENTO DE ANULAR LA TRANSACCIÓN - INGRESE SOLO TIPO DE DATOS NUMÉRICOS" ;
                    }
                }else{
                    estadoAnulacion = "HUBO UN ERROR AL MOMENTO DE ANULAR LA TRANSACCIÓN - INGRESE SOLO TIPO DE DATOS NUMÉRICOS" ;
                }
            }else{
                estadoAnulacion = "NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON"; //
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            estadoAnulacion = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, SOLO TIPO NUMÉRICO O NO INGRESO LOS 16 DÍGITOS DE LA TARJETA";
            System.out.println("FORMATO JSON INVALIDO : "+e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Hubo un error al actualizar la tarjeta de credito : "+e.getMessage());
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


    public static boolean esMayorQue24Horas(Date fecha1, Date fecha2) {
        long diferenciaEnMillis = fecha2.getTime() - fecha1.getTime();
        long millisEn24Horas = 24 * 60 * 60 * 1000;
        return diferenciaEnMillis > millisEn24Horas;
    }


    public static boolean esFloatValido(String str) {
        String regex = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
        String intRegex = "\\d+";
        boolean respuestaInt = str.matches(regex);
        if(str.matches(regex)==false){
            respuestaInt = str.matches(intRegex);
        }
        return respuestaInt;
    }


    public boolean isValidDataJson(String jsonString , String keyJson1 , String keyJson2){
        try {
            if(jsonString!=null && !jsonString.isEmpty()){
                Gson gson = new Gson();
                JsonObject object = gson.fromJson(jsonString, JsonObject.class);
                if(keyJson2!=null && !keyJson2.isEmpty()){
                    if( object.keySet() != null && !object.keySet().isEmpty() && object.get(keyJson1)!=null && object.get(keyJson2)!=null){
                        JsonElement cardIdElement = object.get(keyJson1);
                        JsonElement balanceElement = object.get(keyJson2);
                        if (cardIdElement != null && !cardIdElement.isJsonNull() && balanceElement != null && !balanceElement.isJsonNull()) {
                            return true;
                        }else{
                            return false;
                        }
                    }else{
                        return false;
                    }
                }else{
                    if(object.get(keyJson1) != null && !object.get(keyJson1).isJsonNull()){
                        JsonElement cardIdElement = object.get(keyJson1);
                        if (cardIdElement != null && !cardIdElement.isJsonNull()) {
                            return true;
                        }else{
                            return false;
                        }
                    }else{
                        return false;
                    }
                }
            }else{
                return false;
            }
        } catch (JsonSyntaxException e) {
            return false;
        }
    }




}
