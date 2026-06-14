package ui;

import dao.EnquiryDAO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.ResultSet;

public class EnquiryListFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> sorter;

    public EnquiryListFrame() {

        setTitle("Assigned Enquiries - Leads");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ---------------- NAVBAR ----------------
        add(new NavbarPanel(this), BorderLayout.NORTH);

        // ---------------- HEADER PANEL ----------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel("Leads");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JComboBox<String> cmbView = new JComboBox<>(new String[]{"Recently Viewed"});
        cmbView.setPreferredSize(new Dimension(180, 30));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftHeader.add(lblTitle);
        leftHeader.add(Box.createHorizontalStrut(20));
        leftHeader.add(cmbView);

        txtSearch = new JTextField(20);
        JButton btnRefresh = new JButton("Refresh");

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightHeader.add(new JLabel("Search: "));
        rightHeader.add(txtSearch);
        rightHeader.add(btnRefresh);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        add(headerPanel, BorderLayout.CENTER);

	String[] columns = {
    "ID",        // hidden
    "Name",
    "Household",
    "Phone",
    "Email",
    "Status",
    "Owner",
    "Reason",
   
};

        model = new DefaultTableModel(columns, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 8; // ONLY Follow-Up button clickable
    }
	};

   table = new JTable(model);

// Hide ID column
TableColumn idCol = table.getColumnModel().getColumn(0);
idCol.setMinWidth(0);
idCol.setMaxWidth(0);
idCol.setPreferredWidth(0);
	 
	 
/* ADD THIS CODE JUST AFTER JTable IS CREATED */
/*table.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) { // double click
            int row = table.getSelectedRow();
            if (row != -1) {
                int modelRow = table.convertRowIndexToModel(row);

                String phone = model.getValueAt(modelRow, 2).toString();

                new EnquiryDetailFrame(phone).setVisible(true);
                dispose(); // close current list frame
            }
        }
    }
});*/

JScrollPane scrollPane = new JScrollPane(table);
add(scrollPane);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // ---------------- LOAD DATA ----------------
        loadAssignedEnquiries();
		// ---------------- ROW CLICK ACTION ----------------
/*table.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {

        // double click
        if (e.getClickCount() == 2) {

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) return;

            // convert because sorter is used
            int modelRow = table.convertRowIndexToModel(selectedRow);

            // Mobile number (column index 2)
            String mobile = model.getValueAt(modelRow, 2).toString();

            // open next page
            EnquiryDetailFrame frame = new EnquiryDetailFrame(mobile);
            frame.setVisible(true);
        }
    }
});*/
	
		table.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getClickCount() == 2) {

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) return;

            int modelRow = table.convertRowIndexToModel(selectedRow);

            int enquiryId = (int) model.getValueAt(modelRow, 0); // 👈 ID column

            new EnquiryDetailFrame(enquiryId).setVisible(true);
            dispose();
        }
    }
});

        // ---------------- SEARCH FILTER ----------------
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }
        });

        // ---------------- REFRESH ----------------
        btnRefresh.addActionListener(e -> {
    txtSearch.setText("");      // clear search box
    sorter.setRowFilter(null);  // remove filter
    loadAssignedEnquiries();    // reload all enquiries
});

    }

    private void filter() {
        String text = txtSearch.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void loadAssignedEnquiries() {
        model.setRowCount(0);

        try {
            ResultSet rs = EnquiryDAO.getAllAssignedEnquiries();

            while (rs.next()) {
				 int enquiryId = rs.getInt("customer_id");
                String name = rs.getString("name");
                String household = name + " Household";
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String status = rs.getString("status");
                String owner = rs.getString("sc_name");
                String reason = rs.getString("enquiry_type");

               
				model.addRow(new Object[]{
    enquiryId,
    name,
    household,
    phone,
    email,
    status,
    owner,
    reason,
    "See Follow-Up"
	});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
