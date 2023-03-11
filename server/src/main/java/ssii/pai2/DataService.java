package ssii.pai2;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

import org.springframework.stereotype.Service;

import com.github.cliftonlabs.json_simple.JsonObject;

@Service
public class DataService {
    
    //Esta función se va a encargar de guardar en una carpeta un nonce, puede ser por parte del cliente 
    //como del servidor.
    public void SaveNonce(String nonce, String host) throws IOException{
        
        //Accedemos a la ruta de la carpeta
        String rutaArchivo = ".\\PAI-2-SecurityTeam-13\\server\\src\\main\\resources\\nonces" + host + "\\" + nonce;
        System.out.println(rutaArchivo);
        File archivo = new File(rutaArchivo);
        
        //Guardamos el nonce en dicha carpeta.
        archivo.createNewFile();
        
        //Y escribimos en el interior de la carpeta el nombre del log.
        FileWriter escritor = new FileWriter(archivo);
        escritor.write(nonce);
        escritor.close();
    }

    //Esta función se va a encargar de generar un nonce en la parte del servidor.
    public String GenerarNonce() throws IOException {
        SecureRandom random = new SecureRandom();
        // Tamaño del nonce en bytes
        byte[] nonce = new byte[16]; 
        random.nextBytes(nonce);
        //Lo codificamos en Base64
        String nonceBase64 = Base64.getEncoder().encodeToString(nonce);
        //Procedemos a guardar el nonce.
        SaveNonce(nonceBase64.replace("/", "_"), "Servidor");
        
        return nonceBase64;
    }

    //Esta funcion se va a encargar de acceder al nonce que se encuentra almacenado en una carpeta y devolverlo
    //para trabajar con él.
    public String extraerNonce(String host){
        
        List<String> l = new ArrayList<String>();
        String ruta = ".\\PAI-2-SecurityTeam-13\\server\\src\\main\\resources\\";
        File folder = new File(ruta + "nonces" + host + "\\");


        File[] files = folder.listFiles();

        for (File file : files) {
            l.add(file.getName());
        }

        return l.get(0);
    }

    //Esta función se va a encargar de eliminar un nonce que se encuentra almacenado en una carpeta
    public void eliminarNonce(String host){
        String ruta = ".\\PAI-2-SecurityTeam-13\\server\\src\\main\\resources\\";
        File folder = new File(ruta + "nonces" + host + "\\");


        File[] files = folder.listFiles();

        for (File file : files) {
            file.delete();
        }
    }


    //Esta función se encarga realizar el hmac usando SHA-256.
    public String hashing(String mensaje,String nonce,String clave) throws NoSuchAlgorithmException, InvalidKeyException {
        
        //Le concatenamos al mensaje original el nonce ya sea el del cliente o el del servidor.
        String mensajeFinal = mensaje + nonce;
        
        //Generamos la clave secreta
        byte[] keyBytes = clave.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
        
        //Realizamos hashing.
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        mac.update(mensajeFinal.getBytes());
        byte[] macResult = mac.doFinal();
        
        //Encodeamos el hmac a base64 y lo devolvemos.
        String mac64 = Base64.getEncoder().encodeToString(macResult);
        return mac64;
    
}
    
    //Esta función se encarga de comparar los hashes para saber si se ha modificado la integridad del mensaje.
    //Una vez se ha realizado la comprobación, se genera un log y se devuelve un hmac con la respuesta usando el nonce del cliente.
    public Map<String,String> CompareHash(String hmac,String hmacCliente,String nonceCliente,String clave) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        Map<String,String> res = new HashMap<String,String>();
        
            //En el caso de que sean iguales registramos que todo ha salido bien, generamos el log y devolvemos la respuesta correspondiente.
            if(hmacCliente.equals(hmac)) {
                
                //Especificamos la ruta del log.
                String nombreLog =hmacCliente.replace("/", "_") + "-" +LocalDateTime.now().toString().replace(":", "_") + ".log";
                String rutaArchivo = ".\\PAI-2-SecurityTeam-13\\server\\src\\main\\resources\\acceptedLogs" + "\\" + nombreLog;
                System.out.println(rutaArchivo);
                File archivo = new File(rutaArchivo);
                
                //Creamos el log.
                archivo.createNewFile();
                
                FileWriter escritor = new FileWriter(archivo);
                escritor.write(nombreLog);
                escritor.close();


                //Generamos la respuesta.
                String respuesta ="200 OK, ";
                res.put(respuesta, hashing(respuesta,nonceCliente,clave));
                
                //Tras haber almacenado el mensaje, procedemos a eliminar el nonce del cliente y del servidor.
                eliminarNonce("Servidor");
                eliminarNonce("Cliente");

                return res;             	
                
            //En el caso de que no sean iguales registramos que se ha modificado la integridad del mensaje, generamos el log y devolvemos la respuesta correspondiente.
            }else {
                
                //Especificamos la ruta del log.
                String nombreLog =hmacCliente.replace("/", "_") + "-" +LocalDateTime.now().toString().replace(":", "_") + ".log";
                String rutaArchivo = ".\\PAI-2-SecurityTeam-13\\server\\src\\main\\resources\\deniedLogs" + "\\"+nombreLog;
                File archivo = new File(rutaArchivo);
                
                //Creamos el log.
                archivo.createNewFile();	
                
                //Generamos la respuesta.
                String respuesta = "Se ha alterado la integridad del mensaje, ";
                res.put(respuesta, hashing(respuesta,nonceCliente,clave));

                //Tras haber almacenado el mensaje, procedemos a eliminar el nonce del cliente y del servidor.
                eliminarNonce("Servidor");
                eliminarNonce("Cliente");

                return res;
            }
    }

    //Esta función se encarga de devolver los log de los mensajes que han sido aceptados y por lo tanto
    //se ha mantenido su integridad.
    public List<String> logsAccepted(){
        List<String> l = new ArrayList<String>();
        String ruta = ".\\PAI-2-SecurityTeam-13\\server\\src\\main\\resources\\";
        File folder = new File(ruta + "acceptedLogs\\");


        File[] files = folder.listFiles();

        for (File file : files) {
            l.add(file.getName());
        }
        return l;
    }
    
    //Esta función se encarga de devolver los log de los mensajes que han sido denegados y por lo tanto
    //no se ha mantenido su integridad.
    public List<String> logsDenied(){
        List<String> l = new ArrayList<String>();
        String ruta = ".\\PAI-2-SecurityTeam-13\\server\\src\\main\\resources\\";
        File folder = new File(ruta + "deniedLogs\\");


        File[] files = folder.listFiles();

        for (File file : files) {
            l.add(file.getName());
        }
        return l;
    }

    //Esta función se trata de un JSON que devuelve todos los logs de los mensajes que se han realizado
    //entre el cliente y el servidor.
    public JsonObject getAllLogs() {
        List<String> logsAcep = logsAccepted();
        List<String> logsDeni = logsDenied();
        JsonObject json = new JsonObject();

        for (int log = 0; log < logsAcep.size(); log++) {
            json.put("Log Accepted"+ " " + log , logsAcep.get(log));
        }

        for (int log = 0; log < logsDeni.size(); log++) {
            json.put("Log Denied"+ " " + log , logsDeni.get(log));
        }

        return json;
      }

      //Esta función es un JSON que se encarga de devolver el KPI de los mensajes, y además nos muestra
      //el número total de mensajes que se han enviado.
      public JsonObject getKPI() {

        List<String> logsAcep = logsAccepted();
        List<String> logsDeni = logsDenied();
        JsonObject json = new JsonObject();

        Integer totalLogs = logsAcep.size() + logsDeni.size();
        json.put("Número total de envíos", totalLogs);
        Float KPI = (float) (logsAcep.size())/totalLogs;
        json.put("KPI", KPI);

        return json;
      }
}

  

