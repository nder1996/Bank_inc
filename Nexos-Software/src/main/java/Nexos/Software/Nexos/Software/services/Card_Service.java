package Nexos.Software.Nexos.Software.services;

import Nexos.Software.Nexos.Software.entitys.Card_Entity;
import Nexos.Software.Nexos.Software.repositorys.Card_Repository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Random;


/**
 *
Este archivo de servicios en Java JPA (Java Persistence API) se utiliza para definir lógica de
 negocio y operaciones relacionadas con la base de datos. Los servicios encapsulan
 la funcionalidad que se debe realizar en las entidades JPA, como crear, leer, actualizar
 y eliminar registros, así como otras operaciones específicas de la aplicación.
 */





@Service
public class Card_Service {


    /**
     *  es una referencia a un objeto de tipo Card_Repository
     */
    @Autowired /* Se escribe esta anotación para inyectar las dependencias de manera automática */
    private Card_Repository cardRepository;


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
    Card_Entity card_entity = new Card_Entity();



    /**
     * Constructor de la clase Card_Service
     * Este constructor crea una instancia de la clase Card_Service.
     * Puede ser utilizado para inicializar objetos de esta clase.
     */
    public  Card_Service(){}

    public Card_Service(Card_Repository cardRepository){
        this.cardRepository= cardRepository;
    }


    /**
     * Este método se utiliza para crear una nueva tarjeta de
     * crédito con los requerimientos de la documentación
     * @param numProducto // el id del producto
     * @return //  retorna los datos de la tarjeta creada
     *
     */
     public Card_Entity createCard(String numProducto){
         Card_Entity cardResultante  = new Card_Entity(); /* Instancia un nuevo objecto*/
         Card_Entity newCard = new Card_Entity(); /*instancia un nuevo objecto*/
         try {
             Random random = new Random(); /* Este objeto se utiliza para generar números aleatorios. */
             // Definir un rango de números enteros largos
             long min = 1_000_000_000L; // El valor mínimo en el rango
             long max = 9_999_999_9L; // El valor máximo en el rango
             long numberAleatorio = random.nextLong() % (max - min + 1) + min; // Generar un número aleatorio dentro de un rango específico

             if(numProducto.matches("\\d+") && numProducto.length()==6){ /*Este condicional valida que el dato de entrada tiene solo datos de tipo numerico y que tenga 6 digitos*/
                 String numberString = numberAleatorio + ""; /*convierte el numero aleatorio en tipo de dato entero*/
                 LocalDate today = LocalDate.now(); // Obtener la fecha actual
                 String expirationDate = ""+today.getMonthValue()+"/"+(today.getYear() + 3); // genera el formato de de la fecha vencimiento de la tarjeta
                 if(numberString.length()<10){  // verifica si la longitud del numero generado es de 10 de lo contrario coloca 0 donde haga falta
                     int limite = 10 - numberString.length() ;
                     for (int i=0;i<limite;i++){
                         numberString = numberString + "0";
                     }
                 }
                 String idCard = "" + numberString + numProducto; // se crea el id de la tarjeta con los 10 numeros generados aleatorios y los 6 numeros que ingreso el usuario
                 newCard.setIdCard(idCard);
                 newCard.setExpirationDate(expirationDate);
                 /**
                  *ESTADOS DE LA TARJETA ACTIVA (AC) - INACTIVA - (IN) - BLOQUEADA (BL)
                  */
                 newCard.setState("IN");
                 newCard.setBalance(0.0F);
                 cardResultante  = cardRepository.saveAndFlush(newCard);// guarda los datos en la base de datos y esta se actualice
             }
         }catch (Exception e){
             e.printStackTrace();
             System.out.println("HUBO UN ERROR AL CREAR LA TARJETA DE CREDITO : "+e.getMessage());
         }
         return cardResultante;
     }


    /**
     * este metodo activa la tarjetas y devuelve el mensaje del estado si fue activada o no
     * @param idCard // es el id de la tarjeta
     * @return // devuelve el estado de la operacion si fue activado o no o si hubo un error
     */
    public String activeCard(String idCard){
         String cambio = "";
        Card_Entity newCard = new Card_Entity();
        Card_Entity card = new Card_Entity();
         try {
            if(isValidJson(idCard)==true){
                Gson gson = new Gson();// Crear una instancia de Gson para procesar datos JSON
                JsonObject object = gson.fromJson(idCard, JsonObject.class);// Parsear la cadena JSON "cardBalance" a un objeto JsonObject
                String id = object.get("idCard").getAsString(); // Obtener el valor de "idCard" como una cadena
                if(id.matches("\\d+") && id.length()==16){
                    newCard = cardRepository.buscardCardXId(idCard);/*BUSCA EL DATO POR EL ID DE LA TARJETA , LLENA TODOS LOS CAMPOS EN NULL SI NO LOS ENCUENTRA*/
                    String estado = "";
                    if(newCard!=null){
                        estado = newCard.getState();
                        if(!newCard.getState().equals("AC")) {
                            newCard.setState("AC"); /*ACTIVA LA TARJETA */
                            cardRepository.saveAndFlush(newCard);
                            cambio = "TARJETA ACTIVADA";
                        }
                        if(estado.equals("AC")){
                            cambio = "LA TARJETA YA SE ENCUENTRA ACTIVADA";
                        }
                    }else{
                        cambio = "NO EXISTE EL ID DE LA TARJETA EN LA BASE DE DATOS";
                    }

                }else{
                    cambio = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, SOLO TIPO NUMÉRICO O NO INGRESO LOS 16 DÍGITOS DE LA TARJETA";
                }
            }else{
                cambio = "NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON";
            }
         } catch (JsonSyntaxException e) {
             e.printStackTrace();
             cambio = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, SOLO TIPO NUMÉRICO O NO INGRESO LOS 16 DÍGITOS DE LA TARJETA";
             System.out.println("FORMATO JSON INVALIDO : "+e.getMessage());
         }catch (Exception e){
             e.printStackTrace();
             cambio = "HUBO UN ERROR AL ACTIVAR LA TARJETA";
             System.out.println("Hubo un error al actualizar la tarjeta de credito : "+e.getMessage());
         }
         return cambio;
    }


    /**
     * Este metodo cambia de estado la tarjeta a BL (bloqueado)
     * @param idCard // el id de la tarjeta que se quiere bloquear
     * @return // retorna la respuesta de la  operacion si existe manda el mensaje cambio y si no manda un mensaje
     * que no se encontro tarjeta relacionada con el id.
     */
    public String bloqueoCard(String idCard){
        String cambio = "";
        Card_Entity newCard = new Card_Entity();
        try {
            if(isValidJson(idCard)==true){
                Gson gson = new Gson();// Crear una instancia de Gson para procesar datos JSON
                JsonObject object = gson.fromJson(idCard, JsonObject.class);// Parsear la cadena JSON "cardBalance" a un objeto JsonObject
                String id = object.get("idCard").getAsString(); // Obtener el valor
             if(id.matches("\\d+") && id.length()==16){
                    newCard = cardRepository.buscardCardXId(id);
                    if(newCard!=null){
                        if (!newCard.getState().equals("BL")) {
                            newCard.setState("BL");
                            cardRepository.saveAndFlush(newCard);
                            cambio = "TARJETA BLOQUEADA";
                    }else{
                            cambio = "YA SE ENCUENTRA BLOQUEADA ESTA TARJETA";
                        }
                    }else{
                        cambio = "NO EXISTE EL ID DE LA TARJETA EN LA BASE DE DATOS";
                    }
                }else{
                    cambio = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, SOLO TIPO NUMÉRICO O QUE NO SEA IGUAL A 16 DÍGITOS";
                }
            }else{
                cambio = "NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON";
            }
        }catch (JsonSyntaxException e) {
            e.printStackTrace();
            cambio = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, SOLO TIPO NUMÉRICO O NO INGRESO LOS 16 DÍGITOS DE LA TARJETA";
            System.out.println("FORMATO JSON INVALIDO : "+e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            cambio = "HUBO UN ERROR AL BLOQUEAR LA TARJETA DE CRÉDITO";
            System.out.println("Hubo un error al bloquear la tarjeta de credito : "+e.getMessage());
        }
        return cambio;
    }


    /**
     *  Este metodo realiza la recarga de la tarjeta
     * @param cardBalance recibe por parametro un string que contiene el id de la tarjeta y el saldo a recargar
     * @return retorna un mensaje  del estado de la operacion si se realizo si o no.
     */
    public String recargarBalance(String cardBalance){
        String cambio = "";
        Card_Entity newCard = new Card_Entity();
        try {
            if(isValidJson(cardBalance)==true){
                Gson gson = new Gson();// Crear una instancia de Gson para procesar datos JSON
                JsonObject object = gson.fromJson(cardBalance, JsonObject.class);// Parsear la cadena JSON "cardBalance" a un objeto JsonObject
                String idCard = object.get("idCard").getAsString(); // Obtener el valor de "idCard" como una cadena
                String balance = object.get("balance").getAsString();// Obtener el valor de "balance" como una cadena
                if (esFloatValido(balance) && idCard.matches("\\d+") && idCard.length()==16){ //este condicional valida si es tipo de dato float el y verific si el id card corresponde con el formato numerico
                    newCard = cardRepository.buscardCardXId(idCard); // busca el id y devuelve el registro que encontro por el id
                    if(newCard!=null){
                        if (newCard.getState().equals("AC")) {  // verifica si el registro que encontro es nulo por el id
                            float totalBalance = Float.parseFloat(balance) + newCard.getBalance();
                            newCard.setBalance(totalBalance);
                            cardRepository.saveAndFlush(newCard); // guarda el registro modificado que se le agrego el saldo nuevo
                            cambio = "SU TARJETA SE HA RECARGADO";
                        }else{
                            cambio = "TARJETA BLOQUEADA O INACTIVA";
                        }
                    } else {
                        cambio = "TARJETA NO EXISTE EN LA BASE DE DATOS";
                    }
                }else{
                    cambio = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, DEBE INGRESAR SOLO 16 DÍGITOS PARA EL ID DE LA TARJETA";
                }
            }else{
                cambio = "NO INGRESASTE UN JSON, INGRESA NUEVAMENTE LA INFORMACIÓN COMO JSON";
            }
        }catch (JsonSyntaxException e) {
            e.printStackTrace();
            cambio = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, SOLO TIPO NUMÉRICO O NO INGRESO LOS 16 DÍGITOS DE LA TARJETA";
            System.out.println("FORMATO JSON INVALIDO : "+e.getMessage());
        }
            catch (Exception e){
                e.printStackTrace();
                cambio = "HUBO UN ERROR AL MOMENTO DE RECARGAR SU TARJETA DE CRÉDITO";
                System.out.println("Hubo un error al momento de recargar su tarjeta de credito : "+e.getMessage());
            }

        return cambio;
    }


    /**
     * Este metodo se usa para para buscar la tarjeta y verificar su saldo
     * @param idCard el id de la tarjeta
     * @return retorna el valor obtendo de la consulta , si existe la tarjeta o no
     */
      public Object[] consultarBalance(String idCard){
        Object[] consulta = new Object[3];
        Card_Entity cardConsultada = new Card_Entity();
        try {
            if(idCard.matches("\\d+") && idCard.length()==16){
                cardConsultada = cardRepository.buscardCardXId(idCard);
                if(cardConsultada!=null){
                    consulta[0] = "cardId : "+cardConsultada.getIdCard();
                    consulta[1] = "balance : "+ cardConsultada.getBalance();
                    if(cardConsultada.getState().equals("AC")){
                        consulta[2] = "ESTADO DE TARJETA : ACTIVO";
                    }if(cardConsultada.getState().equals("IN")){
                        consulta[2] = "ESTADO DE TARJETA : INACTIVO";
                    }
                    if(cardConsultada.getState().equals("BL")){
                        consulta[2] = "ESTADO DE TARJETA : BLOQUEADO";
                    }
                }else{
                    consulta[0] = "NO EXISTE LA TARJETA";
                    consulta[1] = "";
                    consulta[2] = "";
                }
            }else{
                consulta[0] = "EL DATO QUE INGRESO NO ES VÁLIDO PARA REALIZAR LA OPERACIÓN, DEBE INGRESAR SOLO 16 DÍGITOS PARA EL ID DE LA TARJETA";
                consulta[1] = "";
                consulta[2] = "";
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("HUBO UN ERROR AL BUSCAR LOS DATOS DE LA TARJETA DE CRÉDITO : "+e.getMessage());
        }
        return consulta;
    }


    // Método para verificar si un String es un número float válido

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


    public boolean isValidJson(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return false; // Cadena nula o vacía no es un JSON válido
        }
        try {
            new Gson().fromJson(jsonString, JsonObject.class);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }

}




}

