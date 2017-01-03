package org.brainail.EverboxingSplashFlame._app.dialogs.hardy;

import android.content.Context;
import android.support.annotation.NonNull;

import org.brainail.EverboxingHardyDialogs.BaseDialogSpecification;
import org.brainail.EverboxingHardyDialogs.BaseHardyDialogsHandlers;
import org.brainail.EverboxingHardyDialogs.HardyDialogCodeProvider;
import org.brainail.EverboxingHardyDialogs.HardyDialogFragment;
import org.brainail.EverboxingHardyDialogs.HardyDialogFragment.LayoutParams;
import org.brainail.EverboxingHardyDialogs.ListDialogSpecification;
import org.brainail.EverboxingSplashFlame.R;

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
public final class AppHardyDialogs {

    public static BaseDialogSpecification.Builder<?> generatingFlameDialog () {
        return BaseDialogSpecification.create ()
                .code (AppHardyDialogsCode.D_GENERATING_FLAME_PROGRESS)
                .contentLayoutParams (new LayoutParams (200 /*dp*/, 200 /*dp*/))
                .content (R.layout.view_generating_flame_progress)
                .cancelable (false)
                .translucent (true);
    }

    public static ListDialogSpecification.Builder<?> helpUs () {
        return ListDialogSpecification.create ()
                .code (AppHardyDialogsCode.D_HELP_US)
                .title (R.string.dialog_help_us_title)
                .items (new int [] {
                        R.string.dialog_button_rate_app,
                        R.string.dialog_button_share_with_friends,
                        R.string.dialog_button_feedback_suggestion})
                .cancelable (true);
    }

    public static ListDialogSpecification.Builder<?> flameSideSizes (final @NonNull Context context) {
        return ListDialogSpecification.create ()
                .items (context.getResources ().getStringArray (R.array.dialog_popular_resolutions_body))
                .fromBottom (true)
                .title (R.string.dialog_popular_resolutions_title)
                .code (AppHardyDialogsCode.D_FLAME_SIDE_SIZES);
    }

    public static ListDialogSpecification.Builder<?> flameStyleTypes (final @NonNull Context context) {
        return ListDialogSpecification.create ()
                .items (context.getResources ().getStringArray (R.array.dialog_flame_style_types_body))
                .tags (AppHardyDialogsDataProvider.flameStyleTypesDialogTags (context))
                .fromBottom (true)
                .title (R.string.dialog_flame_style_types_title)
                .code (AppHardyDialogsCode.D_FLAME_STYLE_TYPES);
    }

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ ↓↓↓↓↓↓↓↓↓↓↓ ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ ↓↓ CODES ↓↓ ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ ↓↓↓↓↓↓↓↓↓↓↓ ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    public static enum AppHardyDialogsCode implements HardyDialogCodeProvider {

        // Generating flame
        D_GENERATING_FLAME_PROGRESS ("generating.flame.progress"),
        // Help Us from drawer
        D_HELP_US ("help.us"),
        // Flame side sizes
        D_FLAME_SIDE_SIZES ("flame.side.sizes"),
        // Flame style types
        D_FLAME_STYLE_TYPES ("flame.style.types");

        private final String code;

        AppHardyDialogsCode(final String code) {
            this.code = code;
        }

        public String code () {
            return code;
        }

        @Override
        public String managerTag () {
            return HardyDialogFragment.MANAGER_TAG_PREFIX + code;
        }

    }

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ ↓↓↓↓↓↓↓↓↓↓↓↓ ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ ↓   DATA   ↓ ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ ↓↓↓↓↓↓↓↓↓↓↓↓ ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    public static final class AppHardyDialogsDataProvider {
        public static String [] flameStyleTypesDialogTags(final @NonNull Context context) {
            return context.getResources ().getStringArray (R.array.dialog_flame_style_types_tags);
        }
    }

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ ↓↓↓↓↓↓↓↓↓↓↓↓ ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ ↓ HANDLERS ↓ ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ ↓↓↓↓↓↓↓↓↓↓↓↓ ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    public static class AppHardyDialogsHandlers extends BaseHardyDialogsHandlers {}

}
