package itkach.aard2.slob;

import android.database.DataSetObservable;
import android.database.DataSetObserver;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseDescriptorList <T extends BaseDescriptor> extends AbstractList<T> {

    private final DataSetObservable mDataSetObservable;
    private final DescriptorStore<T> mDescriptorStore;
    private final List<T> mList;
    private final Class<T> mTypeParameterClass;

    private int mUpdating;

    public BaseDescriptorList (Class<T> typeParameterClass, DescriptorStore<T> store) {
        mTypeParameterClass = typeParameterClass;
        mDescriptorStore = store;
        mDataSetObservable = new DataSetObservable ();
        mList = new ArrayList<T> ();
        mUpdating = 0;
    }

    public void registerDataSetObserver (DataSetObserver observer) {
        mDataSetObservable.registerObserver (observer);
    }

    public void unregisterDataSetObserver (DataSetObserver observer) {
        mDataSetObservable.unregisterObserver (observer);
    }

    public void beginUpdate () {
        mUpdating ++;
    }

    public void endUpdate (boolean changed) {
        mUpdating --;
        if (changed) {
            notifyChanged ();
        }
    }

    protected void notifyChanged () {
        if (mUpdating == 0) {
            mDataSetObservable.notifyChanged ();
        }
    }

    public void load () {
        addAll (mDescriptorStore.load (mTypeParameterClass));
    }

    @Override
    public T get (int i) {
        return mList.get (i);
    }

    @Override
    public T set (int location, T object) {
        T result = mList.set (location, object);
        mDescriptorStore.save (object);
        notifyChanged ();
        return result;
    }

    @Override
    public int size () {
        return mList.size ();
    }

    @Override
    public void add (int location, T object) {
        mList.add (location, object);
        mDescriptorStore.save (object);
        notifyChanged ();
    }

    @Override
    public boolean addAll (int location, Collection<? extends T> collection) {
        beginUpdate ();
        boolean result = super.addAll (location, collection);
        endUpdate (result);
        return result;
    }

    @Override
    public boolean addAll (Collection<? extends T> collection) {
        beginUpdate ();
        boolean result = super.addAll (collection);
        endUpdate (result);
        return result;
    }

    @Override
    public T remove (int location) {
        T result = mList.remove (location);
        mDescriptorStore.delete (result.id);
        notifyChanged ();
        return result;
    }

    @Override
    public void clear () {
        boolean wasEmpty = size () == 0;
        beginUpdate ();
        super.clear ();
        endUpdate (!wasEmpty);
    }

}
