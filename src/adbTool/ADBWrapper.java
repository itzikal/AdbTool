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
	private final DefaultListModel<LogcatItem> _logcat = new DefaultListModel<LogcatItem>();
	private Thread _logcatThread;
	private Process _process;
	private OnListChangeListener _listChangeListener;

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

	public void startProcess(LogcatLevel level)
	{
//		ArrayList<String> packages = getInstallPackeages();
//		for(String p : packages)
//		{
//			excuteADBCommand("shell", "pull", p);
//		}
	//	String s= getPID("zemingo.com.bluetoothserver");
		Close();
	//	_logcat.clear();
		//_process = executeLogcatCommand("-v", "time" );
		_process = executeLogcatCommand("*:" + level.getLetter());
		_logcatThread =	new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				while(true)
				{
					String s  = getLine();
					System.out.println(s);
					if(s==null)
					{
						try 
						{
							//System.out.println("line was empty, sleep 200ms");
							Thread.sleep(200);
						}
						catch (InterruptedException e) 
						{

						}
						continue;
					}

					if(s!=null && !s.isEmpty())
					{
						LogcatItem item =  new LogcatItem(s);//LogcatLevel.Debug , "app", "tag", s);
					//	_logcat.addElement(item);
						_listChangeListener.itemAdded(item);
					}
				}
			}
		});
		_logcatThread.start(); 
	}

//	public DefaultListModel<LogcatItem> getLogcatList()
//	{
//		return _logcat;
//	}

	public void setOnListChangeListener(OnListChangeListener onListChangeListener)
	{
		_listChangeListener = onListChangeListener;
	}
	
	private String getLine()
	{
		InputStream is = _process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader bufferdreader= new BufferedReader(isr);
		try 
		{
			return  bufferdreader.readLine();
		}
		catch (IOException e) 
		{
		}

		return null;
	}	

	public void Close()
	{
		_listChangeListener.listCleared();
		if(_logcatThread != null)
		{
			_logcatThread.interrupt();
		}

		if(_process !=null)
		{
			try
			{
				_process.destroy();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
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

	public void clearLogcat()
	{
		executeLogcatCommand("-c");
		_listChangeListener.listCleared();
	//	_logcat.clear();
	}

	private Process excuteADBCommand(String... args)
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

	private Process executeLogcatCommand(String... args)
	{
		String [] commands = new String[args.length+1];
		commands[0] = "logcat";
		int i = 1;
		for(String arg : args)
		{
			commands[i] = arg;
			i++;
		}
		return excuteADBCommand(commands);

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
}
