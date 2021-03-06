package org.brainail.EverboxingSplashFlame.ui.activities.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;

import org.brainail.EverboxingSplashFlame.JApplication;
import org.brainail.EverboxingSplashFlame.api.ClientApi;
import org.brainail.EverboxingSplashFlame.api.google.PlayServices;
import org.brainail.EverboxingSplashFlame.di.ActivityContext;
import org.brainail.EverboxingSplashFlame.di.HasComponent;
import org.brainail.EverboxingSplashFlame.di.component.ActivityComponent;
import org.brainail.EverboxingSplashFlame.di.component.AppComponent;
import org.brainail.EverboxingSplashFlame.di.module.ActivityModule;
import org.brainail.EverboxingSplashFlame.navigator.Navigator;
import org.brainail.EverboxingSplashFlame.ui.notice.NoticeBar;
import org.brainail.EverboxingSplashFlame.ui.notice.NoticeController;
import org.brainail.EverboxingSplashFlame.utils.chrome.CustomTabsSceneHelper;
import org.brainail.EverboxingSplashFlame.utils.manager.SettingsManager;
import org.brainail.EverboxingSplashFlame.utils.manager.ThemeManager;
import org.brainail.EverboxingSplashFlame.utils.tool.ToolToolbar;
import org.brainail.EverboxingTools.utils.tool.ToolFonts;
import org.brainail.EverboxingTools.utils.tool.ToolFragments;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
public abstract class BaseActivity
        extends AppCompatActivity
        implements FragmentManager.OnBackStackChangedListener, NoticeBar.OnActionCallback,
        NoticeBar.OnVisibilityCallback, ClientApi.Supportable, HasComponent<ActivityComponent> {

    private ActivityComponent mComponent;

    // Primary Toolbar
    protected Toolbar mPrimaryToolbar;

    // Theme. I use null to define that this is full recreating
    private ThemeManager.AppTheme mTheme = null;

    // APIs
    protected List<ClientApi> mClientApis;
    protected PlayServices mPlayServices;

    // Chrome tabs stuff
    private CustomTabsSceneHelper mCustomTabsSceneHelper;

    @Inject
    @ActivityContext
    protected Navigator mNavigator;
    @Inject
    @ActivityContext
    protected ToolToolbar mToolbarTuner;
    // @Inject
    // EventBus mGlobalBus;

    @SuppressWarnings ("unchecked")
    public <T extends View> T bindView (final int id) {
        return (T) bindView (id, null);
    }

    @SuppressWarnings ("unchecked")
    public <T extends View> T bindView (
            final int id,
            final View.OnClickListener clickAction) {

        return (T) bindView (id, clickAction, null);
    }

    @SuppressWarnings ("unchecked")
    public <T extends View> T bindView (
            final int id,
            final View.OnClickListener clickAction,
            final View.OnLongClickListener longClickAction) {

        return (T) bindView (id, clickAction, longClickAction, null);
    }

    @SuppressWarnings ("unchecked")
    public <T extends View> T bindView (
            final int id,
            final View.OnClickListener clickAction,
            final View.OnLongClickListener longClickAction,
            final View.OnTouchListener touchAction) {

        final T view = (T) super.findViewById (id);

        if (null != view && null != clickAction) {
            view.setOnClickListener (clickAction);
        }

        if (null != view && null != longClickAction) {
            view.setOnLongClickListener (longClickAction);
        }

        if (null != view && null != touchAction) {
            view.setOnTouchListener (touchAction);
        }

        return view;
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        setupComponent (JApplication.get (this).getComponent ());
        injectMembers (JApplication.get (this).getComponent ());

        // Window background causes an useless overdraw.
        // Nullifying the background removes that overdraw.
        getWindow ().setBackgroundDrawable (null);

        // Monitor fragments
        getSupportFragmentManager ().addOnBackStackChangedListener (this);

        // Init & check theme
        mTheme = ThemeManager.checkOnCreate (this, mTheme);

        // Create notice controller
        NoticeController.from (this).onCreate (savedInstanceState);

        // Init content view
        initContent ();

        // Init toolbar aka action bar
        initToolbar ();

        // Init all APIs (Play, ...)
        initAPIs (savedInstanceState);

        // Chrome tabs stuff
        mCustomTabsSceneHelper = new CustomTabsSceneHelper ();
        mCustomTabsSceneHelper.onCreateScene (self ());
    }

    /**
     * Override this method to create custom component instance. Super implementation <b>must</b> be called first.
     * Note that injection is not allowed here. See {@link BaseActivity#injectMembers(AppComponent)}.
     *
     * @param appComponent default application component, for convenience
     */
    protected void setupComponent (AppComponent appComponent) {
        mComponent = appComponent.plus (new ActivityModule (this));
    }

    /**
     * Override this method to apply field injection. Usually, you shouldn't call super implementation, because
     * fields in base classes are injected automatically.
     *
     * @param appComponent default activity component, for convenience
     */
    protected void injectMembers (AppComponent appComponent) {
        getComponent ().inject (this);
    }

    @Override
    public ActivityComponent getComponent () {
        return mComponent;
    }

    private void initAPIs (Bundle savedInstanceState) {
        // All
        mClientApis = new ArrayList<> ();

        // Google Play Services
        mPlayServices = new PlayServices (this, savedInstanceState);
        mClientApis.add (mPlayServices);
    }

    private void initContent () {
        final Integer resourceId = getLayoutResourceId ();
        if (null != resourceId) {
            setContentView (resourceId);
        }
    }

    private void initToolbar () {
        final Integer resourceId = getPrimaryToolbarLayoutResourceId ();
        if (null != resourceId) {
            mPrimaryToolbar = findViewById(resourceId);
        }

        if (null != mPrimaryToolbar) {
            setSupportActionBar (mPrimaryToolbar);
            // noinspection ConstantConditions
            getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
            getSupportActionBar ().setHomeButtonEnabled (true);
        }
    }

    @Override
    protected void attachBaseContext (final Context baseContext) {
        // Attach the Calligraphy
        super.attachBaseContext (CalligraphyContextWrapper.wrap (baseContext));
    }

    protected String getDefaultFont () {
        return ToolFonts.RobotoFonts.ASSETS_LIGHT;
    }

    protected Integer getLayoutResourceId () {
        return null;
    }

    public Toolbar getPrimaryToolbar () {
        return mPrimaryToolbar;
    }

    protected Integer getPrimaryToolbarLayoutResourceId () {
        return null;
    }

    public final AppCompatActivity self () {
        return this;
    }

    @Override
    protected void onStart () {
        NoticeController.from (this).showScene ();

        // Start APIs
        for (final ClientApi api : mClientApis) {
            if (api.useOn (this)) api.onStart ();
        }

        super.onStart ();

        // Register to catch events
        // mGlobalBus.register (this);

        // Chrome tabs stuff
        mCustomTabsSceneHelper.onStartScene (self ());
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        NoticeController.from (this).onSaveInstanceState (outState);

        // Save APIs
        for (final ClientApi api : mClientApis) {
            if (api.useOn (this)) api.onSave (outState);
        }

        super.onSaveInstanceState (outState);
    }

    @Override
    protected void onStop () {
        NoticeController.from (this).hideScene ();

        // Stop APIs
        for (final ClientApi api : mClientApis) {
            if (api.useOn (this)) api.onStop ();
        }

        super.onStop ();

        // Unregister to stop catching
        // mGlobalBus.unregister (this);

        // Chrome tabs stuff
        mCustomTabsSceneHelper.onStopScene (self ());
    }

    @Override
    protected void onResume () {
        ThemeManager.checkOnResume (this, mTheme);
        super.onResume ();
    }

    @Override
    public void onBackPressed () {
        if (!ToolFragments.navigateBack (this)) {
            supportFinishAfterTransition ();
        }
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem menuItem) {
        int menuItemId = menuItem.getItemId ();

        if (android.R.id.home == menuItemId) {
            navigateUpFromSameTask ();
            return true;
        }

        return super.onOptionsItemSelected (menuItem);
    }

    private void navigateUpFromSameTask () {
        finish ();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBackStackChanged () {
        // Restore some data for toolbar
        updateToolbarColor ();
        updateToolbarTitle ();
        updateToolbarSubtitle ();
        // We should update here options menu
        // to refresh self after recreate (change theme)
        // noinspection ConstantConditions
        getSupportActionBar ().invalidateOptionsMenu ();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onResumeFragments () {
        super.onResumeFragments ();

        // Restore some data for toolbar
        updateToolbarColor ();
        updateToolbarTitle ();
        updateToolbarSubtitle ();
        // We should update here options menu
        // to refresh self after recreate (change theme)
        if (null != getSupportActionBar ()) {
            getSupportActionBar ().invalidateOptionsMenu ();
        }
    }

    protected void updateToolbarColor () {
        mToolbarTuner.updateToolbarColor (null);
    }

    protected void updateToolbarTitle () {
        mToolbarTuner.updateToolbarTitle (null);
    }

    protected void updateToolbarSubtitle () {
        mToolbarTuner.updateToolbarSubtitle (null);
    }

    @Override
    public void onDetachedFromWindow () {
        super.onDetachedFromWindow ();

        // We don't want to monitor fragments after
        getSupportFragmentManager ().removeOnBackStackChangedListener (this);
    }

    @Override
    protected void onDestroy () {
        NoticeController.byeBye (this);

        // Destroy APIs
        for (final ClientApi api : mClientApis) {
            if (api.useOn (this)) api.onDestroy ();
        }

        // Chrome tabs stuff
        mCustomTabsSceneHelper.onDestroyScene (self ());

        super.onDestroy ();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        // Handle result via APIs
        for (final ClientApi api : mClientApis) {
            if (api.handleOnResult (requestCode, resultCode, data)) return;
        }

        super.onActivityResult (requestCode, resultCode, data);
    }

    @Override
    public void onShow (final String token, final int activeSize) {
        // Check token to define your future actions
    }

    @Override
    public void onMute (final String token, final int activeSize) {
        // Check token to define your future actions
    }

    @Override
    public void onAction (final String token) {
        // Check token to define your future actions
    }

    @Override
    public boolean usePlayServices () {
        return SettingsManager.getInstance ().retrieveSyncDataFlag ();
    }

    @Override
    public ClientApi<GoogleApiClient> getPlayServices () {
        return mPlayServices;
    }

    public void onPlayErrorDismissed () {
        // To reset error flag
        if (null != mPlayServices) mPlayServices.onErrorDismissed ();
    }

    public void openUrl (final String url) {
        // Chrome tabs stuff
        mCustomTabsSceneHelper.openCustomTab (self (), url, null, null);
    }

}
