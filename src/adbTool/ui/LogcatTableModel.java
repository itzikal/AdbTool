package adbTool.ui;

import javax.swing.DefaultListModel;
import javax.swing.table.AbstractTableModel;

import adbTool.ADBLogcat;
import adbTool.exceptions.UnreachableCodeException;
import adbTool.interfaces.OnListChangeListener;
import adbTool.models.ILogcatItem;
import adbTool.models.LogcatItem;
import adbTool.models.LogcatLevel;

public class LogcatTableModel extends AbstractTableModel implements OnListChangeListener
{
	public static final String[] COLUMN_TITLES = new String[]{"Level", "Pid", "Application", "Tag", "Text"};
	public static final Class<?>[] COLUMN_CLASSES = {LogcatLevel.class, String.class, String.class, String.class, String.class};
	
	DefaultListModel<LogcatItem> _logcatList = new DefaultListModel<LogcatItem>();
	
	public LogcatTableModel()
	{
        ADBLogcat.init(this);
		//ADBLogcat.getInstance().setOnListChangeListener(this);
	}
	
	@Override
	public int getColumnCount() 
	{
		return COLUMN_TITLES.length; 
	}
	@Override
	public int getRowCount()
	{
		return _logcatList.size();
	}
	
	@Override
	public String getColumnName(int index) 
	{
		return COLUMN_TITLES[index];
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		return COLUMN_CLASSES[column];
	}
	

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		ILogcatItem item = _logcatList.get(rowIndex);
		switch(columnIndex)
		{
			case 0: return item.getLevel();
			case 1: return item.getPid();
			case 2: return item.getApplication();
			case 3: return item.getTag();
			case 4: return item.getText();
			 
			default:
				throw new UnreachableCodeException();
		}
	}

	@Override
	public void itemAdded(LogcatItem item) 
	{
		_logcatList.addElement(item);
		fireTableRowsInserted(0,getRowCount());
	}

	@Override
	public void listCleared() 
	{
		_logcatList.clear();
		fireTableRowsDeleted(0, getRowCount());
	}

    public DefaultListModel<LogcatItem> getItemList()
    {
        return _logcatList;
    }
}
