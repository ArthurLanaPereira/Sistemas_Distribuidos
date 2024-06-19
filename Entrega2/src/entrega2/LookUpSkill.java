package entrega2;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class LookUpSkill {
    public static void handleLookUpSkill(BufferedReader reader, BufferedReader in, PrintWriter out, String token) throws IOException {
    	
    	if (token == null || token.isEmpty()) {
    	    System.out.println("Você precisa estar logado para executar esta operação.");
    	    return;
    	}
    	
    	System.out.println("Digite sua Habilidade:");
        String skill = reader.readLine();
    	
        JsonObject requestJson = Utils.createRequest("LOOKUP_SKILL");
        requestJson.addProperty("token", token);
        JsonObject data = new JsonObject();
        data.addProperty("skill", skill);
        requestJson.add("data", data);

        Utils.sendRequest(requestJson, out);

        String jsonResponse = in.readLine();
        System.out.println("Server recebeu: " + requestJson);
        System.out.println("Server retornou: " + jsonResponse);
    }
}