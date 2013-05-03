import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

public class ButtonEditor extends DefaultCellEditor {

    /**
     * 
     */
    private static final long serialVersionUID = -6574847401130525555L;

    protected JButton button;

    private String label;

    private boolean isPushed;

    private Object value;

    public static ServerProperties server = new ServerProperties();

    public ButtonEditor(JCheckBox checkBox, String label) {
        super(checkBox);
        this.label = label;
        this.button = new JButton();
        // button.setOpaque(true);
        this.button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonEditor.this.fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        this.button.setText(this.label);

        this.value = value;
        this.isPushed = true;
        return this.button;
    }

    @Override
    public Object getCellEditorValue() {

        if (this.isPushed) {
            try {
                BackUpManager.restore((String) this.value);

                System.out.println("\\\\" + server.getBackUpDirectory() + "\\" + this.value);
            }
            catch (Exception e) {
                // TODO: handle exception
            }
        }
        this.isPushed = false;
        return new String(this.label);

    }

    @Override
    public boolean stopCellEditing() {
        this.isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
