package entrega2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GetCompany {
    public static void handleGetCompany(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {


        JsonObject jsonRequest = Utils.createRequest("GET_COMPANY");
        jsonRequest.addProperty("token", token);
        JsonObject data = new JsonObject();

        jsonRequest.add("data", data);
        System.out.println("Client:"+jsonRequest);
        String jsonResponse = Utils.sendRequest(jsonRequest, out, in);
        System.out.println("Server:"+jsonResponse);

        JsonObject dataResponse = Utils.parseJson(jsonResponse).getAsJsonObject("data");
        JsonArray companySet = dataResponse.getAsJsonArray("company");
        
        if (Utils.parseJson(jsonResponse).get("status").getAsString().equals("SUCCESS")){
        	
        	for (int i = 0; i < dataResponse.get("company_size").getAsInt(); i++) {
                JsonObject company = companySet.get(i).getAsJsonObject();
                String name = company.get("name").getAsString();
                String industry = company.get("industry").getAsString();
                String email = company.get("email").getAsString();
                String description = company.get("description").getAsString();
                System.out.println(" / Nome: " + name + " / Industria: " + industry + " / Email: " + email + " / Descrição: " + description);
            }
        	
        }
    }
}
