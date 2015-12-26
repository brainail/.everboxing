package itkach.aard2.ui.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import itkach.slob.Slob;

public class BlobListAdapter extends BaseAdapter {

    private static final String TAG = BlobListAdapter.class.getSimpleName();

    public Handler mainHandler;
    public List<Slob.Blob> list;
    public Iterator<Slob.Blob> iter;
    public Iterator<Slob.Blob> emptyIter = new ArrayList<Slob.Blob>().iterator();
    public ExecutorService executor;

    private final int chunkSize;
    private final int loadMoreThreashold;
    int MAX_SIZE = 10000;


    public BlobListAdapter(Context context) {
        this(context, 20, 5);
    }

    public BlobListAdapter(Context context, int chunkSize, int loadMoreThreashold) {
        this.mainHandler = new Handler(context.getMainLooper());
        this.executor = Executors.newSingleThreadExecutor();
        this.list = new ArrayList<Slob.Blob>(chunkSize);
        this.chunkSize = chunkSize;
        this.loadMoreThreashold = loadMoreThreashold;
    }

    public void setData(Iterator<Slob.Blob> lookupResultsIter) {
        synchronized (list) {
            list.clear();
        }
        this.iter = lookupResultsIter;
        loadChunkSync();
    }

    public void setData(List<Slob.Blob> data) {
        synchronized (list) {
            list.clear();
            list.addAll(data);
            this.iter = emptyIter;
        }
    }

    private void loadChunkSync() {
        long t0 = System.currentTimeMillis();
        int count = 0;
        synchronized (list) {
            while (iter.hasNext() && count < chunkSize
                    && list.size() <= MAX_SIZE) {
                count++;
                Slob.Blob b = iter.next();
                list.add(b);
            }
        }
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            notifyDataSetChanged();
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
        Log.d(TAG,
                String.format("Loaded chunk of %d (adapter size %d) in %d ms",
                        count, list.size(), (System.currentTimeMillis() - t0)));
    }

    private void loadChunk() {
        if (!iter.hasNext()) {
            return;
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                loadChunkSync();
            }
        });
    }

    @Override
    public int getCount() {
        synchronized (list) {
            return list == null ? 0 : list.size();
        }
    }

    @Override
    public Object getItem(int position) {
        Object result;
        synchronized (list) {
            result = list.get(position);
        }
        maybeLoadMore(position);
        return result;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void maybeLoadMore(int position) {
        synchronized (list) {
            if (position >= list.size() - loadMoreThreashold) {
                loadChunk();
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Slob.Blob item = list.get(position);
        Slob slob = item.owner;
        maybeLoadMore(position);

        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_blob_descriptor, parent, false);
        }
        
        TextView titleView = (TextView) view.findViewById(R.id.blob_descriptor_key);
        titleView.setText(item.key);
        TextView sourceView = (TextView) view.findViewById(R.id.blob_descriptor_source);
        sourceView.setText(slob == null ? ToolResources.string (R.string.wtf_emo) : slob.getTags().get("label"));
        TextView timestampView = (TextView) view.findViewById(R.id.blob_descriptor_timestamp);
        timestampView.setText("");
        timestampView.setVisibility(View.GONE);
        return view;

    }

}
