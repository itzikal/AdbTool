package adbTool.models;

import java.util.Date;

public interface ILogcatItem 
{
	 	LogcatLevel getLevel();
	  //  Date getTime();
	    String getApplication();
	    String getTag();
	    String getText();
		String getPid();
}
