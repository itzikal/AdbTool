package adbTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ADBWrapper
{
    private static ADBWrapper _instance;
    public String mPagkage;
    public String mPid;

    protected ADBWrapper()
    {
        //getActivePackageAndPid();
    }

    public static ADBWrapper getInstance()
    {
        if (_instance == null)
        {
            _instance = new ADBWrapper();
        }
        return _instance;
    }

    public void restartServer()
    {
        try
        {
            executeADBCommand("kill-server").wait();
            executeADBCommand("start-server");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public Process executeADBCommand(String... args)
    {
        String[] commands = new String[args.length + 1];
        commands[0] = "adb";
        int i = 1;
        for (String arg : args)
        {
            commands[i] = arg;
            i++;
        }
        Process proc = null;
        ProcessBuilder processBuilder = new ProcessBuilder(commands);

        try
        {
            proc = processBuilder.start();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return proc;
    }


    private ArrayList<String> getInstallPackeages()
    {

        ArrayList<String> l = new ArrayList<String>();
        Process p = executeADBCommand("shell", "pm", "list", "packages", "-3");
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader bufferdreader = new BufferedReader(isr);
        while (true)
        {
            try

            {
                String s = bufferdreader.readLine();
                if (s == null) break;
                if (!s.isEmpty())
                {
                    String x = s.replace("package:", "");
                    l.add(x);
                }
            }
            catch (Exception e)
            {
                break;
            }
        }
        return l;
    }

    private ArrayList<String> getInstallAPKPackeages()
    {

        ArrayList<String> l = new ArrayList<String>();
        Process p = executeADBCommand("shell", "pm", "list", "packages", "-f");
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader bufferdreader = new BufferedReader(isr);
        while (true)
        {
            try

            {
                String s = bufferdreader.readLine();
                if (s == null) break;
                if (!s.isEmpty())
                {
                    String x = s.substring(s.indexOf(":"), s.indexOf("="));
                    l.add(x);
                }
            }
            catch (Exception e)
            {
                break;
            }
        }
        return l;
    }

    private String getPID(String packageName)
    {

        String s = null;
        Process p = executeADBCommand("shell", "ps | grep " + packageName + " |gawk '{print $2}'");
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader bufferdreader = new BufferedReader(isr);
        try

        {
            s = bufferdreader.readLine();
        }
        catch (Exception e)
        {

        }

        return s;
    }

    private void getActivePackageAndPid()
    {

        String s = null;
        Process p = executeADBCommand("shell", "dumpsys activity | grep top-activity");
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader bufferdreader = new BufferedReader(isr);
        try

        {
            s = bufferdreader.readLine();
        }
        catch (Exception e)
        {

        }

        try
        {
            p.waitFor(1000, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        mPagkage = s.substring(s.lastIndexOf(":") + 1, s.lastIndexOf("/"));
        mPid = s.substring(s.indexOf("trm:") + "trm: 0 ".length(), s.lastIndexOf(":"));
    }

    public void installApk(String filename)
    {
        executeADBCommand(filename);
    }

    public String getActivePackage()
    {
        Process p = executeADBCommand("shell", "dumpsys", "activity");//, "|", "find", "\"top-activity\"");



        return "";
    }



    public ArrayList<String> getProcessResult(Process process)
    {
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader bufferdreader = new BufferedReader(isr);
        ArrayList<String> list = new ArrayList<String>();

        try

        {
            process.waitFor();
            String line;
            while ((line = bufferdreader.readLine()) != null)
            {
                list.add(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return list;
    }
}
