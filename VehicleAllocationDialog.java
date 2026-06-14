package ui;

import util.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class VehicleAllocationDialog extends JDialog {

    private JTextField txtModel;
    private JTextField txtVariant;
    private JTextField txtColor;
    private JTextField txtEngine;
    private JTextField txtChassis;
    private JTextField txtDate;

    private int bookingId;
    private int customerId;

    public VehicleAllocationDialog(int bookingId, int customerId) {

        this.bookingId = bookingId;
        this.customerId = customerId;

        setTitle("Vehicle Allocation");
        setSize(400, 420);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7,2,10,10));
        form.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        form.add(new JLabel("Vehicle Model"));
        txtModel = new JTextField();
        form.add(txtModel);

        form.add(new JLabel("Variant"));
        txtVariant = new JTextField();
        form.add(txtVariant);

        form.add(new JLabel("Color"));
        txtColor = new JTextField();
        form.add(txtColor);

        form.add(new JLabel("Engine No"));
        txtEngine = new JTextField();
        form.add(txtEngine);

        form.add(new JLabel("Chassis No"));
        txtChassis = new JTextField();
        form.add(txtChassis);

        form.add(new JLabel("Allocation Date (YYYY-MM-DD)"));
        txtDate = new JTextField();
        form.add(txtDate);

        JButton btnAllocate = new JButton("Allocate Vehicle");
        btnAllocate.setBackground(new Color(0,120,215));
        btnAllocate.setForeground(Color.WHITE);

        form.add(new JLabel());
        form.add(btnAllocate);

        add(form, BorderLayout.CENTER);

        btnAllocate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                allocateVehicle();
            }
        });
    }

    private void allocateVehicle() {

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO vehicle_allocation "
                    + "(booking_id, customer_id, vehicle_model, variant, color, engine_no, chassis_no, allocation_date) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1, bookingId);
            pst.setInt(2, customerId);
            pst.setString(3, txtModel.getText());
            pst.setString(4, txtVariant.getText());
            pst.setString(5, txtColor.getText());
            pst.setString(6, txtEngine.getText());
            pst.setString(7, txtChassis.getText());
            pst.setDate(8, java.sql.Date.valueOf(txtDate.getText()));

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,"Vehicle Allocated Successfully");

            dispose();

        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());
        }
    }
}