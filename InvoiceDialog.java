package ui;

import util.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InvoiceDialog extends JDialog {

    // SEARCH
    private JTextField txtBookingId;
    private JButton btnSearch;

    // CUSTOMER INFO
    private JTextField txtCustomer;
    private JTextField txtMobile;
    private JTextField txtVehicle;
    private JTextField txtVehiclePrice;
    private JTextField txtTotalPaid;
    private JTextField txtBalance;

    // BUTTON
    private JButton btnGenerate;

    // TABLE
    private JTable table;
    private DefaultTableModel model;

    // INTERNAL
    private double vehiclePrice = 0;

    public InvoiceDialog() {

        setTitle("Invoice Generation");
        setSize(780,520);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        add(createHeader(),BorderLayout.NORTH);
        add(createCenter(),BorderLayout.CENTER);
    }

    // HEADER
    private JPanel createHeader(){

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0,Color.LIGHT_GRAY),
                new EmptyBorder(10,15,10,15)
        ));

        JLabel title = new JLabel("Vehicle Invoice");
        title.setFont(new Font("Segoe UI",Font.BOLD,18));

        p.add(title,BorderLayout.WEST);
        return p;
    }

    // CENTER
    private JPanel createCenter(){

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245,246,247));
        main.setBorder(new EmptyBorder(10,10,10,10));

        JPanel top = new JPanel(new BorderLayout(10,10));
        top.add(createSearchPanel(),BorderLayout.NORTH);
        top.add(createInfoPanel(),BorderLayout.CENTER);

        main.add(top,BorderLayout.NORTH);
        main.add(createGeneratePanel(),BorderLayout.CENTER);
        main.add(createHistoryPanel(),BorderLayout.SOUTH);

        return main;
    }

    // SEARCH PANEL
    private JPanel createSearchPanel(){

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(Color.WHITE);
        p.setBorder(new LineBorder(Color.LIGHT_GRAY));

        txtBookingId = new JTextField(10);

        btnSearch = new JButton("Search");
        btnSearch.setFocusPainted(false);

        btnSearch.addActionListener(e -> searchBooking());

        p.add(new JLabel("Booking ID:"));
        p.add(txtBookingId);
        p.add(btnSearch);

        return p;
    }

    // INFO PANEL
    private JPanel createInfoPanel(){

        JPanel p = new JPanel(new GridLayout(2,3,10,8));
        p.setBackground(Color.WHITE);
        p.setBorder(new LineBorder(Color.LIGHT_GRAY));

        txtCustomer = createReadField();
        txtMobile = createReadField();
        txtVehicle = createReadField();
        txtVehiclePrice = createReadField();
        txtTotalPaid = createReadField();
        txtBalance = createReadField();

        p.add(new JLabel("Customer Name"));
        p.add(new JLabel("Mobile"));
        p.add(new JLabel("Vehicle"));

        p.add(txtCustomer);
        p.add(txtMobile);
        p.add(txtVehicle);

        p.add(new JLabel("Vehicle Price"));
        p.add(new JLabel("Total Paid"));
        p.add(new JLabel("Balance"));

        p.add(txtVehiclePrice);
        p.add(txtTotalPaid);
        p.add(txtBalance);

        return p;
    }

    private JTextField createReadField(){
        JTextField t = new JTextField();
        t.setEditable(false);
        return t;
    }

    // GENERATE PANEL
    private JPanel createGeneratePanel(){

        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setBackground(Color.WHITE);
        p.setBorder(new LineBorder(Color.LIGHT_GRAY));

        btnGenerate = new JButton("Generate Invoice");
        btnGenerate.setBackground(new Color(33,150,243));
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setFocusPainted(false);

        btnGenerate.addActionListener(e -> generateInvoice());

        p.add(btnGenerate);

        return p;
    }

    // HISTORY PANEL
    private JPanel createHistoryPanel(){

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new LineBorder(Color.LIGHT_GRAY));

        model = new DefaultTableModel(
                new String[]{"Invoice ID","Date","Booking ID","Total Amount"},0
        );

        table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane sp = new JScrollPane(table);

        p.add(sp,BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(700,200));

        return p;
    }

    // SEARCH BOOKING
    private void searchBooking(){

        try{

            int bookingId = Integer.parseInt(txtBookingId.getText());

            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT c.name,c.phone,b.vehicle_model,b.variant,b.color,b.vehicle_price " +
                    "FROM bookings b JOIN customer c ON b.enquiry_id=c.customer_id " +
                    "WHERE b.booking_id=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1,bookingId);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){

                txtCustomer.setText(rs.getString("name"));
                txtMobile.setText(rs.getString("phone"));

                txtVehicle.setText(
                        rs.getString("vehicle_model")+" "+
                        rs.getString("variant")+" "+
                        rs.getString("color")
                );

                vehiclePrice = rs.getDouble("vehicle_price");
                txtVehiclePrice.setText("₹ "+vehiclePrice);

                loadPaymentSummary();
                loadInvoices();

            }
            else{

                JOptionPane.showMessageDialog(this,"Booking not found");
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    // LOAD PAYMENT SUMMARY
    private void loadPaymentSummary(){

        try{

            int bookingId = Integer.parseInt(txtBookingId.getText());

            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT IFNULL(SUM(amount),0) FROM transactions WHERE booking_id=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1,bookingId);

            ResultSet rs = ps.executeQuery();

            double paid = 0;

            if(rs.next())
                paid = rs.getDouble(1);

            double balance = vehiclePrice - paid;

            txtTotalPaid.setText("₹ "+paid);
            txtBalance.setText("₹ "+balance);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    // GENERATE INVOICE
    private void generateInvoice(){

        try{

            double balance =
                    Double.parseDouble(txtBalance.getText().replace("₹","").trim());

            if(balance > 0){

                JOptionPane.showMessageDialog(this,
                        "Full payment required before invoice generation");

                return;
            }

            int bookingId = Integer.parseInt(txtBookingId.getText());

            Connection con = DBConnection.getConnection();

            String sql =
                    "INSERT INTO invoice(booking_id,total_amount,paid_amount) VALUES(?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1,bookingId);
            ps.setDouble(2,vehiclePrice);
            ps.setDouble(3,vehiclePrice);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Invoice Generated Successfully");

            loadInvoices();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    // LOAD INVOICES
    private void loadInvoices(){

        try{

            model.setRowCount(0);

            int bookingId = Integer.parseInt(txtBookingId.getText());

            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT invoice_id,invoice_date,booking_id,total_amount " +
                    "FROM invoice WHERE booking_id=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1,bookingId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt("invoice_id"),
                        rs.getString("invoice_date"),
                        rs.getInt("booking_id"),
                        rs.getDouble("total_amount")
                });
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}