package org.brainail.EverboxingTools.utils.manager.ui;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.view.View;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
class SystemUiHelperImplJB extends SystemUiHelperImplICS {

    SystemUiHelperImplJB(Activity activity, int level, int flags,
            SystemUiHelper.OnVisibilityChangeListener onVisibilityChangeListener) {
        super(activity, level, flags, onVisibilityChangeListener);
    }

    @Override
    protected int createShowFlags() {
        int flag = super.createShowFlags();

        if (mLevel >= SystemUiHelper.LEVEL_HIDE_STATUS_BAR) {
            flag |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

            if (mLevel >= SystemUiHelper.LEVEL_LEAN_BACK) {
                flag |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            }
        }

        return flag;
    }

    @Override
    protected int createHideFlags() {
        int flag = super.createHideFlags();

        if (mLevel >= SystemUiHelper.LEVEL_HIDE_STATUS_BAR) {
            flag |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;

            if (mLevel >= SystemUiHelper.LEVEL_LEAN_BACK) {
                flag |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            }
        }

        return flag;
    }

    @Override
    protected void onSystemUiShown() {
        if (mLevel == SystemUiHelper.LEVEL_LOW_PROFILE) {
            // Manually show the action bar when in low profile mode.
            ActionBar ab = mActivity.getActionBar();
            if (ab != null) {
                ab.show();
            }
        }

        setIsShowing(true);
    }

    @Override
    protected void onSystemUiHidden() {
        if (mLevel == SystemUiHelper.LEVEL_LOW_PROFILE) {
            // Manually hide the action bar when in low profile mode.
            ActionBar ab = mActivity.getActionBar();
            if (ab != null) {
                ab.hide();
            }
        }

        setIsShowing(false);
    }
}
