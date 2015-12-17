package org.brainail.EverboxingHardyDialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.brainail.EverboxingHardyDialogs.HardyDialogFragment.ActionRequestCode;
import org.brainail.EverboxingHardyDialogs.HardyDialogFragment.Args;
import org.brainail.EverboxingHardyDialogs.HardyDialogFragment.IsolatedDialogHandler;
import org.brainail.EverboxingHardyDialogs.utils.BiDiFormatterUtils;

import java.io.Serializable;
import java.util.Locale;

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
 * <p/>
 * <br/><br/>
 * Base dialog specification, contains base information to show a dialog <br/>
 * - title text <br/>
 * - body text <br/>
 * - content layout id <br/>
 * - request codes for cancel & dismiss actions <br/>
 * - target fragment if it's presented <br/>
 */
public class BaseDialogSpecification implements Serializable {
    
    // Value to show that we don't want to change the current value
    public static final int KEEP_DEFAULT_VALUE = -1;
    // To put our specification inside of an intent
    static final String EXTRA_DIALOG_INSTANCE = "dialog_instance";
    
    // Title text
    private String mTitle;
    // Body text
    private String mBody;
    // Body id inside of some layout/content
    private int mBodyId;
    // Layout id for content
    private int mContentLayoutId;
    // Request code for cancel action
    private int mCancelActionRequestCode;
    // Request code for dismiss action
    private int mDismissActionRequestCode;
    // If it's presented then we will try to handle some actions on this fragment
    // via parent Fragment (don't confuse with target Fragment)
    private transient Fragment mTargetFragment;
    private boolean mHasTargetFragment;
    // Dialog code/ID from the spec or custom
    private DialogCode mCode;
    // Isolated handler to handle some dialog actions/preparations
    private IsolatedDialogHandler mIsolatedHandler;
    // Would you like to handle some actions/preparations from dialog?
    private boolean mHasCallbacks;
    // Sometimes we don't wanna give abilities to cancel our dialog (back key, touch outside)
    private boolean mIsCancelable;
    // We can attach some own data (Serializable object or Parcelable object)
    private Object mAttachedData;
    // If you want to handle some actions/preparations by non-serializable callback
    // then we have to close our dialog after rotation (or by some other reasons)
    // to stay in a safe state without leaks and crashes. Notice that this
    // option can be applied even if our dialog is non-cancelable (it'll stay as non-cancelable
    // for instance while we are in the current orientation)
    private boolean mIsRestorable;
    // Allow or disallow to finish
    // the dialog's underlay (in most cases this is Activity with the Translucent theme)
    private boolean mHasDestroyableUnderlay;
    
    // Don't let to create this instance directly
    private BaseDialogSpecification () {}
    
    protected BaseDialogSpecification (final Builder<?> builder) {
        mTitle = builder.title;
        mBody = builder.body;
        mBodyId = builder.bodyId;
        mContentLayoutId = builder.contentLayoutId;
        mCancelActionRequestCode = builder.cancelActionRequestCode;
        mDismissActionRequestCode = builder.dismissActionRequestCode;
        mTargetFragment = builder.targetFragment;
        mHasTargetFragment = builder.hasTargetFragment;
        mCode = builder.code;
        mIsolatedHandler = builder.isolatedHandler;
        mHasCallbacks = builder.hasCallbacks;
        mIsCancelable = builder.isCancelable;
        mAttachedData = builder.attachedData;
        mIsRestorable = builder.isRestorable;
        mHasDestroyableUnderlay = builder.hasDestroyableUnderlay;
    }
    
    // BaseDialogSpecification's builder
    public static class Builder <T extends Builder<T>> {
        
        private String title = null;
        private String body = null;
        private int bodyId = HardyDialogFragment.NO_RESOURCE_ID;
        private int contentLayoutId = HardyDialogFragment.NO_RESOURCE_ID;
        private int cancelActionRequestCode = ActionRequestCode.CANCEL;
        private int dismissActionRequestCode = ActionRequestCode.UNHANDLED;
        private transient Fragment targetFragment = null;
        private boolean hasTargetFragment = false;
        private DialogCode code = DialogCode.UNKNOWN;
        private IsolatedDialogHandler isolatedHandler = null;
        private boolean hasCallbacks = false;
        private boolean isCancelable = true;
        private transient Object attachedData = null;
        private boolean isRestorable = true;
        private boolean hasDestroyableUnderlay = false;
        
        protected Builder () {
            fillDefaultValues ();
        }
        
        protected Builder (final BaseDialogSpecification specification) {
            title = specification.mTitle;
            body = specification.mBody;
            bodyId = specification.mBodyId;
            contentLayoutId = specification.mContentLayoutId;
            cancelActionRequestCode = specification.mCancelActionRequestCode;
            dismissActionRequestCode = specification.mDismissActionRequestCode;
            targetFragment = specification.mTargetFragment;
            hasTargetFragment = specification.mHasTargetFragment;
            code = specification.mCode;
            isolatedHandler = specification.mIsolatedHandler;
            hasCallbacks = specification.mHasCallbacks;
            isCancelable = specification.mIsCancelable;
            attachedData = specification.mAttachedData;
            isRestorable = specification.mIsRestorable;
            hasDestroyableUnderlay = specification.mHasDestroyableUnderlay;
        }
        
        // Fills all default values which are related to this dialog
        protected void fillDefaultValues () {
            // Use the predefined request code for cancel action
            cancelActionRequestCode (ActionRequestCode.CANCEL);
            // Use the predefined request code for dismiss action
            dismissActionRequestCode (ActionRequestCode.UNHANDLED);
            // No layout id for content
            content (HardyDialogFragment.NO_RESOURCE_ID);
            // No code
            code (DialogCode.UNKNOWN);
            // We don't want to be annoying guys by default
            cancelable (true);
            // By default we restore our dialog for instance after orientation changes
            restorable (true);
            // Don't finish underlay by default
            hasDestroyableUnderlay (false);
        }
        
        @SuppressWarnings ("unchecked")
        protected T self () {
            // We return "this" but casts it to the generic type
            // that is able to stand higher in the hierarchy of builders
            // Without it we should use all builder's methods in a special order
            // from the highest to the lowest. But we want to use it in any order.
            // It's all about of builders subclassing and this is a solution to avoid it.
            return (T) this;
        }
        
        public T noButtons () {
            return self ();
        }
        
        public T restorable (final boolean isRestorable) {
            this.isRestorable = isRestorable;
            return self ();
        }
        
        public T cancelable (final boolean isCancelable) {
            this.isCancelable = isCancelable;
            return self ();
        }
        
        public T hasDestroyableUnderlay (final boolean hasDestroyableUnderlay) {
            this.hasDestroyableUnderlay = hasDestroyableUnderlay;
            return self ();
        }
        
        public T code (final DialogCode code) {
            this.code = code;
            return self ();
        }
        
        public T cancelActionRequestCode (final int cancelActionRequestCode) {
            this.cancelActionRequestCode = cancelActionRequestCode;
            return self ();
        }
        
        public T dismissActionRequestCode (final int dismissActionRequestCode) {
            this.dismissActionRequestCode = dismissActionRequestCode;
            return self ();
        }
        
        public T title (final String title) {
            this.title = title;
            return self ();
        }
        
        public T title (final int stringId) {
            return title (ViberApplication.getInstance ().getString (stringId));
        }
        
        public T title (final int stringId, final Object... args) {
            if (KEEP_DEFAULT_VALUE == stringId) {
                return title (String.format (Locale.US, title, args));
            } else {
                return title (ViberApplication.getInstance ().getString (stringId, args));
            }
        }
        
        public T customBody (final int resourceId, final int stringId) {
            this.bodyId = resourceId;
            return body (stringId);
        }
        
        public T customBody (final int resourceId, final String body) {
            this.bodyId = resourceId;
            return body (body);
        }
        
        public T customBody (final int resourceId, final int stringId, final Object... args) {
            this.bodyId = resourceId;
            return body (stringId, args);
        }
        
        public T body (final String body) {
            this.body = body;
            return self ();
        }
        
        public T content (final int contentLayoutId) {
            this.contentLayoutId = contentLayoutId;
            return self ();
        }
        
        public T body (final int stringId) {
            return body (ViberApplication.getInstance ().getString (stringId));
        }
        
        public T body (final int stringId, final Object... args) {
            if (KEEP_DEFAULT_VALUE == stringId) {
                return body (String.format (Locale.US, body, BiDiFormatterUtils.wrapArguments (args)));
            } else {
                return body (BiDiFormatterUtils.wrapStringArguments (ViberApplication.getInstance (), stringId, args));
            }
        }
        
        public T attachedData (final Serializable attachedData) {
            this.attachedData = attachedData;
            return self ();
        }
        
        public T attachedData (final Parcelable attachedData) {
            this.attachedData = attachedData;
            return self ();
        }
        
        public T attachedDataOfIntent (final Intent intent) {
            if (intent.hasExtra (Args.INTENT_ATTACHED_PARCELABLE_DATA)) {
                this.attachedData = intent.getParcelableExtra (Args.INTENT_ATTACHED_PARCELABLE_DATA);
            }
            
            return self ();
        }
        
        /**
         * Pass a handler to handle some actions/preparations for dialog. <br/>
         * <b><font color="red">
         * Be sure that this is a static field or an instance of separated class
         * or an instance of some inner static class. <br/>
         * Also it should be {@link Serializable}
         * </font></b>
         *
         * @param handler An instance of the {@link IsolatedDialogHandler} class.
         */
        public T setCallbacks (final IsolatedDialogHandler handler) {
            this.isolatedHandler = handler;
            this.hasCallbacks = null != handler;
            this.targetFragment = null;
            this.hasTargetFragment = false;
            return self ();
        }
        
        public T setCallbacks (final Fragment handler) {
            this.targetFragment = handler;
            this.hasTargetFragment = null != handler;
            this.hasCallbacks = null != handler;
            this.isolatedHandler = null;
            return self ();
        }
        
        public T setCallbacks (@SuppressWarnings ("unused") final Activity handler) {
            this.hasCallbacks = null != handler;
            this.targetFragment = null;
            this.hasTargetFragment = false;
            this.isolatedHandler = null;
            return self ();
        }
        
        // Shows without context
        // We'll use the SystemDialogActivity to show it
        public void showIsolated () {
            build ().showIsolated ();
        }
        
        // Shows without context
        // We'll use the provided activity to show it
        public void showIsolated (final Class<?> activity) {
            build ().showIsolated (activity);
        }
        
        // Returns an Intent to use the SystemDialogActivity to show it later via this intent
        public Intent getIsolatedIntent () {
            return build ().getIsolatedIntent ();
        }
        
        /// Returns an Intent to use the provided activity to show it later via this intent
        public Intent getIsolatedIntent (final Class<?> activity) {
            return build ().getIsolatedIntent (activity);
        }
        
        // Shows on some Fragment
        public HardyDialogFragment show (final Fragment fragment) {
            return show (hasTargetFragment ?
                    fragment.getChildFragmentManager () :
                    fragment.getFragmentManager ()
            );
        }
        
        /**
         * <font color="red">NOTE:</font> <b>Use it at your own risk</b>
         */
        public HardyDialogFragment showAllowingStateLoss (final Fragment fragment) {
            return showAllowingStateLoss (hasTargetFragment ?
                    fragment.getChildFragmentManager () :
                    fragment.getFragmentManager ()
            );
        }
        
        // Shows on some FragmentActivity
        public HardyDialogFragment show (final FragmentActivity activity) {
            return build ().show (activity.getSupportFragmentManager ());
        }
        
        /**
         * <font color="red">NOTE:</font> <b>Use it at your own risk</b>
         */
        public HardyDialogFragment showAllowingStateLoss (final FragmentActivity activity) {
            return build ().showAllowingStateLoss (activity.getSupportFragmentManager ());
        }
        
        // Shows by some FragmentManager
        public HardyDialogFragment show (final FragmentManager fragmentManager) {
            return build ().show (fragmentManager);
        }
        
        /**
         * <font color="red">NOTE:</font> <b>Use it at your own risk</b>
         */
        public HardyDialogFragment showAllowingStateLoss (final FragmentManager fragmentManager) {
            return build ().showAllowingStateLoss (fragmentManager);
        }
        
        // Shows by some Context
        public HardyDialogFragment show (final Context context) {
            if (context instanceof FragmentActivity) {
                return build ().show ((FragmentActivity) context);
            } else {
                if (BuildConfig.DEBUG) {
                    throw new UnsupportedOperationException ("You should use the Activity's context");
                }
                
                return null;
            }
        }
        
        /**
         * <font color="red">NOTE:</font> <b>Use it at your own risk</b>
         */
        public HardyDialogFragment showAllowingStateLoss (final Context context) {
            if (context instanceof FragmentActivity) {
                return build ().showAllowingStateLoss ((FragmentActivity) context);
            } else {
                if (BuildConfig.DEBUG) {
                    throw new UnsupportedOperationException ("You should use the Activity's context");
                }
                
                return null;
            }
        }
        
        // Builds builder to the dialog specification
        BaseDialogSpecification build () {
            return new BaseDialogSpecification (this);
        }
        
    }
    
    public Builder<?> basedOn () {
        return new Builder (this);
    }
    
    // Creates a new builder
    public static Builder<?> create () {
        return new Builder ();
    }
    
    // Shows without Context via the provided activity
    public void showIsolated (final Class<?> activity) {
        final Intent dialogIntent = new Intent (ViberApplication.getInstance (), activity);
        showIsolated (dialogIntent, true);
    }
    
    // Shows without Context via the SystemDialogActivity
    public void showIsolated () {
        final Intent dialogIntent = RemoteDialogActivity.getIntent (RemoteDialogActivity.TYPE_REMOTE_DIALOG);
        showIsolated (dialogIntent, true);
    }
    
    // Returns an intent to show this dialog without Context via the provided activity
    public Intent getIsolatedIntent (final Class<?> activity) {
        final Intent dialogIntent = new Intent (ViberApplication.getInstance (), activity);
        return showIsolated (dialogIntent, false);
    }
    
    // Returns an intent to show this dialog without Context via the SystemDialogActivity
    public Intent getIsolatedIntent () {
        final Intent dialogIntent = RemoteDialogActivity.getIntent (RemoteDialogActivity.TYPE_REMOTE_DIALOG);
        return showIsolated (dialogIntent, false);
    }
    
    private Intent showIsolated (final Intent dialogIntent, final boolean showImmediately) {
        if (BuildConfig.DEBUG && !HardyDialogVerifier.verify (this, true)) {
            return null;
        }
        
        dialogIntent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        
        if (mAttachedData instanceof Parcelable) {
            dialogIntent.putExtra (Args.INTENT_ATTACHED_PARCELABLE_DATA, (Parcelable) mAttachedData);
            mAttachedData = null;
        }
        
        dialogIntent.putExtra (EXTRA_DIALOG_INSTANCE, this);
        
        if (showImmediately) {
            ViberApplication.getInstance ().startActivity (dialogIntent);
        }
        
        return dialogIntent;
    }
    
    public HardyDialogFragment show (final Context context) {
        if (context instanceof FragmentActivity) {
            return show ((FragmentActivity) context);
        } else {
            if (BuildConfig.DEBUG) {
                throw new UnsupportedOperationException ("You should use the Activity's context");
            }
            
            return null;
        }
    }
    
    /**
     * <font color="red">NOTE:</font> <b>Use it at your own risk</b>
     */
    public HardyDialogFragment showAllowingStateLoss (final Context context) {
        if (context instanceof FragmentActivity) {
            return showAllowingStateLoss ((FragmentActivity) context);
        } else {
            if (BuildConfig.DEBUG) {
                throw new UnsupportedOperationException ("You should use the Activity's context");
            }
            
            return null;
        }
    }
    
    public HardyDialogFragment show (final Fragment fragment) {
        return show (mHasTargetFragment ?
                fragment.getChildFragmentManager () :
                fragment.getFragmentManager ()
        );
    }
    
    /**
     * <font color="red">NOTE:</font> <b>Use it at your own risk</b>
     */
    public HardyDialogFragment showAllowingStateLoss (final Fragment fragment) {
        return showAllowingStateLoss (mHasTargetFragment ?
                fragment.getChildFragmentManager () :
                fragment.getFragmentManager ()
        );
    }
    
    public HardyDialogFragment show (final FragmentActivity activity) {
        return show (activity.getSupportFragmentManager ());
    }
    
    /**
     * <font color="red">NOTE:</font> <b>Use it at your own risk</b>
     */
    public HardyDialogFragment showAllowingStateLoss (final FragmentActivity activity) {
        return showAllowingStateLoss (activity.getSupportFragmentManager ());
    }
    
    public HardyDialogFragment show (final FragmentManager fragmentManager) {
        return showInternally (fragmentManager, false);
    }
    
    /**
     * <font color="red">NOTE:</font> <b>Use it at your own risk</b>
     */
    public HardyDialogFragment showAllowingStateLoss (final FragmentManager fragmentManager) {
        return showInternally (fragmentManager, true);
    }
    
    private HardyDialogFragment showInternally (final FragmentManager fragmentManager, final boolean allowingStateLoss) {
        // Prepare bundle
        final Bundle args = new Bundle ();
        fillBundle (args);
        
        // Create a new hardy dialog fragment
        final HardyDialogFragment dialog = HardyDialogFragment.newInstance (args);
        
        // We want to handle some actions/preparations on the target fragment
        if (null != mTargetFragment) {
            // Check that you pass a right FragmentManager to show this dialog
            if (BuildConfig.DEBUG && fragmentManager != mTargetFragment.getChildFragmentManager ()) {
                throw new IllegalArgumentException (
                        "If you want to handle some actions/preparations\n " +
                                "on the target Fragment then you have to pass a child FragmentManager from this Fragment\n " +
                                "to show this dialog (@see Fragment#getChildFragmentManager()),\n " +
                                "otherwise it tries to handle all stuff on the owner Activity or via an isolated handler."
                );
            }
        }
        
        // Show it safely
        return showInternally (dialog, fragmentManager, allowingStateLoss);
    }
    
    private HardyDialogFragment showInternally (
            final HardyDialogFragment newDialog,
            final FragmentManager fragmentManager,
            final boolean allowingStateLoss) {
        
        if (BuildConfig.DEBUG && !HardyDialogVerifier.verify (this, false)) {
            return null;
        }
        
        try {
            if (!allowingStateLoss) {
                try {
                    newDialog.show (newTransaction (fragmentManager), mCode.managerTag ());
                } catch (final Exception exception) {
                    newTransaction (fragmentManager).add (newDialog, mCode.managerTag ()).commitAllowingStateLoss ();
                }
            } else {
                newTransaction (fragmentManager).add (newDialog, mCode.managerTag ()).commitAllowingStateLoss ();
            }
        } catch (final Exception exception) {
            if (BuildConfig.DEBUG) {
                throw exception;
            }
        }
        
        return newDialog;
    }
    
    private FragmentTransaction newTransaction (final FragmentManager fragmentManager) {
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction ();
        final Fragment currentDialog = fragmentManager.findFragmentByTag (mCode.managerTag ());
        
        // Remove previous instances
        if (null != currentDialog) {
            fragmentTransaction.remove (currentDialog);
        }
        
        return fragmentTransaction;
    }
    
    protected void fillBundle (final Bundle args) {
        args.putString (Args.TITLE, mTitle);
        args.putString (Args.BODY, mBody);
        args.putInt (Args.BODY_ID, mBodyId);
        args.putInt (Args.CONTENT_LAYOUT_ID, mContentLayoutId);
        args.putInt (Args.CANCEL_ACTION_REQUEST_CODE, mCancelActionRequestCode);
        args.putInt (Args.DISMISS_ACTION_REQUEST_CODE, mDismissActionRequestCode);
        args.putSerializable (Args.DIALOG_CODE, mCode);
        args.putSerializable (Args.ISOLATED_HANDLER, mIsolatedHandler);
        args.putBoolean (Args.HAS_CALLBACKS, mHasCallbacks);
        args.putBoolean (Args.IS_CANCELABLE, mIsCancelable);
        args.putBoolean (Args.HAS_TARGET_FRAGMENT, mHasTargetFragment);
        args.putBoolean (Args.IS_RESTORABLE, mIsRestorable);
        args.putBoolean (Args.HAS_DESTROYABLE_UNDERLAY, mHasDestroyableUnderlay);
        
        // Attach data by type
        if (mAttachedData instanceof Parcelable) {
            args.putParcelable (Args.ATTACHED_PARCELABLE_DATA, (Parcelable) mAttachedData);
        } else if (mAttachedData instanceof Serializable) {
            args.putSerializable (Args.ATTACHED_SERIALIZABLE_DATA, (Serializable) mAttachedData);
        }
    }
    
    /** @ package-local */
    boolean isRestorable () {
        return mIsRestorable;
    }
    
    /** @ package-local */
    boolean hasCallbacks () {
        return mHasCallbacks;
    }
    
    /** @ package-local */
    IsolatedDialogHandler isolatedHandler () {
        return mIsolatedHandler;
    }
    
    public DialogCode code () {
        return mCode;
    }
    
    public Object attachedData () {
        return mAttachedData;
    }
    
    @Override
    public String toString () {
        return super.toString () + " {" + "mCode=" + mCode + "}";
    }
    
}

