package ui;

import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AssignmentQueueFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> cmbSegment, cmbLocation, cmbModel;
    private JLabel lblPending;

    public AssignmentQueueFrame() {

        setTitle("Mahindra Salesforce – Assignment Queue");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== NAVBAR (MANDATORY) =====
        add(new NavbarPanel(this), BorderLayout.NORTH);

        // ===== TOP FILTER PANEL =====
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));

        cmbSegment = new JComboBox<>(new String[]{"ALL", "BEV", "LMM"});
        cmbModel = new JComboBox<>(new String[]{"ALL", "ZEO", "XUV", "BOLERO"});
        cmbLocation = new JComboBox<>(new String[]{"RAHATA_CORNERSTONE AUTOMOBILES","NAGAR_CORNERSTONE AUTOMOBILES","SHRIRAMPUR_CORNERSTONE AUTOMOBILES"});
		//cmbLocation = new JComboBox<>(new String[]{"NAGAR_CORNERSTONE AUTOMOBILES"});

        filters.add(new JLabel("Select Segment"));
        filters.add(cmbSegment);

        filters.add(new JLabel("Select Model"));
        filters.add(cmbModel);

        filters.add(new JLabel("Dealership"));
        filters.add(cmbLocation);

        topPanel.add(filters);

        lblPending = new JLabel("Total Pending Quick Enquiry: 0");
        lblPending.setForeground(Color.RED);
        topPanel.add(lblPending);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new String[]{"ID", "Name", "Product Model", "Segment", "Created Date", "Search Owner"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        table = new JTable(model);
        table.setRowHeight(32);

       table.getColumn("Search Owner")
     .setCellRenderer(new ButtonRenderer());

		table.getColumn("Search Owner")
     .setCellEditor(new AssignSCButtonEditor(new JCheckBox(), table));

        JScrollPane scrollPane = new JScrollPane(table);

        // ===== CENTER CONTENT PANEL (FIX FOR NAVBAR ISSUE) =====
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        loadEnquiries();

        cmbSegment.addActionListener(e -> loadEnquiries());
        cmbModel.addActionListener(e -> loadEnquiries());

        setVisible(true);
    }

    private void loadEnquiries() {

        model.setRowCount(0);
        int count = 0;

        String segment = cmbSegment.getSelectedItem().toString();
        String modelSel = cmbModel.getSelectedItem().toString();

        String sql =
                "SELECT customer_id, name, product_model, segment, created_date " +
                "FROM customer_enquiry " +
                "WHERE status='NEW' AND assigned_sc_id IS NULL";

        if (!segment.equals("ALL")) sql += " AND segment=?";
        if (!modelSel.equals("ALL")) sql += " AND product_model=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int index = 1;
            if (!segment.equals("ALL")) ps.setString(index++, segment);
            if (!modelSel.equals("ALL")) ps.setString(index++, modelSel);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int customerId = rs.getInt("customer_id");

                model.addRow(new Object[]{
        customerId,
        rs.getString("name"),
        rs.getString("product_model"),
        rs.getString("segment"),
        rs.getDate("created_date"),
        "Assign SC"   // ✅ JUST TEXT
		});
				
                model.addRow(new Object[]{
                        customerId,
                        rs.getString("name"),
                        rs.getString("product_model"),
                        rs.getString("segment"),
                        rs.getDate("created_date"),
                        
                });

                count++;
            }

            lblPending.setText("Total Pending Quick Enquiry: " + count);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void assignSC(int customerId) {

        JComboBox<String> cmbSC = new JComboBox<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement("SELECT sc_id, sc_name FROM sales_consultant")) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmbSC.addItem(rs.getInt("sc_id") + " - " + rs.getString("sc_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                cmbSC,
                "Assign Sales Consultant",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {

            int scId = Integer.parseInt(
                    cmbSC.getSelectedItem().toString().split(" - ")[0]
            );

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps =
                         con.prepareStatement(
                                 "UPDATE customer_enquiry " +
                                 "SET assigned_sc_id=?, status='ASSIGNED' " +
                                 "WHERE customer_id=?")) {

                ps.setInt(1, scId);
                ps.setInt(2, customerId);
                ps.executeUpdate();

                loadEnquiries();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
