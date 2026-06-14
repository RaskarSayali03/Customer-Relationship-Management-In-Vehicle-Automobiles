package ui;

import javax.swing.*;
import java.awt.*;

public class AssignSCButtonEditor extends DefaultCellEditor {

    private JButton button;
    private JTable table;

    public AssignSCButtonEditor(JCheckBox checkBox, JTable table) {
        super(checkBox);
        this.table = table;

        button = new JButton("Assign SC");
        button.setOpaque(true);

        button.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;

            int modelRow = table.convertRowIndexToModel(row);
            int enquiryId = (int) table.getModel().getValueAt(modelRow, 0);

            JOptionPane.showMessageDialog(
                table,
                "Assign SC clicked for Enquiry ID: " + enquiryId
            );
        });
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "Assign SC";
    }
}