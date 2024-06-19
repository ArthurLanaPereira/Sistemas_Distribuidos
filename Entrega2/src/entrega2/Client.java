package entrega2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {    
    private static String serverIP;
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        EnterIP ipscn = new EnterIP();
        ipscn.setVisible(true);
    }

    public static void startConnection(String ip, boolean isEmpresa) {
        serverIP = ip;
        try {
            socket = new Socket(serverIP, 21234);
            System.out.println("Conectado ao servidor: " + serverIP);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            handleUserType(isEmpresa);

        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
        }
    }

    private static void handleUserType(boolean isEmpresa) throws IOException {
        if (isEmpresa) {
            handleRecruiterActions();
        } else {
            handleCandidateActions();
        }
    }

    private static void handleCandidateActions() throws IOException {
        String token = null;
        while (true) {
        	System.out.println("Logado como Candidato");
            System.out.println("Escolha uma opção:");
            System.out.println("1. Login");
            System.out.println("2. Cadastro");
            System.out.println("3. Atualização de dados");
            System.out.println("4. Exclusão de conta");
            System.out.println("5. Sair");
            System.out.println("6. Ver dados");
            System.out.println("7. Inserir Habilidade");
            System.out.println("8. Olhar Habilidade");
            System.out.println("9. Olhar todas Habilidade");
            System.out.println("10. Deletar Habilidade");
            System.out.println("11. Atualizar Habilidade");
            System.out.println("12. Buscar Emprego");

            int option = getUserOption();

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
                case 7:
                    IncludeSkill.handleIncludeSkill(reader, out, in, token);
                    break;
                case 8:
                    LookUpSkill.handleLookUpSkill(reader, in, out, token);
                    break;
                case 9:
                	LookUpSkillSet.handleLookUpSkillSet(in, out, token);
                    break;
                case 10:
                    DeleteSkill.handleDeleteSkill(reader, out, in, token);
                    break;
                case 11:
                    UpdateSkill.handleUpdateSkill(reader, out, in, token);
                    break;
                case 12:
                    SearchJob.handleSearchJob(reader, out, in, token);
                    break;
                default:
                    System.out.println("Opção inválida. Por favor, digite novamente.");
            }
        }
    }

    private static void handleRecruiterActions() throws IOException {
        String token = null;
        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1. Login");
            System.out.println("2. Cadastro");
            System.out.println("3. Atualização de dados");
            System.out.println("4. Exclusão de conta");
            System.out.println("5. Sair");
            System.out.println("6. Ver dados");
            System.out.println("7. Inserir Emprego");
            System.out.println("8. Olhar Emprego");
            System.out.println("9. Olhar todos os Empregos");
            System.out.println("10. Deletar Emprego");
            System.out.println("11. Atualizar Emprego");

            int option = getUserOption();

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
                case 7:
                    IncludeJob.handleIncludeJob(reader, out, in, token);
                    break;
                case 8:
                    LookUpJob.handleLookUpJob(reader, in, out, token);
                    break;
                case 9:
                	LookUpJobSet.handleLookUpJobSet(in, out, token);
                    break;
                case 10:
                    DeleteJob.handleDeleteJob(reader, out, in, token);
                    break;
                case 11:
                    UpdateJob.handleUpdateJob(reader, out, in, token);
                    break;
                default:
                    System.out.println("Opção inválida. Por favor, digite novamente.");
            }
        }
    }

    private static int getUserOption() throws IOException {
        int option;
        try {
            option = Integer.parseInt(reader.readLine());
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida. Por favor, digite novamente.");
            option = -1;
        }
        return option;
    }
}
