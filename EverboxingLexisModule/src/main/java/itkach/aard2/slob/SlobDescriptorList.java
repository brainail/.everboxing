package itkach.aard2.slob;

import java.util.Comparator;

import itkach.aard2.Application;
import itkach.aard2.utils.Util;
import itkach.slob.Slob;

public class SlobDescriptorList extends BaseDescriptorList<SlobDescriptor> {

    private Comparator<SlobDescriptor> comparator;

    public SlobDescriptorList (DescriptorStore<SlobDescriptor> store) {
        super (SlobDescriptor.class, store);

        comparator = new Comparator<SlobDescriptor> () {
            @Override
            public int compare (SlobDescriptor d1, SlobDescriptor d2) {
                //Dictionaries that are unfavorited
                //go immediately after favorites
                if (d1.priority == 0 && d2.priority == 0) {
                    return Util.compare (d2.lastAccess, d1.lastAccess);
                }
                //Favorites are always above other
                if (d1.priority == 0 && d2.priority > 0) {
                    return 1;
                }
                if (d1.priority > 0 && d2.priority == 0) {
                    return -1;
                }
                //Old favorites are above more recent ones
                return Util.compare (d1.priority, d2.priority);
            }
        };
    }

    public Slob resolve (SlobDescriptor sd) {
        return Application.app ().getSlob (sd.id);
    }

    public void sort () {
        Util.sort (this, comparator);
    }

    @Override
    public void load () {
        beginUpdate ();
        super.load ();
        sort ();
        endUpdate (true);
    }

}
