package adbTool.ui;

import java.util.ArrayList;

import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataListener;

import adbTool.models.Device;

/**
 * Created by Itzik on 04/09/2014.
 */
public class DevicesComboboxModel implements MutableComboBoxModel<Device>
{
    private final ArrayList<Device> _devices = new ArrayList<>();
    private Device _selectedDevice;
    private ListDataListener _removeListDataListener;
    private ListDataListener _addListDataListener;

    @Override
    public void addElement(Device item)
    {
        _devices.add(item);
    }

    @Override
    public void removeElement(Object obj)
    {
        _devices.remove(obj);
    }

    @Override
    public void insertElementAt(Device item, int index)
    {
        _devices.add(index, item);
    }

    @Override
    public void removeElementAt(int index)
    {
        _devices.remove(index);

    }

    @Override
    public void setSelectedItem(Object anItem)
    {
        _selectedDevice = (Device)anItem;
    }

    @Override
    public Object getSelectedItem()
    {
        return _selectedDevice;
    }

    @Override
    public int getSize()
    {
        return _devices.size();
    }

    @Override
    public Device getElementAt(int index)
    {
        return _devices.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l)
    {
         _addListDataListener = l;
    }

    @Override
    public void removeListDataListener(ListDataListener l)
    {
        _removeListDataListener =l;
    }
}
