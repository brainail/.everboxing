package org.brainail.EverboxingLexis.ui.notice;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.brainail.EverboxingLexis.utils.Plogger;
import org.brainail.EverboxingLexis.utils.tool.ToolPhone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.WeakHashMap;

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
public final class NoticeController {

    private static boolean CHECK_DEPRECATION = false;
    private static HashSet<Class<?>> DEPRECATED_SCENES;
    private static HashSet<Class<?>> DEPRECATED_PHONE_SCENES;
    private static HashSet<Class<?>> DEPRECATED_TABLET_SCENES;
    private static List<Class<?>> DEPRECATED_PHONE_SUBSCENES;
    private static List<Class<?>> DEPRECATED_TABLET_SUBSCENES;

    static {
        //
        // +------------------------------------------------------------+
        // | Deprecated places for all types                            |
        // +------------------------------------------------------------+
        // ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
        DEPRECATED_SCENES = new HashSet<Class<?>>();

        //
        // +------------------------------------------------------------+
        // | Deprecated places only for phones                          |
        // +------------------------------------------------------------+
        // ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
        DEPRECATED_PHONE_SCENES = new HashSet<Class<?>>();

        //
        // +------------------------------------------------------------+
        // | Deprecated places only for tablets                         |
        // +------------------------------------------------------------+
        // ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
        DEPRECATED_TABLET_SCENES = new HashSet<Class<?>>();

        //
        // +------------------------------------------------------------+
        // | Deprecated sub-places only for phones                      |
        // +------------------------------------------------------------+
        // ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
        DEPRECATED_PHONE_SUBSCENES = new ArrayList<Class<?>>();

        //
        // +------------------------------------------------------------+
        // | Deprecated sub-places only for tablets                     |
        // +------------------------------------------------------------+
        // ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
        DEPRECATED_TABLET_SUBSCENES = new ArrayList<Class<?>>();
    }

    private static final WeakHashMap<Object, NoticeOnSceneController> sInstances = new WeakHashMap<>();

    public static synchronized NoticeOnSceneController from(final Object scene) {
        NoticeOnSceneController controller = sInstances.get(scene);

        if (null == controller) {
            controller = createInternally(scene);
            sInstances.put(scene, controller);
        }

        return controller;
    }

    public static synchronized void byeBye(final Object scene) {
        sInstances.remove(scene);
    }

    private static NoticeOnSceneController createInternally(final Object scene) {
        if (isSceneDeprecated(scene)) {
            return new NoticeOnSceneControllerStub();
        } else if (scene instanceof AppCompatActivity) {
            return new NoticeOnActivitySceneController((Activity) scene);
        } else if (scene instanceof Fragment) {
            return new NoticeOnFragmentSceneController((Fragment) scene);
        } else {
            Plogger.logE(Plogger.LogScope.WTF, "Can't create controller for scene");
            return new NoticeOnSceneControllerStub();
        }
    }

    private static boolean isSceneDeprecated(final Object scene) {
        if (!CHECK_DEPRECATION) {
            return false;
        }

        // Common check for scenes
        if (null == scene || DEPRECATED_SCENES.contains(scene.getClass())) {
            return true;
        }

        // Phone check for sub-scenes
        if (!ToolPhone.isTablet() && checkSubscene(scene, DEPRECATED_PHONE_SUBSCENES)) {
            return true;
        }

        // Tablet check for sub-scenes
        if (ToolPhone.isTablet() && checkSubscene(scene, DEPRECATED_TABLET_SUBSCENES)) {
            return true;
        }

        // Phone & tablet check
        if (ToolPhone.isTablet()) {
            return DEPRECATED_PHONE_SCENES.contains(scene.getClass());
        } else {
            return DEPRECATED_TABLET_SCENES.contains(scene.getClass());
        }
    }

    private static boolean checkSubscene(final Object scene, final List<Class<?>> subscenes) {
        for (final Class<?> subscene : subscenes) {
            if (subscene.isInstance(scene)) {
                return true;
            }
        }

        return false;
    }

}
