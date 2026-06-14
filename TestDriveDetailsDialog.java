package ui;

import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class TestDriveDetailsDialog extends JDialog {

    private int enquiryId;
    private JTable tblTestDrives;

    public TestDriveDetailsDialog(JFrame parent, int enquiryId) {
        super(parent, "Test Drive Details", true);
        this.enquiryId = enquiryId;

        setSize(900, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Table setup
        tblTestDrives = new JTable();
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Test Drive ID", "Customer Name", "Mobile No", "Vehicle", "Enquiry ID", "Date", "Time", "Status"}, 0
        ) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblTestDrives.setModel(model);
        add(new JScrollPane(tblTestDrives), BorderLayout.CENTER);

        loadTestDriveData();
    }

    private void loadTestDriveData() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT td.test_drive_id, td.test_date, td.test_time, td.status, " +
                    "c.name AS customer_name, c.phone AS customer_phone, c.model_selected " +
                    "FROM test_drives td " +
                    "JOIN customer c ON td.enquiry_id = c.customer_id " +
                    "WHERE td.enquiry_id = ? " +
                    "ORDER BY td.test_date DESC, td.test_time DESC"
            );
            ps.setInt(1, enquiryId);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tblTestDrives.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("test_drive_id"),
                        rs.getString("customer_name"),
                        rs.getString("customer_phone"),
                        rs.getString("model_selected"),
                        enquiryId,
                        rs.getDate("test_date"),
                        rs.getTime("test_time"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}