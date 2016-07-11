package org.brainail.EverboxingSplashFlame.ui.views;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame.utils.tool.ToolResources;

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
public class BaseIcon extends IconDrawable {

    public BaseIcon (final Context context, final Iconify.IconValue icon) {
        super (context, icon);
    }

    public static BaseIcon colorIcon (
            final Context context,
            final Iconify.IconValue icon,
            final @ColorRes int colorId) {

        final BaseIcon drawable = new BaseIcon (context, icon);
        drawable.color (ContextCompat.getColor (context, colorId));
        drawable.sizeRes (R.dimen.base_icon_size);
        return drawable;
    }

    public static BaseIcon controlIcon (final Context context, final Iconify.IconValue icon) {
        final BaseIcon drawable = new BaseIcon (context, icon);
        drawable.color (ToolResources.retrievePrimaryColor (context));
        drawable.sizeRes (R.dimen.base_icon_size);
        return drawable;
    }

    public static BaseIcon defIcon (final Context context, final Iconify.IconValue icon) {
        final BaseIcon drawable = new BaseIcon (context, icon);
        drawable.sizeRes (R.dimen.base_icon_size);
        return drawable;
    }

}
