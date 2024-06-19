package entrega2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class DeleteJob {

public static void handleDeleteJob(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
    	
    	if (token == null || token.isEmpty()) {
            System.out.println("Por favor, faça login antes de excluir sua conta.");
            return;
        }
    	
    	System.out.println("Digite o id do Trabalho:");
        String idjob = reader.readLine();
 
        JsonObject requestJson = Utils.createRequest("DELETE_JOB");
        requestJson.addProperty("token", token);
        JsonObject data = new JsonObject();
        data.addProperty("id", idjob);
        requestJson.add("data", data);
  
        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println("Server recebeu: " + requestJson);
        System.out.println("Server retornou: " + jsonResponse);
    }
	
}
