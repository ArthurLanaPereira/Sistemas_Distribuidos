package entrega2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LoginClient {
	
	public static String /*void*/ handleLogin(BufferedReader reader, PrintWriter out, BufferedReader in) throws IOException {
    	
        System.out.println("Digite o endereço de email:");
        String email = reader.readLine();

        System.out.println("Digite a senha:");
        String password = reader.readLine();

        JsonObject requestJson = Utils.createRequest("LOGIN_CANDIDATE");
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("password", password);
        requestJson.add("data", data);

        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println(jsonResponse);
        //String token = Utils.parseJson(jsonResponse).get("data").getAsJsonObject().get("token").getAsString();
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
        
        /*if (response.get("status").getAsString().equals("SUCCESS")) {
            return response.getAsJsonObject("data").get("token").getAsString();
        } else {
            return null;
        }*/
    }
        
    //}
//}

