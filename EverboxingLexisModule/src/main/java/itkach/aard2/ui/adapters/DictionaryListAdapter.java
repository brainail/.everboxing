package itkach.aard2.ui.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.net.Uri;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.brainail.EverboxingLexis.R;

import java.util.Locale;

import itkach.aard2.slob.SlobDescriptor;
import itkach.aard2.slob.SlobDescriptorList;

import static java.lang.String.format;

public class DictionaryListAdapter extends BaseAdapter {

    private final SlobDescriptorList mData;
    private final Activity mContext;
    private View.OnClickListener mOnUrlTapListener;
    private AlertDialog deleteConfirmationDialog;

    private final static String hrefTemplate = "<a href=\'%1$s\'>%2$s</a>";

    public DictionaryListAdapter (SlobDescriptorList data, Activity context) {
        mData = data;
        mContext = context;

        final DataSetObserver observer = new DataSetObserver () {
            @Override
            public void onChanged () {
                notifyDataSetChanged ();
            }

            @Override
            public void onInvalidated () {
                notifyDataSetInvalidated ();
            }
        };
        mData.registerDataSetObserver (observer);

        mOnUrlTapListener = new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                String url = (String) v.getTag ();
                if (! isBlank (url)) {
                    try {
                        Uri uri = Uri.parse (url);
                        Intent browserIntent = new Intent (Intent.ACTION_VIEW, uri);
                        v.getContext ().startActivity (browserIntent);
                    } catch (Exception e) {
                        // ...
                    }
                }
            }
        };
    }

    private static boolean isBlank (String value) {
        return value == null || value.trim ().equals ("");
    }

    private void onDictionarySwitchChanged (final View view) {
        final SwitchCompat switcher = (SwitchCompat) view;
        final Integer position = (Integer) view.getTag ();

        if (null != position) {
            final SlobDescriptor slobDescription = mData.get (position);
            slobDescription.isActive = switcher.isChecked ();
            mData.set (position, slobDescription);
        }
    }

    @Override
    public View getView (int position, final View convertView, ViewGroup parent) {
        SlobDescriptor desc = (SlobDescriptor) getItem (position);
        String label = desc.getLabel ();
        String path = desc.path;

        long blobCount = desc.blobCount;
        boolean available = this.mData.resolve (desc) != null;
        View view;

        if (convertView != null) {
            view = convertView;
        } else {
            LayoutInflater inflater = (LayoutInflater) parent.getContext ().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate (R.layout.list_item_dictionary, parent, false);

            View licenseView = view.findViewById (R.id.dictionary_license);
            licenseView.setOnClickListener (mOnUrlTapListener);

            View sourceView = view.findViewById (R.id.dictionary_source);
            sourceView.setOnClickListener (mOnUrlTapListener);

            SwitchCompat activeSwitch = (SwitchCompat) view.findViewById (R.id.dictionary_active);
            activeSwitch.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View view) {
                    onDictionarySwitchChanged (view);
                }
            });

            activeSwitch.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener () {
                @Override
                public void onCheckedChanged (CompoundButton view, boolean isChecked) {
                    onDictionarySwitchChanged (view);
                }
            });

            View btnForget = view.findViewById (R.id.dictionary_btn_forget);
            btnForget.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View view) {
                    Integer position = (Integer) view.getTag ();
                    forget (position);
                }
            });

            View btnToggleDetail = view.findViewById (R.id.dictionary_btn_toggle_detail);
            btnToggleDetail.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View view) {
                    Integer position = (Integer) view.getTag ();
                    SlobDescriptor desc = mData.get (position);
                    desc.expandDetail = !desc.expandDetail;
                    mData.set (position, desc);
                }
            });
        }

        Resources r = parent.getResources ();

        SwitchCompat switchView = (SwitchCompat) view.findViewById (R.id.dictionary_active);
        switchView.setChecked (desc.isActive);
        switchView.setTag (position);

        TextView titleView = (TextView) view.findViewById (R.id.dictionary_label);
        titleView.setEnabled (available);
        titleView.setText (label);
        titleView.setTag (position);

        View detailView = view.findViewById (R.id.dictionary_details);
        detailView.setVisibility (desc.expandDetail ? View.VISIBLE : View.GONE);

        setupBlobCountView (desc, blobCount, available, view, r);
        setupCopyrightView (desc, available, view);
        setupLicenseView (desc, available, view);
        setupSourceView (desc, available, view);
        setupPathView (path, available, view);
        setupErrorView (desc, view);

        ImageView btnToggleDetail = (ImageView) view.findViewById (R.id.dictionary_btn_toggle_detail);
        btnToggleDetail.setTag (position);

        ImageView btnForget = (ImageView) view.findViewById (R.id.dictionary_btn_forget);
        btnForget.setTag (position);

        return view;
    }

    private void setupPathView (String path, boolean available, View view) {
        View pathRow = view.findViewById (R.id.dictionary_path_row);

        TextView pathView = (TextView) view.findViewById (R.id.dictionary_path);
        pathView.setText (path);

        pathRow.setEnabled (available);
    }

    private void setupErrorView (SlobDescriptor desc, View view) {
        View errorRow = view.findViewById (R.id.dictionary_error_row);

        TextView errorView = (TextView) view.findViewById (R.id.dictionary_error);
        errorView.setText (desc.error);

        errorRow.setVisibility (desc.error == null ? View.GONE : View.VISIBLE);
    }

    private void setupBlobCountView (SlobDescriptor desc, long blobCount, boolean available, View view, Resources r) {
        TextView blobCountView = (TextView) view.findViewById (R.id.dictionary_blob_count);

        blobCountView.setEnabled (available);
        blobCountView.setVisibility (desc.error == null ? View.VISIBLE : View.GONE);

        blobCountView.setText (format (Locale.US,
                r.getQuantityString (R.plurals.dict_item_count, (int) blobCount), blobCount));
    }

    private void setupCopyrightView (SlobDescriptor desc, boolean available, View view) {
        View copyrightRow = view.findViewById (R.id.dictionary_copyright_row);

        TextView copyrightView = (TextView) view.findViewById (R.id.dictionary_copyright);
        String copyright = desc.tags.get ("copyright");
        copyrightView.setText (copyright);

        copyrightRow.setVisibility (isBlank (copyright) ? View.GONE : View.VISIBLE);
        copyrightRow.setEnabled (available);
    }

    private void setupSourceView (SlobDescriptor desc, boolean available, View view) {
        View sourceRow = view.findViewById (R.id.dictionary_license_row);
        TextView sourceView = (TextView) view.findViewById (R.id.dictionary_source);
        View sourceIcon = view.findViewById (R.id.dictionary_source_icon);

        String source = desc.tags.get ("source");
        CharSequence sourceHtml = Html.fromHtml (String.format (hrefTemplate, source, source));
        sourceView.setText (sourceHtml);
        sourceView.setTag (source);

        int visibility = isBlank (source) ? View.GONE : View.VISIBLE;

        // Setting visibility on layout seems to have no effect
        // if one of the children is a link
        sourceIcon.setVisibility (visibility);
        sourceView.setVisibility (visibility);
        sourceRow.setVisibility (visibility);
        sourceRow.setEnabled (available);
    }

    private void setupLicenseView (SlobDescriptor desc, boolean available, View view) {
        View licenseRow = view.findViewById (R.id.dictionary_license_row);
        TextView licenseView = (TextView) view.findViewById (R.id.dictionary_license);
        View licenseIcon = view.findViewById (R.id.dictionary_license_icon);

        String licenseName = desc.tags.get ("license.name");
        String licenseUrl = desc.tags.get ("license.url");

        CharSequence license;
        if (isBlank (licenseUrl)) {
            license = licenseName;
        } else {
            if (isBlank (licenseName)) {
                licenseName = licenseUrl;
            }
            license = Html.fromHtml (String.format (hrefTemplate, licenseUrl, licenseName));
        }

        licenseView.setText (license);
        licenseView.setTag (licenseUrl);

        int visibility = (isBlank (licenseName) && isBlank (licenseUrl)) ? View.GONE : View.VISIBLE;
        licenseIcon.setVisibility (visibility);
        licenseView.setVisibility (visibility);
        licenseRow.setVisibility (visibility);
        licenseRow.setEnabled (available);
    }

    private void forget (final int position) {
        SlobDescriptor desc = mData.get (position);
        final String label = desc.getLabel ();
        String message = mContext.getString (R.string.dictionaries_confirm_forget, label);
        deleteConfirmationDialog = new AlertDialog.Builder (mContext)
                .setIcon (android.R.drawable.ic_dialog_alert)
                .setTitle ("")
                .setMessage (message)
                .setPositiveButton (android.R.string.yes, new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick (DialogInterface dialog, int which) {
                        mData.remove (position);
                    }
                })
                .setNegativeButton (android.R.string.no, null)
                .create ();
        deleteConfirmationDialog.setOnDismissListener (new DialogInterface.OnDismissListener () {
            @Override
            public void onDismiss (DialogInterface dialogInterface) {
                deleteConfirmationDialog = null;
            }
        });
        deleteConfirmationDialog.show ();
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public Object getItem (int position) {
        return mData.get (position);
    }

    @Override
    public int getCount () {
        return mData.size ();
    }

}
