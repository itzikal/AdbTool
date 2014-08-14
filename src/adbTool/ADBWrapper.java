package adbTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import adbTool.interfaces.OnListChangeListener;
import adbTool.models.LogcatItem;
import adbTool.models.LogcatLevel;

public class ADBWrapper 
{
	private static ADBWrapper _instance;
	
	protected ADBWrapper()
	{
	}

	public static ADBWrapper getInstance() 
	{
		if(_instance == null)
		{
			_instance = new ADBWrapper();
		}
		return _instance;
	} 

	public void restartServer()
	{
		try
		{
			excuteADBCommand("kill-server").wait();
			excuteADBCommand("start-server");
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}

	public Process excuteADBCommand(String... args)
	{
		String [] commands = new String[args.length+1];
		commands[0] = "adb";
		int i = 1;
		for(String arg : args)
		{
			commands[i] = arg;
			i++;
		}
		Process proc = null;
		ProcessBuilder processBuilder = new ProcessBuilder(commands);

		try {
			proc = processBuilder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return proc;
	}


	
	private ArrayList<String> getInstallPackeages()
	{

		ArrayList<String> l = new ArrayList<String>();
		Process p = excuteADBCommand("shell", "pm", "list", "packages", "-3");
		InputStream is = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader bufferdreader= new BufferedReader(isr);
		while(true)
		{
			try 

			{
				String s =   bufferdreader.readLine();
				if(s==null) break;
				if(!s.isEmpty())
				{
					String x = s.replace("package:", "");
					l.add(x);
				}
			}
			catch(Exception e)
			{
				break;
			}
		}
		return l;
	}
	private ArrayList<String> getInstallAPKPackeages()
	{

		ArrayList<String> l = new ArrayList<String>();
		Process p = excuteADBCommand("shell", "pm", "list", "packages", "-f");
		InputStream is = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader bufferdreader= new BufferedReader(isr);
		while(true)
		{
			try 

			{
				String s =   bufferdreader.readLine();
				if(s==null) break;
				if(!s.isEmpty())
				{
					String x = s.substring(s.indexOf(":"), s.indexOf("="));
					l.add(x);
				}
			}
			catch(Exception e)
			{
				break;
			}
		}
		return l;
	}
	
	private String getPID(String packageName)
	{

	String s = null;
		Process p = excuteADBCommand("shell", "ps | grep "+packageName+" |gawk '{print $2}'");
		InputStream is = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader bufferdreader= new BufferedReader(isr);
					try 

			{
				 s =   bufferdreader.readLine();
			}
			catch(Exception e)
			{
			
			}
		
		return s;
	}
	
	public void installApk(String filename)
	{
		excuteADBCommand(filename);
	}
}
