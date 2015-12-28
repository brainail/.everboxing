package itkach.aard2.slob;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.utils.Plogger;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import itkach.slob.Slob;


public class SlobDescriptor extends BaseDescriptor {

    private final static transient String TAG = SlobDescriptor.class.getSimpleName();

    public String path;
    public Map<String, String> tags = new HashMap<String, String>();
    public boolean isActive = true;
    public long priority;
    public long blobCount;
    public String error;
    public boolean expandDetail = false;

    public void update(Slob s) {
        this.id = s.getId().toString();
        this.path = s.file.getAbsolutePath();
        this.tags = s.getTags();
        this.blobCount = s.getBlobCount();
        this.error = null;
    }

    public Slob load() {
        Slob slob = null;
        File f = new File(path);
        try {
            slob = new Slob(f);
            this.update(slob);
        } catch (Exception e) {
            Plogger.logE(e, "Error while opening " + this.path);
            error = e.getMessage();
            expandDetail = true;
            isActive = false;
        }
        return slob;
    }

    public String getLabel() {
        String label = tags.get("label");
        if (label == null || label.trim().length() == 0) {
            label = ToolResources.string (R.string.wtf_emo);
        }
        return label;
    }

    public static SlobDescriptor fromFile(File file) {
        SlobDescriptor s = new SlobDescriptor();
        s.path = file.getAbsolutePath();
        s.load();
        return s;
    }

}
