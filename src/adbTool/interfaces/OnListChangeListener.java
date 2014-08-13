package adbTool.interfaces;

import adbTool.models.LogcatItem;

public interface OnListChangeListener
{
	void itemAdded(LogcatItem item);
	void listCleared();
}
