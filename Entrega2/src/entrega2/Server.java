package entrega2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class Server extends Thread{
		private ServerSocket serverSocket;
		private Socket clientSocket;
	    private BufferedWriter fileWriter;
	    public Server(Socket clientSoc, BufferedWriter writer) {
	        clientSocket = clientSoc;
	        fileWriter = writer;
	        start();
	    }
	    public class JWTValidator {
	        private static final String TOKEN_KEY = "DISTRIBUIDOS";
	        private static final Algorithm algorithm = Algorithm.HMAC256(TOKEN_KEY);
	        private static final JWTVerifier verifier = JWT.require(algorithm).build();

	        public static String generateToken(int id, String role) {
	            return JWT.create()
	                    .withClaim("id", id)
	                    .withClaim("role", role)
	                    .sign(algorithm);
	        }

	        public static int getIdClaim(String token) throws JWTVerificationException {
	            DecodedJWT jwt = verifier.verify(token);
	            return jwt.getClaim("id").asInt();
	        }

	        public static String getRoleClaim(String token) throws JWTVerificationException {
	            DecodedJWT jwt = verifier.verify(token);
	            return jwt.getClaim("role").asString();
	        }
	    }

	    @Override
	    public void run() {
	        BufferedReader in = null;
	        PrintWriter out = null;

	        try {
	            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	            out = new PrintWriter(clientSocket.getOutputStream(), true);

	            boolean running = true;
	            while (running) {
	                String jsonMessage = in.readLine();

	                if (jsonMessage != null) {
	                    fileWriter.write(jsonMessage);
	                    fileWriter.newLine();
	                    fileWriter.flush();

	                    JsonObject requestJson = Utils.parseJson(jsonMessage);
	                    String operation = requestJson.get("operation").getAsString();

	                    switch (operation) {
	                    
	                        case "LOGIN_CANDIDATE":
	                            handleLogin(requestJson, out);
	                            break;
	                            
	                        case "LOGIN_RECRUITER":
	                            handleLoginCompany(requestJson, out);
	                            break;
	                            
	                        case "SIGNUP_CANDIDATE":
	                            handleSignup(requestJson, out);
	                            break;
	                            
	                        case "SIGNUP_RECRUITER":
	                            handleSignupCompany(requestJson, out);
	                            break;
	                            
	                        case "UPDATE_ACCOUNT_CANDIDATE":
	                            handleUpdate(requestJson, out);
	                            break;
	                            
	                        case "UPDATE_ACCOUNT_RECRUITER":
	                            handleUpdateCompany(requestJson, out);
	                            break;
	                            
	                        case "DELETE_ACCOUNT_CANDIDATE":
	                            handleDelete(requestJson, out);
	                            break;
	                            
	                        case "DELETE_ACCOUNT_RECRUITER":
	                            handleDeleteCompany(requestJson, out);
	                            break;
	                            
	                        case "LOGOUT_CANDIDATE":
	                            handleLogout(requestJson, out);
	                            break;
	                            
	                        case "LOGOUT_RECRUITER":
	                            handleLogoutCompany(requestJson, out);
	                            break;
	                        
	                        case "LOOKUP_ACCOUNT_CANDIDATE":
	                        	handleLookup(requestJson, out);
	                            break;
	                            
	                        case "LOOKUP_ACCOUNT_RECRUITER":
	                        	handleLookupCompany(requestJson, out);
	                            break;
	                        
	                        case "INCLUDE_SKILL":
	                        	handleIncludeSkill(requestJson, out);
	                            break;
	                        
	                        case "LOOKUP_SKILL":
	                        	handleLookupSkill(requestJson, out);
	                            break;
	                           
	                        case "LOOKUP_SKILLSET":
	                        	handleLookupSkillSet(requestJson, out);
	                            break;
	                            
	                        case "DELETE_SKILL":
	                        	handleDeleteSkill(requestJson, out);
	                            break;
	                            
	                        case "UPDATE_SKILL":
	                        	handleUpdateSkill(requestJson, out);
	                            break;
	                            
	                        case "SEARCH_JOB":
	                        	handleSearchJob(requestJson, out);
	                            break;
	                            
	                        case "INCLUDE_JOB":
	                        	handleIncludeJob(requestJson, out);
	                            break;
	                            
	                        case "LOOKUP_JOB":
	                        	handleLookupJob(requestJson, out);
	                            break;
	                            
	                        case "LOOKUP_JOBSET":
	                        	handleLookupJobSet(requestJson, out);
	                            break;
	                            
	                        case "DELETE_JOB":
	                        	handleDeleteJob(requestJson, out);
	                            break;
	                            
	                        case "UPDATE_JOB":
	                        	handleUpdateJob(requestJson, out);
	                            break;
	                    }
	                }
	            }
	            
	        } catch (IOException | SQLException e) {
	            System.err.println("Erro no servidor: " + e.getMessage());
	        } finally {
	            try {
	                if (in != null) in.close();
	                if (out != null) out.close();
	                if (fileWriter != null) fileWriter.close();
	                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
	            } catch (IOException e) {
	                System.err.println("Erro ao fechar recursos: " + e.getMessage());
	            }
	        }
	    }

	    // Usuário
	    
	    private void handleLogin(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String email = data.get("email").getAsString();
	        String password = data.get("password").getAsString();

	        if(data.get("email").getAsString().equals("") || data.get("password").getAsString().equals("")) {
	        	
	        	JsonObject responseJson = Utils.createResponse("LOGIN_CANDIDATE", "INVALID_LOGIN", "");
	        	System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	            out.println(Utils.toJsonString(responseJson));
                return;
	        }
	        
	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM candidate WHERE email = ? AND password = ?");
	        st.setString(1, email);
	        st.setString(2, password);

	        ResultSet rs;
	        rs = st.executeQuery();


	        if (rs.next()) {
	            int idcandidate = Integer.parseInt(rs.getString("idcandidate"));
	            String token = JWTValidator.generateToken(idcandidate, "CANDIDATE");
	            JsonObject responseJson = Utils.createResponse("LOGIN_CANDIDATE", "SUCCESS", token);
	            System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	            out.println(Utils.toJsonString(responseJson));
	            return;

	        }

	        JsonObject responseData = new JsonObject();
	        JsonObject responseJson = Utils.createResponse("LOGIN_CANDIDATE", "INVALID_LOGIN", "");
	        responseJson.remove("data"); 
	        responseJson.add("data", responseData);
	        System.out.println("Server recebeu: " + requestJson);
            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	        out.println(Utils.toJsonString(responseJson));
	    }

	    private void handleSignup(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String email = data.get("email").getAsString();
	        String password = data.get("password").getAsString();
	        String name = data.get("name").getAsString();
	        PreparedStatement st;
	        ResultSet rs;

	        if(data.get("email").getAsString().equals("") || data.get("password").getAsString().equals("") || data.get("name").getAsString().equals("")) {
	        	
	        	JsonObject responseJson = Utils.createResponse("SIGNUP_CANDIDATE", "INVALID_FIELD", "");
	        	System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
                out.println(Utils.toJsonString(responseJson));
                return;
	        }
	        
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM candidate WHERE email = ?");
            st.setString(1, email);

            rs = st.executeQuery();

            if (rs.next()) {
                JsonObject Response = Utils.createResponse("SIGNUP_CANDIDATE", "USER_EXISTS", "");
                System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(Response));
                out.println(Utils.toJsonString(Response));
                return;
            }
            else {

                st = ConnectionBD.conectaBD().prepareStatement("INSERT INTO candidate (email, name, password) VALUES (?, ?, ?)");
                st.setString(1, email);
                st.setString(2, name);
                st.setString(3, password);
                st.executeUpdate();

            }
		        JsonObject responseData = new JsonObject();
		        JsonObject responseJson = Utils.createResponse("SIGNUP_CANDIDATE", "SUCCESS", "");
		        responseJson.remove("data"); 
		        responseJson.add("data", responseData);
		        System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
		        out.println(Utils.toJsonString(responseJson));
	        }
	    
	    private void handleLookup(JsonObject requestJson, PrintWriter out) throws IOException, SQLException{
	        
	    	String token = requestJson.get("token").getAsString();
	        int idcandidate = JWTValidator.getIdClaim(token);
	        
	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM candidate WHERE idcandidate = ?");
	        st.setInt(1, idcandidate);

	        ResultSet rs;
	        rs = st.executeQuery();

	        if (rs.next()) {
	            String email = rs.getString("email");
	            String password = rs.getString("password");
	            String name = rs.getString("name");
	            JsonObject data = new JsonObject();
	            data.addProperty("email",email);
	            data.addProperty("password",password);
	            data.addProperty("name",name);

	            JsonObject responseJson = Utils.createResponse("LOOKUP_ACCOUNT_CANDIDATE", "SUCCESS", "");
	            responseJson.add("data",data);
	            System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	            out.println(Utils.toJsonString(responseJson));
	            return;

	        }
	    }
	    
	    private void handleUpdate(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        
	        if ((data.get("email").getAsString() == null || data.get("email").getAsString().isEmpty() ) || (data.get("password").getAsString() == null || data.get("password").getAsString().isEmpty()) || (data.get("name").getAsString() == null || data.get("name").getAsString().isEmpty())) {
	            JsonObject responseJson = Utils.createResponse("UPDATE_ACCOUNT_CANDIDATE", "INVALID_FIELD", "");
	            System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	            out.println(Utils.toJsonString(responseJson));
	            return;
	        }
	        
	        String token = requestJson.get("token").getAsString();
	        String email = data.get("email").getAsString();
	        String password = data.get("password").getAsString();
	        String name = data.get("name").getAsString();

	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM candidate WHERE email = ?");
	        st.setString(1, email);

	        ResultSet rs;
	        rs = st.executeQuery();

	        if (rs.next()) {
	            JsonObject Response = Utils.createResponse("UPDATE_ACCOUNT_CANDIDATE", "INVALID_EMAIL", "");
	            System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(Response));
	            out.println(Utils.toJsonString(Response));
	            return;
	        }

	        int idcandidate = JWTValidator.getIdClaim(token);
	        
	        st = ConnectionBD.conectaBD().prepareStatement("UPDATE candidate SET email = ?, name = ?, password = ? WHERE idcandidate = ?");
            st.setString(1, email);
            st.setString(2, name);
            st.setString(3, password);
            st.setInt(4, idcandidate);
            st.executeUpdate();

            JsonObject responseJson = Utils.createResponse("UPDATE_ACCOUNT_CANDIDATE", "SUCCESS", "");
            System.out.println("Server recebeu: " + requestJson);
            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
            out.println(Utils.toJsonString(responseJson));
	    }
	    
	    private void handleDelete(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        //JsonObject data = requestJson.getAsJsonObject("data");
	        String token = requestJson.get("token").getAsString();
	        int idcandidate = JWTValidator.getIdClaim(token);
	        
	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("DELETE FROM candidate WHERE idcandidate = ?");
	        st.setInt(1, idcandidate);
	        st.executeUpdate();

	        JsonObject responseJson = Utils.createResponse("DELETE_ACCOUNT_CANDIDATE", "SUCCESS", "");
	        System.out.println("Server recebeu: " + requestJson);
            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	        out.println(Utils.toJsonString(responseJson));
	    }
	  
	    public void handleLogout(JsonObject requestJson, PrintWriter out) throws IOException {
	    	
	    	String token = requestJson.get("token").getAsString();	
	    	JsonObject accountData = new JsonObject();
	        JsonObject responseJson = Utils.createResponse("LOGOUT_CANDIDATE", "SUCCESS", new JsonObject()); 
	        responseJson.add("data", accountData);
	        System.out.println("Server recebeu: " + requestJson);
            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	        out.println(Utils.toJsonString(responseJson));
	        out.flush();
	    }
	    
	    private void handleIncludeSkill(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String token = requestJson.get("token").getAsString();
	        int idcandidate = JWTValidator.getIdClaim(token);
	        String skill = data.get("skill").getAsString();
	        int experience = data.get("experience").getAsInt();
	        
	        PreparedStatement st;
	        ResultSet rs;

	        if(data.get("skill").getAsString().equals("") || data.get("experience").getAsString().equals("")) {
	        	
	        	JsonObject responseJson = Utils.createResponse("INCLUDE_SKILL", "INVALID_FIELD", "");
	        	System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
                out.println(Utils.toJsonString(responseJson));
                return;
	        }
	        
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM skill_dataset WHERE skills_available = ?");
            st.setString(1, skill);
            
            rs = st.executeQuery();

            if (rs.next()) {
            	int idskill = rs.getInt("idskill_dataset");
            	st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM skills WHERE candidate_id = ? AND idskill_dataset = ?");
            	st.setInt(1, idcandidate);
            	st.setInt(2, idskill);
            	rs = st.executeQuery();
            	
            	if (rs.next()) {
            		
            		JsonObject responseData = new JsonObject();
    		        JsonObject responseJson = Utils.createResponse("INCLUDE_SKILL", "SKILL_EXISTS", "");
    		        responseJson.remove("data"); 
    		        responseJson.add("data", responseData);
    		        System.out.println("Server recebeu: " + requestJson);
    	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
    		        out.println(Utils.toJsonString(responseJson));
            		
            	}
            	
            	else {
            		
            		st = ConnectionBD.conectaBD().prepareStatement("INSERT INTO skills (candidate_id, idskill_dataset, experience) VALUES (?, ?, ?)");
                    st.setInt(1, idcandidate);
                    st.setInt(2, idskill);
                    st.setInt(3, experience);
                    st.executeUpdate();
                    
                    JsonObject responseData = new JsonObject();
    		        JsonObject responseJson = Utils.createResponse("INCLUDE_SKILL", "SUCCESS", "");
    		        responseJson.remove("data"); 
    		        responseJson.add("data", responseData);
    		        System.out.println("Server recebeu: " + requestJson);
    	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
    		        out.println(Utils.toJsonString(responseJson));
            		
            	}
            	
            }
            
            else {
            	
            	JsonObject responseData = new JsonObject();
		        JsonObject responseJson = Utils.createResponse("INCLUDE_SKILL", "SKILL_NOT_EXISTS", "");
		        responseJson.remove("data"); 
		        responseJson.add("data", responseData);
		        System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
		        out.println(Utils.toJsonString(responseJson));
                
            }
		        
	        }
	    
	    private void handleLookupSkill(JsonObject requestJson, PrintWriter out) throws IOException, SQLException{
	        
	    	JsonObject data = requestJson.getAsJsonObject("data");
	    	String token = requestJson.get("token").getAsString();
	        int idcandidate = JWTValidator.getIdClaim(token);
	        String skill = data.get("skill").getAsString();
	        
	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM skill_dataset WHERE skills_available = ?"); // Olha se existe no BD a skill, caso sim, recebe a id p validar se o usuário possui aquela skill
	        st.setString(1, skill);

	        ResultSet rs;
	        rs = st.executeQuery();

	        if (rs.next()) { // verifica se a habilidade que o usuário escreveu tem na sua "ficha"
	        	int idskill = rs.getInt("idskill_dataset");
	        	st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM skills WHERE candidate_id = ? AND idskill_dataset = ?");
            	st.setInt(1, idcandidate);
	        	st.setInt(2, idskill);
	        	rs = st.executeQuery();
	        	
            	if(rs.next()) {
            		// Skill existe na "ficha" do usuário
            		int experience = rs.getInt("experience");
            		data.addProperty("experience", experience);
            		JsonObject responseJson = Utils.createResponse("LOOKUP_SKILL", "SUCCESS", "");
    	            responseJson.add("data",data);
    	            System.out.println("Server recebeu: " + requestJson);
    	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
    	            out.println(Utils.toJsonString(responseJson));
    	            return;
            		
            	}

            	else {           		
            		// a skill existe, mas o usuário não tem ela
            		JsonObject responseData = new JsonObject();
    		        JsonObject responseJson = Utils.createResponse("LOOKUP_SKILL", "SKILL_NOT_FOUND", "");
    		        responseJson.remove("data"); 
    		        responseJson.add("data", responseData);
    		        System.out.println("Server recebeu: " + requestJson);
    	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
    		        out.println(Utils.toJsonString(responseJson));
            		
            	}

	        }
	        
	        else {
            	
            	JsonObject responseData = new JsonObject();
		        JsonObject responseJson = Utils.createResponse("LOOKUP_SKILL", "SKILL_NOT_FOUND", "");
		        responseJson.remove("data"); 
		        responseJson.add("data", responseData);
		        System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
		        out.println(Utils.toJsonString(responseJson));
                
            }
	    }
	    
	    private void handleLookupSkillSet(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        String token = requestJson.get("token").getAsString();
	        int idcandidate = JWTValidator.getIdClaim(token);

	        // SQL para buscar as skills do candidato, fazendo join com a tabela skill_dataset permitindo mostrar o nome das skills invés do id
	        String query = "SELECT s.experience, sd.skills_available " +
	                       "FROM skills s " +
	                       "JOIN skill_dataset sd ON s.idskill_dataset = sd.idskill_dataset " +
	                       "WHERE s.candidate_id = ?";

	        PreparedStatement st = ConnectionBD.conectaBD().prepareStatement(query);
	        st.setInt(1, idcandidate);

	        ResultSet rs = st.executeQuery();

	        // Criação de uma ArrayList para armazenar as skills
	        ArrayList<JsonObject> skillList = new ArrayList<>();

	        while (rs.next()) {
	            int experience = rs.getInt("experience");
	            String skillName = rs.getString("skills_available");

	            // Criação de um objeto Json para cada skill
	            JsonObject skillJson = new JsonObject();
	            skillJson.addProperty("skill", skillName);
	            skillJson.addProperty("experience", experience);

	            // Adição do objeto Json à ArrayList
	            skillList.add(skillJson);
	        }

	        // Criação do objeto de resposta
	        JsonObject responseJson = Utils.createResponse("LOOKUP_SKILLSET", "SUCCESS", "");
	        JsonObject responseData = new JsonObject();
	        JsonArray skillsArray = new JsonArray();

	        for (JsonObject skillJson : skillList) {
	            skillsArray.add(skillJson);
	        }

	        // Adiciona o tamanho do array de skills e o array de skills ao objeto data
	        responseData.addProperty("skillset_size", skillList.size());
	        responseData.add("skillset", skillsArray);

	        // Adiciona o objeto data ao objeto de resposta
	        responseJson.add("data", responseData);

	        System.out.println("Server recebeu: " + requestJson);
	        System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	        out.println(Utils.toJsonString(responseJson));
	    }


	    
	    private void handleDeleteSkill(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	    	JsonObject data = requestJson.getAsJsonObject("data");
	    	String token = requestJson.get("token").getAsString();
	        int idcandidate = JWTValidator.getIdClaim(token);
	        String skill = data.get("skill").getAsString();
	        
	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM skill_dataset WHERE skills_available = ?"); // Olha se existe no BD a skill, caso sim, recebe a id p validar se o usuário possui aquela skill
	        st.setString(1, skill);

	        ResultSet rs;
	        rs = st.executeQuery();

	        if (rs.next()) { // verifica se a habilidade que o usuário escreveu tem na sua "ficha"
	        	int idskill = rs.getInt("idskill_dataset");
	        	st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM skills WHERE candidate_id = ? AND idskill_dataset = ?");
            	st.setInt(1, idcandidate);
	        	st.setInt(2, idskill);
	        	rs = st.executeQuery();
            	if(rs.next()) {
            		// Skill existe na "ficha" do usuário
            		
            		st = ConnectionBD.conectaBD().prepareStatement("DELETE FROM skills WHERE candidate_id = ? AND idskill_dataset = ?");
            		st.setInt(1, idcandidate);
    	        	st.setInt(2, idskill);
            		st.executeUpdate();
            		JsonObject responseData = new JsonObject();
    		        JsonObject responseJson = Utils.createResponse("DELETE_SKILL", "SUCCESS", "");
            		responseJson.remove("data");
    	            responseJson.add("data",responseData);
    	            System.out.println("Server recebeu: " + requestJson);
    	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
    	            out.println(Utils.toJsonString(responseJson));
    	            return;
            		
            	}

            	else {           		
            		// a skill existe, mas o usuário não tem ela
            		JsonObject responseData = new JsonObject();
    		        JsonObject responseJson = Utils.createResponse("DELETE_SKILL", "SKILL_NOT_FOUND", "");
    		        responseJson.remove("data"); 
    		        responseJson.add("data", responseData);
    		        System.out.println("Server recebeu: " + requestJson);
    	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
    		        out.println(Utils.toJsonString(responseJson));
            		
            	}

	        }
	        
	        else {
            	
            	JsonObject responseData = new JsonObject();
		        JsonObject responseJson = Utils.createResponse("DELETE_SKILL", "SKILL_NOT_FOUND", "");
		        responseJson.remove("data"); 
		        responseJson.add("data", responseData);
		        System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
		        out.println(Utils.toJsonString(responseJson));
                
            }
	    }
	    
	    
	    private void handleUpdateSkill(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        
	        if ((data.get("skill").getAsString() == null || data.get("skill").getAsString().isEmpty() ) || (data.get("newSkill").getAsString() == null || data.get("newSkill").getAsString().isEmpty()) || (data.get("experience").getAsString() == null || data.get("experience").getAsString().isEmpty())) {
	            JsonObject responseJson = Utils.createResponse("UPDATE_SKILL", "INVALID_FIELD", "");
	            System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	            out.println(Utils.toJsonString(responseJson));
	            return;
	        }
	        
	        
	        String token = requestJson.get("token").getAsString();
	        int idcandidate = JWTValidator.getIdClaim(token);
	        String skill = data.get("skill").getAsString();
	        String newSkill = data.get("newSkill").getAsString();
	        int experience = data.get("experience").getAsInt();


	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM skill_dataset WHERE skills_available = ?"); // Olha se existe no BD a skill atual
	        st.setString(1, skill);
	        ResultSet rs;
	        rs = st.executeQuery();
	        

	        if (rs.next()) { // verifica se a nova habilidade que o usuário escreveu tem no BD
	        	int idSkill = rs.getInt("idskill_dataset");
	        	st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM skill_dataset WHERE skills_available = ?");
		        st.setString(1, newSkill);
	        	rs = st.executeQuery();
	        	
	        	if(rs.next()) { // Verifica se a nova skill é igual à alguma skill já descrita
	        		int idnewSkill = rs.getInt("idskill_dataset");
	        		st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM skills WHERE idskill_dataset = ? AND candidate_id = ?");
			        st.setInt(1, idnewSkill);
			        st.setInt(2, idcandidate);
		        	rs = st.executeQuery();
		        	
		        	if(rs.next()) { 
		        		
		        		JsonObject responseData = new JsonObject();
	    		        JsonObject responseJson = Utils.createResponse("UPDATE_SKILL", "SKILL_EXISTS", "");
	    		        responseJson.remove("data"); 
	    		        responseJson.add("data", responseData);
	    		        System.out.println("Server recebeu: " + requestJson);
	    	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	    		        out.println(Utils.toJsonString(responseJson));
		        		
		        	}
		        	
		        	else {
	            		st = ConnectionBD.conectaBD().prepareStatement("UPDATE skills SET idskill_dataset = ?, experience = ? WHERE candidate_id = ? and idskill_dataset = ? ");
	                    st.setInt(1, idnewSkill);
	                    st.setInt(2, experience);
	                    st.setInt(3, idcandidate);
	                    st.setInt(4, idSkill);
	                    st.executeUpdate();
	            		JsonObject responseData = new JsonObject();
	    		        JsonObject responseJson = Utils.createResponse("UPDATE_SKILL", "SUCCESS", "");
	            		responseJson.remove("data");
	    	            responseJson.add("data",responseData);
	    	            System.out.println("Server recebeu: " + requestJson);
	    	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	    	            out.println(Utils.toJsonString(responseJson));
	    	            return;
		        		
		        	}
		        	
	        	}

            	else {           		
            		// a skill existe, mas o usuário não tem ela
            		JsonObject responseData = new JsonObject();
    		        JsonObject responseJson = Utils.createResponse("UPDATE_SKILL", "SKILL_NOT_FOUND", "");
    		        responseJson.remove("data"); 
    		        responseJson.add("data", responseData);
    		        System.out.println("Server recebeu: " + requestJson);
    	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
    		        out.println(Utils.toJsonString(responseJson));
            		
            	}

	        }
	        
	        else {
            	
            	JsonObject responseData = new JsonObject();
		        JsonObject responseJson = Utils.createResponse("UPDATE_SKILL", "SKILL_NOT_FOUND", "");
		        responseJson.remove("data"); 
		        responseJson.add("data", responseData);
		        System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
		        out.println(Utils.toJsonString(responseJson));
                
            }
	    }
	    
	    private void handleSearchJob(JsonObject requestJson, PrintWriter out) throws IOException, SQLException{
	    	JsonObject data = requestJson.getAsJsonObject("data");
	    	int experience = data.get("experience").getAsInt();
	    	String skill = data.get("skill").getAsString();
	    	String filter = data.get("filter").getAsString();
	    	String token = requestJson.get("token").getAsString();
	        int idcandidate = JWTValidator.getIdClaim(token);
	        
	        
	        PreparedStatement st;
	        ResultSet rs;
	        
	        //Busca por Experiência
	        if(data.get("skill").getAsString() == null || data.get("skill").getAsString().isEmpty())
	        {
	        	String query = "SELECT j.experience " +
	                       "FROM job_dataset j " +
	                       "JOIN skill_dataset sd ON j.skill = sd.idskill_dataset " +
	                       "WHERE j.experience = ?";

	        	st = ConnectionBD.conectaBD().prepareStatement(query);
	        	st.setInt(1, experience);
		        
		        rs = st.executeQuery();
		        
		        if(rs.next()) {
		        	
		        		JsonObject responseJson = Utils.createResponse("SEARCH_JOB", "SUCCESS", "");
			            responseJson.add("data",data);
			            System.out.println("Server recebeu: " + requestJson);
			            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
			            out.println(Utils.toJsonString(responseJson));
			            return;
		        }
	        }
	        
	        // Busca por Skill
	        else if(data.get("experience").getAsString() == null || data.get("experience").getAsString().isEmpty())
	        {
	        	
	        	JsonObject responseJson = Utils.createResponse("SEARCH_JOB", "SUCCESS", "");
		           // responseJson.add("data",data);
		            System.out.println("Server recebeu: " + requestJson);
		            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
		            out.println(Utils.toJsonString(responseJson));
		            return;
	        	
	        }
	        
	        // Busca por ambos (uso de filtro)
	        else {
	        	
	        }
	        
	       /* PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM candidate WHERE idskill = ?");
	        st.setInt(1, idskill);

	        ResultSet rs;
	        rs = st.executeQuery();

	        if (rs.next()) {
	            String email = rs.getString("email");
	            String password = rs.getString("password");
	            String name = rs.getString("name");
	            JsonObject data = new JsonObject();
	            data.addProperty("email",email);
	            data.addProperty("password",password);
	            data.addProperty("name",name);*/

	            JsonObject responseJson = Utils.createResponse("SEARCH_JOB", "SUCCESS", "");
	           // responseJson.add("data",data);
	            System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	            out.println(Utils.toJsonString(responseJson));
	            return;

	        }
	    
	    //Empresa
	    
	    private void handleSignupCompany(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String email = data.get("email").getAsString();
	        String password = data.get("password").getAsString();
	        String name = data.get("name").getAsString();
	        String industry = data.get("industry").getAsString();
	        String description = data.get("description").getAsString();
	        PreparedStatement st;
	        ResultSet rs;

	        if(data.get("email").getAsString().equals("") || data.get("password").getAsString().equals("") || data.get("name").getAsString().equals("") || data.get("industry").getAsString().equals("") || data.get("description").getAsString().equals("")) {
	        	JsonObject responseData = new JsonObject();
	        	JsonObject responseJson = Utils.createResponse("SIGNUP_RECRUITER", "USER_EXISTS", "");
	        	responseJson.remove("data"); 
	        	responseJson.add("data", responseData);
	        	System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
                out.println(Utils.toJsonString(responseJson));
                return;
	        }
	        
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM recruiter WHERE email = ?");
            st.setString(1, email);

            rs = st.executeQuery();

            if (rs.next()) {
                JsonObject Response = Utils.createResponse("SIGNUP_RECRUITER", "USER_EXISTS", "");
                System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(Response));
                out.println(Utils.toJsonString(Response));;
                return;
            }
            else {

                st = ConnectionBD.conectaBD().prepareStatement("INSERT INTO recruiter (email, name, password, industry, description) VALUES (?, ?, ?, ?, ?)");
                st.setString(1, email);
                st.setString(2, name);
                st.setString(3, password);
                st.setString(4, industry);
                st.setString(5, description);
                st.executeUpdate();

            }

	        JsonObject responseData = new JsonObject();
	        JsonObject responseJson = Utils.createResponse("SIGNUP_RECRUITER", "SUCCESS", "");
	        responseJson.remove("data"); 
	        responseJson.add("data", responseData);
	        System.out.println("Server recebeu: " + requestJson);
            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	        out.println(Utils.toJsonString(responseJson));
	    }
	    
	    private void handleLoginCompany(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String email = data.get("email").getAsString();
	        String password = data.get("password").getAsString();
	        
	        if(data.get("email").getAsString().equals("") || data.get("password").getAsString().equals("")) {
        	
	        JsonObject responseData = new JsonObject();
        	JsonObject responseJson = Utils.createResponse("LOGIN_RECRUITER", "INVALID_LOGIN", "");
        	responseJson.remove("data"); 
	        responseJson.add("data", responseData);
	        System.out.println("Server recebeu: " + requestJson);
            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
            out.println(Utils.toJsonString(responseJson));
            return;
        }
	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM recruiter WHERE email = ? AND password = ?");
	        st.setString(1, email);
	        st.setString(2, password);

	        ResultSet rs;
	        rs = st.executeQuery();

	        if (rs.next()) {
	            int idrecruiter = Integer.parseInt(rs.getString("idrecruiter"));
	            String token = JWTValidator.generateToken(idrecruiter, "RECRUITER");
	            JsonObject responseJson = Utils.createResponse("LOGIN_RECRUITER", "SUCCESS", token);
	            System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	            out.println(Utils.toJsonString(responseJson));
	            return;

	        }
	        
	        JsonObject responseData = new JsonObject();
	        JsonObject responseJson = Utils.createResponse("LOGIN_RECRUITER", "INVALID_LOGIN", "");
	        responseJson.remove("data"); 
	        responseJson.add("data", responseData);
	        System.out.println("Server recebeu: " + requestJson);
            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	        out.println(Utils.toJsonString(responseJson));
	    }
	    
	    public void handleLogoutCompany(JsonObject requestJson, PrintWriter out) throws IOException {
	  
	    	String token = requestJson.get("token").getAsString();
	    	JsonObject accountData = new JsonObject();
	        JsonObject responseJson = Utils.createResponse("LOGOUT_RECRUITER", "SUCCESS", new JsonObject());
	        responseJson.add("data", accountData);
	        System.out.println("Server recebeu: " + requestJson);
            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	        out.println(Utils.toJsonString(responseJson));
	        out.flush();
	    }
	    
	    private void handleLookupCompany(JsonObject requestJson, PrintWriter out) throws IOException, SQLException{
	        
	    	String token = requestJson.get("token").getAsString();
	        int idrecruiter = JWTValidator.getIdClaim(token);
	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM recruiter WHERE idrecruiter = ?");
	        st.setInt(1, idrecruiter);

	        ResultSet rs;
	        rs = st.executeQuery();


	        if (rs.next()) {
	            String email = rs.getString("email");
	            String password = rs.getString("password");
	            String name = rs.getString("name");
	            String industry = rs.getString("industry");
                String description = rs.getString("description");
	            JsonObject data = new JsonObject();
	            data.addProperty("email",email);
	            data.addProperty("password",password);
	            data.addProperty("name",name);
	            data.addProperty("industry",industry);
                data.addProperty("description",description);

	            JsonObject responseJson = Utils.createResponse("LOOKUP_ACCOUNT_RECRUITER", "SUCCESS", "");
	            responseJson.add("data",data);
	            System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	            out.println(Utils.toJsonString(responseJson));
	            return;

	        }
	    }
	    
	    private void handleUpdateCompany(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        
	        if ((data.get("email").getAsString() == null || data.get("email").getAsString().isEmpty() ) || (data.get("password").getAsString() == null || data.get("password").getAsString().isEmpty()) || (data.get("name").getAsString() == null || data.get("name").getAsString().isEmpty()) || (data.get("industry").getAsString() == null || data.get("industry").getAsString().isEmpty()) || (data.get("description").getAsString() == null || data.get("description").getAsString().isEmpty())) {
	            JsonObject responseJson = Utils.createResponse("UPDATE_ACCOUNT_RECRUITER", "INVALID_FIELD", "");
	            out.println(Utils.toJsonString(responseJson));
	            return;
	        }
	        
	        String token = requestJson.get("token").getAsString();
	        String email = data.get("email").getAsString();
	        String password = data.get("password").getAsString();
	        String name = data.get("name").getAsString();
	        String industry = data.get("industry").getAsString();
	        String description = data.get("description").getAsString();

	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM recruiter WHERE email = ?");
	        st.setString(1, email);

	        ResultSet rs;
	        rs = st.executeQuery();

	        if (rs.next()) {
	            JsonObject Response = Utils.createResponse("UPDATE_ACCOUNT_RECRUITER", "INVALID_EMAIL", "");
	            System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(Response));
	            out.println(Utils.toJsonString(Response));
	            return;
	        }

	        int idrecruiter = JWTValidator.getIdClaim(token);
	        
	        st = ConnectionBD.conectaBD().prepareStatement("UPDATE recruiter SET email = ?, name = ?, password = ?, industry = ?, description = ? WHERE idrecruiter = ?");
	        st.setString(1, email);
	        st.setString(2, name);
	        st.setString(3, password);
	        st.setString(4, industry);
	        st.setString(5, description);
	        st.setInt(6, idrecruiter);
	        st.executeUpdate();

	        JsonObject responseJson = Utils.createResponse("UPDATE_ACCOUNT_RECRUITER", "SUCCESS", "");
	        System.out.println("Server recebeu: " + requestJson);
            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	        out.println(Utils.toJsonString(responseJson));
	    }

	    private void handleDeleteCompany(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String token = requestJson.get("token").getAsString();
	        
	        int idrecruiter = JWTValidator.getIdClaim(token);
	        

	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("DELETE FROM recruiter WHERE idrecruiter = ?");
	        st.setInt(1, idrecruiter);
	        st.executeUpdate();

	        JsonObject responseJson = Utils.createResponse("DELETE_ACCOUNT_RECRUITER", "SUCCESS", "");
	        System.out.println("Server recebeu: " + requestJson);
            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	        out.println(Utils.toJsonString(responseJson));
	    }
	    
	    private void handleIncludeJob(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	    	JsonObject data = requestJson.getAsJsonObject("data");
	        String token = requestJson.get("token").getAsString();
	        int recruiter_id = JWTValidator.getIdClaim(token);
	        String skill = data.get("skill").getAsString();
	        int experience = data.get("experience").getAsInt();
	        
	        PreparedStatement st;
	        ResultSet rs;

	        if(data.get("skill").getAsString().equals("") || data.get("experience").getAsString().equals("")) {
	        	
	        	JsonObject responseJson = Utils.createResponse("INCLUDE_JOB", "INVALID_FIELD", "");
	        	System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
                out.println(Utils.toJsonString(responseJson));
                return;
	        }
	        
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM skill_dataset WHERE BINARY skills_available = ?");
            st.setString(1, skill);
            
            rs = st.executeQuery();
            
            if(rs.next()) {
            	int idskill = rs.getInt("idskill_dataset");
            	st = ConnectionBD.conectaBD().prepareStatement("INSERT INTO job_dataset (experience, skill, recruiter_id) VALUES (?, ?, ?)");
                st.setInt(1, experience);
                st.setInt(2, idskill);
                st.setInt(3, recruiter_id);
                st.executeUpdate();  
                JsonObject responseData = new JsonObject();
                JsonObject responseJson = Utils.createResponse("INCLUDE_JOB", "SUCCESS", "");
                responseJson.remove("data"); 
                responseJson.add("data", responseData);
                System.out.println("Server recebeu: " + requestJson);
                System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
                out.println(Utils.toJsonString(responseJson));
 
            }
            
            else {
            	
            	JsonObject responseData = new JsonObject();
		        JsonObject responseJson = Utils.createResponse("INCLUDE_SKILL", "SKILL_NOT_EXISTS", "");
		        responseJson.remove("data"); 
		        responseJson.add("data", responseData);
		        System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
		        out.println(Utils.toJsonString(responseJson));
		        
            }
               
            }
	    
	    
	    private void handleLookupJob(JsonObject requestJson, PrintWriter out) throws IOException, SQLException{
	    	
	    	JsonObject data = requestJson.getAsJsonObject("data");
	    	String token = requestJson.get("token").getAsString();
	        int idrecruiter = JWTValidator.getIdClaim(token);
	        int idjob = data.get("id").getAsInt();
	        
	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM job_dataset WHERE recruiter_id = ? AND id_job = ?");
	        st.setInt(1, idrecruiter);
	        st.setInt(2, idjob);

	        ResultSet rs;
	        rs = st.executeQuery();

	        if (rs.next()) {
	        	
	        	int experience = rs.getInt("experience");
	        	data.addProperty("experience", experience);
	        	int idskill = rs.getInt("skill");
	        	st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM skill_dataset WHERE idskill_dataset = ?");
		        st.setInt(1, idskill);
		        rs = st.executeQuery();
		        
		        if(rs.next()) {
		        	
		        	String skill = rs.getString("skills_available");
		        	data.addProperty("skill", skill);
		        	JsonObject responseJson = Utils.createResponse("LOOKUP_JOB", "SUCCESS", "");
		            responseJson.add("data",data);
		            System.out.println("Server recebeu: " + requestJson);
		            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
		            out.println(Utils.toJsonString(responseJson));
		            return;
		            
		        }
		        
		        else {
		        	
		        	JsonObject responseData = new JsonObject();
			        JsonObject responseJson = Utils.createResponse("LOOKUP_JOB", "JOB_NOT_FOUND", "");
			        responseJson.remove("data"); 
			        responseJson.add("data", responseData);
			        System.out.println("Server recebeu: " + requestJson);
		            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
			        out.println(Utils.toJsonString(responseJson));
		        	
		        }

	        }
	        
	        else {
	        	
	        	JsonObject responseData = new JsonObject();
		        JsonObject responseJson = Utils.createResponse("LOOKUP_JOB", "JOB_NOT_FOUND", "");
		        responseJson.remove("data"); 
		        responseJson.add("data", responseData);
		        System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
		        out.println(Utils.toJsonString(responseJson));
	        	
	        }
	   }
	    
	    private void handleLookupJobSet(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        String token = requestJson.get("token").getAsString();
	        int recruiter_id = JWTValidator.getIdClaim(token);

	        // SQL para buscar as skills do job, fazendo join com a tabela skill_dataset permitindo mostrar o nome das skills invés do id
	        String query = "SELECT j.id_job, j.experience, sd.skills_available " +
	                       "FROM job_dataset j " +
	                       "JOIN skill_dataset sd ON j.skill = sd.idskill_dataset " +
	                       "WHERE j.recruiter_id = ?";

	        PreparedStatement st = ConnectionBD.conectaBD().prepareStatement(query);
	        st.setInt(1, recruiter_id);

	        ResultSet rs = st.executeQuery();

	        // Criação de uma ArrayList para armazenar as skills do job
	        ArrayList<JsonObject> jobList = new ArrayList<>();

	        while (rs.next()) {
	            int jobId = rs.getInt("id_job");
	            int experience = rs.getInt("experience");
	            String skillName = rs.getString("skills_available");

	            // Criação de um objeto Json para cada skill
	            JsonObject jobJson = new JsonObject();
	            jobJson.addProperty("id", jobId);
	            jobJson.addProperty("skill", skillName);
	            jobJson.addProperty("experience", experience);

	            // Adição do objeto Json à ArrayList
	            jobList.add(jobJson);
	        }

	        // Criação do objeto de resposta
	        JsonObject responseJson = Utils.createResponse("LOOKUP_JOBSET", "SUCCESS", "");
	        JsonObject responseData = new JsonObject();
	        JsonArray jobsArray = new JsonArray();

	        for (JsonObject jobJson : jobList) {
	            jobsArray.add(jobJson);
	        }

	        // Adiciona o tamanho do array de jobs e o array de jobs ao objeto data
	        responseData.addProperty("jobset_size", jobList.size());
	        responseData.add("jobset", jobsArray);

	        // Adiciona o objeto data ao objeto de resposta
	        responseJson.add("data", responseData);

	        System.out.println("Server recebeu: " + requestJson);
	        System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	        out.println(Utils.toJsonString(responseJson));
	    }

	    
	    private void handleDeleteJob(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String token = requestJson.get("token").getAsString();
	        int idrecruiter = JWTValidator.getIdClaim(token);
	        int idjob = data.get("id").getAsInt(); 
	        
	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("DELETE FROM job_dataset WHERE id_job = ? AND recruiter_id = ?");
	        st.setInt(1, idjob);
	        st.setInt(2, idrecruiter);
	        st.executeUpdate();
	        JsonObject responseData = new JsonObject();
	        JsonObject responseJson = Utils.createResponse("DELETE_JOB", "SUCCESS", "");
	        responseJson.remove("data");
            responseJson.add("data",responseData);
	        System.out.println("Server recebeu: " + requestJson);
            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	        out.println(Utils.toJsonString(responseJson));
	        return;
	    }
	 
	    private void handleUpdateJob(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        
	        /*if ((data.get("email").getAsString() == null || data.get("email").getAsString().isEmpty() ) || (data.get("password").getAsString() == null || data.get("password").getAsString().isEmpty()) || (data.get("name").getAsString() == null || data.get("name").getAsString().isEmpty())) {
	            JsonObject responseJson = Utils.createResponse("UPDATE_JOB", "INVALID_FIELD", "");
	            System.out.println("Server recebeu: " + requestJson);
	            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
	            out.println(Utils.toJsonString(responseJson));
	            return;
	        }*/
	        
	        String token = requestJson.get("token").getAsString();
	        int idrecruiter = JWTValidator.getIdClaim(token);
	        String skill = data.get("skill").getAsString();
	        int experience = data.get("experience").getAsInt();
	        int idjob = data.get("id").getAsInt();

	        PreparedStatement st;
	        
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM skill_dataset WHERE skills_available = ?"); // Olha se existe no BD a skill atual
	        st.setString(1, skill);
	        
	        ResultSet rs;
	        rs = st.executeQuery();

	        if (rs.next()) {
	        	
	        	int idnewSkill = rs.getInt("idskill_dataset");
	        	st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM job_dataset WHERE id_job = ? AND recruiter_id = ? ");
		        st.setInt(1, idjob);
		        st.setInt(2, idrecruiter);

		        rs = st.executeQuery();

		        if (rs.next()) {
		        	st = ConnectionBD.conectaBD().prepareStatement("UPDATE job_dataset SET skill = ?, experience = ? WHERE id_job = ? and recruiter_id = ? ");
	                st.setInt(1, idnewSkill);
	                st.setInt(2, experience);
	                st.setInt(3, idjob);
	                st.setInt(4, idrecruiter);
	                st.executeUpdate();
	        		JsonObject responseData = new JsonObject();
			        JsonObject responseJson = Utils.createResponse("UPDATE_SKILL", "SUCCESS", "");
	        		responseJson.remove("data");
		            responseJson.add("data",responseData);
		            System.out.println("Server recebeu: " + requestJson);
		            System.out.println("Server enviou: " + Utils.toJsonString(responseJson));
		            out.println(Utils.toJsonString(responseJson));
		        }
	        	
	        }
	        
	        

	    }
	    
	    public static void main(String[] args) {
	        int serverPort = 21234;

	        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("serverTest_log.txt", true));
	             ServerSocket serverSocket = new ServerSocket(serverPort)) {

	            System.out.println("Servidor iniciado na porta " + serverPort);

	            while (true) {
	                System.out.println("Aguardando conexão...");
	                Socket clientSocket = serverSocket.accept();
	                System.out.println("Cliente conectado: " + clientSocket);	         
	                
	                new Server(clientSocket, fileWriter);
	            }
	        } catch (IOException e) {
	            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
	        }
	    }
	
}
