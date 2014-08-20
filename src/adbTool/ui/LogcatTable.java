package adbTool.ui;

import java.awt.Color;
import java.awt.Component;
import java.sql.Date;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import adbTool.ADBWrapper;
import adbTool.models.LogcatLevel;

public class LogcatTable  extends JTable
{
	private TableRowSorter<LogcatTableModel> _sorter = new TableRowSorter<LogcatTableModel>();
	private static final Color SELECTION_BACKGROUND_COLOR = Color.BLUE;
	private static final Color STANDARD_BACKGROUND_COLOR = Color.LIGHT_GRAY;
	
	public LogcatTable()
	{
	//	setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
	}
	
	@Override
	public void setModel(TableModel arg0) 
	{
		super.setModel(new LogcatTableModel());
	}
	
	
//	@Override
//	public void setRowSorter(RowSorter<? extends TableModel> arg0) {
//		// TODO Auto-generated method stub
//	//	super.setRowSorter(_sorter);
//	}

//	@Override
//	public void setDefaultRenderer(Class<?> arg0, TableCellRenderer arg1) {
//		// TODO Auto-generated method stub
//		super.setDefaultRenderer(LogcatTable.class, new LogcatTableCellRenderer());
//	}
	
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component cell = super.prepareRenderer(renderer, row, column);
		LogcatLevel  x =(LogcatLevel) getModel().getValueAt(row, 0);
		cell.setForeground(x.getColor());
		if (isCellSelected(row, column)) cell.setBackground(SELECTION_BACKGROUND_COLOR);
		
		else cell.setBackground(STANDARD_BACKGROUND_COLOR);

		return cell;
	}
}
