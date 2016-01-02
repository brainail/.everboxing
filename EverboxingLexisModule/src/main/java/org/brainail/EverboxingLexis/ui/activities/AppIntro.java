package org.brainail.EverboxingLexis.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.paolorotolo.appintro.AppIntroFragment;

import org.brainail.EverboxingLexis.R;

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
public class AppIntro extends com.github.paolorotolo.appintro.AppIntro2 {

    private int mScrollDurationFactor = 1;

    public static void introduce (final Activity scene) {
        final Intent intent = new Intent (scene, AppIntro.class);
        scene.startActivity (intent);
    }

    @Override
    public void init (@Nullable Bundle savedInstanceState) {
        setScrollDurationFactor (mScrollDurationFactor);

        addSlide (AppIntroFragment.newInstance (
                getResources ().getString (R.string.app_intro_slide_no_01_title),
                getResources ().getString (R.string.app_intro_slide_no_01_description),
                R.drawable.ic_app_intro_slide_no_01,
                getResources ().getColor (R.color.md_pink_900)
        ));

        addSlide (AppIntroFragment.newInstance (
                getResources ().getString (R.string.app_intro_slide_no_02_title),
                getResources ().getString (R.string.app_intro_slide_no_02_description),
                R.drawable.ic_app_intro_slide_no_02,
                getResources ().getColor (R.color.md_pink_900)
        ));

        addSlide (AppIntroFragment.newInstance (
                getResources ().getString (R.string.app_intro_slide_no_03_title),
                getResources ().getString (R.string.app_intro_slide_no_03_description),
                R.drawable.ic_app_intro_slide_no_03,
                getResources ().getColor (R.color.md_pink_900)
        ));

        setZoomAnimation ();

//        showSkipButton (true);
        setProgressButtonEnabled (true);
        showStatusBar (true);
    }

    private void launchHome () {
        // final Intent intent = new Intent (this, HomeActivity.class);
        // startActivity (intent);
        finish ();
    }

//    @Override
//    public void onSkipPressed () {
//        launchHome ();
//    }

    @Override
    public void onNextPressed () {}

    @Override
    public void onDonePressed () {
        launchHome ();
    }

    @Override
    public void onSlideChanged () {}

    @Override
    public void onBackPressed () {
        launchHome ();
    }

}
