package org.brainail.EverboxingHardyDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import static org.brainail.EverboxingHardyDialogs.BaseDialogSpecification.EXTRA_DIALOG_INSTANCE;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p/>
 * The MIT License (MIT) <br/><br/>
 * <p/>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p/>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public class HardyDialogFragment extends AppCompatDialogFragment {
    
    // Value to show that this dialog doesn't use a layout for content
    public static final int NO_RESOURCE_ID = -1;
    // Prefix of tag for FragmentManager
    public static final String MANAGER_TAG_PREFIX = "org.brainail.EverboxingHardyDialogs.tag#";
    
    // Predefined action request codes
    public static abstract class ActionRequestCode {
        // Click on a positive button
        public static final int POSITIVE = DialogInterface.BUTTON_POSITIVE;
        // Click on a negative button
        public static final int NEGATIVE = DialogInterface.BUTTON_NEGATIVE;
        // Click on a neutral button
        public static final int NEUTRAL = DialogInterface.BUTTON_NEUTRAL;

        private static final int CUSTOM_CODE_BASE = -999;
        // Cancel a dialog
        public static final int CANCEL = CUSTOM_CODE_BASE - 1;
        // Dismiss a dialog
        public static final int DISMISS = CUSTOM_CODE_BASE - 2;
        
        // Show that we don't want to handle some particular action
        // For instance this is the default value for "dismiss" action because of the frequency
        public static final int UNHANDLED = Integer.MIN_VALUE;
    }
    
    // Arguments that come from dialog specifications
    static abstract class Args {
        public static final String TITLE = "title";
        public static final String BODY = "body";
        public static final String BODY_ID = "body_id";
        public static final String CONTENT_LAYOUT_ID = "body_layout_id";
        public static final String CONTENT_LAYOUT_PARAMS = "body_layout_params";
        public static final String POSITIVE_BUTTON = "positive_button";
        public static final String POSITIVE_BUTTON_ID = "positive_button_id";
        public static final String POSITIVE_ACTION_REQUEST_CODE = "positive_action_request_code";
        public static final String NEGATIVE_BUTTON = "negative_button";
        public static final String NEGATIVE_BUTTON_ID = "negative_button_id";
        public static final String NEGATIVE_ACTION_REQUEST_CODE = "negative_action_request_code";
        public static final String NEUTRAL_BUTTON = "neutral_button";
        public static final String NEUTRAL_BUTTON_ID = "neutral_button_id";
        public static final String NEUTRAL_ACTION_REQUEST_CODE = "neutral_action_request_code";
        public static final String CANCEL_ACTION_REQUEST_CODE = "cancel_action_request_code";
        public static final String DISMISS_ACTION_REQUEST_CODE = "dismiss_action_request_code";
        public static final String DIALOG_CODE = "dialog_code";
        public static final String ISOLATED_HANDLER = "isolated_handler";
        public static final String HAS_CALLBACKS = "has_callbacks";
        public static final String IS_CANCELABLE = "is_cancelable";
        public static final String IS_TRANSLUCENT = "is_translucent";
        public static final String ATTACHED_PARCELABLE_DATA = "attached_parcelable_data";
        public static final String ATTACHED_SERIALIZABLE_DATA = "attached_serializable_data";
        public static final String INTENT_ATTACHED_PARCELABLE_DATA = "intent_attached_parcelable_data";
        public static final String HAS_TARGET_FRAGMENT = "has_target_fragment";
        public static final String HAS_PROGRESS = "has_progress";
        public static final String IS_INDETERMINATE_PROGRESS = "is_indeterminate_progress";
        public static final String IS_RESTORABLE = "is_restorable";
        public static final String HAS_DESTROYABLE_UNDERLAY = "has_destroyable_underlay";
        public static final String HAS_LIST = "has_list";
        public static final String LIST_ITEMS = "list_items";
        public static final String LIST_ITEMS_TAGS = "list_items_tags";
    }

    /**
     * @see android.view.ViewGroup.LayoutParams
     */
    public static class LayoutParams implements Serializable {

        private int width, height;

        /**
         * @see android.view.ViewGroup.LayoutParams#LayoutParams(int, int)
         */
        public LayoutParams (final int width, final int height) {
            this.width = width;
            this.height = height;
        }

        int heightInPixels () {
            return sizeInPixels (height);
        }

        int widthInPixels () {
            return sizeInPixels (width);
        }

        @SuppressWarnings ("all")
        private int sizeInPixels (final int size) {
            if (ViewGroup.LayoutParams.FILL_PARENT != size
                    && ViewGroup.LayoutParams.MATCH_PARENT != size
                    && ViewGroup.LayoutParams.WRAP_CONTENT != size) {
                final DisplayMetrics displayMetrics = HardyDialogsContext.get ().getResources ().getDisplayMetrics ();
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) size, displayMetrics);
            }

            return size;
        }

    }
    
    private String mTitle;
    private String mBody;
    private int mBodyId;
    private LayoutParams mContentLayoutParams;
    private int mContentLayoutId;
    private String mPositiveButton;
    private int mPositiveButtonId;
    private int mPositiveActionRequestCode;
    private String mNegativeButton;
    private int mNegativeButtonId;
    private int mNegativeActionRequestCode;
    private String mNeutralButton;
    private int mNeutralButtonId;
    private int mNeutralActionRequestCode;
    private int mCancelActionRequestCode;
    private int mDismissActionRequestCode;
    private BaseHardyDialogsCode mDialogCode;
    private IsolatedDialogHandler mIsolatedHandler;
    private boolean mHasCallbacks;
    private boolean mIsCancelable;
    private boolean mIsTranslucent;
    private Object mAttachedData;
    private boolean mHasTargetFragment;
    private boolean mIsRestorable;
    private boolean mHasDestroyableUnderlay;
    
    private boolean mHasProgress;
    private boolean mIsIndeterminateProgress;
    
    private boolean mHasList;
    private CharSequence [] mListItems;
    private String [] mListItemsTags;
    
    // Callback for actions
    public static interface OnDialogActionCallback {
        public void onDialogAction (HardyDialogFragment dialog, int actionRequestCode);
    }
    
    // Callback for list actions
    public static interface OnDialogListActionCallback {
        public void onDialogListAction (HardyDialogFragment dialog, int whichItem, String item, String itemTag);
    }
    
    // Callback for preparation of dialog
    public static interface OnDialogPrepareCallback {
        public void onPrepareDialogView (HardyDialogFragment dialog, View view, int layoutId);
    }
    
    // Callback is invoked when the dialog is shown
    public static interface OnDialogShowCallback {
        public void onDialogShow (HardyDialogFragment dialog);
    }
    
    /**
     * @see BaseDialogSpecification.Builder#setCallbacks(IsolatedDialogHandler)
     */
    public static class IsolatedDialogHandler implements
            OnDialogActionCallback, OnDialogPrepareCallback,
            OnDialogShowCallback, OnDialogListActionCallback,
            Serializable {
        @Override
        public void onDialogAction (HardyDialogFragment dialog, int actionRequestCode) {}
        
        @Override
        public void onDialogListAction (HardyDialogFragment dialog, int whichItem, String item, String itemTag) {}
        
        @Override
        public void onPrepareDialogView (HardyDialogFragment dialog, View view, int layoutId) {}
        
        @Override
        public void onDialogShow (HardyDialogFragment dialog) {}
    }
    
    // Creates our dialog with arguments
    public static HardyDialogFragment newInstance (final Bundle args) {
        final HardyDialogFragment dialog = new HardyDialogFragment ();
        dialog.setArguments (args);
        return dialog;
    }
    
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        
        final Bundle args = getArguments ();
        if (null == args || args.isEmpty ()) {
            throw new IllegalStateException ("Some arguments must be supplied to build an alert dialog");
        }
        
        // Common
        mTitle = args.getString (Args.TITLE);
        mBody = args.getString (Args.BODY);
        mBodyId = args.getInt (Args.BODY_ID);
        mContentLayoutId = args.getInt (Args.CONTENT_LAYOUT_ID);
        mContentLayoutParams = (LayoutParams) args.getSerializable (Args.CONTENT_LAYOUT_PARAMS);
        mPositiveButton = args.getString (Args.POSITIVE_BUTTON);
        mPositiveButtonId = args.getInt (Args.POSITIVE_BUTTON_ID);
        mPositiveActionRequestCode = args.getInt (Args.POSITIVE_ACTION_REQUEST_CODE);
        mNegativeButton = args.getString (Args.NEGATIVE_BUTTON);
        mNegativeButtonId = args.getInt (Args.NEGATIVE_BUTTON_ID);
        mNegativeActionRequestCode = args.getInt (Args.NEGATIVE_ACTION_REQUEST_CODE);
        mNeutralButton = args.getString (Args.NEUTRAL_BUTTON);
        mNeutralButtonId = args.getInt (Args.NEUTRAL_BUTTON_ID);
        mNeutralActionRequestCode = args.getInt (Args.NEUTRAL_ACTION_REQUEST_CODE);
        mCancelActionRequestCode = args.getInt (Args.CANCEL_ACTION_REQUEST_CODE);
        mDismissActionRequestCode = args.getInt (Args.DISMISS_ACTION_REQUEST_CODE);
        mDialogCode = (BaseHardyDialogsCode) args.getSerializable (Args.DIALOG_CODE);
        mIsolatedHandler = (IsolatedDialogHandler) args.getSerializable (Args.ISOLATED_HANDLER);
        mHasCallbacks = args.getBoolean (Args.HAS_CALLBACKS);
        mIsCancelable = args.getBoolean (Args.IS_CANCELABLE);
        mIsTranslucent = args.getBoolean (Args.IS_TRANSLUCENT);
        mHasTargetFragment = args.getBoolean (Args.HAS_TARGET_FRAGMENT);
        mIsRestorable = args.getBoolean (Args.IS_RESTORABLE);
        mHasDestroyableUnderlay = args.getBoolean (Args.HAS_DESTROYABLE_UNDERLAY);
        
        // Progress' stuff
        mHasProgress = args.getBoolean (Args.HAS_PROGRESS);
        mIsIndeterminateProgress = args.getBoolean (Args.IS_INDETERMINATE_PROGRESS);
        
        // List's stuff
        mHasList = args.getBoolean (Args.HAS_LIST);
        final ArrayList<String> items = getArguments ().getStringArrayList (Args.LIST_ITEMS);
        mListItems = (null != items && ! items.isEmpty ())
                ? items.toArray (new CharSequence [items.size ()])
                : new CharSequence [] {};
        final ArrayList<String> itemsTags = getArguments ().getStringArrayList (Args.LIST_ITEMS_TAGS);
        mListItemsTags = (null != itemsTags && ! itemsTags.isEmpty ())
                ? itemsTags.toArray (new String [itemsTags.size ()])
                : new String [] {};
        
        if (! mIsRestorable) {
            // Remove to avoid leaks and crashes
            args.remove (Args.ISOLATED_HANDLER);
            args.remove (Args.ATTACHED_PARCELABLE_DATA);
            args.remove (Args.ATTACHED_SERIALIZABLE_DATA);
        }
        
        // Get attached data by type
        if (args.containsKey (Args.ATTACHED_PARCELABLE_DATA)) {
            mAttachedData = args.getParcelable (Args.ATTACHED_PARCELABLE_DATA);
        } else if (args.containsKey (Args.ATTACHED_SERIALIZABLE_DATA)) {
            mAttachedData = args.getSerializable (Args.ATTACHED_SERIALIZABLE_DATA);
        }
    }
    
    @Override
    @NonNull
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        // Don't let to restore this dialog
        if (null != savedInstanceState && ! mIsRestorable) {
            dismiss ();
        }
        
        final AlertDialog.Builder builder = ! mHasProgress ? new AlertDialog.Builder (getActivity ()) : null;
        final ProgressDialog progress = mHasProgress ? new ProgressDialog (getActivity ()) : null;
        
        if (! TextUtils.isEmpty (mTitle)) {
            if (null != progress) {
                progress.setTitle (mTitle);
            } else {
                builder.setTitle (mTitle);
            }
        }
        
        if (! TextUtils.isEmpty (mBody)) {
            if (null != progress) {
                progress.setMessage (mBody);
            } else if (NO_RESOURCE_ID == mBodyId) {
                builder.setMessage (mBody);
            }
        }

        // Content view binding
        View contentView = null;
        if (null != builder && NO_RESOURCE_ID != mContentLayoutId) {
            contentView = getActivity ().getLayoutInflater ().inflate (mContentLayoutId, null);
            builder.setView (contentView);
            handleDialogViewPreparation (contentView, mContentLayoutId);
        }
        
        if (null != builder && NO_RESOURCE_ID != mBodyId && null != contentView) {
            final View body = contentView.findViewById (mBodyId);
            if (body instanceof TextView) {
                ((TextView) body).setText (mBody);
            }
        }
        
        if (null != builder) {
            if (NO_RESOURCE_ID != mPositiveButtonId && null != contentView) {
                final View positiveButton = contentView.findViewById (mPositiveButtonId);
                if (positiveButton instanceof TextView) {
                    ((TextView) positiveButton).setText (mPositiveButton);
                    positiveButton.setOnClickListener (mOnPositiveButtonListenerClickWrapper);
                }
            } else if (! TextUtils.isEmpty (mPositiveButton)) {
                builder.setPositiveButton (mPositiveButton, mOnPositiveButtonListener);
            }
        }
        
        if (null != builder) {
            if (NO_RESOURCE_ID != mNeutralButtonId && null != contentView) {
                final View neutralButton = contentView.findViewById (mNeutralButtonId);
                if (neutralButton instanceof TextView) {
                    ((TextView) neutralButton).setText (mNeutralButton);
                    neutralButton.setOnClickListener (mOnNeutralButtonListenerClickWrapper);
                }
            } else if (! TextUtils.isEmpty (mNeutralButton)) {
                builder.setNeutralButton (mNeutralButton, mOnNeutralButtonListener);
            }
        }
        
        if (null != builder) {
            if (NO_RESOURCE_ID != mNegativeButtonId && null != contentView) {
                final View negativeButton = contentView.findViewById (mNegativeButtonId);
                if (negativeButton instanceof TextView) {
                    ((TextView) negativeButton).setText (mNegativeButton);
                    negativeButton.setOnClickListener (mOnNegativeButtonListenerClickWrapper);
                }
            } else if (! TextUtils.isEmpty (mNegativeButton)) {
                builder.setNegativeButton (mNegativeButton, mOnNegativeButtonListener);
            }
        }
        
        if (null != builder && mHasList) {
            builder.setItems (mListItems, new DialogInterface.OnClickListener () {
                @Override
                public void onClick (DialogInterface dialog, int which) {
                    handleDialogListAction (which);
                }
            });
        }
        
        if (null != progress) {
            progress.setIndeterminate (mIsIndeterminateProgress);
        }

        final Dialog dialog = null != builder ? builder.create () : progress;
        final Window window = dialog.getWindow ();
        
        if (TextUtils.isEmpty (mTitle)) {
            if (null != builder) {
                ((AlertDialog) dialog).supportRequestWindowFeature (Window.FEATURE_NO_TITLE);
            } else {
                dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
            }
        }
        
        if (! mIsCancelable) {
            dialog.setCancelable (false);
            dialog.setCanceledOnTouchOutside (false);
        }

        if (mIsTranslucent) {
            window.setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
            // dialog.getWindow ().clearFlags (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        if (null != builder && NO_RESOURCE_ID != mContentLayoutId && null != mContentLayoutParams) {
            window.setLayout (mContentLayoutParams.widthInPixels (), mContentLayoutParams.heightInPixels ());
        }

        setOnKeyListener (dialog);
        
        dialog.setOnShowListener (new DialogInterface.OnShowListener () {
            @Override
            public void onShow (DialogInterface dialog) {
                handleDialogShow ();
            }
        });
        
        return dialog;
    }
    
    private void setOnKeyListener (final Dialog dialog) {
        dialog.setOnKeyListener (new DialogInterface.OnKeyListener () {
            @Override
            public boolean onKey (DialogInterface dialog, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        if (mIsCancelable) {
                            dialog.cancel ();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
    
    private View.OnClickListener mOnPositiveButtonListenerClickWrapper = new View.OnClickListener () {
        @Override
        public void onClick (View v) {
            mOnPositiveButtonListener.onClick (getDialog (), DialogInterface.BUTTON_POSITIVE);
            dismiss ();
        }
    };
    
    private DialogInterface.OnClickListener mOnPositiveButtonListener = new DialogInterface.OnClickListener () {
        @Override
        public void onClick (DialogInterface dialog, int which) {
            handleDialogAction (mPositiveActionRequestCode, null);
        }
    };
    
    private View.OnClickListener mOnNeutralButtonListenerClickWrapper = new View.OnClickListener () {
        @Override
        public void onClick (View v) {
            mOnNeutralButtonListener.onClick (getDialog (), DialogInterface.BUTTON_NEUTRAL);
            dismiss ();
        }
    };
    
    private DialogInterface.OnClickListener mOnNeutralButtonListener = new DialogInterface.OnClickListener () {
        @Override
        public void onClick (DialogInterface dialog, int which) {
            handleDialogAction (mNeutralActionRequestCode, null);
        }
    };
    
    private View.OnClickListener mOnNegativeButtonListenerClickWrapper = new View.OnClickListener () {
        @Override
        public void onClick (View v) {
            mOnNegativeButtonListener.onClick (getDialog (), DialogInterface.BUTTON_NEGATIVE);
            dismiss ();
        }
    };
    
    private DialogInterface.OnClickListener mOnNegativeButtonListener = new DialogInterface.OnClickListener () {
        @Override
        public void onClick (DialogInterface dialog, int which) {
            handleDialogAction (mNegativeActionRequestCode, null);
        }
    };
    
    @Override
    public void onCancel (DialogInterface dialog) {
        handleDialogAction (mCancelActionRequestCode, null);
        super.onCancel (dialog);
    }
    
    @Override
    public void onDismiss (DialogInterface dialog) {
        handleDialogAction (mDismissActionRequestCode, null);
        super.onDismiss (dialog);
        finishIfDialogActivity ();
    }
    
    private void finishIfDialogActivity () {
        // Finish the transparent system dialog activity
        final Activity activity = getActivity ();
        if (activity instanceof RemoteHardyDialogsActivity) {
            activity.finish ();
        } else if (null != activity && mHasDestroyableUnderlay) {
            activity.finish ();
        }
    }
    
    private void handleDialogShow () {
        if (mHasCallbacks) {
            if (null != mIsolatedHandler) {
                mIsolatedHandler.onDialogShow (this);
            } else if (mHasTargetFragment && getParentFragment () instanceof OnDialogShowCallback) {
                ((OnDialogShowCallback) getParentFragment ()).onDialogShow (this);
            } else if (getActivity () instanceof OnDialogShowCallback) {
                ((OnDialogShowCallback) getActivity ()).onDialogShow (this);
            }
        }
    }
    
    private void handleDialogListAction (final int which) {
        if (mHasCallbacks) {
            final String item = which < mListItems.length ? mListItems [which].toString () : null;
            final String itemTag = which < mListItemsTags.length ? mListItemsTags [which] : null;
            if (null != mIsolatedHandler) {
                mIsolatedHandler.onDialogListAction (this, which, item, itemTag);
            } else if (mHasTargetFragment && getParentFragment () instanceof OnDialogListActionCallback) {
                ((OnDialogListActionCallback) getParentFragment ()).onDialogListAction (this, which, item, itemTag);
            } else if (getActivity () instanceof OnDialogListActionCallback) {
                ((OnDialogListActionCallback) getActivity ()).onDialogListAction (this, which, item, itemTag);
            }
        }
    }
    
    private void handleDialogViewPreparation (final View view, final int layoutId) {
        if (mHasCallbacks) {
            if (null != mIsolatedHandler) {
                mIsolatedHandler.onPrepareDialogView (this, view, layoutId);
            } else if (mHasTargetFragment && getParentFragment () instanceof OnDialogPrepareCallback) {
                ((OnDialogPrepareCallback) getParentFragment ()).onPrepareDialogView (this, view, layoutId);
            } else if (getActivity () instanceof OnDialogPrepareCallback) {
                ((OnDialogPrepareCallback) getActivity ()).onPrepareDialogView (this, view, layoutId);
            }
        }
    }
    
    private void handleDialogAction (final int actionRequestCode, final String analyticsAction) {
        if (mHasCallbacks && ActionRequestCode.UNHANDLED != actionRequestCode) {
            if (null != mIsolatedHandler) {
                mIsolatedHandler.onDialogAction (this, actionRequestCode);
            } else if (mHasTargetFragment && getParentFragment () instanceof OnDialogActionCallback) {
                ((OnDialogActionCallback) getParentFragment ()).onDialogAction (this, actionRequestCode);
            } else if (getActivity () instanceof OnDialogActionCallback) {
                ((OnDialogActionCallback) getActivity ()).onDialogAction (this, actionRequestCode);
            }
        }
    }
    
    @Override
    public void dismiss () {
        // NullPointerException at #dismissInternal(...)
        if (null != getFragmentManager ()) {
            try {
                super.dismiss ();
            } catch (final Exception exception) {
                // ...
            }
        }
    }
    
    @Override
    public void dismissAllowingStateLoss () {
        // NullPointerException at #dismissInternal(...)
        if (null != getFragmentManager ()) {
            try {
                super.dismissAllowingStateLoss ();
            } catch (final Exception exception) {
                // ...
            }
        }
    }
    
    public BaseHardyDialogsCode getDialogCode () {
        return mDialogCode;
    }
    
    public Object getAttachedData () {
        return mAttachedData;
    }
    
    // Helper method to determine if this dialog is based on some dialog specification with the same code
    public boolean isDialogWithCode (@NonNull final BaseHardyDialogsCode dialogCode) {
        return dialogCode.code ().equals (mDialogCode.code ());
    }

    // Helper method to determine if this isolated dialog is based on some dialog specification with the same code
    static boolean isIsolatedDialogWithCode (final BaseHardyDialogsCode dialogCode, final Intent intent) {
        BaseDialogSpecification dialog = (BaseDialogSpecification) intent.getSerializableExtra (EXTRA_DIALOG_INSTANCE);
        if (null != dialog) {
            return dialogCode.code ().equals (dialog.code ().code ());
        } else {
            return false;
        }
    }

    public static BaseDialogSpecification handleIsolated (final Context context, final Intent intent) {
        // Get a dialog from the intent
        BaseDialogSpecification dialog = (BaseDialogSpecification) intent.getSerializableExtra (EXTRA_DIALOG_INSTANCE);
        if (null != dialog) {
            // Attach own data (workaround for Parcelable because it isn't Serializable) and show it
            dialog = dialog.basedOn ().attachedDataOfIntent (intent).build ();
            if (null != dialog.show (context)) {
                return dialog;
            }
        }
        return null;
    }
    
}
