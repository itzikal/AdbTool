package adbTool.models;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;

import java.io.IOException;

/**
 * Created by Itzik on 31/08/2014.
 */
public class Device
{
    private IDevice _device;
    private String _manufacturer;
    private String _model;
    private String _release;
    private String _sdk;

    public Device(IDevice device)//String deviceId)
    {
        _device = device;
    }

    public IDevice getDevice()
    {
        return _device;
    }

    public String getIDeviceId()
    {
        return _device.getSerialNumber();
    }

    public String getManufacturer()
    {
        return _device.getProperty("ro.product.manufacturer");
    }

//    public void setManufacturer(String _manufacturer)
//    {
//        this._manufacturer = _manufacturer;
//    }

    public String getModel()
    {
        return _device.getProperty("ro.product.model");
    }

//    public void setModel(String _model)
//    {
//        this._model = _model;
//    }

//    public void setRelease(String release)
//    {
//        this._release = release;
//    }

    public String getRelease()
    {
        return _device.getProperty("ro.build.version.release");
    }

//    public void setSDK(String sdk)
//    {
//        this._sdk = sdk;
//    }

    public String getSDK()
    {
        return _device.getProperty("ro.build.version.sdk");
    }

    @Override
    public String toString()
    {
        try
        {
            return String.format("%s %s %s (API %s)", getManufacturer().toUpperCase(), getModel(), getRelease(), getSDK());
        }
        catch (Exception e)
        {
            return getIDeviceId();
        }
    }

    public String getSerialNumber()
    {
        return _device.getSerialNumber();
    }

    public void installPackage(String file, boolean x)
    {
        try
        {
            _device.installPackage(file, x);
        }
        catch (IOException e)
        {

            e.printStackTrace();
        }
    }
    public boolean executeShellCommand(String shellCmd, IShellOutputReceiver receiver, int i)
    {
        try
        {
            _device.executeShellCommand(shellCmd, receiver, i);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
