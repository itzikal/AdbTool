package adbTool.models;

import java.util.Date;

public class LogcatItem  implements ILogcatItem 
{
	private String _logcatString;
	private LogcatLevel _level;
	//private Date _time;
	private String _application;
	private String _tag;
	private String _text;
	private String _pid;
	
	public LogcatItem(String logcatString)
	{
		_logcatString = logcatString;
		parseString();
	}
	
	
	private void parseString() 
	{
		_level = LogcatLevel.convert(_logcatString.charAt(0));
		if(_level == LogcatLevel.Unknown)
		{
			_tag = "??";
			_text = _logcatString;
			_pid = "??";
		}
		else
		{
		_tag = _logcatString.substring(_logcatString.indexOf("/") + 1, _logcatString.indexOf("("));
		_text = _logcatString.substring(_logcatString.indexOf(":") +1);
		_pid = _logcatString.substring(_logcatString.indexOf("(")+2, _logcatString.indexOf(")"));
		}
		_application = "unknown yet";
	}


	public LogcatItem(LogcatLevel level,  String app, String tag, String text)
	{
		_level = level;
	//	_time = time;
		_application = app;
		_tag = tag;
		_text = text;
				
	}
	
	@Override
	public LogcatLevel getLevel() 
	{
		return _level;
	}

	

	@Override
	public String getApplication() 
	{
		return _application;
	}

	@Override
	public String getTag() 
	{
		return _tag;
	}

	@Override
	public String getText()
	{
		return _text;
	}
	
	@Override
	public String getPid()
	{
		return _pid;
	}
}
