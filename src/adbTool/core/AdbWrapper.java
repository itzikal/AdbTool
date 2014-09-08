/*
 * Copyright (C) 2011 Dr.SuperChamp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package adbTool.core;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IClientChangeListener;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;

import java.util.ArrayList;
import java.util.List;

import adbTool.models.AndroidPackage;
import adbTool.models.Device;
import adbTool.models.NullAndroidPackage;

public class AdbWrapper
{
    private static AdbWrapper sSingletonInstance = null;
    private DeviceConnectionListener mDeviceStateListener = null;
    private boolean mIsAdbInitialized = false;
    private final List<Device> mConnectedDevices = new ArrayList<>();
    private IClientChangeListener mClientChangeListener = new ClientChangeListener();
    private IDeviceChangeListener mDeviceChangeListener = new DeviceChangeListener();

    private Device mDevice;

    private adbTool.core.ShellOutputReceiver dummyOutput = new adbTool.core.ShellOutputReceiver(null, null);

    public void getPackages(ShellOutputReceiver reciver, String... args)
    {
        executeShellCommand(reciver, "pm", "list", "packages", "-3", "-e");
    }

    public void executeShellCommand(ShellOutputReceiver reciver, String... args)
    {
        StringBuilder sb = new StringBuilder();

        for (String s : args)
        {
            sb.append(s);
            sb.append(" ");
        }

        executeShellCommand(sb.toString(), reciver);
    }

    public void getActivePackage(ShellCommandResult<AndroidPackage> result)
    {
        adbTool.core.ShellOutputReceiver reciver = new adbTool.core.ShellOutputReceiver("top-activity", new adbTool.core.ShellOutputReceiver.ShellOutputReceiverResults() {
            @Override
            public void onResultReceived(String[] results)
            {
                AndroidPackage p;
                if(results == null || results.length == 0)
                {
                    p = new NullAndroidPackage();
                }
                else
                {
                    String s = results[0];
                    if(!s.contains("trm"))
                    {
                        return;
                    }
                    Util.DbgLog("On active package received: " + s);
                    p = new AndroidPackage();
                    p.setName(s.substring(s.lastIndexOf(":") + 1, s.lastIndexOf("/")));
                    p.setPid(s.substring(s.indexOf("trm:") + "trm: 0 ".length(), s.lastIndexOf(":")));
                }
                result.onCommandResult(p);
            }
        });
        executeShellCommand(reciver, "dumpsys", "activity");
    }

    public void sendText(String text)
    {
        String replaceSpaces = text.replace(" ", " && input keyevent 62 && input text ");
        String replacePriod = replaceSpaces.replace(".", " && input keyevent 56 && input text ");
        String replaceComma = replacePriod.replace(",", " && input keyevent 55 && input text ");
        String concat = "input text ".concat(replaceComma);
        executeShellCommand(concat, dummyOutput);
    }

    public void clearAppData(String packageName)
    {
        executeShellCommand(dummyOutput,"pm", "clear", packageName);
    }

    public Device getActiveDevice()
    {
        return mDevice;
    }

    /**
     * Interface to listen device connection states.
     */
    public static interface DeviceConnectionListener
    {
        void deviceConnected(Device devSerialNumber);

        void deviceDisconnected(Device devSerialNumber);

        void deviceChanged(Device deviceModel);
    }

    public static interface ShellOutputReceiver extends IShellOutputReceiver
    {}

    public interface ShellCommandResult<T>
    {
        <T> void onCommandResult(T result);
    }

    private AdbWrapper()
    {
    }

    public static synchronized AdbWrapper getInstance()
    {
        if (sSingletonInstance == null)
        {
            sSingletonInstance = new AdbWrapper();
        }
        return sSingletonInstance;
    }

    //    private static boolean checkPath(String filePath)
    //    {
    //        if (filePath != null)
    //        {
    //            if ((new File(filePath)).exists())
    //            {
    //                return true;
    //            }
    //            else
    //            {
    //                Util.DbgLog("File not found: " + filePath);
    //            }
    //        }
    //        else
    //        {
    //            Util.DbgLog("filePath is null");
    //        }
    //        return false;
    //    }

    public boolean connect(String adbFilePath, DeviceConnectionListener listener)
    {
        //        if (!checkPath(adbFilePath)) {
        //            Util.DbgLog("Error occured in setting adb binary file path");
        //            return false;
        //        }

        if (mIsAdbInitialized)
        {
            Util.DbgLog("Already connected..");
            return false;
        }

        mDeviceStateListener = listener;

        AndroidDebugBridge.init(false /* no need to support debug*/);
        AndroidDebugBridge.addClientChangeListener(mClientChangeListener);
        AndroidDebugBridge.addDeviceChangeListener(mDeviceChangeListener);

        AndroidDebugBridge.createBridge();//adbFilePath true);

        //adbFilePath, true /* forceNewBridge */);
        mIsAdbInitialized = true;
        return true;
    }

    public void disconnect()
    {
        if (!mIsAdbInitialized)
        {
            Util.DbgLog("not connected..");
            return;
        }
        mDeviceStateListener = null;
        AndroidDebugBridge.disconnectBridge();
    }

    public List<Device> getConnectedDevices()
    {
        return mConnectedDevices;
    }

    public void setDevice(Device device)
    {
        mDevice = device;
    }

    public void executeShellCommand(String shellCmd, ShellOutputReceiver receiver)
    {
        if (mDevice == null)
        {
            return;
        }
        Util.DbgLog("About to execute shell command: " + shellCmd);
        new Thread(()->mDevice.executeShellCommand(shellCmd, receiver, 0 /*timeout*/)).start();
    }

    public boolean executeShellCommand(Device device, String shellCmd, ShellOutputReceiver receiver)
    {
        return device.executeShellCommand(shellCmd, receiver, 0 /*timeout*/);
    }

    private Device findDevice(String serialNumber)
    {
        synchronized (mConnectedDevices)
        {
            for (Device device : mConnectedDevices)
            {
                if (device.getSerialNumber().equals(serialNumber)) return device;
            }
        }
        return null;
    }

    public void installApk(String apk)
    {
        if(mDevice !=null)
        {
            mDevice.installPackage(apk, false);
        }
    }
    private class ClientChangeListener implements IClientChangeListener
    {
        @Override
        public void clientChanged(Client arg0, int arg1)
        {
            Util.DbgLog();
        }
    }

    private class DeviceChangeListener implements IDeviceChangeListener
    {
        @Override
        public void deviceConnected(IDevice device)
        {
            Util.DbgLog();
            Device deviceModel = new Device(device);
            synchronized (mConnectedDevices)
            {
                mConnectedDevices.add(deviceModel);
            }

            if (mDeviceStateListener != null)
            {
                mDeviceStateListener.deviceConnected(deviceModel);
            }
        }

        @Override
        public void deviceDisconnected(IDevice device)
        {
            Util.DbgLog();
            Device deviceModel = findDevice(device.getSerialNumber());
            synchronized (mConnectedDevices)
            {
                mConnectedDevices.remove(deviceModel);
            }

            if (mDeviceStateListener != null)
            {
                mDeviceStateListener.deviceDisconnected(deviceModel);
            }
        }

        @Override
        public void deviceChanged(IDevice device, int changeMask)
        {
            Util.DbgLog();
            Device deviceModel = findDevice(device.getSerialNumber());
            if (mDeviceStateListener != null)
            {
                mDeviceStateListener.deviceChanged(deviceModel);
            }

            //            Client[] clients = device.getClients();
//            Map<String, String> properties = device.getProperties();
//            for (String key: properties.keySet())
//            {
//                Util.DbgLog("key: "+key +", value: " +properties.get(key));
//            }
        }
    }
}
