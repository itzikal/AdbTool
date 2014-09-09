package adbTool.core;

import com.android.ddmlib.IShellOutputReceiver;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Base implementation of {@link com.android.ddmlib.IShellOutputReceiver}, that takes the raw data coming from the
 * socket, and convert it into {@link String} objects.
 * <p/>Additionally, it splits the string by lines.
 * <p/>Classes extending it must implement {@link #processNewLines(String[])} which receives
 * new parsed lines as they become available.
 */
public class ShellOutputReceiver implements IShellOutputReceiver
{
    public void setFilter(String filter)
    {
        _filter = filter;
    }

    public interface ShellOutputReceiverResults
    {
        void onResultReceived(String[] results);
    }

    public ShellOutputReceiver(String filter, ShellOutputReceiverResults shellOutputReceiverResults)
    {
        this._shellOutputReceiverResults = shellOutputReceiverResults;
        _filter = filter;
    }

    ShellOutputReceiverResults _shellOutputReceiverResults;
    private String _filter;
    private boolean isCancelled = false;

    private boolean mTrimLines = true;

    /**
     * unfinished message line, stored for next packet
     */
    private String mUnfinishedLine = null;

    private final ArrayList<String> mArray = new ArrayList<String>();

    /**
     * Set the trim lines flag.
     *
     * @param trim hether the lines are trimmed, or not.
     */
    public void setTrimLine(boolean trim)
    {
        mTrimLines = trim;
    }

    /* (non-Javadoc)
     * @see com.android.ddmlib.adb.IShellOutputReceiver#addOutput(
     *      byte[], int, int)
     */
    public final void addOutput(byte[] data, int offset, int length)
    {
        if (isCancelled() == false)
        {
            String s = null;
            try
            {
                s = new String(data, offset, length, "ISO-8859-1"); //$NON-NLS-1$
            }
            catch (UnsupportedEncodingException e)
            {
                // normal encoding didn't work, try the default one
                s = new String(data, offset, length);
            }

            // ok we've got a string
            if (s != null)
            {
                // if we had an unfinished line we add it.
                if (mUnfinishedLine != null)
                {
                    s = mUnfinishedLine + s;
                    mUnfinishedLine = null;
                }

                // now we split the lines
                mArray.clear();
                int start = 0;
                do
                {
                    int index = s.indexOf("\r\n", start); //$NON-NLS-1$

                    // if \r\n was not found, this is an unfinished line
                    // and we store it to be processed for the next packet
                    if (index == -1)
                    {
                        mUnfinishedLine = s.substring(start);
                        break;
                    }

                    // so we found a \r\n;
                    // extract the line
                    String line = s.substring(start, index);
                    if (mTrimLines)
                    {
                        line = line.trim();
                    }
                    if(_filter == null || line.contains(_filter))
                    {
                        mArray.add(line);
                    }

                    // move start to after the \r\n we found
                    start = index + 2;
                } while (true);

                if (mArray.size() > 0)
                {
                    // at this point we've split all the lines.
                    // make the array
                    String[] lines = mArray.toArray(new String[mArray.size()]);

                    // send it for final processing
                    processNewLines(lines);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.android.ddmlib.adb.IShellOutputReceiver#flush()
     */
    public final void flush()
    {
        if (mUnfinishedLine != null)
        {
            processNewLines(new String[]{mUnfinishedLine});
        }

        done();
    }

    /**
     * Terminates the process. This is called after the last lines have been through
     * {@link #processNewLines(String[])}.
     */
    public void done()
    {
        // do nothing.
    }

    /**
     * Called when new lines are being received by the remote process.
     * <p/>It is guaranteed that the lines are complete when they are given to this method.
     *
     * @param lines The array containing the new lines.
     */
    public void processNewLines(String[] lines)
    {
        if( _shellOutputReceiverResults != null)
        {
            _shellOutputReceiverResults.onResultReceived(lines);
        }
    }

    @Override
    public boolean isCancelled()
    {
        return isCancelled;
    }

}
