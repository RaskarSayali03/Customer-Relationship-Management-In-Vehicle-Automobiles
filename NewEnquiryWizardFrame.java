package ui;

import util.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class NewEnquiryWizardFrame extends JFrame {
	
	private JFrame previousFrame;
    CardLayout cardLayout;
    JPanel cardPanel;
    JButton btnNext, btnPrevious;
    int step = 1;

    JTextField txtPhone, txtName, txtEmail;
    JComboBox<String> cmbLocation, cmbEnquiryType, cmbModel, cmbDealer;
    JComboBox<ComboItem> cmbSC;

    public NewEnquiryWizardFrame(JFrame previousFrame) {
		this.previousFrame = previousFrame;

        setTitle("New Enquiry – Customer Enquiry Management System");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(pageSearch(), "1");
        cardPanel.add(pageCustomer(), "2");
        cardPanel.add(pageQuickEnquiry(), "3");
        cardPanel.add(pageProduct(), "4");
        cardPanel.add(pageAssignment(), "5");

        add(cardPanel, BorderLayout.CENTER);
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));

		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(e -> {
		this.dispose();
		if (previousFrame != null) {
			previousFrame.setVisible(true);
		}
	});

		topPanel.add(btnBack, BorderLayout.EAST);
		add(topPanel, BorderLayout.NORTH);
	    add(bottomPanel(), BorderLayout.SOUTH);
    }

    // ---------------- PAGE 1 ----------------
    JPanel pageSearch() {
        JPanel p = basePanel("Search Customer");

        txtPhone = new JTextField(20);
        JButton btnSearch = new JButton("Search");

        btnSearch.addActionListener(e -> fetchCustomer());

        addRow(p, 1, "Phone Number", txtPhone);
        addRow(p, 2, "", btnSearch);

        return p;
    }

    void fetchCustomer() {
        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM customer_enquiry WHERE phone=? ORDER BY customer_id DESC LIMIT 1");
            ps.setString(1, txtPhone.getText().trim());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtName.setText(rs.getString("name"));
                txtEmail.setText(rs.getString("email"));
                cmbLocation.setSelectedItem(rs.getString("location"));

                txtName.setEditable(false);
                txtEmail.setEditable(false);
            } else {
                txtName.setText("");
                txtEmail.setText("");
                txtName.setEditable(true);
                txtEmail.setEditable(true);

                JOptionPane.showMessageDialog(this, "New customer");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------------- PAGE 2 ----------------
    JPanel pageCustomer() {
        JPanel p = basePanel("Customer Information");

        txtName = new JTextField(20);
        txtEmail = new JTextField(20);
        cmbLocation = new JComboBox<>(new String[]{
                "Rahata", "Shrirampur", "Sangamner", "Sarjepura"
        });

        addRow(p, 1, "Customer Name", txtName);
        addRow(p, 2, "Email", txtEmail);
        addRow(p, 3, "Location", cmbLocation);

        return p;
    }

    // ---------------- PAGE 3 ----------------
    JPanel pageQuickEnquiry() {
        JPanel p = basePanel("Quick Enquiry");

        cmbEnquiryType = new JComboBox<>(new String[]{
                "Walk-In", "Digital", "Telephone", "Field"
        });

        addRow(p, 1, "Enquiry Type", cmbEnquiryType);
        return p;
    }

    // ---------------- PAGE 4 ----------------
    JPanel pageProduct() {
        JPanel p = basePanel("Product Interest");

        cmbModel = new JComboBox<>(new String[]{
                "BOLERO", "XUV", "SCORPIO", "THAR"
        });

        addRow(p, 1, "Interested Model", cmbModel);
        return p;
    }

    // ---------------- PAGE 5 ----------------
    JPanel pageAssignment() {
        JPanel p = basePanel("Sales Consultant Assignment");

        cmbDealer = new JComboBox<>(new String[]{
                "SARJEPURA_CORNERSTONE AUTOMOBILES",
                "RAHATA_CORNERSTONE AUTOMOBILES"
        });

        cmbSC = new JComboBox<>();
        loadSCs();

        addRow(p, 1, "Dealer", cmbDealer);
        addRow(p, 2, "Sales Consultant", cmbSC);

        return p;
    }

    void loadSCs() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT sc_id, sc_name FROM search_consultant WHERE is_active=1");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cmbSC.addItem(new ComboItem(
                        rs.getInt("sc_id"),
                        rs.getString("sc_name")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------------- SAVE ----------------
    void saveEnquiry() {

        if (txtPhone.getText().trim().isEmpty()
                || txtName.getText().trim().isEmpty()
                || cmbSC.getSelectedItem() == null) {

            JOptionPane.showMessageDialog(this, "Please complete all required fields");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO customer" +
                "(name,email,phone,location,model_selected,enquiry_type,status,assigned_sc_id) " +
                "VALUES (?,?,?,?,?,?,'NEW',?)");

            ps.setString(1, txtName.getText());
            ps.setString(2, txtEmail.getText());
            ps.setString(3, txtPhone.getText());
            ps.setString(4, cmbLocation.getSelectedItem().toString());
            ps.setString(5, cmbModel.getSelectedItem().toString());
            ps.setString(6, cmbEnquiryType.getSelectedItem().toString());
            ps.setInt(7, ((ComboItem) cmbSC.getSelectedItem()).id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Enquiry Saved Successfully");
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ---------------- BOTTOM ----------------
    JPanel bottomPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnPrevious = new JButton("Previous");
        btnNext = new JButton("Next");

        btnPrevious.addActionListener(e -> {
            if (step > 1) {
                step--;
                cardLayout.show(cardPanel, String.valueOf(step));
            }
        });

        btnNext.addActionListener(e -> {
            if (step < 5) {
                step++;
                cardLayout.show(cardPanel, String.valueOf(step));
            } else {
                saveEnquiry();
            }
        });

        p.add(btnPrevious);
        p.add(btnNext);
        return p;
    }

    // ---------------- UI HELPERS ----------------
    JPanel basePanel(String title) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(30, 120, 30, 120));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 25, 0);
        p.add(lbl, gbc);

        return p;
    }

    void addRow(JPanel p, int row, String label, Component field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = row;
        p.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        p.add(field, gbc);
    }

    // ---------------- HELPER ----------------
    static class ComboItem {
        int id;
        String name;

        ComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }
}
