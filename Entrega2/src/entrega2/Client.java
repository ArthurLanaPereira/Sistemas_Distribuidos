package entrega2;

import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;

public class Client {	
	 public static void main(String[] args) {
		 
		 /*if (args.length < 1) {
			 IPScreen ipScreen = new IPScreen();
		    } else {
		        String serverIP = args[0];*/
	        try {
	            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	           String serverIP = enterIP(reader);

	            Socket socket = new Socket(serverIP, 21234);
	            System.out.println("Conectado ao servidor: " + serverIP);

	            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	            
	            String token = null;

	            System.out.println("Você é 1 - Candidato ou 2 - Recrutador");
	            
	            int resposta = Integer.parseInt(reader.readLine());
	               
	            if(resposta == 1) {
	            	while (true) {
		                System.out.println("Escolha uma opção:");
		                System.out.println("1. Login");
		                System.out.println("2. Cadastro");
		                System.out.println("3. Atualização de dados");
		                System.out.println("4. Exclusão de conta");
		                System.out.println("5. Sair");
		                System.out.println("6. Ver dados");

		                int option;
		                try {
		                    option = Integer.parseInt(reader.readLine());
		                } catch (NumberFormatException e) {
		                    System.out.println("Opção inválida. Por favor, digite novamente.");
		                    continue;
		                }

		                switch (option) {
		                    case 1:
		                        token = LoginClient.handleLogin(reader, out, in);
		                        break;
		                    case 2:
		                        SignupClient.handleSignup(reader, out, in);
		                        break;
		                    case 3:
		                        UpdateClient.handleUpdateAccount(reader, out, in, token);
		                        break;
		                    case 4:
		                        DeleteClient.handleDeleteAccount(reader, out, in, token);
		                        break;
		                    case 5:
		                        token = LogoutClient.handleLogout(in, out, token);
		                        break;
		                    case 6:
		                        LookUpClient.handleLookUp(in, out, token);
		                        break;
		                    default:
		                        System.out.println("Opção inválida. Por favor, digite novamente.");
		                }
		            }
	            }
	            else {
	            	while (true) {
		                System.out.println("Escolha uma opção:");
		                System.out.println("1. Login");
		                System.out.println("2. Cadastro");
		                System.out.println("3. Atualização de dados");
		                System.out.println("4. Exclusão de conta");
		                System.out.println("5. Sair");
		                System.out.println("6. Ver dados");

		                int option;
		                try {
		                    option = Integer.parseInt(reader.readLine());
		                } catch (NumberFormatException e) {
		                    System.out.println("Opção inválida. Por favor, digite novamente.");
		                    continue;
		                }

		                switch (option) {
		                    case 1:
		                        token = LoginCompany.handleLoginCompany(reader, out, in);
		                        break;
		                    case 2:
		                        SignupCompany.handleSignupCompany(reader, out, in);
		                        break;
		                    case 3:
		                        UpdateCompany.handleUpdateAccountCompany(reader, out, in, token);
		                        break;
		                    case 4:
		                        DeleteCompany.handleDeleteAccountCompany(reader, out, in, token);
		                        break;
		                    case 5:
		                        token = LogoutCompany.handleLogoutCompany(in, out, token);
		                        break;
		                    case 6:
		                        LookUpCompany.handleLookUpCompany(in, out, token);
		                        break;
		                    default:
		                        System.out.println("Opção inválida. Por favor, digite novamente.");
		                }
		            }
	            	
	            }
	            
		        } catch (IOException e) {
		            System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
		        }
	        }

	 
	 public static String enterIP(BufferedReader reader) throws IOException {
		    System.out.println("Digite o endereço IP do servidor:");
		    return reader.readLine();
	 }
}
