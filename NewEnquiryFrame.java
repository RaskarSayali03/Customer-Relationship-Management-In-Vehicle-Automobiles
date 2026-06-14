package ui;
import model.Customer;
import dao.CustomerDAO;

import javax.swing.*;
import java.awt.*;

public class NewEnquiryFrame extends JFrame {

    public NewEnquiryFrame() {

        setTitle("Customer Enquiry Management System");

        /* ================= TOP BAR (BACK + TITLE) ================= */
        JButton btnBack = new JButton("← Back");
        btnBack.setPreferredSize(new Dimension(80, 30));

        JLabel lblTitle = new JLabel("New Customer Enquiry");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 37, 41));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(240, 242, 245));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        titlePanel.add(btnBack, BorderLayout.WEST);
        titlePanel.add(lblTitle, BorderLayout.CENTER);

        /* ================= FONTS ================= */
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        /* ================= FIELDS ================= */
        JLabel lblName = new JLabel("Customer Name ");
        lblName.setFont(labelFont);
        JLabel starName = new JLabel("*");
        starName.setForeground(Color.RED);
        JTextField txtName = new JTextField(20);
        txtName.setFont(fieldFont);
        JLabel errorName = new JLabel(" ");
        errorName.setForeground(Color.RED);

        JLabel lblEmail = new JLabel("Email ");
        lblEmail.setFont(labelFont);
        JLabel starEmail = new JLabel("*");
        starEmail.setForeground(Color.RED);
        JTextField txtEmail = new JTextField(20);
        txtEmail.setFont(fieldFont);
        JLabel errorEmail = new JLabel(" ");
        errorEmail.setForeground(Color.RED);

        JLabel lblMobile = new JLabel("Mobile Number ");
        lblMobile.setFont(labelFont);
        JLabel starMobile = new JLabel("*");
        starMobile.setForeground(Color.RED);
        JTextField txtMobile = new JTextField(20);
        txtMobile.setFont(fieldFont);
        JLabel errorMobile = new JLabel(" ");
        errorMobile.setForeground(Color.RED);

        JLabel lblSource = new JLabel("Enquiry Source ");
        lblSource.setFont(labelFont);
        JLabel starSource = new JLabel("*");
        starSource.setForeground(Color.RED);
        String[] sources = {"Walk-in", "Telephone", "Digital", "Field Visit"};
        JComboBox<String> cmbSource = new JComboBox<>(sources);
        JLabel errorSource = new JLabel(" ");
        errorSource.setForeground(Color.RED);

        JLabel lblModel = new JLabel("Interested Product ");
        lblModel.setFont(labelFont);
        JLabel starModel = new JLabel("*");
        starModel.setForeground(Color.RED);
        JTextField txtModel = new JTextField(20);
        txtModel.setFont(fieldFont);
        JLabel errorModel = new JLabel(" ");
        errorModel.setForeground(Color.RED);

        JLabel lblStatus = new JLabel("Status ");
        lblStatus.setFont(labelFont);
        String[] statuses = {"New", "In Progress", "Closed"};
        JComboBox<String> cmbStatus = new JComboBox<>(statuses);

        JLabel lblRemarks = new JLabel("Remarks");
        lblRemarks.setFont(labelFont);
        JTextArea txtRemarks = new JTextArea(4, 22);
        txtRemarks.setLineWrap(true);
        txtRemarks.setWrapStyleWord(true);
        txtRemarks.setFont(fieldFont);
        JScrollPane remarkScroll = new JScrollPane(txtRemarks);

        /* ================= BUTTONS ================= */
        JButton btnSave = new JButton("Save Enquiry");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(new Color(13, 110, 253));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);

        JButton btnReset = new JButton("Reset");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(btnReset);
        buttonPanel.add(btnSave);

        /* ================= FORM PANEL ================= */
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0 - Name
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblName, gbc);
        gbc.gridx = 1; formPanel.add(starName, gbc);
        gbc.gridx = 2; formPanel.add(txtName, gbc);
        gbc.gridx = 3; formPanel.add(errorName, gbc);

        // Row 1 - Email
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(lblEmail, gbc);
        gbc.gridx = 1; formPanel.add(starEmail, gbc);
        gbc.gridx = 2; formPanel.add(txtEmail, gbc);
        gbc.gridx = 3; formPanel.add(errorEmail, gbc);

        // Row 2 - Mobile
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(lblMobile, gbc);
        gbc.gridx = 1; formPanel.add(starMobile, gbc);
        gbc.gridx = 2; formPanel.add(txtMobile, gbc);
        gbc.gridx = 3; formPanel.add(errorMobile, gbc);

        // Row 3 - Source
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(lblSource, gbc);
        gbc.gridx = 2; formPanel.add(cmbSource, gbc);
        gbc.gridx = 3; formPanel.add(errorSource, gbc);

        // Row 4 - Product
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(lblModel, gbc);
        gbc.gridx = 2; formPanel.add(txtModel, gbc);
        gbc.gridx = 3; formPanel.add(errorModel, gbc);

        // Row 5 - Status
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(lblStatus, gbc);
        gbc.gridx = 2; formPanel.add(cmbStatus, gbc);

        // Row 6 - Remarks
        gbc.gridx = 0; gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(lblRemarks, gbc);
        gbc.gridx = 2; formPanel.add(remarkScroll, gbc);

        // Row 7 - Buttons
        gbc.gridx = 2; gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(buttonPanel, gbc);

        /* ================= FRAME ================= */
        setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        /* ================= ACTIONS ================= */
        btnBack.addActionListener(e -> {
            // ✅ FIX: pass a string to DashboardFrame to remove compile error
            new DashboardFrame("Admin").setVisible(true);
            dispose();
        });

        btnReset.addActionListener(e -> {
            txtName.setText("");
            txtEmail.setText("");
            txtMobile.setText("");
            txtModel.setText("");
            txtRemarks.setText("");
            cmbSource.setSelectedIndex(0);
            cmbStatus.setSelectedIndex(0);

            errorName.setText(" ");
            errorEmail.setText(" ");
            errorMobile.setText(" ");
            errorModel.setText(" ");
            errorSource.setText(" ");
        });

        btnSave.addActionListener(e -> {
            boolean valid = true;

            errorName.setText(" ");
            errorEmail.setText(" ");
            errorMobile.setText(" ");
            errorModel.setText(" ");
            errorSource.setText(" ");

            if (txtName.getText().trim().isEmpty()) {
                errorName.setText("Name cannot be empty");
                valid = false;
            }

            String email = txtEmail.getText().trim();
            if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
                errorEmail.setText("Enter valid email");
                valid = false;
            }

            if (!txtMobile.getText().trim().matches("\\d{10}")) {
                errorMobile.setText("Enter 10-digit number");
                valid = false;
            }

            if (txtModel.getText().trim().isEmpty()) {
                errorModel.setText("Product cannot be empty");
                valid = false;
            }

           if (valid) {

    Customer customer = new Customer(
        txtName.getText().trim(),
        txtEmail.getText().trim(),
        txtMobile.getText().trim(),
        cmbSource.getSelectedItem().toString(),
        txtModel.getText().trim(),
        "General",
        cmbStatus.getSelectedItem().toString()
    );

    CustomerDAO dao = new CustomerDAO();
    boolean saved = dao.insertCustomer(customer);

   if (saved) {
    JOptionPane.showMessageDialog(this, "Enquiry saved successfully");

    new DashboardFrame("admin").setVisible(true);

    dispose();
}
    else {
        JOptionPane.showMessageDialog(this, "Error saving enquiry");
    }
}

        });
    }
}
