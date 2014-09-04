package adbTool.models;

import com.android.ddmlib.IDevice;

import java.io.IOException;

import adbTool.core.AdbWrapper;
import adbTool.core.Util;

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

    public String getIDeviceId()
    {
        return _device.getSerialNumber();
    }

    public String getManufacturer()
    {
        return _manufacturer;
    }

    public void setManufacturer(String _manufacturer)
    {
        this._manufacturer = _manufacturer;
    }

    public String getModel()
    {
        return _model;
    }

    public void setModel(String _model)
    {
        this._model = _model;
    }

    public void setRelease(String release)
    {
        this._release = release;
    }

    public String getRelease()
    {
        return _release;
    }

    public void setSDK(String sdk)
    {
        this._sdk = sdk;
    }

    public String getSDK()
    {
        return _sdk;
    }

    @Override
    public String toString()
    {
        return _device.getSerialNumber();
                //String.format("%s %s %s (API %s)", _manufacturer.toUpperCase(), _model, _release, _sdk);
    }

    public String getSerialNumber()
    {
        return _device.getSerialNumber();
    }

    public boolean executeShellCommand(String shellCmd, AdbWrapper.ShellOutputReceiver receiver, int i)
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
