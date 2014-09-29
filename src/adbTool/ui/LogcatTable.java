package adbTool.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import adbTool.models.LogcatLevel;

public class LogcatTable extends JTable
{
    private TableRowSorter<LogcatTableModel> _sorter = new TableRowSorter<LogcatTableModel>();
    private static final Color SELECTION_BACKGROUND_COLOR = Color.BLUE;
    private static final Color STANDARD_BACKGROUND_COLOR = Color.LIGHT_GRAY;
    private boolean _isAutoScroll = true;

    public LogcatTable()
    {
        //	setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                if (_isAutoScroll)
                {
                    scrollRectToVisible(getCellRect(getRowCount() - 1, 0, true));
                }
            }
        });
    }

    @Override
    public void setModel(TableModel arg0)
    {
        super.setModel(new LogcatTableModel());
    }

    public void setAutoScroll(boolean isAutoScroll)
    {
        _isAutoScroll = isAutoScroll;
    }

    public boolean getIsAutoScroll()
    {
        return _isAutoScroll;
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
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
    {
        Component cell = super.prepareRenderer(renderer, row, column);
        LogcatLevel x = (LogcatLevel) getModel().getValueAt(row, 0);
        cell.setForeground(x.getColor());
        if (isCellSelected(row, column)) cell.setBackground(SELECTION_BACKGROUND_COLOR);

        else cell.setBackground(STANDARD_BACKGROUND_COLOR);

        return cell;
    }
}
