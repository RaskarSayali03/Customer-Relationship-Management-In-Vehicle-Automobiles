package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    JTextField txtUsername;
    JPasswordField txtPassword;
    JButton btnLogin;

    public LoginFrame() {
        setTitle("Customer Enquiry Management System - Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblTitle = new JLabel("LOGIN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setHorizontalAlignment(JLabel.CENTER);

        JLabel lblUser = new JLabel("Username:");
        JLabel lblPass = new JLabel("Password:");

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

        btnLogin = new JButton("Login");

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(lblUser);
        panel.add(txtUsername);
        panel.add(lblPass);
        panel.add(txtPassword);
        panel.add(new JLabel()); // empty cell
        panel.add(btnLogin);

        add(lblTitle, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        // Login button action
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());

                if(username.equals("admin") && password.equals("admin")) {
                    JOptionPane.showMessageDialog(null, "Login Successful");
                    new DashboardFrame(username).setVisible(true); // ✅ show dashboard
                    dispose(); // close login window
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Login");
                }
            }
        });

        setVisible(true);
    }
}
