package adbTool.exceptions;

/**
 * Created by Itzik on 31/08/2014.
 */
public class NoDeviceConnectedException extends Throwable
{
    public NoDeviceConnectedException()
    {
        super("No Device found.");
    }
}
