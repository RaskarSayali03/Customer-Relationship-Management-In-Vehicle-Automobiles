package ui;

import util.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PaymentDialog extends JDialog {

    // ===== SEARCH =====
    private JTextField txtBookingId;
    private JButton btnSearch;

    // ===== CUSTOMER / BOOKING INFO =====
    private JTextField txtCustomer;
    private JTextField txtMobile;
    private JTextField txtVehicle;
    private JTextField txtBookingAmount;
    private JTextField txtVehiclePrice;

    // ===== TRANSACTION =====
    private JTextField txtAmount;
    private JTextField txtRef;
    private JComboBox<String> cbMode;
    private JButton btnSave;

    // ===== SUMMARY =====
    private JLabel lblTotalPaid;
    private JLabel lblBalance;

    // ===== TABLE =====
    private JTable table;
    private DefaultTableModel model;

    private double vehiclePrice = 0;
    private double bookingAmount = 0;

    public PaymentDialog() {

        setTitle("Payment Transactions");
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
        p.setBorder(new EmptyBorder(10,15,10,15));

        JLabel title = new JLabel("Vehicle Payment Transactions");
        title.setFont(new Font("Segoe UI",Font.BOLD,18));

        p.add(title,BorderLayout.WEST);
        return p;
    }

    // CENTER
    private JPanel createCenter(){

        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(new EmptyBorder(10,10,10,10));

        JPanel top = new JPanel(new BorderLayout(10,10));
        top.add(createSearchPanel(),BorderLayout.NORTH);
        top.add(createInfoPanel(),BorderLayout.CENTER);

        main.add(top,BorderLayout.NORTH);
        main.add(createEntryPanel(),BorderLayout.CENTER);
        main.add(createHistoryPanel(),BorderLayout.SOUTH);

        return main;
    }

    // SEARCH PANEL
    private JPanel createSearchPanel(){

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        p.setBorder(new LineBorder(Color.LIGHT_GRAY));

        txtBookingId = new JTextField(10);

        btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchBooking());

        p.add(new JLabel("Booking ID:"));
        p.add(txtBookingId);
        p.add(btnSearch);

        return p;
    }

    // INFO PANEL
    private JPanel createInfoPanel(){

        JPanel p = new JPanel(new GridLayout(2,5,10,8));
        p.setBorder(new LineBorder(Color.LIGHT_GRAY));

        txtCustomer = createReadField();
        txtMobile = createReadField();
        txtVehicle = createReadField();
        txtBookingAmount = createReadField();
        txtVehiclePrice = createReadField();

        p.add(new JLabel("Customer Name"));
        p.add(new JLabel("Mobile"));
        p.add(new JLabel("Vehicle"));
        p.add(new JLabel("Booking Amount"));
        p.add(new JLabel("Vehicle Price"));

        p.add(txtCustomer);
        p.add(txtMobile);
        p.add(txtVehicle);
        p.add(txtBookingAmount);
        p.add(txtVehiclePrice);

        return p;
    }

    private JTextField createReadField(){
        JTextField t = new JTextField();
        t.setEditable(false);
        return t;
    }

    // ENTRY PANEL
    private JPanel createEntryPanel(){

        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new LineBorder(Color.LIGHT_GRAY));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtAmount = new JTextField(10);
        txtRef = new JTextField(10);

        cbMode = new JComboBox<>(new String[]{
                "Cash","UPI","Bank Transfer","Finance Loan"
        });

        btnSave = new JButton("Save Transaction");

        btnSave.addActionListener(e -> saveTransaction());

        g.gridx=0; g.gridy=0; p.add(new JLabel("Amount"),g);
        g.gridx=1; p.add(txtAmount,g);

        g.gridx=2; p.add(new JLabel("Mode"),g);
        g.gridx=3; p.add(cbMode,g);

        g.gridx=0; g.gridy=1; p.add(new JLabel("Reference"),g);
        g.gridx=1; g.gridwidth=2; p.add(txtRef,g);

        g.gridx=3; g.gridwidth=1; p.add(btnSave,g);

        return p;
    }

    // HISTORY PANEL
    private JPanel createHistoryPanel(){

        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new LineBorder(Color.LIGHT_GRAY));

        model = new DefaultTableModel(
                new String[]{"ID","Date","Amount","Mode","Reference"},0
        );

        table = new JTable(model);

        JScrollPane sp = new JScrollPane(table);

        JPanel summary = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        lblTotalPaid = new JLabel("Total Paid: ₹0");
        lblBalance = new JLabel("Balance: ₹0");

        summary.add(lblTotalPaid);
        summary.add(lblBalance);

        p.add(sp,BorderLayout.CENTER);
        p.add(summary,BorderLayout.SOUTH);

        p.setPreferredSize(new Dimension(700,200));

        return p;
    }

    // SEARCH BOOKING
    private void searchBooking(){

        try{

            int bookingId = Integer.parseInt(txtBookingId.getText());

            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT c.name,c.phone,b.vehicle_model,b.variant,b.color," +
                    "b.booking_amount,b.vehicle_price " +
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

                bookingAmount = rs.getDouble("booking_amount");
                vehiclePrice = rs.getDouble("vehicle_price");

                txtBookingAmount.setText("₹ "+bookingAmount);
                txtVehiclePrice.setText("₹ "+vehiclePrice);

                loadTransactions();
                updateSummary();
            }
            else{

                JOptionPane.showMessageDialog(this,"Booking not found");
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    // SAVE TRANSACTION
    private void saveTransaction(){

        try{

            String amountText = txtAmount.getText().trim();

            if(amountText.length()==0){
                JOptionPane.showMessageDialog(this,"Please enter payment amount");
                return;
            }

            double amount = Double.parseDouble(amountText);

            int bookingId = Integer.parseInt(txtBookingId.getText());

            Connection con = DBConnection.getConnection();

            String sql =
                    "INSERT INTO transactions(booking_id,amount,payment_mode,transaction_ref)" +
                    " VALUES(?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1,bookingId);
            ps.setDouble(2,amount);
            ps.setString(3,cbMode.getSelectedItem().toString());
            ps.setString(4,txtRef.getText());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Transaction Saved");

            txtAmount.setText("");
            txtRef.setText("");

            loadTransactions();
            updateSummary();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    // LOAD HISTORY
    private void loadTransactions(){

        try{

            model.setRowCount(0);

            int bookingId = Integer.parseInt(txtBookingId.getText());

            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT transaction_id,transaction_date,amount,payment_mode,transaction_ref " +
                    "FROM transactions WHERE booking_id=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1,bookingId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getDouble(3),
                        rs.getString(4),
                        rs.getString(5)
                });
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    // UPDATE SUMMARY
    private void updateSummary(){

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

            double totalPaid = paid + bookingAmount;
            double balance = vehiclePrice - totalPaid;

            lblTotalPaid.setText("Total Paid: ₹ "+totalPaid);
            lblBalance.setText("Balance: ₹ "+balance);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}