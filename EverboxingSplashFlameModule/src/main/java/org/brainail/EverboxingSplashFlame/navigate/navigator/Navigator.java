package org.brainail.EverboxingSplashFlame.navigate.navigator;

import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;

import org.brainail.EverboxingHardyDialogs.BaseDialogSpecification;
import org.brainail.EverboxingHardyDialogs.HardyDialogFragment;
import org.brainail.EverboxingSplashFlame.di.PerActivity;
import org.brainail.EverboxingSplashFlame.navigate.navigator.action.NavigatorAction;
import org.brainail.EverboxingSplashFlame.navigate.navigator.action.NavigatorActionFactory;
import org.brainail.EverboxingSplashFlame.ui.activities.FlamePreviewActivity;
import org.brainail.EverboxingSplashFlame.ui.activities.common.AboutActivity;
import org.brainail.EverboxingSplashFlame.ui.activities.common.HomeActivity;
import org.brainail.EverboxingSplashFlame.ui.activities.common.SettingsActivity;

import javax.inject.Singleton;

import static org.brainail.EverboxingSplashFlame.ui.fragments.FlamePreviewFragment.Extras.PREV_FLAME_FILE_PATH;

/**
 * Helper class to navigate through the app (like opening of different parts/pages/screens) <br/>
 *
 * @author emalyshev
 */
@Singleton
@PerActivity
public class Navigator {

    private final Context mContext;

    public Navigator (final Context context) {
        mContext = context;
    }

    @CheckResult
    public NavigatorAction homeScreen () {
        final Intent intent = new Intent (mContext, HomeActivity.class);
        return NavigatorActionFactory.create (mContext, intent);
    }

    @CheckResult
    public NavigatorAction flameFilePreview (final String filePath) {
        final Intent intent = new Intent (mContext, FlamePreviewActivity.class);
        intent.putExtra (PREV_FLAME_FILE_PATH, filePath);
        return NavigatorActionFactory.create (mContext, intent);
    }

    @CheckResult
    public NavigatorAction settingsScreen () {
        final Intent intent = new Intent (mContext, SettingsActivity.class);
        return NavigatorActionFactory.create (mContext, intent);
    }

    @CheckResult
    public NavigatorAction aboutScreen () {
        Intent intent = new Intent (mContext, AboutActivity.class);
        return NavigatorActionFactory.create (mContext, intent);
    }

    @CheckResult
    public NavigatorAction openUrlAction (@Nullable final String url) {
        if (url == null) {
            return NavigatorActionFactory.emptyAction ();
        }

        Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse (url));
        return NavigatorActionFactory.create (mContext, intent);
    }

    @CheckResult
    public NavigatorAction openMailToAction (@Nullable MailTo mailto) {
        if (mailto == null) {
            return NavigatorActionFactory.emptyAction ();
        }

        Intent intent = new Intent (Intent.ACTION_SEND);
        intent.setType ("message/rfc822");
        intent.putExtra (Intent.EXTRA_EMAIL, new String[] {mailto.getTo ()});
        intent.putExtra (Intent.EXTRA_SUBJECT, mailto.getSubject ());
        intent.putExtra (Intent.EXTRA_CC, mailto.getCc ());
        intent.putExtra (Intent.EXTRA_TEXT, mailto.getBody ());

        return NavigatorActionFactory.create (mContext, intent);
    }

    @CheckResult
    public NavigatorAction openAppInMarketAction (final @Nullable String packageName) {
        if (packageName == null) {
            return NavigatorActionFactory.emptyAction ();
        }

        final Uri playUri = Uri.parse ("market://details?id=" + packageName);
        final Intent playIntent = new Intent (Intent.ACTION_VIEW, playUri);

        final Uri webPlayUri = Uri.parse ("https://play.google.com/store/apps/details?id=" + packageName);
        final Intent webPlayIntent = new Intent (Intent.ACTION_VIEW, webPlayUri);

        return NavigatorActionFactory.create (mContext, playIntent, webPlayIntent);
    }

    @CheckResult
    public NavigatorAction openAppAction (final @Nullable String packageName) {
        if (packageName == null) {
            return NavigatorActionFactory.emptyAction ();
        }

        if (canLaunchApp (packageName)) {
            final Intent launchIntent = mContext.getPackageManager ().getLaunchIntentForPackage (packageName);
            return NavigatorActionFactory.create (mContext, launchIntent);
        } else {
            return openAppInMarketAction (packageName);
        }
    }

    @CheckResult
    public boolean canLaunchApp (final @Nullable String packageName) {
        return null != mContext.getPackageManager ().getLaunchIntentForPackage (packageName);
    }

    public HardyDialogFragment showDialog (BaseDialogSpecification.Builder dialogSpecification) {
        return dialogSpecification.show (mContext);
    }

}
