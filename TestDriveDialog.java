package ui;

import util.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TestDriveDialog extends JFrame {

    private JTextField txtSearch;
    private JTable tblCustomers;
    private DefaultTableModel model;
    private EnquiryDetailFrame detailFrame;

    private JTextField txtName, txtPhone, txtEmail, txtModel, txtDate, txtTime;
    private JButton btnSchedule, btnDone;

    public TestDriveDialog(EnquiryDetailFrame frame) {
        this.detailFrame = frame;
        initUI();
    }

    private void initUI() {
        setTitle("Test Drive Scheduler");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ================= SEARCH PANEL =================
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        top.add(new JLabel("Enter Customer Number / Enquiry No:"));
        top.add(txtSearch);
        top.add(btnSearch);
        add(top, BorderLayout.NORTH);

        // ================= CUSTOMER TABLE =================
        model = new DefaultTableModel(
                new Object[]{"Enquiry No", "Name", "Phone", "Email", "Model"}, 0
        ) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblCustomers = new JTable(model);
        tblCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tblCustomers);
        add(scroll, BorderLayout.CENTER);

        // ================= DETAILS + ACTIONS =================
        JPanel bottom = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtName = new JTextField(); txtName.setEditable(false);
        txtPhone = new JTextField(); txtPhone.setEditable(false);
        txtEmail = new JTextField(); txtEmail.setEditable(false);
        txtModel = new JTextField(); txtModel.setEditable(false);
        txtDate = new JTextField("yyyy-mm-dd");
        txtTime = new JTextField("HH:mm");

        btnSchedule = new JButton("Schedule Test Drive");
        btnDone = new JButton("Mark Test Drive Done");
        btnDone.setEnabled(false);

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; bottom.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; bottom.add(txtName, gbc);
        gbc.gridx = 2; bottom.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 3; bottom.add(txtPhone, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y; bottom.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; bottom.add(txtEmail, gbc);
        gbc.gridx = 2; bottom.add(new JLabel("Model:"), gbc);
        gbc.gridx = 3; bottom.add(txtModel, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y; bottom.add(new JLabel("Test Drive Date:"), gbc);
        gbc.gridx = 1; bottom.add(txtDate, gbc);
        gbc.gridx = 2; bottom.add(new JLabel("Time:"), gbc);
        gbc.gridx = 3; bottom.add(txtTime, gbc);

        y++;
        gbc.gridx = 2; gbc.gridy = y; bottom.add(btnSchedule, gbc);
        gbc.gridx = 3; bottom.add(btnDone, gbc);

        add(bottom, BorderLayout.SOUTH);

        // ================= EVENTS =================
        btnSearch.addActionListener(e -> searchCustomer());
        tblCustomers.getSelectionModel().addListSelectionListener(e -> loadSelectedCustomer());
        tblCustomers.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) loadSelectedCustomer();
            }
        });
        btnSchedule.addActionListener(e -> scheduleTestDrive());
        btnDone.addActionListener(e -> markTestDriveDone());
    }

    // ================= SEARCH CUSTOMERS =================
    private void searchCustomer() {
        String keyword = txtSearch.getText().trim();
        model.setRowCount(0);
        if (keyword.isEmpty()) return;

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT customer_id, name, phone, email, model_selected " +
                    "FROM customer " +
                    "WHERE customer_id LIKE ? OR phone LIKE ?"
            );
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("model_selected")
                });
            }

            if (model.getRowCount() == 0)
                JOptionPane.showMessageDialog(this, "No matching customer found.");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching customer");
        }
    }

    // ================= LOAD SELECTED CUSTOMER =================
    private void loadSelectedCustomer() {
        int row = tblCustomers.getSelectedRow();
        if (row < 0) return;

        txtName.setText(model.getValueAt(row, 1).toString());
        txtPhone.setText(model.getValueAt(row, 2).toString());
        txtEmail.setText(model.getValueAt(row, 3).toString());
        txtModel.setText(model.getValueAt(row, 4).toString());

        int enquiryId = Integer.parseInt(model.getValueAt(row, 0).toString());
        btnDone.setEnabled(isTestDriveScheduled(enquiryId));
    }

    private boolean isTestDriveScheduled(int enquiryId) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT status FROM test_drives WHERE enquiry_id=? ORDER BY test_drive_id DESC LIMIT 1"
            );
            ps.setInt(1, enquiryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "SCHEDULED".equalsIgnoreCase(rs.getString("status"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // ================= SCHEDULE TEST DRIVE =================
    private void scheduleTestDrive() {
        int row = tblCustomers.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a customer!"); return; }

        int enquiryId = Integer.parseInt(model.getValueAt(row,0).toString());
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO test_drives (enquiry_id, test_date, test_time, status) VALUES (?, ?, ?, 'SCHEDULED')"
            );
            ps.setInt(1, enquiryId);
            ps.setDate(2, java.sql.Date.valueOf(txtDate.getText()));
            ps.setTime(3, java.sql.Time.valueOf(txtTime.getText() + ":00"));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Test Drive Scheduled!");
            btnDone.setEnabled(true);
            if (detailFrame != null) detailFrame.loadTestDrives();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error scheduling test drive:\n"+e.getMessage());
        }
    }

    // ================= MARK TEST DRIVE DONE =================
    private void markTestDriveDone() {
        int row = tblCustomers.getSelectedRow();
        if (row < 0) return;

        int enquiryId = Integer.parseInt(model.getValueAt(row,0).toString());
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE test_drives SET status='COMPLETED', completed_at=NOW() " +
                    "WHERE enquiry_id=? AND status='SCHEDULED'"
            );
            ps.setInt(1, enquiryId);
            int updated = ps.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Test Drive marked COMPLETED!");
                btnDone.setEnabled(false);

                if (detailFrame != null) detailFrame.loadTestDrives();
            } else {
                JOptionPane.showMessageDialog(this, "No scheduled test drive found.");
            }

        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        new TestDriveDialog(null).setVisible(true);
    }
}