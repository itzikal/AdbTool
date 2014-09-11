package adbTool.models;

import java.awt.Color;

public enum LogcatLevel 
{
	Verbose ("Verbose", 'V', Color.GREEN, 0),
	Debug ("Debut", 'D', Color.MAGENTA, 1),
	Info ("Info", 'I', Color.BLUE, 2),
	Warnning ("Warnning", 'W', Color.CYAN, 3),
	Error ("Error", 'E', Color.RED, 4),
	Asset ("Asset", 'A', Color.BLACK, 5),
	Unknown("", ' ', Color.GRAY, -1);

	private String _name;
	private char _letter;
	private Color _color;
    private int _value;

	LogcatLevel(String name, char logcatLetter, Color color, int value)
	{
		_name = name;
		_letter = logcatLetter;
		_color = color;
        _value = value;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public char getLetter()
	{
		return _letter;
	}

    public int getValue()
    {
        return _value;
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
