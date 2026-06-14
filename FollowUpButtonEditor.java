package ui;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FollowUpButtonEditor extends AbstractCellEditor
        implements TableCellEditor, ActionListener {

    private JButton button;
    private JTable table;

    public FollowUpButtonEditor(JTable table) {
        this.table = table;
        button = new JButton("See Follow-Up");
        button.addActionListener(this);
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {

        button.setText("See Follow-Up");
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "See Follow-Up";
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        int row = table.getSelectedRow();
        if (row == -1) return;

        int modelRow = table.convertRowIndexToModel(row);
        int enquiryId = (int) table.getModel().getValueAt(modelRow, 0);

        new EnquiryDetailFrame(enquiryId).setVisible(true);
        fireEditingStopped();
    }
}