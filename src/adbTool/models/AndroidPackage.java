package adbTool.models;

import adbTool.interfaces.IAndroidPackage;

/**
 * Created by Itzik on 31/08/2014.
 */
public class AndroidPackage implements IAndroidPackage
{
    private boolean _isActive;
    private String _pid;
    private String _name;

    @Override
    public boolean isActive()
    {
        return _isActive;
    }

    @Override
    public void setIsActive(boolean isActive)
    {
        _isActive = isActive;
    }

    @Override
    public String getPid()
    {
        return _pid;
    }

    @Override
    public void setPid(String pid)
    {
        _pid = pid;
    }

    @Override
    public String getName()
    {
        return _name;
    }

    @Override
    public void setName(String name)
    {
        _name = name;
    }
}
