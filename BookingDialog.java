package ui;

import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookingDialog extends JFrame {

    private JTextField txtSearch;
    private JTable tblCustomers;
    private DefaultTableModel model;

    private JTextField txtName, txtPhone, txtModel;
    private JComboBox<String> cbVariant, cbColor, cbPaymentMode, cbFinance;
    private JTextField txtBookingAmount, txtDeliveryDate;

    private JButton btnBook;
private JButton btnAllocate;
private int currentBookingId = -1;

    public BookingDialog() {
        initUI();
    }

    private void initUI() {

        setTitle("Vehicle Booking");
        setSize(900,550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ================= SEARCH PANEL =================

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,15,10));

        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");

        top.add(new JLabel("Mobile No / Enquiry ID"));
        top.add(txtSearch);
        top.add(btnSearch);

        add(top,BorderLayout.NORTH);

        // ================= CUSTOMER TABLE =================

        model = new DefaultTableModel(
                new Object[]{"Enquiry No","Customer Name","Phone","Model"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };

        tblCustomers = new JTable(model);
        tblCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tblCustomers);
        add(scroll,BorderLayout.CENTER);

        // ================= BOOKING FORM =================

        JPanel bottom = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtName = new JTextField();
        txtName.setEditable(false);

        txtPhone = new JTextField();
        txtPhone.setEditable(false);

        txtModel = new JTextField();
        txtModel.setEditable(false);

        cbVariant = new JComboBox<>(new String[]{
                "AX3","AX5","AX7","AX7L"
        });

        cbColor = new JComboBox<>(new String[]{
                "White","Black","Silver","Red","Blue"
        });

        cbPaymentMode = new JComboBox<>(new String[]{
                "Cash","Card","UPI","Finance"
        });

        cbFinance = new JComboBox<>(new String[]{
                "NO","YES"
        });

        txtBookingAmount = new JTextField();
        txtDeliveryDate = new JTextField("yyyy-mm-dd");

        btnBook = new JButton("Confirm Booking");
		
		btnAllocate = new JButton("Vehicle Allocation");

        int y=0;

        gbc.gridx=0; gbc.gridy=y;
        bottom.add(new JLabel("Customer Name"),gbc);
        gbc.gridx=1;
        bottom.add(txtName,gbc);

        gbc.gridx=2;
        bottom.add(new JLabel("Phone"),gbc);
        gbc.gridx=3;
        bottom.add(txtPhone,gbc);

        y++;

        gbc.gridx=0; gbc.gridy=y;
        bottom.add(new JLabel("Vehicle Model"),gbc);
        gbc.gridx=1;
        bottom.add(txtModel,gbc);

        gbc.gridx=2;
        bottom.add(new JLabel("Variant"),gbc);
        gbc.gridx=3;
        bottom.add(cbVariant,gbc);

        y++;

        gbc.gridx=0; gbc.gridy=y;
        bottom.add(new JLabel("Color"),gbc);
        gbc.gridx=1;
        bottom.add(cbColor,gbc);

        gbc.gridx=2;
        bottom.add(new JLabel("Booking Amount"),gbc);
        gbc.gridx=3;
        bottom.add(txtBookingAmount,gbc);

        y++;

        gbc.gridx=0; gbc.gridy=y;
        bottom.add(new JLabel("Payment Mode"),gbc);
        gbc.gridx=1;
        bottom.add(cbPaymentMode,gbc);

        gbc.gridx=2;
        bottom.add(new JLabel("Finance Required"),gbc);
        gbc.gridx=3;
        bottom.add(cbFinance,gbc);

        y++;

        gbc.gridx=0; gbc.gridy=y;
        bottom.add(new JLabel("Expected Delivery Date"),gbc);
        gbc.gridx=1;
        bottom.add(txtDeliveryDate,gbc);

        gbc.gridx=2;
bottom.add(btnBook,gbc);

gbc.gridx=3;
bottom.add(btnAllocate,gbc);

        add(bottom,BorderLayout.SOUTH);

        // ================= EVENTS =================

        btnSearch.addActionListener(e -> searchCustomer());

        tblCustomers.getSelectionModel().addListSelectionListener(e -> loadCustomer());

        btnBook.addActionListener(e -> confirmBooking());

		btnAllocate.addActionListener(e -> {

    if(currentBookingId == -1){
        JOptionPane.showMessageDialog(this,"Please confirm booking first.");
        return;
    }

    int row = tblCustomers.getSelectedRow();
    int customerId = Integer.parseInt(model.getValueAt(row,0).toString());

    new VehicleAllocationDialog(currentBookingId, customerId).setVisible(true);
});
		
        txtSearch.addActionListener(e -> searchCustomer());
    }

    // ================= SEARCH CUSTOMER =================

    private void searchCustomer(){

        String keyword = txtSearch.getText().trim();
        model.setRowCount(0);

        if(keyword.isEmpty()) return;

        try(Connection con = DBConnection.getConnection()){

            PreparedStatement ps = con.prepareStatement(
                    "SELECT customer_id,name,phone,model_selected FROM customer " +
                    "WHERE phone LIKE ? OR customer_id LIKE ?"
            );

            ps.setString(1,"%"+keyword+"%");
            ps.setString(2,"%"+keyword+"%");

            ResultSet rs = ps.executeQuery();

            boolean found=false;

            while(rs.next()){

                found=true;

                model.addRow(new Object[]{
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("model_selected")
                });
            }

            if(!found)
                JOptionPane.showMessageDialog(this,"No customer found.");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // ================= LOAD SELECTED CUSTOMER =================

    private void loadCustomer(){

        int row = tblCustomers.getSelectedRow();
        if(row<0) return;

        txtName.setText(model.getValueAt(row,1).toString());
        txtPhone.setText(model.getValueAt(row,2).toString());
        txtModel.setText(model.getValueAt(row,3).toString());
    }

    // ================= CONFIRM BOOKING =================

   private void confirmBooking(){

    int row = tblCustomers.getSelectedRow();

    if(row < 0){
        JOptionPane.showMessageDialog(this,"Select customer first.");
        return;
    }

    int enquiryId = Integer.parseInt(model.getValueAt(row,0).toString());

    try(Connection con = DBConnection.getConnection()){

        PreparedStatement ps = con.prepareStatement(
            "INSERT INTO bookings (enquiry_id,vehicle_model,variant,color,booking_amount,payment_mode,finance_required,expected_delivery,status) VALUES (?,?,?,?,?,?,?,?, 'BOOKED')",
            Statement.RETURN_GENERATED_KEYS
        );

        ps.setInt(1,enquiryId);
        ps.setString(2,txtModel.getText());
        ps.setString(3,cbVariant.getSelectedItem().toString());
        ps.setString(4,cbColor.getSelectedItem().toString());
        ps.setDouble(5,Double.parseDouble(txtBookingAmount.getText()));
        ps.setString(6,cbPaymentMode.getSelectedItem().toString());
        ps.setString(7,cbFinance.getSelectedItem().toString());
        ps.setDate(8,Date.valueOf(txtDeliveryDate.getText()));

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();

        if(rs.next()){
            currentBookingId = rs.getInt(1);

            JOptionPane.showMessageDialog(
                this,
                "Booking Confirmed!\nBooking ID: " + currentBookingId
            );
        }

    }catch(Exception e){
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,"Error saving booking");
    }
}

    public static void main(String[] args){
        new BookingDialog().setVisible(true);
    }
}