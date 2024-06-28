package entrega2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class AvailableJob {
    public static void handleAvailableJob(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {

    	System.out.println("Digite o ID do trabalho:");
        String idJob = reader.readLine();
        
        System.out.println("Digite se está Disponível ou não: (YES/NO): ");
        String available = reader.readLine();
    	
        JsonObject requestJson = Utils.createRequest("SET_JOB_AVAILABLE");
        requestJson.addProperty("token", token);
        JsonObject data = new JsonObject();

        data.addProperty("id", idJob);
        data.addProperty("available", available);
        requestJson.add("data", data);
        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println("Server recebeu: " + requestJson);
        System.out.println("Server retornou: " + jsonResponse);

    }
}
