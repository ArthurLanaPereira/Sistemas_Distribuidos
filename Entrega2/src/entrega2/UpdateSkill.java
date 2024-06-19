package entrega2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class UpdateSkill {
    public static void handleUpdateSkill(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
    	
    	if (token == null || token.isEmpty()) {
            System.out.println("Por favor, faça login antes de atualizar sua conta.");
            return;
        }
    	   	
        System.out.println("Digite a Habilidade que deseja alterar:");
        String skill = reader.readLine();
        
        System.out.println("Digite qual a nova Habilidade:");
        String newSkill = reader.readLine();

        System.out.println("Digite o tempo de experiência:");
        String experience = reader.readLine();

        JsonObject requestJson = Utils.createRequest("UPDATE_SKILL");
        JsonObject data = new JsonObject();
        data.addProperty("skill", skill);
        data.addProperty("newSkill", newSkill);
        data.addProperty("experience", experience);
        requestJson.addProperty("token", token);
        requestJson.add("data", data);

        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println("Server recebeu: " + requestJson);
        System.out.println("Server retornou: " + jsonResponse);
    }
}