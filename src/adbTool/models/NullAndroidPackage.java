package adbTool.models;

/**
 * Created by Itzik on 03/09/2014.
 */
public class NullAndroidPackage extends AndroidPackage
{
    @Override
    public boolean isActive()
    {
        return false;
    }

    @Override
    public String getPid()
    {
        return "";
    }


    @Override
    public String getName()
    {
        return "Null";
    }
}
