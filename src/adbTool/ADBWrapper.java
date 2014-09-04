package adbTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import adbTool.models.*;

public class ADBWrapper
{
    private static ADBWrapper _instance;
    private Device _selectedDevice;

    protected ADBWrapper()
    {

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
            executeADBCommand("kill-server").waitFor();
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
//        commands[1] = _selectedDevice == null? "": "-s";
//        commands[2] = _selectedDevice == null? "": _selectedDevice.getDeviceId();

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
        ArrayList<String> processResult = getProcessResult(executeADBCommand("shell", "pm", "list", "packages", "-3"));

        for (String s : processResult)
        {
            String x = s.replace("package:", "");
            l.add(x);
        }

        return l;
    }

    //    private ArrayList<String> getInstallAPKPackeages()
    //    {
    //
    //        ArrayList<String> l = new ArrayList<String>();
    //        Process p = executeADBCommand("shell", "pm", "list", "packages", "-f");
    //        InputStream is = p.getInputStream();
    //        InputStreamReader isr = new InputStreamReader(is);
    //        BufferedReader bufferdreader = new BufferedReader(isr);
    //        while (true)
    //        {
    //            try
    //
    //            {
    //                String s = bufferdreader.readLine();
    //                if (s == null) break;
    //                if (!s.isEmpty())
    //                {
    //                    String x = s.substring(s.indexOf(":"), s.indexOf("="));
    //                    l.add(x);
    //                }
    //            }
    //            catch (Exception e)
    //            {
    //                break;
    //            }
    //        }
    //        return l;
    //    }
    //
    //    private String getPID(String packageName)
    //    {
    //
    //        String s = null;
    //        Process p = executeADBCommand("shell", "ps | grep " + packageName + " |gawk '{print $2}'");
    //        InputStream is = p.getInputStream();
    //        InputStreamReader isr = new InputStreamReader(is);
    //        BufferedReader bufferdreader = new BufferedReader(isr);
    //        try
    //
    //        {
    //            s = bufferdreader.readLine();
    //        }
    //        catch (Exception e)
    //        {
    //
    //        }
    //
    //        return s;
    //    }


    public Process installApk(String filename)
    {
        return executeADBCommand("install", filename);
    }

    public AndroidPackage getActivePackage()
    {
        ArrayList<String> processResult = searchProcessResult(executeADBCommand("shell", "dumpsys", "activity"), "top-activity");
        if(processResult == null || processResult.isEmpty())
        {
            return new NullAndroidPackage();
        }
        String s = processResult.get(0);
        AndroidPackage p = new AndroidPackage();
        p.setName(s.substring(s.lastIndexOf(":") + 1, s.lastIndexOf("/")));
        p.setPid(s.substring(s.indexOf("trm:") + "trm: 0 ".length(), s.lastIndexOf(":")));
        return p;
    }




    public ArrayList<String> getProcessResult(Process process)
    {
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader bufferdreader = new BufferedReader(isr);
        ArrayList<String> list = new ArrayList<String>();

        try

        {
            //  process.waitFor();
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
        //        catch (InterruptedException e)
        //        {
        //            e.printStackTrace();
        //        }

        return list;
    }

    public ArrayList<String> searchProcessResult(Process process, String search)
    {
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader bufferdreader = new BufferedReader(isr);
        ArrayList<String> list = new ArrayList<String>();

        try

        {
            //  process.waitFor();
            String line;
            while ((line = bufferdreader.readLine()) != null)
            {
                if (line.contains(search)) list.add(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //        catch (InterruptedException e)
        //        {
        //            e.printStackTrace();
        //        }

        return list;
    }

    public void setDevice(Device device)
    {
        this._selectedDevice = device;
    }

    public ArrayList<String> getPackages()
    {
        ArrayList<String> results = new ArrayList<>();
       /*
        pm list packages: prints all packages, optionally only
        those whose package name contains the text in FILTER.  Options:

        -f: see their associated file.
        -d: filter to only show disbled packages.
        -e: filter to only show enabled packages.
        -s: filter to only show system packages.
        -3: filter to only show third party packages.
        -i: see the installer for the packages.
        -u: also include uninstalled packages.

        */
        ArrayList<String> processResult = getProcessResult(executeADBCommand("shell", "pm", "list", "packages", "-3", "-e"));
        for (String s: processResult)
        {
            if(!s.contains(":"))
            {
                continue;
            }
            results.add((s.split(":"))[1]);
        }
        return results;
    }
}
