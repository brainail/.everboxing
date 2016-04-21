package itkach.aard2.ui.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;

import java.text.DateFormat;

import itkach.aard2.slob.BlobDescriptor;
import itkach.aard2.slob.BlobDescriptorList;
import itkach.slob.Slob;

public class BlobDescriptorListAdapter extends BaseAdapter {

    private final BlobDescriptorList mList;
    private final DateFormat mDateFormat;
    private final DataSetObserver mObserver;
    private boolean mSelectionMode;

    public BlobDescriptorListAdapter (final BlobDescriptorList list) {
        mDateFormat = DateFormat.getDateTimeInstance ();

        mObserver = new DataSetObserver () {
            @Override
            public void onChanged () {
                notifyDataSetChanged ();
            }

            @Override
            public void onInvalidated () {
                notifyDataSetInvalidated ();
            }
        };

        mList = list;
        mList.registerDataSetObserver (mObserver);
    }

    @Override
    public int getCount () {
        synchronized (mList) {
            return mList == null ? 0 : mList.size ();
        }
    }

    @Override
    public Object getItem (int position) {
        synchronized (mList) {
            return mList.get (position);
        }
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    public void setSelectionMode (boolean selectionMode) {
        this.mSelectionMode = selectionMode;
        notifyDataSetChanged ();
    }

    public boolean isSelectionMode () {
        return mSelectionMode;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        BlobDescriptor item = mList.get (position);
        CharSequence timestamp = DateUtils.getRelativeTimeSpanString (item.createdAt);

        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            LayoutInflater inflater = (LayoutInflater) parent.getContext ()
                    .getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate (R.layout.list_item_blob_descriptor, parent, false);
        }

        TextView titleView = (TextView) view.findViewById (R.id.blob_descriptor_key);
        titleView.setText (item.key);
        TextView sourceView = (TextView) view.findViewById (R.id.blob_descriptor_source);
        Slob slob = mList.resolveOwner (item);
        sourceView.setText (slob == null
                ? ToolResources.string (R.string.item_without_dictionary)
                : slob.getTags ().get ("label"));

        TextView timestampView = (TextView) view.findViewById (R.id.blob_descriptor_timestamp);
        timestampView.setText (timestamp);
        CheckBox cb = (CheckBox) view.findViewById (R.id.blob_descriptor_checkbox);
        cb.setVisibility (isSelectionMode () ? View.VISIBLE : View.GONE);

        return view;
    }

}
