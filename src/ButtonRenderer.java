import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ButtonRenderer extends JButton implements TableCellRenderer {

    /**
     * 
     */
    private String label = "";

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    private static final long serialVersionUID = -5351055738394303536L;

    public ButtonRenderer(String label) {
        // setOpaque(true);
        this.setLabel(label);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setText(this.getLabel());
        return this;
    }
}

/**
 * @version 1.0 11/09/98
 */

