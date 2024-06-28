package entrega2;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SearchCandidatebyExperience {
    public static void handleSearchCandidatebyExperience(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {

    	System.out.println("Digite a Experiência que busca:");
        String experience = reader.readLine();
    	
        JsonObject jsonRequest = Utils.createRequest("SEARCH_CANDIDATE");
        jsonRequest.addProperty("token", token);
        JsonObject data = new JsonObject();


        data.addProperty("experience", experience);
        jsonRequest.add("data", data);
        System.out.println("Client:"+jsonRequest);
        String jsonResponse = Utils.sendRequest(jsonRequest, out, in);
        System.out.println("Server:"+jsonResponse);

        JsonObject dataResponse = Utils.parseJson(jsonResponse).getAsJsonObject("data");
        JsonArray profileSet = dataResponse.getAsJsonArray("profile");
        System.out.println("Server recebeu: " + jsonRequest);
        System.out.println("Server retornou: " + jsonResponse);

        for (int i = 0; i < dataResponse.get("profile_size").getAsInt(); i++) {
            JsonObject profile = profileSet.get(i).getAsJsonObject();
            String skillName = profile.get("skill").getAsString();
            int experiencia = profile.get("experience").getAsInt();
            int id = profile.get("id").getAsInt();
            int id_user = profile.get("id_user").getAsInt();
            String nome = profile.get("name").getAsString();
            System.out.println(" / ID: " + id + " / Skill: " + skillName + " / Experience: " + experiencia + " / ID_User: " + id_user + " / Nome: " + nome);
        }

    }
}
