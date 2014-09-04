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

import adbTool.models.Device;

public class AdbWrapper
{
    private static AdbWrapper sSingletonInstance = null;
    private DeviceConnectionListener mDeviceStateListener = null;
    private boolean mIsAdbInitialized = false;
    private final List<Device> mConnectedDevices = new ArrayList<>();
    private IClientChangeListener mClientChangeListener = new ClientChangeListener();
    private IDeviceChangeListener mDeviceChangeListener = new DeviceChangeListener();

    private Device mDevice;

    /**
     * Interface to listen device connection states.
     */
    public static interface DeviceConnectionListener
    {
        void deviceConnected(Device devSerialNumber);

        void deviceDisconnected(Device devSerialNumber);
    }

    /**
     * Interface to receive shell-command output.
     * This wrapper is to hide any interfaces from ddmlib.
     *
     * @see {@link #executeShellCommand(String, String, ShellOutputReceiver)}.
     */
    public static interface ShellOutputReceiver extends IShellOutputReceiver
    {}

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

    public boolean executeShellCommand(String shellCmd, ShellOutputReceiver receiver)
    {
            return mDevice.executeShellCommand(shellCmd, receiver, 0 /*timeout*/);
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
        }
    }
}
