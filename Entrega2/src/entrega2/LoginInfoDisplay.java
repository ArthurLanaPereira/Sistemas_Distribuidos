package entrega2;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class LoginInfoDisplay extends JFrame {

    private static JPanel contentPane;
    private static JLabel lblUsers;

    public static void updateDisplay() {
        EventQueue.invokeLater(() -> {
            try {
                if (contentPane != null) {
                    contentPane.removeAll();
                    List<LoggedUsers.UserInfo> users = LoggedUsers.getLoggedUsers();
                    int yPosition = 50;
                    for (LoggedUsers.UserInfo user : users) {
                        JLabel lblIP = new JLabel("IP: " + user.getIp());
                        lblIP.setFont(new Font("Tahoma", Font.PLAIN, 14));
                        lblIP.setBounds(10, yPosition, 400, 30);
                        contentPane.add(lblIP);

                        JLabel lblEmail = new JLabel("Email: " + user.getEmail());
                        lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
                        lblEmail.setBounds(10, yPosition + 30, 400, 30);
                        contentPane.add(lblEmail);

                        JLabel lblName = new JLabel("Nome: " + user.getName());
                        lblName.setFont(new Font("Tahoma", Font.PLAIN, 14));
                        lblName.setBounds(10, yPosition + 60, 400, 30);
                        contentPane.add(lblName);

                        yPosition += 100;
                    }
                    contentPane.revalidate();
                    contentPane.repaint();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LoginInfoDisplay() {
        setTitle("Informações de Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        lblUsers = new JLabel("Usuários logados:");
        lblUsers.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblUsers.setBounds(10, 10, 400, 30);
        contentPane.add(lblUsers);

        updateDisplay(); // Atualiza a exibição ao criar a janela
    }

    public static void updateLoggedUsers(List<LoggedUsers.UserInfo> loggedUsers) {
        EventQueue.invokeLater(() -> {
            try {
                contentPane.removeAll();
                int yPosition = 50;
                for (LoggedUsers.UserInfo user : loggedUsers) {
                    JLabel lblIP = new JLabel("IP: " + user.getIp());
                    lblIP.setFont(new Font("Tahoma", Font.PLAIN, 14));
                    lblIP.setBounds(10, yPosition, 400, 30);
                    contentPane.add(lblIP);

                    JLabel lblEmail = new JLabel("Email: " + user.getEmail());
                    lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
                    lblEmail.setBounds(10, yPosition + 30, 400, 30);
                    contentPane.add(lblEmail);

                    JLabel lblName = new JLabel("Nome: " + user.getName());
                    lblName.setFont(new Font("Tahoma", Font.PLAIN, 14));
                    lblName.setBounds(10, yPosition + 60, 400, 30);
                    contentPane.add(lblName);

                    yPosition += 100;
                }
                contentPane.revalidate();
                contentPane.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void display() {
        EventQueue.invokeLater(() -> {
            try {
                LoginInfoDisplay frame = new LoginInfoDisplay();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
