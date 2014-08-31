package adbTool.models;

/**
 * Created by Itzik on 31/08/2014.
 */
public class Device
{
    private String _deviceId;
    private String _manufacturer;
    private String _model;
    private String _release;
    private String _sdk;

    public Device(String deviceId)
    {
        _deviceId = deviceId;
    }

    public String getDeviceId()
    {
        return _deviceId;
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
}
