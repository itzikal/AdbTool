package adbTool;

import java.util.ArrayList;

/**
 * Created by Itzik on 31/08/2014.
 */
public class ADBDevices
{
    private static ADBDevices _instance;

    protected ADBDevices()
    {
    }

    public static ADBDevices getInstance()
    {
        if (_instance == null)
        {
            _instance = new ADBDevices();
        }
        return _instance;
    }

//    public ArrayList<Device> getDevices()
//    {
//        ArrayList<String> devices = executeADBCommand("devices");
//        ArrayList<String> devicesId = new ArrayList<String>();
//        for(String s : devices)
//        {
//            if(s.contains("List of devices"))
//            {
//                continue;
//            }
//            if(s.contains("device"))
//            {
//                String[] split = s.split("\\t");
//                devicesId.add(split[0]);
//            }
//
//        }
//        if(devicesId.isEmpty())
//        {
//
//        }
//
//        return getDeviceInformation(devicesId);
//    }

//    private ArrayList<Device> getDeviceInformation(ArrayList<String> devicesId)
//    {
//
//        ArrayList<Device> devices = new ArrayList<>();
//        for (String deviceId :devicesId)
//        {
//            Device device = new Device(deviceId);
//
//            ArrayList<String> strings1 = executeADBCommand("-s", deviceId, "shell", "getprop", "ro.product.model");
//            device.setModel(strings1.get(0));
//
//            strings1 = executeADBCommand("-s", deviceId, "shell", "getprop", "ro.product.manufacturer");
//            device.setManufacturer(strings1.get(0));
//
//            strings1 = executeADBCommand("-s", deviceId, "shell", "getprop", "ro.build.version.release");
//            device.setRelease(strings1.get(0));
//
//            strings1 = executeADBCommand("-s", deviceId, "shell", "getprop", "ro.build.version.sdk");
//            device.setSDK(strings1.get(0));
//
//            devices.add(device);
//        }
//        return devices;
//    }

    private ArrayList<String> executeADBCommand(String... strings)
    {
        return ADBWrapper.getInstance().getProcessResult(ADBWrapper.getInstance().executeADBCommand(strings));
    }
}
