package entrega2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SearchableJob {
    public static void handleSearchableJob(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {

    	System.out.println("Digite o ID do trabalho:");
        String idJob = reader.readLine();
        
        System.out.println("Digite se é Buscável ou não: (YES/NO): ");
        String searchable = reader.readLine();
    	
        JsonObject requestJson = Utils.createRequest("SET_JOB_SEARCHABLE");
        requestJson.addProperty("token", token);
        JsonObject data = new JsonObject();

        data.addProperty("id", idJob);
        data.addProperty("searchable", searchable);
        requestJson.add("data", data);
        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println("Server recebeu: " + requestJson);
        System.out.println("Server retornou: " + jsonResponse);

    }
}
