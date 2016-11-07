package org.brainail.EverboxingSplashFlame.di.module;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame.di.ActivityContext;
import org.brainail.EverboxingSplashFlame.di.PerActivity;
import org.brainail.EverboxingSplashFlame.navigator.Navigator;
import org.brainail.EverboxingSplashFlame.navigator.NavigatorConstants.ToolbarTunerExtraKey;
import org.brainail.EverboxingSplashFlame.ui.drawer.DrawerSection;
import org.brainail.EverboxingSplashFlame.utils.tool.ToolToolbar;

import dagger.Module;
import dagger.Provides;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * The MIT License (MIT) <br/><br/>
 *
 * Copyright (c) 2016 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 *
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
@Module
public class ActivityModule {

    private AppCompatActivity mScene;

    public ActivityModule (AppCompatActivity activity) {
        mScene = activity;
    }

    @Provides
    @PerActivity
    @ActivityContext
    Context provideActivityContext () {
        return mScene;
    }

    @Provides
    @PerActivity
    @ActivityContext
    Navigator provideNavigator () {
        return new Navigator (mScene);
    }

    @Provides
    @PerActivity
    @ActivityContext
    ToolToolbar provideToolToolbar () {
        return new ToolToolbar (
                mScene,
                new String [] {DrawerSection.ExtraKey.TITLE, ToolbarTunerExtraKey.TITLE},
                new String [] {ToolbarTunerExtraKey.SUBTITLE},
                new String [] {DrawerSection.ExtraKey.COLOR, ToolbarTunerExtraKey.COLOR},
                R.attr.toolbarDefaultStyle,
                R.id.app_content,
                R.string.app_name);
    }

}
