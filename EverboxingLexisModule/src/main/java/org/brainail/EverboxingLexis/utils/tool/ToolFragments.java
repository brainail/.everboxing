package org.brainail.EverboxingLexis.utils.tool;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.utils.callable.Tagable;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * The MIT License (MIT) <br/><br/>
 *
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
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
public final class ToolFragments {

    public static boolean openFragment(final Activity activity, final Fragment target,  boolean clearTop) {
        // No place
        if (null == activity) return false;

        // It isn't our client
        if (!(target instanceof Tagable)) return false;

        final FragmentManager fragmentManager = activity.getFragmentManager();
        final String tagIdentifier = ((Tagable) target).tag();
        final boolean fragmentPopped = fragmentManager.popBackStackImmediate(tagIdentifier, 0);
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (!fragmentPopped && (null == fragmentManager.findFragmentByTag(tagIdentifier))) {
            if (clearTop) fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentTransaction.replace(R.id.base_fragment_container, target, tagIdentifier);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.addToBackStack(tagIdentifier);
            fragmentTransaction.commit();
        }

        return true;
    }

    public static boolean openFragment(final Activity activity, final Fragment target) {
        return openFragment(activity, target, false);
    }

    public static boolean openDrawerFragment(final Activity activity, final Fragment target) {
        return openFragment(activity, target, true);
    }

    public static boolean navigateBack(final Activity activity) {
        // No place
        if (null == activity) return false;

        // Check back stack
        final FragmentManager fragmentManager = activity.getFragmentManager();
        return fragmentManager.getBackStackEntryCount() > 1 && fragmentManager.popBackStackImmediate();
    }

    public static boolean isPresented(final Activity activity, final Fragment target) {
        // It isn't our client
        if (!(target instanceof Tagable)) return false;

        return isPresented(activity, (Tagable) target);
    }

    public static boolean isPresented(final Activity activity, final Tagable target) {
        // No place
        if (null == activity) return false;

        final FragmentManager fragmentManager = activity.getFragmentManager();
        return (null != fragmentManager.findFragmentByTag(target.tag()));
    }

    public static Fragment topFragment(final Activity activity) {
        // No place
        if (null == activity) return null;

        final FragmentManager fragmentManager = activity.getFragmentManager();
        return fragmentManager.findFragmentById(R.id.base_fragment_container);
    }

}
