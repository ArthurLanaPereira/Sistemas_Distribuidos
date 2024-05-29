package entrega2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

public class LogoutClient {
    
    public static String handleLogout(BufferedReader in, PrintWriter out, String token) throws IOException{

        JsonObject requestJson = Utils.createRequest("LOGOUT_CANDIDATE");
        requestJson.addProperty("token", token); 
        JsonObject data = new JsonObject();
        requestJson.add("data", data);

        Utils.sendRequest(requestJson, out);

        String jsonResponse = in.readLine();
        System.out.println(jsonResponse);
        
        return "";
    }
}
