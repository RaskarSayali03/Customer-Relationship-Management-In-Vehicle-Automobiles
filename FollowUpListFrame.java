package ui;

import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FollowUpListFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private int enquiryId;

    public FollowUpListFrame(int enquiryId) {

        this.enquiryId = enquiryId;

        setTitle("Follow-Ups for Enquiry ID : " + enquiryId);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---------- TABLE ----------
        String[] columns = {
                "FollowUp ID",
                "Created By",
                "Type",
                "Likely Purchase",
                "Remarks",
                "Remark Type",
                "Notes",
                "Next Date",
                "Next Time",
                "Status",
                "Created At"
        };

        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // view only
            }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---------- LOAD DATA ----------
        loadFollowUps();

        // ---------- BACK BUTTON ----------
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnBack);

        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadFollowUps() {

        model.setRowCount(0);

        String sql =
                "SELECT * FROM follow_ups " +
                "WHERE enquiry_id = ? " +
                "ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, enquiryId);
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;

                model.addRow(new Object[]{
                        rs.getInt("followup_id"),
                        rs.getString("created_by_role"),
                        rs.getString("followup_type"),
                        rs.getString("likely_purchase"),
                        rs.getString("remarks"),
                        rs.getString("remark_type"),
                        rs.getString("notes"),
                        rs.getDate("next_followup_date"),
                        rs.getTime("next_followup_time"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                });
            }

            // ✅ CLEAR MESSAGE IF NO FOLLOW-UP
            if (!hasData) {
                JOptionPane.showMessageDialog(
                        this,
                        "No follow-ups found for this enquiry.",
                        "Follow-Up",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading follow-ups",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}