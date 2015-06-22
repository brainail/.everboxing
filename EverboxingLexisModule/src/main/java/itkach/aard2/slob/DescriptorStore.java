package itkach.aard2.slob;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DescriptorStore<T extends BaseDescriptor> {

    private File dir;
    private ObjectMapper mapper;

    public DescriptorStore(ObjectMapper mapper, File dir) {
        this.dir = dir;
        this.mapper = mapper;
    }

    public List<T> load(Class<T> type) {
        List<T> result = new ArrayList<T>();
        File[] files = dir.listFiles();
        if (files != null) {
            try {
                for (File f : files) {
                    T sd = mapper.readValue(f, type);
                    result.add(sd);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public void save(List<T> lst) {
        for (T item : lst) {
            save(item);
        }
    }

    public void save(T item) {
        if (item.id == null) {
            Log.d(getClass().getName(), "Can't save item without id");
            return;
        }
        try {
            mapper.writeValue(new File(dir, item.id), item);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(String itemId) {
        if (itemId == null) {
            return false;
        }
        return new File(dir, itemId).delete();
    }

}
