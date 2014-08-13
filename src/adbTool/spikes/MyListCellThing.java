package adbTool.spikes;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class MyListCellThing extends JLabel implements ListCellRenderer 
{

    public MyListCellThing() {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Assumes the stuff in the list has a pretty toString
        String log = value.toString();
    	setText(log);

    	char level = log.charAt(0);
        
    	switch (level)
    	{
    		case 'V':
    			setForeground(Color.GREEN);
    			break;
    		case 'D':
    			setForeground(Color.PINK);
    			break;
    		case 'I':
    			setForeground(Color.BLUE);
    			break;
    		case 'E':
    			setForeground(Color.RED);
    			break;
    		case 'W':
    			setForeground(Color.GRAY);
    			break;
    		case 'A':
    			setForeground(Color.CYAN);
    			break;
			default:
				setForeground(Color.BLACK);
				break;
    	}

        return this;
    }
}


