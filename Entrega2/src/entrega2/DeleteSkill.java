package entrega2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class DeleteSkill {

public static void handleDeleteSkill(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
    	
    	if (token == null || token.isEmpty()) {
            System.out.println("Por favor, faça login antes de excluir sua conta.");
            return;
        }
    	
    	System.out.println("Digite a Habilidade que quer excluir:");
        String skill = reader.readLine();
 
        JsonObject requestJson = Utils.createRequest("DELETE_SKILL");
        requestJson.addProperty("token", token);
        JsonObject data = new JsonObject();
        data.addProperty("skill", skill);
        requestJson.add("data", data);
  
        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println("Server recebeu: " + requestJson);
        System.out.println("Server retornou: " + jsonResponse);
    }
	
}
