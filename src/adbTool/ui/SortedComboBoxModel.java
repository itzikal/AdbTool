package adbTool.ui;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

/**
 * Created by Itzik on 08/09/2014.
 */
public class SortedComboBoxModel<E> extends AbstractListModel<E> implements MutableComboBoxModel<E>, Serializable
{
    SortedSet<E> objects;
    E selectedObject;

    /**
     * Constructs an empty DefaultComboBoxModel object.
     */
    public SortedComboBoxModel()
    {
        objects = new TreeSet<E>();
    }

    /**
     * Constructs a DefaultComboBoxModel object initialized with
     * an array of objects.
     *
     * @param items an array of Object objects
     */
    public SortedComboBoxModel(final E items[])
    {
        objects = new TreeSet<E>();

        int i, c;
        for (i = 0, c = items.length; i < c; i++)
            objects.add(items[i]);

        if (getSize() > 0)
        {
            selectedObject = getElementAt(0);
        }
    }

    @Override
    public void setSelectedItem(Object anObject)
    {
        if ((selectedObject != null && !selectedObject.equals(anObject)) || selectedObject == null && anObject != null)
        {
            selectedObject = (E)anObject;
            fireContentsChanged(this, -1, -1);
        }
    }

    public Object getSelectedItem()
    {
        return selectedObject;
    }

    public int getSize()
    {
        return objects.size();
    }

    public E getElementAt(int index)
    {
        if (index >= 0 && index < objects.size())
        {
            return (E) objects.toArray()[index];
        }
        else return null;
    }

//    public int getIndexOf(E anObject)
//    {
//        return objects.indexOf(anObject);
//    }

    // implements javax.swing.MutableComboBoxModel
    public void addElement(E anObject)
    {
        objects.add(anObject);
        fireIntervalAdded(this, objects.size() - 1, objects.size() - 1);
        if (objects.size() == 1 && selectedObject == null && anObject != null)
        {
            setSelectedItem(anObject);
        }
    }

    @Override
    public void insertElementAt(E item, int index)
    {
        objects.add(item);
    }

    // implements javax.swing.MutableComboBoxModel
    public void removeElementAt(int index)
    {
        if (getElementAt(index) == selectedObject)
        {
            if (index == 0)
            {
                setSelectedItem(getSize() == 1 ? null : getElementAt(index + 1));
            }
            else
            {
                setSelectedItem(getElementAt(index - 1));
            }
        }

        objects.remove(getElementAt(index));

        fireIntervalRemoved(this, index, index);
    }

    @Override
    public void removeElement(Object anObject)
    {
        objects.remove(anObject);
    }

    /**
     * Empties the list.
     */
    public void removeAllElements()
    {
        if (objects.size() > 0)
        {
            int firstIndex = 0;
            int lastIndex = objects.size() - 1;
            objects.clear();
            selectedObject = null;
            fireIntervalRemoved(this, firstIndex, lastIndex);
        }
        else
        {
            selectedObject = null;
        }
    }

    public void clear()
    {
        removeAllElements();
    }

    public void fireContentsChanged(E source)
    {
        fireContentsChanged(source, 0, objects.size());
    }

    @Override
    protected void fireContentsChanged(Object source, int index0, int index1)
    {
        super.fireContentsChanged(source, index0, index1);
    }
}
