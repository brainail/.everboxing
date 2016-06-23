package org.brainail.EverboxingSplashFlame.di.component;

import org.brainail.EverboxingSplashFlame.di.PerActivity;
import org.brainail.EverboxingSplashFlame.di.module.ActivityModule;
import org.brainail.EverboxingSplashFlame.di.module.WebModule;
import org.brainail.EverboxingSplashFlame.ui.activities.base.BaseActivity;
import org.brainail.EverboxingSplashFlame.ui.activities.common.HomeActivity;
import org.brainail.EverboxingSplashFlame.ui.fragments.base.BaseFragment;

import dagger.Subcomponent;

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
@PerActivity
@Subcomponent (modules = ActivityModule.class)
public interface ActivityComponent {
    void inject (BaseActivity activity);
    void inject (HomeActivity activity);

    void inject (BaseFragment fragment);

    WebComponent plus (WebModule module);
}
