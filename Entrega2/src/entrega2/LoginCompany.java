package entrega2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LoginCompany {
	
	public static String handleLoginCompany(BufferedReader reader, PrintWriter out, BufferedReader in) throws IOException {
    	
        System.out.println("Digite o endereço de email:");
        String email = reader.readLine();

        System.out.println("Digite a senha:");
        String password = reader.readLine();

        JsonObject requestJson = Utils.createRequest("LOGIN_RECRUITER");
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("password", password);
        requestJson.add("data", data);

        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println("Server recebeu: " + requestJson);
        System.out.println("Server retornou: " + jsonResponse);
        JsonObject response = Utils.parseJson(jsonResponse);
        
        String status = response.get("status").getAsString();

        switch (status) {
            case "SUCCESS":
                return response.getAsJsonObject("data").get("token").getAsString();
            case "INVALID_PASSWORD":
                System.out.println("Senha inválida. Por favor, tente novamente.");
                break;
            case "USER_NOT_FOUND":
                System.out.println("Usuário não encontrado. Por favor, verifique suas credenciais.");
                break;
            default:
                System.out.println("Erro ao fazer login. Tente novamente mais tarde.");
        }
        return null;
    }
        
    }


