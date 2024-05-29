package entrega2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class DeleteClient {

public static void handleDeleteAccount(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
    	
    	if (token == null || token.isEmpty()) {
            System.out.println("Por favor, faça login antes de excluir sua conta.");
            return;
        }
 
        JsonObject requestJson = Utils.createRequest("DELETE_ACCOUNT_CANDIDATE");
        requestJson.addProperty("token", token);
        JsonObject data = new JsonObject();
        requestJson.add("data", data);
  
        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println(jsonResponse);
    }
	
}
