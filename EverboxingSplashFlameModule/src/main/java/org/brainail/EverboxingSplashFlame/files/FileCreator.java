package org.brainail.EverboxingSplashFlame.files;

import android.support.annotation.CheckResult;

import org.brainail.EverboxingSplashFlame.Constants;
import org.brainail.EverboxingTools.utils.tool.ToolFile;

import java.io.File;
import java.io.FileFilter;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p>
 * The MIT License (MIT) <br/><br/>
 * <p>
 * Copyright (c) 2016 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public final class FileCreator {
    private static final String FLAME_PREVIEW_FILE_PREFIX = "fpf_";

    public static final FileFilter FLAME_PREVIEWS_FILES_FILTER
            = file -> file.getAbsolutePath ().contains (FLAME_PREVIEW_FILE_PREFIX);

    public @CheckResult File provideOrCreateFlamePreviewFile () {
        final File filePath = new File (Constants.APP_MEDIA_DIR_PATH, provideFlamePreviewFileName ());

        if (! filePath.getParentFile ().exists ()) {
            // noinspection ResultOfMethodCallIgnored
            filePath.getParentFile ().mkdirs ();
        }

        // we don't wanna show these files in a gallery
        ToolFile.createNomediaFile (filePath.getParentFile ().getAbsolutePath ());

        return filePath;
    }

    public String provideFlamePreviewFileName () {
        return FLAME_PREVIEW_FILE_PREFIX + System.currentTimeMillis () + ".jpeg";
    }
}
