package adbTool.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

public class SortedListModel<T> extends AbstractListModel {
    SortedSet<T> model;

    public SortedListModel() {
        model = new TreeSet<T>();
    }

    public int getSize() {
        return model.size();
    }

    public T getElementAt(int index) {
        return (T)model.toArray()[index];
    }

    public void add(T element) {
        if (model.add(element)) {
            fireContentsChanged(this, 0, getSize());
        }
    }
    public void addAll(T elements[]) {
        Collection<T> c = Arrays.asList(elements);
        model.addAll(c);
        fireContentsChanged(this, 0, getSize());
    }

    public void addAll(ArrayList<T> array) {
        //Collection<Object> c = Arrays.asList(array);
        model.addAll(array);
        fireContentsChanged(this, 0, getSize());
    }

    public void clear() {
        model.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public boolean contains(T element) {
        return model.contains(element);
    }

    public T firstElement() {
        return model.first();
    }

    public Iterator iterator() {
        return model.iterator();
    }

    public T lastElement() {
        return model.last();
    }

    public boolean removeElement(T element) {
        boolean removed = model.remove(element);
        if (removed) {
            fireContentsChanged(this, 0, getSize());
        }
        return removed;
    }
}