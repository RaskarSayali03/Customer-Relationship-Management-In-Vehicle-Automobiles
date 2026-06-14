package ui;
import util.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FollowUpDialog extends JDialog {

    JComboBox<String> cbType, cbLikely, cbRemarks, cbRemarkType;
    JTextArea txtNotes;
    JTextField txtDate, txtTime;

    int enquiryId;
    String role; // ICRE or SC

    public FollowUpDialog(JFrame parent, int enquiryId, String role) {
        super(parent, "Create Follow Up", true);
        this.enquiryId = enquiryId;
        this.role = role;

        setSize(700, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbType = new JComboBox<>(new String[]{"Call", "Visit", "WhatsApp", "Email"});
        cbLikely = new JComboBox<>(new String[]{"< 7 days", "< 15 days", "< 30 days", "Not Sure"});
        cbRemarks = new JComboBox<>(new String[]{"None", "Interested", "Not Interested", "Call Later"});
        cbRemarkType = new JComboBox<>(new String[]{"None","Price", "Features", "Finance", "Availability"});

        txtNotes = new JTextArea(4, 20);
        JScrollPane noteScroll = new JScrollPane(txtNotes);

        txtDate = new JTextField("yyyy-mm-dd");
        txtTime = new JTextField("HH:mm");

        int y = 0;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Follow Up Type"), gbc);
        gbc.gridx = 1; form.add(cbType, gbc);
        gbc.gridx = 2; form.add(new JLabel("Likely Purchase"), gbc);
        gbc.gridx = 3; form.add(cbLikely, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Remarks"), gbc);
        gbc.gridx = 1; form.add(cbRemarks, gbc);
        gbc.gridx = 2; form.add(new JLabel("Remark Type *"), gbc);
        gbc.gridx = 3; form.add(cbRemarkType, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Notes *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; form.add(noteScroll, gbc);
        gbc.gridwidth = 1;

        y++;
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Next Follow Up Date"), gbc);
        gbc.gridx = 1; form.add(txtDate, gbc);
        gbc.gridx = 2; form.add(new JLabel("Time"), gbc);
        gbc.gridx = 3; form.add(txtTime, gbc);

        add(form, BorderLayout.CENTER);

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnSave);
        bottom.add(btnCancel);

        add(bottom, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> saveFollowUp());
        btnCancel.addActionListener(e -> dispose());
    }

   /* private void saveFollowUp() {
        try (Connection con = DBConnection.getConnection()) {

            String sql = "INSERT INTO followups "
                    + "(enquiry_id, created_by_role, followup_type, likely_purchase, remarks, remark_type, notes, next_followup_date, next_followup_time) "
                    + "VALUES (?,?,?,?,?,?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, enquiryId);
            ps.setString(2, role);
            ps.setString(3, cbType.getSelectedItem().toString());
            ps.setString(4, cbLikely.getSelectedItem().toString());
            ps.setString(5, cbRemarks.getSelectedItem().toString());
            ps.setString(6, cbRemarkType.getSelectedItem().toString());
            ps.setString(7, txtNotes.getText());
            ps.setDate(8, java.sql.Date.valueOf(txtDate.getText()));
			ps.setTime(9, java.sql.Time.valueOf(txtTime.getText()));

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Follow Up Added Successfully");
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving follow up");
        }
    }*/
	
	private void saveFollowUp() {
    try (Connection con = DBConnection.getConnection()) {

        String sql = "INSERT INTO follow_ups "
                + "(enquiry_id, created_by_role, followup_type, likely_purchase, remarks, remark_type, notes, next_followup_date, next_followup_time) "
                + "VALUES (?,?,?,?,?,?,?,?,?)";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, enquiryId);
        ps.setString(2, role);
        ps.setString(3, cbType.getSelectedItem().toString());
        ps.setString(4, cbLikely.getSelectedItem().toString());
        ps.setString(5, cbRemarks.getSelectedItem().toString());
        ps.setString(6, cbRemarkType.getSelectedItem().toString());
        ps.setString(7, txtNotes.getText());
        ps.setDate(8, java.sql.Date.valueOf(txtDate.getText()));
        ps.setTime(9, java.sql.Time.valueOf(txtTime.getText() + ":00"));

        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Follow Up Added Successfully");
        dispose();

    } catch (Exception e) {
    e.printStackTrace();   // VERY IMPORTANT
    JOptionPane.showMessageDialog(this, e.getMessage());
	}
}
}