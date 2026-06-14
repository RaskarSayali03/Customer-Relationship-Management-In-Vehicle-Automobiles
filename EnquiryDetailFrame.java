package ui;

import util.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class EnquiryDetailFrame extends JFrame {

    private int enquiryId;

    // ========== DETAIL LABELS ==========
    JLabel lblName, lblPhone, lblEmail, lblLocation;
    JLabel lblModel, lblType, lblStatus, lblEnquiryNo;

    // ========== FOLLOW-UP ==========
    JPanel followUpListPanel;
    JButton btnAddFollowUp;

    public EnquiryDetailFrame(int enquiryId) {
        this.enquiryId = enquiryId;

        setTitle("Enquiry Details - Mahindra Salesforce");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // MANDATORY NAVBAR
        add(new NavbarPanel(this), BorderLayout.NORTH);

        // page content below navbar
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(actionNavbar(), BorderLayout.NORTH);
        wrapper.add(mainContent(), BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        loadData();
        loadFollowUps();
    }

    // ================= RED ACTION BAR =================
JPanel actionNavbar() {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
    p.setBackground(new Color(139, 0, 0));

    // Declare buttons
    JButton btnEdit = new JButton("Edit Details");
    JButton btnProduct = new JButton("Add Product Interests");
    btnAddFollowUp = new JButton("Add Follow Up");
    JButton btnConvert = new JButton("Convert into Enquiry");
    JButton btnTransfer = new JButton("Quick Enquiry Transfer");
    JButton btnViewTestDrives = new JButton("View Test Drives"); // new button
	
	btnViewTestDrives.addActionListener(e -> {
    // Open the Test Drive Details dialog for this enquiry
    TestDriveDetailsDialog dialog = new TestDriveDetailsDialog(this, enquiryId);
    dialog.setVisible(true);
	});
    // Put all buttons into an array for styling & adding to panel
    JButton[] buttons = {btnEdit, btnProduct, btnAddFollowUp, btnConvert, btnTransfer, btnViewTestDrives};
    for (JButton b : buttons) {
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(139, 0, 0));
        b.setFocusPainted(false);
        p.add(b);
    }

    // Add Follow-up action
    btnAddFollowUp.addActionListener(e -> {
        FollowUpDialog dialog = new FollowUpDialog(this, enquiryId, "ICRE");
        dialog.setVisible(true);
        loadFollowUps(); // refresh follow-ups
    });

    // Add Test Drives action
    btnViewTestDrives.addActionListener(e -> {
        TestDriveDetailsDialog dialog = new TestDriveDetailsDialog(this, enquiryId);
        dialog.setVisible(true);
    });

    return p;
}
    // ================= MAIN CONTENT =================
    JPanel mainContent() {
        JPanel body = new JPanel(new GridLayout(1, 3, 15, 0));
        body.setBorder(new EmptyBorder(15, 15, 15, 15));

        body.add(relatedPanel());
        body.add(detailPanel());
        body.add(followUpPanel()); // only follow-ups now

        return body;
    }

    // ================= LEFT PANEL =================
    JPanel relatedPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new TitledBorder("Related"));

        p.add(new JLabel("_ Product Interests"));
        p.add(Box.createVerticalStrut(10));
        p.add(new JLabel("_ Finance"));
        p.add(Box.createVerticalStrut(10));
        p.add(new JLabel("_ Exchange"));

        return p;
    }

    // ================= CENTER PANEL =================
    JPanel detailPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new TitledBorder("Enquiry Details"));

        lblName = new JLabel();
        lblPhone = new JLabel();
        lblEmail = new JLabel();
        lblLocation = new JLabel();
        lblModel = new JLabel();
        lblType = new JLabel();
        lblStatus = new JLabel();
        lblEnquiryNo = new JLabel();

        p.add(section("Customer"));
        p.add(lblName);
        p.add(lblPhone);
        p.add(lblEmail);
        p.add(lblLocation);

        p.add(Box.createVerticalStrut(10));
        p.add(section("Quick Enquiry"));
        p.add(lblEnquiryNo);
        p.add(lblModel);
        p.add(lblType);
        p.add(lblStatus);

        return p;
    }

    JLabel section(String title) {
        JLabel l = new JLabel(title);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return l;
    }

    // ================= FOLLOW-UP PANEL =================
    JPanel followUpPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(5, 5, 5, 5));

        followUpListPanel = new JPanel();
        followUpListPanel.setLayout(new BoxLayout(followUpListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollFollowUp = new JScrollPane(followUpListPanel);
        scrollFollowUp.setBorder(new TitledBorder("Follow Ups"));

        root.add(scrollFollowUp, BorderLayout.CENTER);
        return root;
    }

    // ================= LOAD FOLLOW-UPS =================
    void loadFollowUps() {
        followUpListPanel.removeAll();

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT remarks, notes, created_at, status " +
                            "FROM follow_ups WHERE enquiry_id=? ORDER BY created_at DESC"
            );
            ps.setInt(1, enquiryId);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                JPanel card = createFollowUpCard(
                        rs.getString("remarks"),
                        rs.getString("notes"),
                        rs.getTimestamp("created_at"),
                        rs.getString("status")
                );
                followUpListPanel.add(card);
                followUpListPanel.add(Box.createVerticalStrut(8));
            }

            if (!found) followUpListPanel.add(new JLabel("No follow-ups added yet"));

        } catch (Exception e) { e.printStackTrace(); }

        followUpListPanel.revalidate();
        followUpListPanel.repaint();
    }

    JPanel createFollowUpCard(String remark, String notes, Timestamp date, String status) {
        Color strip = "COMPLETED".equalsIgnoreCase(status) ? new Color(0, 150, 0) : new Color(0, 120, 215);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new MatteBorder(0, 4, 0, 0, strip),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel lblDate = new JLabel(new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(date));
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDate.setForeground(Color.GRAY);

        JLabel lblRemark = new JLabel("Remark: " + remark);
        lblRemark.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblNotes = new JLabel("<html>" + notes + "</html>");
        lblNotes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNotes.setForeground(Color.DARK_GRAY);

        card.add(lblDate);
        card.add(Box.createVerticalStrut(6));
        card.add(lblRemark);
        card.add(Box.createVerticalStrut(4));
        card.add(lblNotes);

        return card;
    }

    // ================= LOAD ENQUIRY DATA =================
    void loadData() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM customer WHERE customer_id=?"
            );
            ps.setInt(1, enquiryId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblName.setText("Name: " + rs.getString("name"));
                lblPhone.setText("Mobile: " + rs.getString("phone"));
                lblEmail.setText("Email: " + rs.getString("email"));
                lblLocation.setText("Location: " + rs.getString("location"));

                lblEnquiryNo.setText("Enquiry No: QE-" + enquiryId);
                lblModel.setText("Model: " + rs.getString("model_selected"));
                lblType.setText("Enquiry Type: " + rs.getString("enquiry_type"));
                lblStatus.setText("Status: " + rs.getString("status"));
            }

        } catch (Exception e) { e.printStackTrace(); }
    }
	// ===== DUMMY METHOD TO SATISFY TestDriveDialog CALLS =====
	public void loadTestDrives() {
    // Test drive table is removed, so nothing to refresh here
	}
}