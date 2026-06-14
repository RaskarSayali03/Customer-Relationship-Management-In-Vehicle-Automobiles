package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import util.DBConnection;
import java.sql.*;

public class DashboardFrame extends JFrame {

    private String adminId;

    public DashboardFrame(String adminId) {
        this.adminId = adminId;

        setTitle("Mahindra Salesforce – KPI Dashboard");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createTopNavbar(), BorderLayout.NORTH);
        add(createMainDashboard(), BorderLayout.CENTER);
		setVisible(true);
    }

    // ================= TOP NAVBAR =================
   private JPanel createTopNavbar() {

    JPanel nav = new JPanel(new BorderLayout());
    nav.setBackground(Color.WHITE);
    nav.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
    nav.setPreferredSize(new Dimension(1200, 90));

    // ===== TOP ROW =====
    JPanel topRow = new JPanel(new BorderLayout());
    topRow.setBackground(Color.WHITE);

    JLabel logo = new JLabel("Cerebro | Mahindra");
    logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
    logo.setBorder(new EmptyBorder(10, 20, 5, 10));

    topRow.add(logo, BorderLayout.WEST);

    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
    rightPanel.setBackground(Color.WHITE);

    JButton adminBtn = new JButton("Admin | ID: " + adminId);
    adminBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    adminBtn.setFocusPainted(false);

    JPopupMenu profileMenu = new JPopupMenu();
    JMenuItem profileItem = new JMenuItem("View Profile");
    JMenuItem logoutItem = new JMenuItem("Logout");

    profileMenu.add(profileItem);
    profileMenu.addSeparator();
    profileMenu.add(logoutItem);

    adminBtn.addActionListener(e ->
            profileMenu.show(adminBtn, 0, adminBtn.getHeight())
    );

    logoutItem.addActionListener(e -> {
        dispose();
        new LoginFrame().setVisible(true);
    });

    rightPanel.add(adminBtn);
    topRow.add(rightPanel, BorderLayout.EAST);

    // ===== SECOND ROW (MENU BUTTONS) =====
   JPanel menu = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 5));
    menu.setBackground(Color.WHITE);
    menu.setBorder(new EmptyBorder(5, 20, 10, 10));

    menu.add(createNewEnquiryButton());
    menu.add(createEnquiryButton());
  //  menu.add(createAssignmentQueueButton());
    menu.add(createBookingButton());
    menu.add(createTestDriveButton());
    menu.add(createAllocatedVehiclesButton());
	//menu.add(createPaymentButton());
	menu.add(createTransactionButton());
	 menu.add(createInvoiceButton());
	 menu.add(createDeliveryButton());
    menu.add(createNavButton("Activity Planner"));

    nav.add(topRow, BorderLayout.NORTH);
    nav.add(menu, BorderLayout.CENTER);

    return nav;
}
    // ================= NORMAL NAV BUTTON =================
    private JButton createNavButton(String title) {
        JButton btn = new JButton(title);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setBorderPainted(false);

        btn.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        title + " module will open here",
                        "Navigation",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );
        return btn;
    }
	
	    // ================= ASSIGNMENT QUEUE BUTTON (REAL PAGE) =================
   /* private JButton createAssignmentQueueButton() {
        JButton btn = new JButton("Assignment Queue");
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setBorderPainted(false);

        btn.addActionListener(e -> {
            new AssignmentQueueFrame().setVisible(true);
        });

        return btn;
    }*/
// ================= ENQUIRY BUTTON (OPEN ASSIGNED ENQUIRIES) =================
private JButton createEnquiryButton() {
    JButton btn = new JButton("Enquiry");
    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    btn.setBackground(Color.WHITE);
    btn.setBorderPainted(false);

    btn.addActionListener(e -> {
        dispose(); // close dashboard
        new EnquiryListFrame().setVisible(true); // open assigned enquiries page
    });

    return btn;
}

// ================= NEW ENQUIRY BUTTON =================
private JButton createNewEnquiryButton() {
    JButton btn = new JButton("New Enquiry");
    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    btn.setBackground(Color.WHITE);
    btn.setBorderPainted(false);

    btn.addActionListener(e -> {
        dispose(); // close dashboard
      NewEnquiryWizardFrame wizard =
        new NewEnquiryWizardFrame(this); // ← THIS means Dashboard
		wizard.setVisible(true);
		this.setVisible(false);                 // hide Dashboard
    });

    return btn;
}

          // ================= TEST DRIVE BUTTON =================
private JButton createTestDriveButton() {
    JButton btn = new JButton("Test Drive");
    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    btn.setBackground(Color.WHITE);
    btn.setBorderPainted(false);

    btn.addActionListener(e -> {
    TestDriveDialog dialog = new TestDriveDialog(null); // no frame to refresh
    dialog.setVisible(true);
});
	      
	

    return btn;
}

// ================= BOOKING BUTTON =================
 private JButton createBookingButton() {

JButton btn = new JButton("Vehicle Booking");

btn.setFocusPainted(false);
btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
btn.setBackground(Color.WHITE);
btn.setBorderPainted(false);
btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

btn.addActionListener(e -> {

    BookingDialog bookingDialog = new BookingDialog();
    bookingDialog.setVisible(true);

});

return btn;

}

// ================= ALLOCATED VEHICLES BUTTON =================
private JButton createAllocatedVehiclesButton() {

    JButton btn = new JButton("Allocated Vehicles");

    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    btn.setBackground(Color.WHITE);
    btn.setBorderPainted(false);

    btn.addActionListener(e -> {
        new VehicleAllocationListFrame().setVisible(true);
    });

    return btn;
}



		/* //=============== PAYMENT BTN =========
		private JButton createPaymentButton(){

    JButton btn = new JButton("Payment");

    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    btn.setBackground(Color.WHITE);
    btn.setBorderPainted(false);

    btn.addActionListener(e -> {

        PaymentDialog dialog = new PaymentDialog();
        dialog.setVisible(true);

    });

    return btn;
}*/

		//==========TRANSACTION BTN =========
		private JButton createTransactionButton(){

    JButton btn = new JButton("Transactions");

    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    btn.setBackground(Color.WHITE);
    btn.setBorderPainted(false);

    btn.addActionListener(e -> {

        new PaymentDialog().setVisible(true);

    });

    return btn;
}

	//------INVOICE BUTTON------------
		private JButton createInvoiceButton(){

    JButton btn = new JButton("Invoice");

    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    btn.setBackground(Color.WHITE);
    btn.setBorderPainted(false);

    btn.addActionListener(e -> {

        InvoiceDialog dialog = new InvoiceDialog();
        dialog.setVisible(true);

    });

    return btn;
}
	//------DELIVERY BUTTON------------
private JButton createDeliveryButton(){

    JButton btn = new JButton("Vehicle Delivery");

    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    btn.setBackground(Color.WHITE);
    btn.setBorderPainted(false);

    btn.addActionListener(e -> {

        DeliveryDialog dialog = new DeliveryDialog();
        dialog.setVisible(true);

    });

    return btn;
}
		// ===== ADDED FOR DYNAMIC KPI COUNT =====
private int getTableCount(String tableName) {

    int count = 0;

    try {
        Connection con = DBConnection.getConnection();

        String sql = "SELECT COUNT(*) FROM " + tableName;

        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            count = rs.getInt(1);
        }

    } catch(Exception e){
        e.printStackTrace();
    }

    return count;
}

    // ================= MAIN DASHBOARD =================
    private JPanel createMainDashboard() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 246, 247));
        main.setBorder(new EmptyBorder(10, 15, 15, 15));

        main.add(createHeader(), BorderLayout.NORTH);
        main.add(createKpiCards(), BorderLayout.CENTER);

        return main;
    }

    private JPanel createHeader() {

    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(new Color(245,246,247));

    JButton refresh = new JButton("Refresh");
    refresh.setFont(new Font("Segoe UI", Font.PLAIN, 13));

    refresh.addActionListener(e -> {
        dispose();
        new DashboardFrame(adminId).setVisible(true);
    });

    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    right.setBackground(new Color(245,246,247));
    right.add(refresh);

    header.add(right, BorderLayout.EAST);

    return header;
}

    // ================= KPI CARDS =================
	private JPanel createKpiCards() {

    JPanel cards = new JPanel(new GridLayout(1, 3, 25, 25));
    cards.setBackground(new Color(245, 246, 247));
    cards.setBorder(new EmptyBorder(25, 20, 20, 20));

    int enquiryCount = getTableCount("customer");
    int testDriveCount = getTableCount("test_drive");
    int bookingCount = getTableCount("bookings");

    cards.add(createKpiCard("Enquiry List New", enquiryCount,
            new String[]{"Total Enquiries"}));

    cards.add(createKpiCard("Test Drives List New", testDriveCount,
            new String[]{"Customer Test Drives"}));

    cards.add(createKpiCard("Booking List New", bookingCount,
            new String[]{"Vehicle Bookings"}));

    return cards;
}

   private JPanel createKpiCard(String title, int count, String[] stages) {

    JPanel card = new JPanel(new BorderLayout());
    card.setBackground(Color.WHITE);
    card.setBorder(new CompoundBorder(
            new LineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(15,15,15,15)
    ));

    card.setPreferredSize(new Dimension(340,200));

    JLabel lblTitle = new JLabel(title);
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

    JLabel lblCount = new JLabel(String.valueOf(count), SwingConstants.CENTER);
    lblCount.setFont(new Font("Segoe UI", Font.BOLD, 36));
    lblCount.setForeground(new Color(33,150,243));

    JPanel stagePanel = new JPanel();
    stagePanel.setLayout(new BoxLayout(stagePanel, BoxLayout.Y_AXIS));
    stagePanel.setBackground(Color.WHITE);

    for(String s : stages){
        JLabel lbl = new JLabel("• " + s);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        stagePanel.add(lbl);
    }

    card.add(lblTitle, BorderLayout.NORTH);
    card.add(lblCount, BorderLayout.CENTER);
    card.add(stagePanel, BorderLayout.SOUTH);

    return card;
}
    // ================= LOGIN =================
    static class LoginFrame extends JFrame {

        JTextField txtUser;
        JPasswordField txtPass;

        public LoginFrame() {
            setTitle("Login");
            setSize(350, 200);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));

            panel.add(new JLabel("Admin ID:"));
            txtUser = new JTextField();
            panel.add(txtUser);

            panel.add(new JLabel("Password:"));
            txtPass = new JPasswordField();
            panel.add(txtPass);

            JButton loginBtn = new JButton("Login");
            panel.add(new JLabel());
            panel.add(loginBtn);

            add(panel, BorderLayout.CENTER);

            loginBtn.addActionListener(e -> {
                new DashboardFrame(txtUser.getText()).setVisible(true);
                dispose();
            });
        }
    }

    public static void main(String[] args) {
        new LoginFrame().setVisible(true);
    }
}
