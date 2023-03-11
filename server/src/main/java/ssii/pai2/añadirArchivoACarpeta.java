package ssii.pai2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.cliftonlabs.json_simple.JsonObject;

public class añadirArchivoACarpeta {
    
    public static void main (String[] args){

    }

    public List<String> logs(){
        List<String> l = new ArrayList<String>();
        File folder = new File("log/");

        File[] files = folder.listFiles();

        for (File file : files) {
            l.add(file.getName());
        }
        return l;
    }
        


    public JsonObject getAll() {
        List<String> l = logs();
        JsonObject json = new JsonObject();
        for (int log = 0; log < l.size(); log++) {
            json.put("Log"+ " " + log , l.get(log));
        }
        return json;
      }
   
}
