package entrega2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class SignupCompany {
	
    public static void handleSignupCompany(BufferedReader reader, PrintWriter out, BufferedReader in) throws IOException {
        System.out.println("Digite o endereço de email:");
        String email = reader.readLine();

        System.out.println("Digite a senha:");
        String password = reader.readLine();

        System.out.println("Digite o nome:");
        String name = reader.readLine();
        
        System.out.println("Digite a industria:");
        String industry = reader.readLine();
        
        System.out.println("Digite a descrição:");
        String description = reader.readLine();

        JsonObject requestJson = Utils.createRequest("SIGNUP_RECRUITER");
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("password", password);
        data.addProperty("name", name);
        data.addProperty("industry", industry);
        data.addProperty("description", description);
        requestJson.add("data", data);
        
        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println("Server recebeu: " + requestJson);
        System.out.println("Server retornou: " + jsonResponse);
    }
}
