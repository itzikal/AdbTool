package adbTool.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class LogcatTableCellRenderer extends DefaultTableCellRenderer 
{
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	  {
		 Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		 c.setForeground(Color.BLUE);

		 return c;
	  }
}