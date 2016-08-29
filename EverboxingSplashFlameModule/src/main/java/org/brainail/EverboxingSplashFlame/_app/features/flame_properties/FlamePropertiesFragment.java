package org.brainail.EverboxingSplashFlame._app.features.flame_properties;

import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import org.brainail.EverboxingHardyDialogs.HardyDialogFragment;
import org.brainail.EverboxingHardyDialogs.HardyDialogsHelper;
import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame._app.dialogs.hardy.AppHardyDialogs;
import org.brainail.EverboxingSplashFlame.di.component.ActivityComponent;
import org.brainail.EverboxingSplashFlame.files.FileCreator;
import org.brainail.EverboxingSplashFlame.ui.fragments.base.RxBaseFragment;
import org.brainail.EverboxingSplashFlame.utils.tool.ToolFractal;
import org.brainail.EverboxingTools.utils.PooLogger;
import org.brainail.EverboxingTools.utils.tool.ToolNumber;
import org.brainail.EverboxingTools.utils.tool.ToolNumber.ValidationStatus;
import org.brainail.EverboxingTools.utils.tool.ToolPhone;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.brainail.EverboxingSplashFlame._app.dialogs.hardy.AppHardyDialogs.AppHardyDialogsCode.D_FLAME_SIDE_SIZES;
import static org.brainail.EverboxingSplashFlame._app.dialogs.hardy.AppHardyDialogs.AppHardyDialogsCode.D_FLAME_STYLE_TYPES;
import static org.brainail.EverboxingSplashFlame._app.dialogs.hardy.AppHardyDialogs.AppHardyDialogsCode.D_GENERATING_FLAME_PROGRESS;

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
public class FlamePropertiesFragment
        extends RxBaseFragment
        implements HardyDialogFragment.OnDialogListActionCallback {

    public static final class SideSizeRange {
        private static final int LOW = 640;
        private static final int HIGH = 2048;

        public final int low, high;

        public SideSizeRange(final int low, final int high) {
            this.low = low;
            this.high = high;
        }
    }

    @BindView (R.id.flame_it)
    protected View mFlameIt;
    @BindView (R.id.first_side_size_text_input_layout)
    protected TextInputLayout mFirstSideSizeInputLayout;
    @BindView (R.id.first_side_size_edit_text)
    protected EditText mFirstSideSizeEditText;
    @BindView (R.id.second_side_size_text_input_layout)
    protected TextInputLayout mSecondSideSizeInputLayout;
    @BindView (R.id.second_side_size_edit_text)
    protected EditText mSecondSideSizeEditText;
    @BindView (R.id.style_type_edit_text)
    protected EditText mStyleTypeSizeEditText;

    @Inject
    protected FileCreator mFileCreator;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate (R.layout.fragment_flame_properties, container, false);
        bind (view);
        initViews();
        return view;
    }

    private void initViews () {
        final Resources resources = getContext ().getResources ();

        // Init default style type
        final int DEFAULT_STYLE_TYPE_INDEX = 0;
        mStyleTypeSizeEditText.setText (
                resources.getStringArray (R.array.dialog_flame_style_types_body) [DEFAULT_STYLE_TYPE_INDEX]);
        mStyleTypeSizeEditText.setTag (
                resources.getStringArray (R.array.dialog_flame_style_types_tags) [DEFAULT_STYLE_TYPE_INDEX]);
    }

    @Override
    public void injectMembers (ActivityComponent activityComponent) {
        activityComponent.inject (this);
    }

    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);

        final Observable<String> observable = getCachedObservable (false);
        bindSubscription (null != observable ? observable.subscribe (this :: openPreview) : null);

        bindSubscription (RxTextView.textChanges (mFirstSideSizeEditText).subscribe (this :: firstSideSizeChanged));
        bindSubscription (RxTextView.textChanges (mSecondSideSizeEditText).subscribe (this :: secondSideSizeChanged));
        bindSubscription (RxView.clicks (mFlameIt).throttleFirst (1L, TimeUnit.SECONDS)
                        .observeOn (AndroidSchedulers.mainThread ())
                        .subscribe ($ -> {flameIt ();})
        );

        initDefaultSize (savedInstanceState);
    }

    private void initDefaultSize(final @Nullable Bundle savedInstanceState) {
        if (null == savedInstanceState) {
            final Point sizes = ToolPhone.realScreenSize (getContext ());
            final int defaultFirstSideSize = Math.max (sizes.x, sizes.y);
            final int defaultSecondSideSize = Math.min (sizes.x, sizes.y);
            mFirstSideSizeEditText.setText (String.valueOf (defaultFirstSideSize));
            mSecondSideSizeEditText.setText (String.valueOf (defaultSecondSideSize));
        }
    }

    private int firstSideSizeInt () {
        return Integer.parseInt (mFirstSideSizeEditText.getText ().toString ());
    }

    private int secondSideSizeInt () {
        return Integer.parseInt (mSecondSideSizeEditText.getText ().toString ());
    }

    private int styleTypeInt () {
        return Integer.parseInt ((String) mStyleTypeSizeEditText.getTag ());
    }

    private void firstSideSizeChanged (final CharSequence text) {
        checkSideSizeText (mFirstSideSizeInputLayout, text.toString ());
    }

    private void secondSideSizeChanged (final CharSequence text) {
        checkSideSizeText (mSecondSideSizeInputLayout, text.toString ());
    }

    private boolean checkBothSideSizeText () {
        if (! checkSideSizeText(mFirstSideSizeInputLayout, mFirstSideSizeEditText.getText ().toString ())) {
            return false;
        }

        if (! checkSideSizeText(mSecondSideSizeInputLayout, mSecondSideSizeEditText.getText ().toString ())) {
            return false;
        }

        return true;
    }

    private SideSizeRange calcSideSizeRange () {
        final Point sizes = ToolPhone.realScreenSize (getContext ());
        return new SideSizeRange (SideSizeRange.LOW, Math.max (Math.max (sizes.x, sizes.y), SideSizeRange.HIGH));
    }

    private boolean checkSideSizeText (final TextInputLayout sideSize, final String text) {
        final SideSizeRange sideSizeRange = calcSideSizeRange ();
        if (ValidationStatus.OK != ToolNumber.validateIntNumber (text, sideSizeRange.low, sideSizeRange.high)) {
            sideSize.setErrorEnabled (true);
            sideSize.setError (getString(R.string.side_size_incorrect_format, sideSizeRange.low, sideSizeRange.high));
            return false;
        } else {
            sideSize.setErrorEnabled (false);
            sideSize.setError (null);
            return true;
        }
    }

    private void flameIt () {
        if (checkBothSideSizeText ()) {
            AppHardyDialogs.generatingFlameDialog ().show (this);
            bindSubscription (cacheObservable (this.<String> createCachedObservable ()).subscribe (this :: openPreview));
        }
    }

    @Override
    protected <T> Observable<T> createCachedObservable () {
        // noinspection unchecked
        return (Observable<T>) Observable.fromCallable (this :: warmUp)
                .subscribeOn (Schedulers.computation ())
                .observeOn (AndroidSchedulers.mainThread ())
                .cache ();
    }

    @WorkerThread
    private String warmUp () {
        final File filePath = mFileCreator.provideOrCreateFlamePreview ();
        PooLogger.debug ("warmUp: filePath = ?", filePath);

        ToolFractal.warmUp (
                filePath.getAbsolutePath (),
                firstSideSizeInt (),
                secondSideSizeInt (),
                styleTypeInt ());

        return filePath.getAbsolutePath ();
    }

    @UiThread
    private void openPreview (final String filePath) {
        cacheObservable (null);
        HardyDialogsHelper.dismissDialog (this, D_GENERATING_FLAME_PROGRESS);
        mNavigator.flameFilePreview (filePath).start ();
    }

    @OnClick (R.id.first_side_size_selector)
    protected final void selectFirstSideSize () {
        AppHardyDialogs.flameSideSizes (getContext ()).setCallbacks (this).show (this);
    }

    @OnClick (R.id.style_type_selector)
    protected final void selectStyleType () {
        AppHardyDialogs.flameStyleTypes (getContext ()).setCallbacks (this).show (this);
    }

    @Override
    public void onDialogListAction (HardyDialogFragment dialog, int whichItem, String item, String itemTag) {
        if (dialog.isDialogWithCode (D_FLAME_SIDE_SIZES)) {
            final String [] sides = item.split ("\\s?x\\s?");
            final int firstSideSize = Integer.parseInt (sides [0]);
            final int secondSideSize = Integer.parseInt (sides [1]);
            mFirstSideSizeEditText.setText (String.valueOf (Math.max (firstSideSize, secondSideSize)));
            mSecondSideSizeEditText.setText (String.valueOf (Math.min (secondSideSize, secondSideSize)));
        } else if (dialog.isDialogWithCode (D_FLAME_STYLE_TYPES)) {
            mStyleTypeSizeEditText.setText (item);
            mStyleTypeSizeEditText.setTag (itemTag);
        }
    }

}
