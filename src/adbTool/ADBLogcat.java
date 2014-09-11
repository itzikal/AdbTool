package adbTool;

import adbTool.core.AdbWrapper;
import adbTool.core.ShellOutputReceiver;
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
    ShellOutputReceiver _logcatReceiver;
    private String _logcatPidFilter;

    private void initReceiver()
    {
        _logcatReceiver = new ShellOutputReceiver(null, results -> {
            if (results != null && results.length != 0)
            {
                for (String s : results)
                {
                    if(s.isEmpty()) continue;

                    LogcatItem item = new LogcatItem(s);
                    _listChangeListener.itemAdded(item);
                }
            }
        });
    }

    protected ADBLogcat()
    {
        initReceiver();
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
        startLogcat("logcat", "*:" + _logcatLevel.getLetter());
//        _process = executeLogcatCommand("*:" + _logcatLevel.getLetter());// + " |grep " + ADBWrapper.getInstance().mPid);
//        _logcatThread = new Thread(() -> {
//            while (true)
//            {
//                String s = getLine();
//                System.out.println(s);
//                if (s == null)
//                {
//                    try
//                    {
//                        Thread.sleep(200);
//                    }
//                    catch (InterruptedException e)
//                    {
//
//                    }
//                    continue;
//                }
//
//                if (s != null && !s.isEmpty())
//                {
//                    LogcatItem item = new LogcatItem(s);
//                    _listChangeListener.itemAdded(item);
//                }
//            }
//        });
//
//        _logcatThread.start();
    }

    public void setOnListChangeListener(OnListChangeListener onListChangeListener)
    {
        _listChangeListener = onListChangeListener;
    }

//    private String getLine()
//    {
//        InputStream is = _process.getInputStream();
//        InputStreamReader isr = new InputStreamReader(is);
//        BufferedReader bufferdreader = new BufferedReader(isr);
//        try
//        {
//            return bufferdreader.readLine();
//        }
//        catch (IOException e)
//        {
//        }
//
//        return null;
//    }

    public void close()
    {
        _listChangeListener.listCleared();
//        if (_logcatThread != null)
//        {
//            _logcatThread.interrupt();
//        }
//
//        if (_process != null)
//        {
//            try
//            {
//                _process.destroy();
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
    }

    public void clearLogcat()
    {
        AdbWrapper.getInstance().executeShellCommand(new ShellOutputReceiver(null, null), "logcat", "-c");
        _listChangeListener.listCleared();
    }

    private void startLogcat(String... args)
    {

        AdbWrapper.getInstance().executeShellCommand(_logcatReceiver, args);
//        String[] commands = new String[args.length + 1];
//        commands[0] = "logcat";
//        int i = 1;
//        for (String arg : args)
//        {
//            commands[i] = arg;
//            i++;
//        }
//        return ADBWrapper.getInstance().executeADBCommand(commands);

    }

    public void setLogcatLevel(LogcatLevel level)
    {
        _logcatLevel = level;
        startLogcat();
    }

    public void setLogcatPidFilter(String logcatPidFilter)
    {
        _logcatReceiver.setFilter(logcatPidFilter);
    }
}
