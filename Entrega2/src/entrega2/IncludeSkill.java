package entrega2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class IncludeSkill {
	
	public static void handleIncludeSkill(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
        System.out.println("Digite sua Habilidade:");
        String skill = reader.readLine();

        System.out.println("Digite quanto tempo de experiência você tem:");
        String experience = reader.readLine();

        JsonObject requestJson = Utils.createRequest("INCLUDE_SKILL");
        JsonObject data = new JsonObject();
        data.addProperty("skill", skill);
        data.addProperty("experience", experience);
        requestJson.addProperty("token", token);
        requestJson.add("data", data);

        System.out.println("Server recebeu: " + requestJson);
        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println("Server retornou: " + jsonResponse);
    }

}
