package ssii.pai2;
import com.github.cliftonlabs.json_simple.JsonObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class DataController {

final static String clave = "mi_clave_secreta";


  @Autowired
  DataService dataService;

  //Este end-point se encarga de recibir el nonce enviado por el cliente y responder con su propio nonce.
  @PostMapping("/requestNonce")
  @CrossOrigin(origins = "*")
  public String getData(@RequestBody @Valid String nonce) throws IOException {
    dataService.SaveNonce(nonce, "Cliente");
    return dataService.GenerarNonce();
  }

  //Este end-point se encarga de recibir el mensaje de una transferencia y además un HMAC por parte del cliente.
  //Este end-point responderá con un 200 OK si se ha mantenido la integridad, o con un mensaje de error si no 
  //se ha mantenido dicha integridad.
  @PostMapping("/requestMessage")
  @CrossOrigin(origins = "*")
  public Map<String,String> getData(@RequestBody @Valid DataDTO transferencia) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
    String nonceServidor = dataService.extraerNonce("Servidor");
    DataDTO transferData = new DataDTO(transferencia.getCuentaOrigen(), transferencia.getCuentaOrigen(), transferencia.getCantidad());
    String hMacServidor = dataService.hashing(transferData.toString(), nonceServidor, clave);
    String nonceCliente = dataService.extraerNonce("Cliente");
    Map<String,String> res = dataService.CompareHash(hMacServidor, transferencia.getClientHMAC(), nonceCliente, clave);

    return res;
  }

  //Este end-point se encarga de mostrar todos los logs de los mensajes realizados hasta el momento.
  @GetMapping("/logs")
  @CrossOrigin(origins = "http://localhost:5173")
  public JsonObject getAllLogs() {
    return dataService.getAllLogs();
  }

  //Este end-point se encarga de mostrar el número total de mensajes enviados y el KAI de estos mensajes. 
  //El KAI se trata del nº de mensajes enviados exitosamente / nº total de mensajes enviados.
  @GetMapping("/KAI")
  @CrossOrigin(origins = "http://localhost:5173")
  public JsonObject getKAI() {
    return dataService.getKPI();
  }

}
