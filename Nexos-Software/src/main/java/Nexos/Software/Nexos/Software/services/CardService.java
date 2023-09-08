package Nexos.Software.Nexos.Software.services;

import Nexos.Software.Nexos.Software.entitys.CardEntity;
import Nexos.Software.Nexos.Software.repositorys.CardRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;


/**
 *
Este archivo de servicios en Java JPA (Java Persistence API) se utiliza para definir lógica de
 negocio y operaciones relacionadas con la base de datos. Los servicios encapsulan
 la funcionalidad que se debe realizar en las entidades JPA, como crear, leer, actualizar
 y eliminar registros, así como otras operaciones específicas de la aplicación.
 */





@Service
public class CardService {


    /**
     *  es una referencia a un objeto de tipo Card_Repository
     */
    @Autowired /* Se escribe esta anotación para inyectar las dependencias de manera automática */
    private CardRepository cardRepository;


    /**
     * EntityManager Es una interfaz que forma parte de Java Persistence API (JPA) y
     *se utiliza para interactuar con la base de datos en aplicaciones que utilizan JPA para el acceso a datos.
     */
    @PersistenceContext /* esta annotacion  inyecta las dependencias necesarias para comunicarme con la base de datos */
    private EntityManager entityManager;
    /**
     EntityManager es una interfaz de Java Persistence API (JPA)
     que se utiliza para interactuar con la base de datos. Esta interfaz proporciona métodos
     para realizar operaciones de persistencia, como guardar y recuperar datos de la base de datos
     */




    /**
     * Crea una nueva instancia (objeto) de la clase Card_Entity
     * y asigna esta instancia a la variable card_entity
     */
    CardEntity card_entity = new CardEntity();



    /**
     * Constructor de la clase Card_Service
     * Este constructor crea una instancia de la clase Card_Service.
     * Puede ser utilizado para inicializar objetos de esta clase.
     */
    public CardService(){}

    public CardService(CardRepository cardRepository){
        this.cardRepository= cardRepository;
    }


    /**
     * Este método se utiliza para crear una nueva tarjeta de
     * crédito con los requerimientos de la documentación
     * @param numProducto // el id del producto
     * @return //  retorna los datos de la tarjeta creada
     *
     */
    public Optional<?> createCard(String numProducto){
        String estadoActive = "";
        CardEntity card = new CardEntity();
        CardEntity cardNew =  new CardEntity();
        try {
            if (numProducto != null && !numProducto.isEmpty() && numProducto.trim().length() == 6 && numProducto.matches("\\d+")) {
                String numGenerate = generarNum10();
                String numCard = numGenerate +numProducto;
                card.setIdCard(numCard);
                LocalDate today = LocalDate.now(); // Obtener la fecha actual
                String expirationDate = ""+today.getMonthValue()+"/"+(today.getYear() + 3); // genera el formato de de la fecha vencimiento de la tarjeta
                card.setExpirationDate(expirationDate);
                card.setState("IN");
                card.setBalance(0.0F);
                cardNew  = cardRepository.saveAndFlush(card);// guarda los datos en la base de datos y esta se actualice
                return Optional.of(cardNew);
            }if (numProducto == null) {
                System.err.println("LA CADENA ES NULA");
                estadoActive = "LA CADENA ES NULA";
            }if(numProducto != null){
                 if (numProducto.isEmpty()) {
                    System.err.println("NO INGRESASTE NINGÚN DATO");
                     estadoActive = "NO INGRESASTE NINGÚN DATO";
                } else if (numProducto.trim().length() != 6) {
                    System.out.println("LA CADENA NO TIENE 6 CARACTERES");
                     estadoActive = "LA CADENA NO TIENE 6 CARACTERES";
                } else if (!numProducto.matches("\\d+")) {
                    System.out.println("NO INGRESASTE DATO VáLIDO , SOLO NÚMEROS");
                     estadoActive = "NO INGRESASTE DATO VáLIDO , SOLO NÚMEROS";
                }
            }
        }catch (Exception e){
            estadoActive = "HUBO UN ERROR AL CREAR LA TARJETA DE CREDITO";
            e.printStackTrace();
            System.out.println("HUBO UN ERROR AL CREAR LA TARJETA DE CREDITO : "+e.getMessage());
        }
        return Optional.of(estadoActive);
    }




    /**
     * este metodo activa la tarjetas y devuelve el mensaje del estado si fue activada o no
     * @param idCard // es el id de la tarjeta
     * @return // devuelve el estado de la operacion si fue activado o no o si hubo un error
     */
    public String activeCard(String idCard){
        String estadoActive = "";
        CardEntity newCard = new CardEntity();
        CardEntity card = new CardEntity();
        try {
            if(isValidDataJson(idCard , "idCard" , null)==true){
                Gson gson = new Gson();// Crear una instancia de Gson para procesar datos JSON
                JsonObject object = gson.fromJson(idCard, JsonObject.class);// Parsear la cadena JSON "cardBalance" a un objeto JsonObject
                    String id =  object.get("idCard").getAsString();
                    if(id.matches("\\d+") && !id.isEmpty() && id.trim().length() == 16 ){
                        newCard = cardRepository.buscardCardXId(id);/*BUSCA EL DATO POR EL ID DE LA TARJETA , LLENA TODOS LOS CAMPOS EN NULL SI NO LOS ENCUENTRA*/
                        String estado = "";
                        if(newCard!=null){
                            estado = newCard.getState();
                            if(!newCard.getState().equals("AC")) {
                                newCard.setState("AC"); /*ACTIVA LA TARJETA */
                                cardRepository.saveAndFlush(newCard);
                                estadoActive = "TARJETA ACTIVADA";
                            }
                            if(estado.equals("AC")){
                                estadoActive = "LA TARJETA YA SE ENCUENTRA ACTIVADA";
                            }
                        }else{
                            estadoActive = "NO EXISTE EL ID DE LA TARJETA EN LA BASE DE DATOS";
                        }
                    }else{
                        estadoActive = "EL KEY DE idCard NO CUMPLE CON EL FORMATO , VUELVE A INGRESARLO NUEVAMENTE";
                    }

            }else{
                estadoActive = "NO INGRESASTE UN JSON O NO COLOCASTE BIEN EL KEY DEL JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON";
            }
        }catch (JsonSyntaxException e) {
            e.printStackTrace();
            estadoActive = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, SOLO TIPO NUMÉRICO O NO INGRESO LOS 16 DÍGITOS DE LA TARJETA";
            System.out.println("FORMATO JSON INVALIDO : "+e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            estadoActive = "HUBO UN ERROR AL ACTIVAR LA TARJETA";
            System.out.println("Hubo un error al actualizar la tarjeta de credito : "+e.getMessage());
        }
        return estadoActive;
    }


    /**
     * Este metodo cambia de estado la tarjeta a BL (bloqueado)
     * @param idCard // el id de la tarjeta que se quiere bloquear
     * @return // retorna la respuesta de la  operacion si existe manda el mensaje cambio y si no manda un mensaje
     * que no se encontro tarjeta relacionada con el id.
     */
    public String bloqueoCard(String idCard){
        String estadoBloqueo = "";
        CardEntity card = new CardEntity();
        try {
            if(idCard !=null && idCard.matches("\\d+")  && !idCard.isEmpty()  && idCard.trim().length() == 16 ){
                card = cardRepository.buscardCardXId(idCard);
                if(card!=null){
                    if (!card.getState().equals("BL")) {
                        card.setState("BL");
                        cardRepository.saveAndFlush(card);
                        estadoBloqueo = "TARJETA BLOQUEADA";
                    }else{
                        estadoBloqueo = "YA SE ENCUENTRA BLOQUEADA ESTA TARJETA";
                    }
                }else{
                    estadoBloqueo = "NO EXISTE EL ID DE LA TARJETA EN LA BASE DE DATOS";
                }
            }else{
                estadoBloqueo = "EL ID QUE INGRESASTE NO CUMPLE CON LAS CONDICCIONES , VUELVE INGRESARLO";
            }
        }catch (JsonSyntaxException e) {
            e.printStackTrace();
            estadoBloqueo = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, SOLO TIPO NUMÉRICO O NO INGRESO LOS 16 DÍGITOS DE LA TARJETA";
            System.out.println("FORMATO JSON INVALIDO : "+e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            estadoBloqueo = "HUBO UN ERROR AL BLOQUEAR LA TARJETA DE CRÉDITO";
            System.out.println("Hubo un error al bloquear la tarjeta de credito : "+e.getMessage());
        }
        return estadoBloqueo;
    }




    /**
     *  Este metodo realiza la recarga de la tarjeta
     * @param cardBalance recibe por parametro un string que contiene el id de la tarjeta y el saldo a recargar
     * @return retorna un mensaje  del estado de la operacion si se realizo si o no.
     */

    public String recargarBalance(String cardBalance){
        String estadoRecarga = "";
        CardEntity card = new CardEntity();
        try {
            if(isValidDataJson(cardBalance , "idCard" , "balance")==true){
                Gson gson = new Gson();// Crear una instancia de Gson para procesar datos JSON
                JsonObject object = gson.fromJson(cardBalance, JsonObject.class);// Parsear la cadena JSON "cardBalance" a un objeto JsonObject
                    String idCard = object.get("idCard").getAsString(); // Obtener el valor de "idCard" como una cadena
                    String balance = object.get("balance").getAsString();// Obtener el valor de "balance" como una cadena
                    if(idCard !=null && idCard.matches("\\d+") && !idCard.isEmpty() && idCard.trim().length() == 16 && balance !=null && !balance.isEmpty() &&  esFloatValido(balance)==true){
                        card = cardRepository.buscardCardXId(idCard);
                        if(card!=null){
                            if (card.getState().equals("AC")) {  // verifica si el registro que encontro es nulo por el id
                                float balanceFloat = Float.parseFloat(balance);
                                float totalBalance = balanceFloat + card.getBalance();
                                card.setBalance(totalBalance);
                                cardRepository.saveAndFlush(card); // guarda el registro modificado que se le agrego el saldo nuevo
                                estadoRecarga = "SU TARJETA SE HA RECARGADO";
                            }else{
                                estadoRecarga = "SU TARJETA ESTA BLOQUEADA O ESTA INACTIVA";
                            }
                        }else{
                            estadoRecarga = "TARJETA NO EXISTE EN LA BASE DE DATOS";
                        }
                    }else{
                        estadoRecarga = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, DEBE INGRESAR SOLO 16 DÍGITOS PARA EL ID DE LA TARJETA";
                    }
                }else{
                    estadoRecarga = "NO INGRESASTE UN JSON O NO COLOCASTE BIEN EL KEY DEL JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON";
                }

        }catch (JsonSyntaxException e) {
            e.printStackTrace();
            estadoRecarga = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, SOLO TIPO NUMÉRICO O NO INGRESO LOS 16 DÍGITOS DE LA TARJETA";
            System.out.println("FORMATO JSON INVALIDO : "+e.getMessage());
        }catch (NullPointerException e){
            e.printStackTrace();
            estadoRecarga = "HUBO UN ERROR AL MOMENTO DE RECARGAR SU TARJETA DE CRÉDITO";
            System.out.println("Hubo un error al momento de recargar su tarjeta : "+e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            estadoRecarga = "HUBO UN ERROR AL MOMENTO DE RECARGAR SU TARJETA DE CRÉDITO";
            System.out.println("Hubo un error al momento de recargar su tarjeta  : "+e.getMessage());
        }

        return estadoRecarga;
    }


    /**
     * Este metodo se usa para para buscar la tarjeta y verificar su saldo
     * @param idCard el id de la tarjeta
     * @return retorna el valor obtendo de la consulta , si existe la tarjeta o no
     */
    public Optional<?> consultarBalance(String idCard){
        CardEntity cardConsultada = new CardEntity();
        String estadoConsulta = "";
        try {
            if( idCard!=null && !idCard.isEmpty() && idCard.matches("\\d+") && idCard.length()==16){
                cardConsultada = cardRepository.buscardCardXId(idCard);
                if(cardConsultada!=null){
                    return Optional.of(cardConsultada);
                }else{
                    estadoConsulta = "NO EXISTE LA TARJETA EN LA BASE DE DATOS";
                }
            }else{
                estadoConsulta = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, DEBE INGRESAR SOLO 16 DÍGITOS PARA EL ID DE LA TARJETA";
            }
        }catch (Exception e){
            e.printStackTrace();
            estadoConsulta = "HUBO UN ERROR AL BUSCAR LOS DATOS DE LA TARJETA DE CRÉDITO";
            System.out.println("HUBO UN ERROR AL BUSCAR LOS DATOS DE LA TARJETA DE CRÉDITO : "+e.getMessage());
        }

        return Optional.of(estadoConsulta);
    }






    /**
     * Método para verificar si un String es un número float válido
     * @param str recibe el string que quiere verificar
     * @return devuelve true si es float y si es false no es float
     */
    public static boolean esFloatValido(String str) {
        String regex = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
        String intRegex = "\\d+";
        boolean respuestaInt = str.matches(regex);
        if(str.matches(regex)==false){
             respuestaInt = str.matches(intRegex);
        }
        return respuestaInt;
    }





    /**
     * este metodo comprueba si el valor que se recibio del controller tiene el key validos y el value
     * son los correctos
     * @param jsonString un string con el farmato json correspondiene
     * @param keyJson1 el id1 del json - key
     * @param keyJson2 el id2 del json - key
     * @return
     */
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

    public static String generarNum10(){
        String numAleatorio = "";
        try {
            // Definir un rango de números enteros largos
            Random random = new Random(); /* Este objeto se utiliza para generar números aleatorios. */
            long min = 1_000_000_000L; // El valor mínimo en el rango
            long max = 9_999_999_9L; // El valor máximo en el rango
            long numberAleatorio = random.nextLong() % (max - min + 1) + min; // Generar un número aleatorio dentro de un rango específico
            String numberString = numberAleatorio + "";
            if(numberString.length()<10){  // verifica si la longitud del numero generado es de 10 de lo contrario coloca 0 donde haga falta
                int limite = 10 - numberString.length() ;
                for (int i=0;i<limite;i++){
                    numberString = numberString + "0";
                }
                numAleatorio = numberString;
            }else{
                numAleatorio = numberString;
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("HUbo un error al crear el numero aleatorio "+e.getMessage());
        }

        return numAleatorio;
    }





}

