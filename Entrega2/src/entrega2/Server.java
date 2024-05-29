package entrega2;

import com.google.gson.JsonObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
	    private static final String DATABASE_FILE = "usuario_database.txt";
	    private static final String COMPANY_DATABASE_FILE = "empresa_database.txt";

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

	    private static class User {
	        private String email;
	        private String password;
	        private String name;
	        private int id;

	    	public User(String email, String password, String name, int id) {
	            this.email = email;
	            this.password = password;
	            this.name = name;
	            this.id = id;
	        }
	        

	        public String getEmail() {
	            return email;
	        }

	        public void setEmail(String email) {
	            this.email = email;
	        }

	        public String getPassword() {
	            return password;
	        }

	        public void setPassword(String password) {
	            this.password = password;
	        }

	        public String getName() {
	            return name;
	        }

	        public void setName(String name) {
	            this.name = name;
	        }
	        
	        public int getId() {
				return id;
			}


			public void setId(int id) {
				this.id = id;
			}
	        
	    }

	    
	    private static class Company {
	        private String email;
	        private String password;
	        private String name;
	        private String industry;
			private String description;
	        private int id;

	    	public Company(String email, String password, String name, String industry, String description, int id) {
	            this.email = email;
	            this.password = password;
	            this.name = name;
	            this.industry = industry;
	            this.description = description;
	            this.id = id;
	        }
	        

	        public String getEmail() {
	            return email;
	        }

	        public void setEmail(String email) {
	            this.email = email;
	        }

	        public String getPassword() {
	            return password;
	        }

	        public void setPassword(String password) {
	            this.password = password;
	        }

	        public String getName() {
	            return name;
	        }

	        public void setName(String name) {
	            this.name = name;
	        }
	        
	        public String getIndustry() {
				return industry;
			}


			public void setIndustry(String industry) {
				this.industry = industry;
			}


			public String getDescription() {
				return description;
			}


			public void setDescription(String description) {
				this.description = description;
			}
	        
	        public int getId() {
				return id;
			}


			public void setId(int id) {
				this.id = id;
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
	            
	        } catch (IOException e) {
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
	    
	    private void handleLogin(JsonObject requestJson, PrintWriter out) throws IOException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String email = data.get("email").getAsString();
	        String password = data.get("password").getAsString();

	        /*if(data.get("email").getAsString().equals("") || data.get("password").getAsString().equals("")) {
	        	
	        	JsonObject responseJson = Utils.createResponse("LOGIN_CANDIDATE", "INVALID_LOGIN", "");
                out.println(Utils.toJsonString(responseJson));
                return;
	        }*/
	        
	        List<User> users = readUserDatabase();
	        for (User user : users) {
	            if (user.getEmail().equals(email)) {
	                if (user.getPassword().equals(password)) {
	                	
	                    String token = JWTValidator.generateToken(user.getId(), "CANDIDATE");
	                    JsonObject responseJson = Utils.createResponse("LOGIN_CANDIDATE", "SUCCESS", token);
	                    out.println(Utils.toJsonString(responseJson));
	                    return;
	                } else {
	                    
	                	JsonObject responseData = new JsonObject();
	                    JsonObject responseJson = Utils.createResponse("LOGIN_CANDIDATE", "INVALID_LOGIN", "");
	                    responseJson.remove("data"); 
	                    responseJson.add("data", responseData);
	                    out.println(Utils.toJsonString(responseJson));
	                    return;
	                }
	            }
	        }
	        
	        JsonObject responseData = new JsonObject();
	        JsonObject responseJson = Utils.createResponse("LOGIN_CANDIDATE", "INVALID_LOGIN", "");
	        responseJson.remove("data"); 
	        responseJson.add("data", responseData);
	        out.println(Utils.toJsonString(responseJson));
	    }

	    private void handleSignup(JsonObject requestJson, PrintWriter out) throws IOException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String email = data.get("email").getAsString();
	        String password = data.get("password").getAsString();
	        String name = data.get("name").getAsString();
	        int id;

	        if(data.get("email").getAsString().equals("") || data.get("password").getAsString().equals("") || data.get("name").getAsString().equals("")) {
	        	
	        	JsonObject responseJson = Utils.createResponse("SIGNUP_CANDIDATE", "USER_EXISTS", "");
                out.println(Utils.toJsonString(responseJson));
                return;
	        }
	        
	        	List<User> users = readUserDatabase();
		        if(users.size() == 0) {
		        	id = 1;
		        }
		        else {
		        	User lastCreated = users.get(users.size()-1);
		        	id = lastCreated.getId() + 1;
		        }
		        for (User user : users) {
		            if (user.getEmail().equals(email)) {
		                
		                JsonObject responseJson = Utils.createResponse("SIGNUP_CANDIDATE", "USER_EXISTS", "");
		                out.println(Utils.toJsonString(responseJson));
		                return;
		            }
		        }

		        User newUser = new User(email, password, name, id);
		        users.add(newUser);
		        writeUserDatabase(users);

		        JsonObject responseData = new JsonObject();
		        JsonObject responseJson = Utils.createResponse("SIGNUP_CANDIDATE", "SUCCESS", "");
		        responseJson.remove("data"); 
		        responseJson.add("data", responseData);
		        out.println(Utils.toJsonString(responseJson));
	        }
	    
	    private void handleLookup(JsonObject requestJson, PrintWriter out) throws IOException{
	        if (requestJson != null && requestJson.has("token")) {
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
	        }
	    }
	    
	    private void handleUpdate(JsonObject requestJson, PrintWriter out) throws IOException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String token = requestJson.get("token").getAsString(); 

	        int userId = JWTValidator.getIdClaim(token);
	        List<User> users = readUserDatabase();
	        
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
	        }

	        JsonObject responseJson = Utils.createResponse("UPDATE_ACCOUNT_CANDIDATE", "INVALID_EMAIL", new JsonObject());
	        out.println(Utils.toJsonString(responseJson));
	    }
	    
	    private void handleDelete(JsonObject requestJson, PrintWriter out) throws IOException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String token = requestJson.get("token").getAsString();
	        try {
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
	        out.println(Utils.toJsonString(responseJson));
	    }
	  
	    public void handleLogout(JsonObject requestJson, PrintWriter out) throws IOException {
	    	
	    	String token = requestJson.get("token").getAsString();
	    	JsonObject accountData = new JsonObject();
	        JsonObject responseJson = Utils.createResponse("LOGOUT_CANDIDATE", "SUCCESS", new JsonObject()); 
	        responseJson.add("data", accountData);
	    
	        out.println(Utils.toJsonString(responseJson));
	        out.flush();
	    }
	    
	    // Criação BD Usuário
	    
	    private List<User> readUserDatabase() throws IOException {
	        List<User> users = new ArrayList<>();
	        try (BufferedReader br = new BufferedReader(new FileReader(DATABASE_FILE))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                String[] parts = line.split(",");
	                User user = new User(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
	                users.add(user);
	            }
	        } catch (FileNotFoundException e) {
	        	
	        }
	        return users;
	    }

	    private void writeUserDatabase(List<User> users) throws IOException {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATABASE_FILE))) {
	            for (User user : users) {
	                String userString = user.getEmail() + "," + user.getPassword() + "," + user.getName() + "," + user.getId();
	                bw.write(userString);
	                bw.newLine();
	            }
	        }
	    }
	    
	    //Empresa
	    
	    private void handleSignupCompany(JsonObject requestJson, PrintWriter out) throws IOException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String email = data.get("email").getAsString();
	        String password = data.get("password").getAsString();
	        String name = data.get("name").getAsString();
	        String industry = data.get("industry").getAsString();
	        String description = data.get("description").getAsString();
	        int id;

	        if(data.get("email").getAsString().equals("") || data.get("password").getAsString().equals("") || data.get("name").getAsString().equals("") || data.get("industry").getAsString().equals("") || data.get("description").getAsString().equals("")) {
	        	JsonObject responseData = new JsonObject();
	        	JsonObject responseJson = Utils.createResponse("SIGNUP_RECRUITER", "USER_EXISTS", "");
	        	responseJson.remove("data"); 
	        	responseJson.add("data", responseData);
                out.println(Utils.toJsonString(responseJson));
                return;
	        }
	        
	        List<Company> companys = readCompanyDatabase();
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
	        writeCompanyDatabase(companys);

	        JsonObject responseData = new JsonObject();
	        JsonObject responseJson = Utils.createResponse("SIGNUP_RECRUITER", "SUCCESS", "");
	        responseJson.remove("data"); 
	        responseJson.add("data", responseData);
	        out.println(Utils.toJsonString(responseJson));
	    }
	    
	    private void handleLoginCompany(JsonObject requestJson, PrintWriter out) throws IOException {
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

	        List<Company> companys = readCompanyDatabase();
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
	    
	    private void handleLookupCompany(JsonObject requestJson, PrintWriter out) throws IOException{
	        if (requestJson != null && requestJson.has("token")) {
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
	        }
	    }
	    
	    private void handleUpdateCompany(JsonObject requestJson, PrintWriter out) throws IOException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String token = requestJson.get("token").getAsString(); 

	        int companyId = JWTValidator.getIdClaim(token);
	        List<Company> companys = readCompanyDatabase();
	        
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
	    }
	    
	    private void handleDeleteCompany(JsonObject requestJson, PrintWriter out) throws IOException {
	        JsonObject data = requestJson.getAsJsonObject("data");
	        String token = requestJson.get("token").getAsString();
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
	        out.println(Utils.toJsonString(responseJson));
	    }
	    
	    //Criação BD Empresa 
	    
	    private List<Company> readCompanyDatabase() throws IOException {
	        List<Company> companys = new ArrayList<>();
	        try (BufferedReader br = new BufferedReader(new FileReader(COMPANY_DATABASE_FILE))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                String[] parts = line.split(",");
	                Company company = new Company(parts[0], parts[1], parts[2], parts[3], parts[4], Integer.parseInt(parts[5]));
	                companys.add(company);
	            }
	        } catch (FileNotFoundException e) {
	        	
	        }
	        return companys;
	    }

	    private void writeCompanyDatabase(List<Company> companys) throws IOException {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COMPANY_DATABASE_FILE))) {
	            for (Company company : companys) {
	                String userString = company.getEmail() + "," + company.getPassword() + "," + company.getName() + "," + company.getIndustry() + "," + company.getDescription() + "," + company.getId();
	                bw.write(userString);
	                bw.newLine();
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
