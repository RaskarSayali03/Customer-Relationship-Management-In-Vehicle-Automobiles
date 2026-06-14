package ui;

import util.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DeliveryDialog extends JDialog {

    // SEARCH
    private JTextField txtBookingId;
    private JButton btnSearch;

    // INFO
    private JTextField txtCustomer;
    private JTextField txtMobile;
    private JTextField txtVehicle;
    private JTextField txtInvoice;

    // DELIVERY ENTRY
    private JTextField txtDate;
    private JTextField txtDeliveredBy;
    private JTextField txtRemarks;
    private JButton btnDeliver;

    // TABLE
    private JTable table;
    private DefaultTableModel model;

    // STORE ENQUIRY ID
    private int enquiryId;

    public DeliveryDialog(){

        setTitle("Vehicle Delivery");
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

        JLabel title = new JLabel("Vehicle Delivery");
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
        btnSearch.setFocusPainted(false);

        btnSearch.addActionListener(e -> searchBooking());

        p.add(new JLabel("Booking ID:"));
        p.add(txtBookingId);
        p.add(btnSearch);

        return p;
    }

    // INFO PANEL
    private JPanel createInfoPanel(){

        JPanel p = new JPanel(new GridLayout(2,4,10,8));
        p.setBorder(new LineBorder(Color.LIGHT_GRAY));

        txtCustomer = createReadField();
        txtMobile = createReadField();
        txtVehicle = createReadField();
        txtInvoice = createReadField();

        p.add(new JLabel("Customer Name"));
        p.add(new JLabel("Mobile"));
        p.add(new JLabel("Vehicle"));
        p.add(new JLabel("Invoice ID"));

        p.add(txtCustomer);
        p.add(txtMobile);
        p.add(txtVehicle);
        p.add(txtInvoice);

        return p;
    }

    private JTextField createReadField(){
        JTextField t = new JTextField();
        t.setEditable(false);
        return t;
    }

    // DELIVERY ENTRY
    private JPanel createEntryPanel(){

        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new LineBorder(Color.LIGHT_GRAY));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtDate = new JTextField(10);
        txtDeliveredBy = new JTextField(10);
        txtRemarks = new JTextField(15);

        btnDeliver = new JButton("Mark Delivered");
        btnDeliver.setBackground(new Color(33,150,243));
        btnDeliver.setForeground(Color.WHITE);

        btnDeliver.addActionListener(e -> saveDelivery());

        g.gridx=0; g.gridy=0; p.add(new JLabel("Delivery Date"),g);
        g.gridx=1; p.add(txtDate,g);

        g.gridx=2; p.add(new JLabel("Delivered By"),g);
        g.gridx=3; p.add(txtDeliveredBy,g);

        g.gridx=0; g.gridy=1; p.add(new JLabel("Remarks"),g);
        g.gridx=1; g.gridwidth=2; p.add(txtRemarks,g);

        g.gridx=3; g.gridwidth=1; p.add(btnDeliver,g);

        return p;
    }

    // HISTORY TABLE
    private JPanel createHistoryPanel(){

        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new LineBorder(Color.LIGHT_GRAY));

        model = new DefaultTableModel(
                new String[]{"Delivery ID","Date","Booking ID","Delivered By","Remarks"},0
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
            "SELECT c.customer_id,c.name,c.phone,b.vehicle_model,b.variant,b.color,i.invoice_id " +
            "FROM bookings b " +
            "JOIN customer c ON b.enquiry_id=c.customer_id " +
            "LEFT JOIN invoice i ON b.booking_id=i.booking_id " +
            "WHERE b.booking_id=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1,bookingId);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){

                enquiryId = rs.getInt("customer_id");

                txtCustomer.setText(rs.getString("name"));
                txtMobile.setText(rs.getString("phone"));

                txtVehicle.setText(
                        rs.getString("vehicle_model")+" "+
                        rs.getString("variant")+" "+
                        rs.getString("color")
                );

                txtInvoice.setText(String.valueOf(rs.getInt("invoice_id")));

                loadDeliveryHistory();

            }else{

                JOptionPane.showMessageDialog(this,"Booking not found");
            }

        }catch(Exception ex){

            ex.printStackTrace();
        }
    }

    // SAVE DELIVERY
    private void saveDelivery(){

        try{

            if(txtInvoice.getText().trim().equals("0") ||
               txtInvoice.getText().trim().isEmpty()){

                JOptionPane.showMessageDialog(this,
                "Generate invoice before delivery");

                return;
            }

            int bookingId = Integer.parseInt(txtBookingId.getText());
            int invoiceId = Integer.parseInt(txtInvoice.getText());

            Connection con = DBConnection.getConnection();

            String sql =
            "INSERT INTO delivery(enquiry_id,booking_id,invoice_id,delivery_date,delivered_by,remarks) " +
            "VALUES(?,?,?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1,enquiryId);
            ps.setInt(2,bookingId);
            ps.setInt(3,invoiceId);
            ps.setString(4,txtDate.getText());
            ps.setString(5,txtDeliveredBy.getText());
            ps.setString(6,txtRemarks.getText());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Vehicle Delivered Successfully");

            txtDate.setText("");
            txtDeliveredBy.setText("");
            txtRemarks.setText("");

            loadDeliveryHistory();

        }catch(Exception ex){

            ex.printStackTrace();
        }
    }

    // LOAD DELIVERY HISTORY
    private void loadDeliveryHistory(){

        try{

            model.setRowCount(0);

            int bookingId = Integer.parseInt(txtBookingId.getText());

            Connection con = DBConnection.getConnection();

            String sql =
            "SELECT delivery_id,delivery_date,booking_id,delivered_by,remarks " +
            "FROM delivery WHERE booking_id=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1,bookingId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt("delivery_id"),
                        rs.getString("delivery_date"),
                        rs.getInt("booking_id"),
                        rs.getString("delivered_by"),
                        rs.getString("remarks")
                });
            }

        }catch(Exception ex){

            ex.printStackTrace();
        }
    }
}