package ui;

import javax.swing.*;
import java.awt.*;

public class NavbarPanel extends JPanel {

    public NavbarPanel(JFrame currentFrame) {

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1200, 60));
        setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Mahindra Salesforce");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        menuPanel.setBackground(Color.WHITE);

        JButton btnDashboard = new JButton("Dashboard");
        JButton btnAssignment = new JButton("Assignment Queue");

        btnDashboard.addActionListener(e -> {
            currentFrame.dispose();
            new DashboardFrame("Executive").setVisible(true);
        });

        menuPanel.add(btnDashboard);
        menuPanel.add(btnAssignment);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            currentFrame.dispose();
            new DashboardFrame("Executive").setVisible(true);
        });

        add(lblTitle, BorderLayout.WEST);
        add(menuPanel, BorderLayout.CENTER);
        add(btnBack, BorderLayout.EAST);
    }
}
