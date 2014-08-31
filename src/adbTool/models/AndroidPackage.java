package adbTool.models;

/**
 * Created by Itzik on 31/08/2014.
 */
public class AndroidPackage
{
    private boolean _isActive;
    private String _pid;
    private String _name;

    public boolean isActive()
    {
        return _isActive;
    }

    public void setIsActive(boolean isActive)
    {
        _isActive = isActive;
    }

    public String getPid()
    {
        return _pid;
    }

    public void setPid(String pid)
    {
        _pid = pid;
    }

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
        _name = name;
    }
}
