package entrega2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class UpdateCompany {
	
    public static void handleUpdateAccountCompany(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {
    	
    	if (token == null || token.isEmpty()) {
            System.out.println("Por favor, faça login antes de atualizar sua conta.");
            return;
        }
    	   	
        System.out.println("Digite o novo endereço de email:");
        String email = reader.readLine();

        System.out.println("Digite a nova senha:");
        String password = reader.readLine();

        System.out.println("Digite o novo nome:");
        String name = reader.readLine();
        
        System.out.println("Digite a indústria:");
        String industry = reader.readLine();
        
        System.out.println("Digite descrição:");
        String description = reader.readLine();

        JsonObject requestJson = Utils.createRequest("UPDATE_ACCOUNT_RECRUITER");
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("password", password);
        data.addProperty("name", name);
        data.addProperty("industry", industry);
        data.addProperty("description", description);
        requestJson.addProperty("token", token);
        requestJson.add("data", data);

        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println(jsonResponse);
    }
}