package entrega2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class SearchJob {
	
	public static void handleSearchJob(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
        
		System.out.println("Digite sua Habilidade:");
        String skill = reader.readLine();

        System.out.println("Digite quanto tempo de experiência você tem:");
        String experience = reader.readLine();
        
        System.out.println("Digite o Filtro: (AND/OR): ");
        String filter = reader.readLine();

        JsonObject requestJson = Utils.createRequest("SEARCH_JOB");
        JsonObject data = new JsonObject();
        data.addProperty("skill", skill);
        data.addProperty("experience", experience);
        data.addProperty("filter", filter);
        requestJson.addProperty("token", token);
        requestJson.add("data", data);

        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println("Server recebeu: " + requestJson);
        System.out.println("Server retornou: " + jsonResponse);
    }

}
