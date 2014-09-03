package adbTool.interfaces;

/**
 * Created by Itzik on 03/09/2014.
 */
public interface IAndroidPackage
{
    boolean isActive();

    void setIsActive(boolean isActive);

    String getPid();

    void setPid(String pid);

    String getName();

    void setName(String name);
}
