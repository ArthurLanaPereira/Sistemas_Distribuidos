package entrega2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class UpdateJob {
    public static void handleUpdateJob(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
    	
    	if (token == null || token.isEmpty()) {
            System.out.println("Por favor, faça login antes de atualizar sua conta.");
            return;
        }
    	   	
    	System.out.println("Digite o id do Trabalho que será alterado:");
        String idjob = reader.readLine();
    	   	
        System.out.println("Digite a nova Habilidade do Trabalho:");
        String skill = reader.readLine();

        System.out.println("Digite o tempo de experiência para este Trabalho:");
        String experience = reader.readLine();

        JsonObject requestJson = Utils.createRequest("UPDATE_JOB");
        JsonObject data = new JsonObject();
        data.addProperty("id", idjob);
        data.addProperty("skill", skill);
        data.addProperty("experience", experience);
        requestJson.addProperty("token", token);
        requestJson.add("data", data);

        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println("Server recebeu: " + requestJson);
        System.out.println("Server retornou: " + jsonResponse);
    }
}