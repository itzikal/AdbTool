package adbTool.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

public class SortedListModel<T> extends AbstractListModel {
    SortedSet<T> _model;

    public SortedListModel() {
        _model = new TreeSet<T>();
    }

    public int getSize() {
        return _model.size();
    }

    public T getElementAt(int index) {
        return (T) _model.toArray()[index];
    }

    public void add(T element) {
        if (_model.add(element)) {
            fireContentsChanged(this, 0, getSize());
        }
    }
    public void addAll(T elements[]) {
        Collection<T> c = Arrays.asList(elements);
        _model.addAll(c);
        fireContentsChanged(this, 0, getSize());
    }

    public void addAll(ArrayList<T> array) {
        //Collection<Object> c = Arrays.asList(array);
        _model.addAll(array);
        fireContentsChanged(this, 0, getSize());
    }

    public void clear() {
        _model.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public boolean contains(T element) {
        return _model.contains(element);
    }

    public T firstElement() {
        return _model.first();
    }

    public Iterator iterator() {
        return _model.iterator();
    }

    public T lastElement() {
        return _model.last();
    }

    public boolean removeElement(T element) {
        boolean removed = _model.remove(element);
        if (removed) {
            fireContentsChanged(this, 0, getSize());
        }
        return removed;
    }
}