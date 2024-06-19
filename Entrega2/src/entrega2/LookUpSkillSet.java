package entrega2;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class LookUpSkillSet {
    public static void handleLookUpSkillSet(BufferedReader in, PrintWriter out, String token) throws IOException {
    	
    	if (token == null || token.isEmpty()) {
    	    System.out.println("Você precisa estar logado para executar esta operação.");
    	    return;
    	}
    	
        JsonObject requestJson = Utils.createRequest("LOOKUP_SKILLSET");
        requestJson.addProperty("token", token);
        JsonObject data = new JsonObject();
        requestJson.add("data", data);

        Utils.sendRequest(requestJson, out);

        String jsonResponse = in.readLine();
        System.out.println("Server recebeu: " + requestJson);
        System.out.println("Server retornou: " + jsonResponse);
    }
}