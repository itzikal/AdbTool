package adbTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import adbTool.interfaces.OnListChangeListener;
import adbTool.models.LogcatItem;
import adbTool.models.LogcatLevel;

public class ADBLogcat
{
    private Thread _logcatThread;
    private Process _process;
    private OnListChangeListener _listChangeListener;

    private static ADBLogcat _instance;
    private LogcatLevel _logcatLevel = LogcatLevel.Debug;

    protected ADBLogcat()
    {
    }

    public static ADBLogcat getInstance()
    {
        if (_instance == null)
        {
            _instance = new ADBLogcat();
        }
        return _instance;
    }

    public void startLogcat()
    {
        close();
        _process = executeLogcatCommand("*:" + _logcatLevel.getLetter());// + " |grep " + ADBWrapper.getInstance().mPid);
        _logcatThread = new Thread(() -> {
            while (true)
            {
                String s = getLine();
                System.out.println(s);
                if (s == null)
                {
                    try
                    {
                        Thread.sleep(200);
                    }
                    catch (InterruptedException e)
                    {

                    }
                    continue;
                }

                if (s != null && !s.isEmpty())
                {
                    LogcatItem item = new LogcatItem(s);
                    _listChangeListener.itemAdded(item);
                }
            }
        });

        _logcatThread.start();
    }

    public void setOnListChangeListener(OnListChangeListener onListChangeListener)
    {
        _listChangeListener = onListChangeListener;
    }

    private String getLine()
    {
        InputStream is = _process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader bufferdreader = new BufferedReader(isr);
        try
        {
            return bufferdreader.readLine();
        }
        catch (IOException e)
        {
        }

        return null;
    }

    public void close()
    {
        _listChangeListener.listCleared();
        if (_logcatThread != null)
        {
            _logcatThread.interrupt();
        }

        if (_process != null)
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

    public void clearLogcat()
    {
        executeLogcatCommand("-c");
        _listChangeListener.listCleared();
    }

    public Process executeLogcatCommand(String... args)
    {
        String[] commands = new String[args.length + 1];
        commands[0] = "logcat";
        int i = 1;
        for (String arg : args)
        {
            commands[i] = arg;
            i++;
        }
        return ADBWrapper.getInstance().executeADBCommand(commands);

    }

    public void setLogcatLevel(LogcatLevel level)
    {
        _logcatLevel = level;
    }
}
