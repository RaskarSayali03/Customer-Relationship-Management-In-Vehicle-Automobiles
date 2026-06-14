package ui;

import util.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class BookingFrame extends JFrame {

    private JTextField txtSearch;
    private JTable tblEnquiries;
    private DefaultTableModel tableModel;

    private JTextField txtCustomerName, txtMobile, txtVehicleModel;
    private JTextField txtBookingAmount;
    private JComboBox<String> cbPaymentMode;
    private JButton btnSave, btnCancel;

    private int selectedEnquiryId = -1;

    public BookingFrame() {
        setTitle("Booking - Mahindra Salesforce");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        add(createTopPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFormPanel(), BorderLayout.EAST);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    // ---------------- TOP PANEL ----------------
    private JPanel createTopPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        p.setBorder(new EmptyBorder(10,10,10,10));
        p.add(new JLabel("Search by Mobile No:"));

        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        p.add(txtSearch);
        p.add(btnSearch);

        btnSearch.addActionListener(e -> searchEnquiries());

        return p;
    }

    // ---------------- TABLE PANEL ----------------
    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(new Object[]{"ID","Name","Phone","Model"}, 0) {
            public boolean isCellEditable(int row, int col){ return false; }
        };
        tblEnquiries = new JTable(tableModel);
        tblEnquiries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblEnquiries.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) loadSelectedEnquiry();
            }
        });

        return new JScrollPane(tblEnquiries);
    }

    // ---------------- FORM PANEL ----------------
    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridLayout(6,2,10,10));
        form.setBorder(new TitledBorder("Booking Details"));
        form.setPreferredSize(new Dimension(300,0));
        form.setBorder(new EmptyBorder(15,15,15,15));

        form.add(new JLabel("Customer Name:"));
        txtCustomerName = new JTextField(); txtCustomerName.setEditable(false);
        form.add(txtCustomerName);

        form.add(new JLabel("Mobile No:"));
        txtMobile = new JTextField(); txtMobile.setEditable(false);
        form.add(txtMobile);

        form.add(new JLabel("Vehicle Model:"));
        txtVehicleModel = new JTextField(); txtVehicleModel.setEditable(false);
        form.add(txtVehicleModel);

        form.add(new JLabel("Booking Amount:"));
        txtBookingAmount = new JTextField();
        form.add(txtBookingAmount);

        form.add(new JLabel("Payment Mode:"));
        cbPaymentMode = new JComboBox<>(new String[]{"Cash","Card","UPI"});
        form.add(cbPaymentMode);

        form.add(new JLabel("Booking Date/Time:"));
        JLabel lblDateTime = new JLabel(new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(new java.util.Date()));
        form.add(lblDateTime);

        return form;
    }

    // ---------------- BUTTON PANEL ----------------
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Save Booking");
        btnCancel = new JButton("Cancel");
        panel.add(btnSave);
        panel.add(btnCancel);

        btnSave.addActionListener(e -> saveBooking());
        btnCancel.addActionListener(e -> dispose());
        return panel;
    }

    // ---------------- SEARCH ENQUIRIES ----------------
   private void searchEnquiries() {
    String mobile = txtSearch.getText().trim();
    tableModel.setRowCount(0); // clear table
    selectedEnquiryId = -1;

    if (mobile.isEmpty()) return;

    // remove all non-digit characters from input
    mobile = mobile.replaceAll("\\D", "");

    try (Connection con = DBConnection.getConnection()) {
        PreparedStatement ps = con.prepareStatement(
            "SELECT customer_id, name, phone, model_selected FROM customer " +
            "WHERE status='Enquired' " +
            "AND REGEXP_REPLACE(phone, '[^0-9]', '') LIKE ? " +
            "ORDER BY customer_id DESC"
        );
        ps.setString(1, "%" + mobile + "%");
        ResultSet rs = ps.executeQuery();

        boolean hasResults = false;
        while(rs.next()){
            hasResults = true;
            tableModel.addRow(new Object[]{
                rs.getInt("customer_id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("model_selected")
            });
        }

        if(!hasResults) JOptionPane.showMessageDialog(this,"No matching enquiries found.");

    } catch(Exception e){
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,"Error searching enquiries: "+e.getMessage());
    }
}
    // ---------------- LOAD SELECTED ENQUIRY ----------------
    private void loadSelectedEnquiry() {
        int row = tblEnquiries.getSelectedRow();
        if(row < 0) return;

        selectedEnquiryId = Integer.parseInt(tableModel.getValueAt(row,0).toString());
        txtCustomerName.setText(tableModel.getValueAt(row,1).toString());
        txtMobile.setText(tableModel.getValueAt(row,2).toString());
        txtVehicleModel.setText(tableModel.getValueAt(row,3).toString());
    }

    // ---------------- SAVE BOOKING ----------------
    private void saveBooking() {
        if(selectedEnquiryId == -1){
            JOptionPane.showMessageDialog(this,"Please select an enquiry first.");
            return;
        }
        String amountStr = txtBookingAmount.getText().trim();
        if(amountStr.isEmpty()){
            JOptionPane.showMessageDialog(this,"Enter booking amount.");
            return;
        }
        String paymentMode = (String)cbPaymentMode.getSelectedItem();

        try(Connection con = DBConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO bookings(enquiry_id, vehicle_model, customer_name, mobile_no, booking_date, booking_time, booking_amount, payment_mode, booking_status) " +
                "VALUES (?, ?, ?, ?, CURRENT_DATE(), CURRENT_TIME(), ?, ?, 'Pending')"
            );
            ps.setInt(1, selectedEnquiryId);
            ps.setString(2, txtVehicleModel.getText());
            ps.setString(3, txtCustomerName.getText());
            ps.setString(4, txtMobile.getText());
            ps.setDouble(5, Double.parseDouble(amountStr));
            ps.setString(6, paymentMode);
            int inserted = ps.executeUpdate();

            if(inserted>0){
                PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE customer SET status='Booked' WHERE customer_id=?"
                );
                ps2.setInt(1, selectedEnquiryId);
                ps2.executeUpdate();

                JOptionPane.showMessageDialog(this,"Booking saved successfully!");
                dispose();
            }

        } catch(Exception e){ e.printStackTrace(); 
            JOptionPane.showMessageDialog(this,"Error saving booking: "+e.getMessage());
        }
    }

    public static void main(String[] args){
        new BookingFrame().setVisible(true);
    }
}