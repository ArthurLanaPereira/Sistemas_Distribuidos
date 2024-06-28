package entrega2;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SearchJobbyExperience {
    public static void handleSearchJobbyExperience(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {

    	System.out.println("Digite a Experiência que buscar:");
        String experience = reader.readLine();
    	
        JsonObject jsonRequest = Utils.createRequest("SEARCH_JOB");
        jsonRequest.addProperty("token", token);
        JsonObject data = new JsonObject();


        data.addProperty("experience", experience);
        jsonRequest.add("data", data);
        System.out.println("Client:"+jsonRequest);
        String jsonResponse = Utils.sendRequest(jsonRequest, out, in);
        System.out.println("Server:"+jsonResponse);

        JsonObject dataResponse = Utils.parseJson(jsonResponse).getAsJsonObject("data");
        JsonArray jobSet = dataResponse.getAsJsonArray("jobset");
        System.out.println("Server recebeu: " + jsonRequest);
        System.out.println("Server retornou: " + jsonResponse);

        for (int i = 0; i < dataResponse.get("jobset_size").getAsInt(); i++) {
            JsonObject job = jobSet.get(i).getAsJsonObject();
            String skillName = job.get("skill").getAsString();
            int experiencia = job.get("experience").getAsInt();
            int id = job.get("id").getAsInt();
            System.out.println(" / ID: " + id + " / Skill: " + skillName + " / Experience: " + experiencia);
        }

    }
}
