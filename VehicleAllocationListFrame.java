package ui;

import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VehicleAllocationListFrame extends JFrame {

    JTable table;
    DefaultTableModel model;

    public VehicleAllocationListFrame(){

        setTitle("Allocated Vehicles");
        setSize(900,500);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(
                new Object[]{
                        "Allocation ID",
                        "Booking ID",
                        "Customer Name",
                        "Phone",
                        "Vehicle Model",
                        "Variant",
                        "Color",
                        "Engine No",
                        "Chassis No",
                        "Allocation Date"
                },0
        );

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        add(scroll);

        loadAllocations();
    }

    private void loadAllocations(){

        model.setRowCount(0);

        try(Connection con = DBConnection.getConnection()){

            String sql =
                    "SELECT va.allocation_id,va.booking_id,c.name,c.phone," +
                    "va.vehicle_model,va.variant,va.color,va.engine_no,va.chassis_no,va.allocation_date " +
                    "FROM vehicle_allocation va " +
                    "JOIN bookings b ON va.booking_id=b.booking_id " +
                    "JOIN customer c ON b.enquiry_id=c.customer_id";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt("allocation_id"),
                        rs.getInt("booking_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("vehicle_model"),
                        rs.getString("variant"),
                        rs.getString("color"),
                        rs.getString("engine_no"),
                        rs.getString("chassis_no"),
                        rs.getDate("allocation_date")
                });
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}