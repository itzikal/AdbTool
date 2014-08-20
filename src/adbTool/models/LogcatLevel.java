package adbTool.models;

import java.awt.Color;

public enum LogcatLevel 
{
	Verbose ("Verbose", 'V', Color.GREEN),
	Debug ("Debut", 'D', Color.MAGENTA),
	Info ("Info", 'I', Color.BLUE),
	Warnning ("Warnning", 'W', Color.CYAN),
	Error ("Error", 'E', Color.RED),
	Asset ("Asset", 'A', Color.BLACK),
	Unknown("", ' ', Color.GRAY);

	private String _name;
	private char _letter;
	private Color _color;
	LogcatLevel(String name, char logcatLetter, Color color)
	{
		_name = name;
		_letter = logcatLetter;
		_color = color;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public char getLetter()
	{
		return _letter;
	}
	
//
//	public static LogcatLevel convert(String name)
//	{
//		switch (name)
//		{
//		case "Verbose":
//			return LogcatLevel.Verbose;
//		case "Debug":
//			return LogcatLevel.Debug;
//		case "Info":
//			return LogcatLevel.Info;
//		case "Warnning":
//			return LogcatLevel.Warnning;
//		case "Error":
//			return LogcatLevel.Error;
//		case "Asset":
//			return LogcatLevel.Asset;
//		default:
//			return LogcatLevel.Unknown;
//		}
//
//	}
	
	public static LogcatLevel convert(char letter)
	{
		switch (letter) 
		{
		case 'V':
			return LogcatLevel.Verbose;
		case 'D':
			return LogcatLevel.Debug; 
		case 'I':
			return LogcatLevel.Info; 
		case 'W':
			return LogcatLevel.Warnning;
		case 'E':
			return LogcatLevel.Error; 
		case 'A':
			return LogcatLevel.Asset; 
		default:
			return LogcatLevel.Unknown;
		}
	}

	public Color getColor()
	{	
		return _color;
	}

	public static LogcatLevel[] getValues() {
		return new LogcatLevel[]{ LogcatLevel.Verbose, LogcatLevel.Debug, LogcatLevel.Info, LogcatLevel.Warnning, LogcatLevel.Error, LogcatLevel.Asset};
	}
}
