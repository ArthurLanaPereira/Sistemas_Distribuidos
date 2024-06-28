package entrega2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SearchJobbySkill {
    public static void handleSearchJobbySkill(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
    	
    	System.out.println("Digite a Habilidade que pesquisar:"); //Se quiser pesquisar mais de uma, separar por ','
        String skill = reader.readLine();

        JsonObject jsonRequest = Utils.createRequest("SEARCH_JOB");
        jsonRequest.addProperty("token", token);
        JsonObject data = new JsonObject();

        String [] skills = skill.split(",");
        JsonArray skillArray = new JsonArray();
        for (String skill1 : skills) {
            skillArray.add(skill1);
        }

        data.add("skill", skillArray);
        jsonRequest.add("data", data);
        System.out.println("Client:"+jsonRequest);
        String jsonResponse = Utils.sendRequest(jsonRequest, out, in);
        System.out.println("Server:"+jsonResponse);

        JsonObject dataResponse = Utils.parseJson(jsonResponse).getAsJsonObject("data");
        JsonArray jobSet = dataResponse.getAsJsonArray("jobset");

        for (int i = 0; i < dataResponse.get("jobset_size").getAsInt(); i++) {
            JsonObject job = jobSet.get(i).getAsJsonObject();
            String skillName = job.get("skill").getAsString();
            int experiencia = job.get("experience").getAsInt();
            int id = job.get("id").getAsInt();
            System.out.println(" / ID: " + id + " / Skill: " + skillName + " / Experience: " + experiencia);
        }

    }
}
