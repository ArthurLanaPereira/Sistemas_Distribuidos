package entrega2;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EnterIP extends JFrame {

    private JPanel enterIPPanel;
    private JTextField IPTextField;
    private JCheckBox isEmpresaCheckBox;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    EnterIP frame = new EnterIP();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public EnterIP() {
        setTitle("Conexão de IP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        enterIPPanel = new JPanel();
        enterIPPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(enterIPPanel);
        enterIPPanel.setLayout(null);

        JLabel lblNewLabel = new JLabel("Digite IP do servidor:");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel.setBounds(10, 102, 137, 40);
        enterIPPanel.add(lblNewLabel);

        IPTextField = new JTextField();
        IPTextField.setBounds(157, 104, 196, 40);
        enterIPPanel.add(IPTextField);
        IPTextField.setColumns(10);

        isEmpresaCheckBox = new JCheckBox("Empresa");
        isEmpresaCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 16));
        isEmpresaCheckBox.setBounds(6, 7, 97, 23);
        enterIPPanel.add(isEmpresaCheckBox);

        JButton enterIPButton = new JButton("Entrar");
        enterIPButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String serverIP = IPTextField.getText();
                boolean isEmpresa = isEmpresaCheckBox.isSelected();
                Client.startConnection(serverIP, isEmpresa);
                dispose();
            }
        });
        enterIPButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
        enterIPButton.setBounds(147, 175, 116, 40);
        enterIPPanel.add(enterIPButton);
    }
}
