package entrega2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ChooseCandidate {
    public static void handleChooseCandidate(BufferedReader reader, PrintWriter out, BufferedReader in, String token) throws IOException {

    	System.out.println("Digite o ID do candidato selecionado:");
        String idCandidate = reader.readLine();

        JsonObject requestJson = Utils.createRequest("CHOOSE_CANDIDATE");
        requestJson.addProperty("token", token);
        JsonObject data = new JsonObject();

        data.addProperty("id_user", idCandidate);
        requestJson.add("data", data);
        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println("Server recebeu: " + requestJson);
        System.out.println("Server retornou: " + jsonResponse);

    }
}
