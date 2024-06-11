package entrega2;

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
	            System.out.println(Utils.toJsonString(responseJson));
	            out.println(Utils.toJsonString(responseJson));
	            return;

	        }

	        JsonObject responseData = new JsonObject();
	        JsonObject responseJson = Utils.createResponse("LOGIN_CANDIDATE", "INVALID_LOGIN", "");
	        responseJson.remove("data"); 
	        responseJson.add("data", responseData);
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
                out.println(Utils.toJsonString(responseJson));
                return;
	        }
	        
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM candidate WHERE email = ?");
            st.setString(1, email);

            rs = st.executeQuery();

            if (rs.next()) {
                JsonObject Response = Utils.createResponse("SIGNUP_CANDIDATE", "USER_EXISTS", "");

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
	            out.println(Utils.toJsonString(responseJson));
	            return;

	        }
	    	
	    	/*if (requestJson != null && requestJson.has("token")) {
	            String token = requestJson.get("token").getAsString();
	            
	            int userId = JWTValidator.getIdClaim(token);
	            List<User> users = readUserDatabase();
	            
	            Optional<User> optionalUser = users.stream()
	                                               .filter(user -> user.getId() == userId)
	                                               .findFirst();
	            if (optionalUser.isPresent()) {
	                User user = optionalUser.get();
	                
	                JsonObject accountData = new JsonObject();
	                accountData.addProperty("email", user.getEmail());
	                accountData.addProperty("password", user.getPassword());
	                accountData.addProperty("name", user.getName());
	                JsonObject responseJson = Utils.createResponse("LOOKUP_ACCOUNT_CANDIDATE", "SUCCESS", "");
	                responseJson.add("data", accountData);
	                
	                out.println(Utils.toJsonString(responseJson));
	                out.flush();
	            }
	        }*/
	    }
	    
	    private void handleUpdate(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        
	        if ((data.get("email").getAsString() == null || data.get("email").getAsString().isEmpty() ) || (data.get("password").getAsString() == null || data.get("password").getAsString().isEmpty()) || (data.get("name").getAsString() == null || data.get("name").getAsString().isEmpty())) {
	            JsonObject responseJson = Utils.createResponse("UPDATE_ACCOUNT_CANDIDATE", "INVALID_FIELD", "");
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
            out.println(Utils.toJsonString(responseJson));
	    }
	    

	        /*List<User> users = readUserDatabase();
	        
	       Optional<User> optionalUser = users.stream()
	                                           .filter(user -> user.getId() == userId)
	                                          .findFirst();
	        if (optionalUser.isPresent()) {
	           User user = optionalUser.get();
	            boolean anyFieldUpdated = false;

	            if (data.has("email")) {
	                String newEmail = data.get("email").getAsString();
	                if (!newEmail.isEmpty()) {
	                    user.setEmail(newEmail);
	                    anyFieldUpdated = true;
	                }
	            }
	            if (data.has("password")) {
	                String newPassword = data.get("password").getAsString();
	                if (!newPassword.isEmpty()) {
	                    user.setPassword(newPassword);
	                    anyFieldUpdated = true;
	                }
	            }
	            if (data.has("name")) {
	                String newName = data.get("name").getAsString();
	                if (!newName.isEmpty()) {
	                    user.setName(newName);
	                    anyFieldUpdated = true;
	                }
	            }

	            if (!anyFieldUpdated) {
	                JsonObject responseJson = Utils.createResponse("UPDATE_ACCOUNT_CANDIDATE", "INVALID_EMAIL", new JsonObject());
	                out.println(Utils.toJsonString(responseJson));
	                return;
	            }

	            writeUserDatabase(users);
	            token = "";
	            JsonObject responseData = new JsonObject();
	            JsonObject responseJson = Utils.createResponse("UPDATE_ACCOUNT_CANDIDATE", "SUCCESS", "");
	            responseJson.remove("data");
	            responseJson.add("data", responseData);
	            out.println(Utils.toJsonString(responseJson));

	            return;
	        }*/

	        
	    private void handleDelete(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        //JsonObject data = requestJson.getAsJsonObject("data");
	        String token = requestJson.get("token").getAsString();
	        int idcandidate = JWTValidator.getIdClaim(token);
	        
	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("DELETE FROM candidate WHERE idcandidate = ?");
	        st.setInt(1, idcandidate);
	        st.executeUpdate();

	        JsonObject responseJson = Utils.createResponse("DELETE_ACCOUNT_CANDIDATE", "SUCCESS", "");
	        out.println(Utils.toJsonString(responseJson));
	        
	        /*try {
	            int userId = JWTValidator.getIdClaim(token);
	            List<User> users = readUserDatabase();
	            if (!users.stream().anyMatch(user -> user.getId() == userId)) {
	                JsonObject responseJson = Utils.createResponse("DELETE_ACCOUNT_CANDIDATE", "INVALID_EMAIL", "");
	                out.println(Utils.toJsonString(responseJson));
	                return;
	            }
	            

	            for (User user : users) {
	                if (user.getId() == userId) {
	                    users.remove(user);
	                    writeUserDatabase(users);
	                    JsonObject responseData = new JsonObject();
	                    JsonObject responseJson = Utils.createResponse("DELETE_ACCOUNT_CANDIDATE", "SUCCESS", "");
	                    responseJson.remove("data");
	                    responseJson.add("data", responseData);
	                    responseJson.remove("token");
	                    responseJson.add("token", responseData);
	                    out.println(Utils.toJsonString(responseJson));
	                    return;
	                }
	            }
	        } catch (JWTVerificationException e) {
	            JsonObject responseJson = Utils.createResponse("DELETE_ACCOUNT_CANDIDATE", "INVALID_TOKEN", "");
	            out.println(Utils.toJsonString(responseJson));
	            return;
	        }
	        JsonObject responseJson = Utils.createResponse("DELETE_ACCOUNT_CANDIDATE", "USER_NOT_FOUND", "");
	        out.println(Utils.toJsonString(responseJson));*/
	    }
	  
	    public void handleLogout(JsonObject requestJson, PrintWriter out) throws IOException {
	    	
	    	String token = requestJson.get("token").getAsString();	
	    	JsonObject accountData = new JsonObject();
	        JsonObject responseJson = Utils.createResponse("LOGOUT_CANDIDATE", "SUCCESS", new JsonObject()); 
	        responseJson.add("data", accountData);
	    
	        out.println(Utils.toJsonString(responseJson));
	        out.flush();
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
	        //int id;

	        if(data.get("email").getAsString().equals("") || data.get("password").getAsString().equals("") || data.get("name").getAsString().equals("") || data.get("industry").getAsString().equals("") || data.get("description").getAsString().equals("")) {
	        	JsonObject responseData = new JsonObject();
	        	JsonObject responseJson = Utils.createResponse("SIGNUP_RECRUITER", "USER_EXISTS", "");
	        	responseJson.remove("data"); 
	        	responseJson.add("data", responseData);
                out.println(Utils.toJsonString(responseJson));
                return;
	        }
	        
	       /* List<Company> companys = readCompanyDatabase();
	        if(companys.size() == 0) {
	        	id = 1;
	        }
	        else {
	        	Company lastCreated = companys.get(companys.size()-1);
	        	id = lastCreated.getId() + 1;
	        }
	        
	        for (Company company : companys) {
	            if (company.getEmail().equals(email)) {
	                
	                JsonObject responseJson = Utils.createResponse("SIGNUP_RECRUITER", "USER_EXISTS", "");
	                out.println(Utils.toJsonString(responseJson));
	                return;
	            }
	        }

	        Company newUser = new Company(email, password, name, industry, description, id);
	        companys.add(newUser);
	        writeCompanyDatabase(companys);*/
	        st = ConnectionBD.conectaBD().prepareStatement("SELECT * FROM recruiter WHERE email = ?");
            st.setString(1, email);

            rs = st.executeQuery();

            if (rs.next()) {
                JsonObject Response = Utils.createResponse("SIGNUP_RECRUITER", "USER_EXISTS", "");

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
            out.println(Utils.toJsonString(responseJson));
            return;
        }

	        /*List<Company> companys = readCompanyDatabase();
	        for (Company company : companys) {
	            if (company.getEmail().equals(email)) {
	                if (company.getPassword().equals(password)) {
	                	
	                    String token = JWTValidator.generateToken(company.getId(), "RECRUITER");
	                    JsonObject responseJson = Utils.createResponse("LOGIN_RECRUITER", "SUCCESS", token);
	                    out.println(Utils.toJsonString(responseJson));
	                    return;
	                } else {
	                    
	                	JsonObject responseData = new JsonObject();
	                    JsonObject responseJson = Utils.createResponse("LOGIN_RECRUITER", "INVALID_LOGIN", "");
	                    responseJson.remove("data"); 
	                    responseJson.add("data", responseData);
	                    out.println(Utils.toJsonString(responseJson));
	                    return;
	                }
	            }
	        }*/
	        
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
	            System.out.println(Utils.toJsonString(responseJson));
	            out.println(Utils.toJsonString(responseJson));
	            return;

	        }
	        
	        JsonObject responseData = new JsonObject();
	        JsonObject responseJson = Utils.createResponse("LOGIN_RECRUITER", "INVALID_LOGIN", "");
	        responseJson.remove("data"); 
	        responseJson.add("data", responseData);
	        out.println(Utils.toJsonString(responseJson));
	    }
	    
	    public void handleLogoutCompany(JsonObject requestJson, PrintWriter out) throws IOException {
	  
	    	String token = requestJson.get("token").getAsString();
	    	JsonObject accountData = new JsonObject();
	        JsonObject responseJson = Utils.createResponse("LOGOUT_RECRUITER", "SUCCESS", new JsonObject());
	        responseJson.add("data", accountData);
	    
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
	            out.println(Utils.toJsonString(responseJson));
	            return;

	        }
	    	
	    	
	    	/*if (requestJson != null && requestJson.has("token")) {
	            String token = requestJson.get("token").getAsString();
	            
	            int companyId = JWTValidator.getIdClaim(token);
	            List<Company> companys = readCompanyDatabase();
	            
	            Optional<Company> optionalCompany = companys.stream()
	                                               .filter(company -> company.getId() == companyId)
	                                               .findFirst();
	            if (optionalCompany.isPresent()) {
	                Company company = optionalCompany.get();
	                
	                JsonObject accountData = new JsonObject();
	                accountData.addProperty("email", company.getEmail());
	                accountData.addProperty("password", company.getPassword());
	                accountData.addProperty("name", company.getName());
	                accountData.addProperty("industry", company.getIndustry());
	                accountData.addProperty("description", company.getDescription());
	                JsonObject responseJson = Utils.createResponse("LOOKUP_ACCOUNT_RECRUITER", "SUCCESS", "");
	                responseJson.add("data", accountData);
	                
	                out.println(Utils.toJsonString(responseJson));
	                out.flush();
	            }
	        }*/
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
	        out.println(Utils.toJsonString(responseJson));
	    }
	        
	      /*  List<Company> companys = readCompanyDatabase();
	        
	        Optional<Company> optionalCompany = companys.stream()
	                                           .filter(company -> company.getId() == companyId)
	                                           .findFirst();
	        if (optionalCompany.isPresent()) {
	            Company company = optionalCompany.get();
	            boolean anyFieldUpdated = false;

	            if (data.has("email")) {
	                String newEmail = data.get("email").getAsString();
	                if (!newEmail.isEmpty()) {
	                    company.setEmail(newEmail);
	                    anyFieldUpdated = true;
	                }
	            }
	            if (data.has("password")) {
	                String newPassword = data.get("password").getAsString();
	                if (!newPassword.isEmpty()) {
	                    company.setPassword(newPassword);
	                    anyFieldUpdated = true;
	                }
	            }
	            if (data.has("name")) {
	                String newName = data.get("name").getAsString();
	                if (!newName.isEmpty()) {
	                    company.setName(newName);
	                    anyFieldUpdated = true;
	                }
	            }
	            
	            if (data.has("industry")) {
	                String newIndustry = data.get("industry").getAsString();
	                if (!newIndustry.isEmpty()) {
	                    company.setIndustry(newIndustry);
	                    anyFieldUpdated = true;
	                }
	            }
	            
	            if (data.has("description")) {
	                String newDescription = data.get("description").getAsString();
	                if (!newDescription.isEmpty()) {
	                    company.setDescription(newDescription);
	                    anyFieldUpdated = true;
	                }
	            }

	            if (!anyFieldUpdated) {
	                JsonObject responseJson = Utils.createResponse("UPDATE_ACCOUNT_RECRUITER", "INVALID_EMAIL", new JsonObject());
	                out.println(Utils.toJsonString(responseJson));
	                return;
	            }

	            writeCompanyDatabase(companys);
	            token = "";
	            JsonObject responseData = new JsonObject();
	            JsonObject responseJson = Utils.createResponse("UPDATE_ACCOUNT_RECRUITER", "SUCCESS", "");
	            responseJson.remove("data");
	            responseJson.add("data", responseData);
	            out.println(Utils.toJsonString(responseJson));

	            return;
	        }

	        JsonObject responseJson = Utils.createResponse("UPDATE_ACCOUNT_RECRUITER", "INVALID_EMAIL", new JsonObject());
	        out.println(Utils.toJsonString(responseJson));
	    }*/
	    
	    private void handleDeleteCompany(JsonObject requestJson, PrintWriter out) throws IOException, SQLException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String token = requestJson.get("token").getAsString();
	        
	        int idrecruiter = JWTValidator.getIdClaim(token);
	        

	        PreparedStatement st;
	        st = ConnectionBD.conectaBD().prepareStatement("DELETE FROM recruiter WHERE idrecruiter = ?");
	        st.setInt(1, idrecruiter);
	        st.executeUpdate();

	        JsonObject responseJson = Utils.createResponse("DELETE_ACCOUNT_RECRUITER", "SUCCESS", "");
	        out.println(Utils.toJsonString(responseJson));
	        /*
	        try {
	            int companyId = JWTValidator.getIdClaim(token);
	            List<Company> companys = readCompanyDatabase();
	            if (!companys.stream().anyMatch(company -> company.getId() == companyId)) {
	                JsonObject responseJson = Utils.createResponse("DELETE_ACCOUNT_RECRUITER", "INVALID_EMAIL", "");
	                out.println(Utils.toJsonString(responseJson));
	                return;
	            }
	            

	            for (Company company : companys) {
	                if (company.getId() == companyId) {
	                    companys.remove(company);
	                    writeCompanyDatabase(companys);
	                    JsonObject responseData = new JsonObject();
	                    JsonObject responseJson = Utils.createResponse("DELETE_ACCOUNT_RECRUITER", "SUCCESS", "");
	                    responseJson.remove("data");
	                    responseJson.add("data", responseData);
	                    responseJson.remove("token");
	                    responseJson.add("token", responseData);
	                    out.println(Utils.toJsonString(responseJson));
	                    return;
	                }
	            }
	        } catch (JWTVerificationException e) {
	            JsonObject responseJson = Utils.createResponse("DELETE_ACCOUNT_RECRUITER", "INVALID_TOKEN", "");
	            out.println(Utils.toJsonString(responseJson));
	            return;
	        }
	        JsonObject responseJson = Utils.createResponse("DELETE_ACCOUNT_RECRUITER", "USER_NOT_FOUND", "");
	        out.println(Utils.toJsonString(responseJson));*/
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
