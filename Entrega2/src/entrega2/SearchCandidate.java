package entrega2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SearchCandidate {
    public static void handleSearchCandidate(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {

    	System.out.println("Digite a Habilidade:");
        String skill = reader.readLine();

        System.out.println("Digite quanto tempo de experiência você precisa:");
        String experience = reader.readLine();
        
        System.out.println("Digite o Filtro: (AND/OR): ");
        String filter = reader.readLine();
    	
        JsonObject jsonRequest = Utils.createRequest("SEARCH_CANDIDATE");
        jsonRequest.addProperty("token", token);
        JsonObject data = new JsonObject();

        String [] skills = skill.split(",");
        JsonArray skillArray = new JsonArray();
        for (String skill1 : skills) {
            skillArray.add(skill1);
        }

        data.add("skill", skillArray);
        data.addProperty("experience", experience);
        data.addProperty("filter", filter);
        jsonRequest.add("data", data);
        System.out.println("Client:"+jsonRequest);
        String jsonResponse = Utils.sendRequest(jsonRequest, out, in);
        System.out.println("Server:"+jsonResponse);

        JsonObject dataResponse = Utils.parseJson(jsonResponse).getAsJsonObject("data");
        JsonArray profileSet = dataResponse.getAsJsonArray("profile");
        
        if (Utils.parseJson(jsonResponse).get("status").getAsString().equals("SUCCESS")){
        	
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
}
