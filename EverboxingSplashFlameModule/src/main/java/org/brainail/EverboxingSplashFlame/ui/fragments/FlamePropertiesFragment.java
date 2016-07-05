package org.brainail.EverboxingSplashFlame.ui.fragments;

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

import org.brainail.EverboxingHardyDialogs.HardyDialogsHelper;
import org.brainail.EverboxingSplashFlame.Constants;
import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame.ui.fragments.base.RxBaseFragment;
import org.brainail.EverboxingSplashFlame.ui.views.dialogs.hardy.AppHardyDialogs;
import org.brainail.EverboxingSplashFlame.utils.tool.ToolFractal;
import org.brainail.EverboxingTools.utils.tool.ToolNumber;
import org.brainail.EverboxingTools.utils.tool.ToolNumber.ValidationStatus;
import org.brainail.EverboxingTools.utils.tool.ToolPhone;

import java.io.File;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.brainail.EverboxingSplashFlame.ui.views.dialogs.hardy.AppHardyDialogsCode.D_GENERATING_FLAME_PROGRESS;

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
public class FlamePropertiesFragment extends RxBaseFragment {

    public static final class SideSizeRange {
        private static final int LOW = 640;
        private static final int HIGH = 2560;

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

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate (R.layout.fragment_flame_properties, container, false);
        return bind (view);
    }

    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);

        final Observable<String> observable = getCachedObservable (false);
        bindSubscription (null != observable ? observable.subscribe (this :: openPreview) : null);

        bindSubscription (RxTextView.textChanges (mFirstSideSizeEditText).subscribe (this :: firstSideSizeChanged));
        bindSubscription (RxTextView.textChanges (mSecondSideSizeEditText).subscribe (this :: secondSideSizeChanged));
        bindSubscription (RxView.clicks (mFlameIt).debounce (1L, TimeUnit.SECONDS)
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
        final File filePath = new File (Constants.APP_MEDIA_DIR_PATH, "ff_" + System.currentTimeMillis () + ".jpeg");

        if (! filePath.getParentFile ().exists ()) {
            // noinspection ResultOfMethodCallIgnored
            filePath.getParentFile ().mkdirs ();
        }

        ToolFractal.warmUp (
                filePath.getAbsolutePath (),
                firstSideSizeInt (),
                secondSideSizeInt (),
                (int) ((System.currentTimeMillis () % 20) + 1));

        return filePath.getAbsolutePath ();
    }

    @UiThread
    private void openPreview (final String filePath) {
        cacheObservable (null);
        HardyDialogsHelper.dismissDialog (this, D_GENERATING_FLAME_PROGRESS);
        mNavigator.flameFilePreview (filePath).start ();
    }

    @OnClick (R.id.first_side_size_selector)
    protected final void selectFirstSideSize () {}

    @OnClick (R.id.second_side_size_selector)
    protected final void selectSecondSideSize () {}

    @OnClick (R.id.style_type_selector)
    protected final void selectStyleType () {}

}
