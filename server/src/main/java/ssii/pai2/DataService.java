package ssii.pai2;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

import org.springframework.stereotype.Service;

import com.github.cliftonlabs.json_simple.JsonObject;

@Service
public class DataService {

  public String generarHash(DataDTO data) throws IOException{
    String banco = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    System.out.println(data);
    for (int i = 0; i < data.getToken().length(); i++) {
        if(!banco.contains(String.valueOf(data.getToken().charAt(i)))) {
            return "Caracter no permitido";
        }
    }
    
    String cadenaBase64 = "";
    String entradaOriginal = data.getFichero();
    String cadenaCodificada = buscarFichero(entradaOriginal);
    
    if(data.getReto() == RetoEnum.RETO1) {
    cadenaBase64 = cadenaCodificada + data.getToken();
    }else if(data.getReto() == RetoEnum.RETO2){
    cadenaBase64 = data.getToken() + cadenaCodificada;
    } else {
      return "Tipo de reto no permitido";
    }

    String hash = hashUtils.sha256(cadenaBase64);
    
  
    return hash;
  }

  public static ArrayList<String> findAllFilesInFolder(File folder) {
		ArrayList<String> listaFicheros = new ArrayList<>();
        for (File file : folder.listFiles()) {
			if (!file.isDirectory()) {
                listaFicheros.add(file.toString());
            } else {
				findAllFilesInFolder(file);
			}
		}
        return listaFicheros;
	}

    public static String buscarFichero(String nombre) throws IOException{
        
        File folder = new File("./PAI-1/pai1/src/main/resources/Ficheros");

        ArrayList<String> listaFicheros = findAllFilesInFolder(folder);
        int numFicheros = listaFicheros.size();
        Collections.sort(listaFicheros);
        String url = ".\\PAI-1\\pai1\\src\\main\\resources\\Ficheros\\";
        nombre = url.concat(nombre).concat(".txt");
        String fichero = busquedaBinaria(listaFicheros, nombre, 0, numFicheros-1);
        Path filePath = Path.of(fichero);
        String content = Files.readString(filePath, StandardCharsets.UTF_8);

        return (Base64.getEncoder().encodeToString(content.getBytes()));
    }




    public static String busquedaBinaria(ArrayList<String> res, String nombre, int izquierda, int derecha){
        if (izquierda > derecha){
            return ("No existe");
        }

        int indiceElemMedio = (int) Math.floor((izquierda+derecha) / 2);
        String archivoMedio = res.get(indiceElemMedio).toString();


        int comparacion = nombre.compareTo(archivoMedio);

        if(comparacion == 0){
            return archivoMedio;
        }

        if(comparacion < 0){
            derecha = indiceElemMedio - 1;
            String busqueda = busquedaBinaria(res, nombre, izquierda, derecha);
            return busqueda;
        }

        else{
            izquierda = indiceElemMedio + 1;
            String busqueda = busquedaBinaria(res, nombre, izquierda, derecha);
            return busqueda;
        }

    }

  public JsonObject getAll() {
    JsonObject json = new JsonObject();
    json.put("fichero1", "clients-emails");
    json.put("fichero2", "presupuesto2020");
    json.put("fichero3", "coches");
    json.put("fichero4", "notasSSII");
    json.put("fichero5", "preciosIVA");
    json.put("fichero6", "confidencial");
    return json;
  }
  
}
