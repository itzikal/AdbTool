package adbTool;

import com.android.ddmlib.NullOutputReceiver;

import java.util.ArrayList;

import adbTool.core.AdbWrapper;
import adbTool.core.ShellOutputReceiver;
import adbTool.core.Util;
import adbTool.interfaces.OnListChangeListener;
import adbTool.models.LogcatItem;
import adbTool.models.LogcatLevel;

public class ADBLogcat
{
    private static ADBLogcat _instance;
    private OnListChangeListener _listChangeListener;
    private LogcatLevel _logcatLevel = LogcatLevel.Debug;
    private ShellOutputReceiver _logcatReceiver;
    private String _logcatPidFilter;
    private final ArrayList<LogcatItem> _logcatItems = new ArrayList<>();
    private Object sync = new Object();

    private void initReceiver()
    {
        _logcatReceiver = new ShellOutputReceiver(null, results -> {
           synchronized (sync)
           {
               if (results != null && results.length != 0)
               {
                   for (String s : results)
                   {
                       LogcatItem item = new LogcatItem(s);
                       _logcatItems.add(item);
                       if (doesPassFilter(item))
                       {
                           _listChangeListener.itemAdded(item);
                       }
                   }
               }
           }
        });
    }

    private boolean doesPassFilter(LogcatItem item)
    {
        return (isInLevelRange(item) && isPassPIDFilter(item));
    }

    private boolean isPassPIDFilter(LogcatItem item)
    {
        return _logcatPidFilter == null || _logcatPidFilter.equals(item.getPid());
    }

    private boolean isInLevelRange(LogcatItem item)
    {
        return item.getLevel().getValue() >= _logcatLevel.getValue();
    }

    protected ADBLogcat(OnListChangeListener listChangeListener)
    {
        _listChangeListener = listChangeListener;
        initReceiver();
        startLogcat();
    }

    public static void init(OnListChangeListener listChangeListener)
    {
        if (_instance == null)
        {
            _instance = new ADBLogcat(listChangeListener);
        }
    }

    public static ADBLogcat getInstance()
    {
        if (_instance == null)
        {
            Util.DbgLog("AdbLogcat was not init");
            //throw new Exception("Logcat singelton has to be init before use");
        }
        return _instance;
    }

    //    public void startLogcat()
    //    {
    //        close();
    //
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
    //    }

//    public void setOnListChangeListener(OnListChangeListener onListChangeListener)
//    {
//        _listChangeListener = onListChangeListener;
//    }

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

//    public void close()
//    {
//        _listChangeListener.listCleared();
//                if (_logcatThread != null)
//                {
//                    _logcatThread.interrupt();
//                }
//
//                if (_process != null)
//                {
//                    try
//                    {
//                        _process.destroy();
//                    }
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//    }

    public void start()
    {
        startLogcat("logcat");
    }
    public void clearLogcat()
    {
        Util.DbgLog("Clear Logcat");
        AdbWrapper.getInstance().executeShellCommand(new NullOutputReceiver(), "logcat", "-c");
//        AdbWrapper.getInstance().executeShellCommand(new ShellOutputReceiver(null, null), "logcat", "-c");
        _listChangeListener.listCleared();
    }

    private void startLogcat(String... args)
    {
        Util.DbgLog("Start Logcat");
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
        restartLogcat();
    }

    private void restartLogcat()
    {
        synchronized (sync)
        {
            _listChangeListener.listCleared();
            for (LogcatItem item : _logcatItems)
            {
                if (doesPassFilter(item))
                {
                    _listChangeListener.itemAdded(item);
                }
            }
        }
    }

    public void setLogcatPidFilter(String logcatPidFilter)
    {
        _logcatReceiver.setFilter(logcatPidFilter);
        restartLogcat();
    }
}
