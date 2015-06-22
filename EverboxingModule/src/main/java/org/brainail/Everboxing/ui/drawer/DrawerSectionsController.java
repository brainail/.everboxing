package org.brainail.Everboxing.ui.drawer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.oauth.api.UserInfoApi;
import org.brainail.Everboxing.oauth.api.google.PlayServices;
import org.brainail.Everboxing.ui.activities.SectionedDrawerActivity;
import org.brainail.Everboxing.utils.callable.Tagable;
import org.brainail.Everboxing.utils.tool.ToolColor;
import org.brainail.Everboxing.utils.tool.ToolFragments;
import org.brainail.Everboxing.utils.tool.ToolStrings;
import org.brainail.Everboxing.utils.tool.ToolUI;

import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

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
final class DrawerSectionsController implements IDrawerSectionsController {

    @Optional @InjectView(R.id.drawer_menu_primary_sections) LinearLayout mPrimarySections;
    @Optional @InjectView(R.id.drawer_menu_help_sections) LinearLayout mHelpSections;
    @Optional @InjectView(R.id.drawer_menu_help_sections_separator) View mHelpSectionsSeparator;
    @Optional @InjectView(R.id.drawer_menu_primary) View mDrawerView;
    @Optional @InjectView(R.id.drawer_menu_user_cover) View mUserCoverArea;
    @Optional @InjectView(R.id.drawer_menu_user_name) TextView mUserName;
    @Optional @InjectView(R.id.drawer_menu_user_email) TextView mUserEmail;

    private DrawerSection mCurrentSection;

    private final LinkedList<DrawerSection> mPrimaryDrawerSections = new LinkedList<DrawerSection>();
    private final LinkedList<DrawerSection> mHelpDrawerSections = new LinkedList<DrawerSection>();

    private final SectionedDrawerActivity mScene;

    public DrawerSectionsController(final SectionedDrawerActivity scene) {
        mScene = scene;
        ButterKnife.inject(this, scene);
        updateUserInfo (PlayServices.formSettingsUserInfo ());
    }

    @Override
    public void addDivider() {
        addDivider (R.layout.drawer_section_divider);
    }

    @Override
    public void addSubheader(final String titleText) {
        final View subheader = getInflater().inflate(R.layout.drawer_section_subheader, mPrimarySections, false);
        final TextView title = ButterKnife.findById(subheader, R.id.drawer_section_subheader_text);
        title.setText(titleText);

        addDivider(R.layout.drawer_section_divider_subheader);
        mPrimarySections.addView (subheader);
    }

    private void addDivider(final int layoutId) {
        mPrimarySections.addView(ToolUI.linearWrapper(mScene, layoutId));
    }

    @Override
    public void addSection(final DrawerSection section) {
        section.withController (this);

        switch (section.getLocationType()) {
            case PRIMARY:
                addPrimarySection(section);
                break;
            case HELP:
                addHelpSection(section);
                break;
            default:
                break;
        }
    }

    // Section by position
    private DrawerSection section(final int position) {
        for (final DrawerSection primarySection : mPrimaryDrawerSections) {
            if (position == primarySection.getPosition()) return primarySection;
        }

        for (final DrawerSection helpSection : mHelpDrawerSections) {
            if (position == helpSection.getPosition()) return helpSection;
        }

        return null;
    }

    // Checks that our sections aren't presented somewhere
    private boolean anyPresented() {
        for (final DrawerSection primarySection : mPrimaryDrawerSections) {
            final Object sectionTarget = primarySection.getTarget();
            if (sectionTarget instanceof Tagable) {
                if (ToolFragments.isPresented(scene(), (Tagable) sectionTarget)) return true;
            }
        }

        for (final DrawerSection helpSection : mHelpDrawerSections) {
            final Object sectionTarget = helpSection.getTarget();
            if (sectionTarget instanceof Tagable) {
                if (ToolFragments.isPresented(scene(), (Tagable) sectionTarget)) return true;
            }
        }

        return false;
    }

    @Override
    // Section by fragment
    public DrawerSection section(final Fragment target) {
        // It isn't our client
        if (!(target instanceof Tagable)) return null;

        final String tagIdentifier = ((Tagable) target).tag();

        for (final DrawerSection primarySection : mPrimaryDrawerSections) {
            final Object sectionTarget = primarySection.getTarget();
            if (sectionTarget instanceof Tagable) {
                final String sectionTagIdentifier = ((Tagable) sectionTarget).tag();
                if (sectionTagIdentifier.equals (tagIdentifier)) return primarySection;
            }
        }

        for (final DrawerSection helpSection : mHelpDrawerSections) {
            final Object sectionTarget = helpSection.getTarget();
            if (sectionTarget instanceof Tagable) {
                final String sectionTagIdentifier = ((Tagable) sectionTarget).tag();
                if (sectionTagIdentifier.equals (tagIdentifier)) return helpSection;
            }
        }

        return null;
    }

    // Unselects sections by position of selected section
    private void selectSection(final int position) {
        mCurrentSection = section(position);

        for (final DrawerSection primarySection : mPrimaryDrawerSections) {
            if (position != primarySection.getPosition()) primarySection.unselect();
        }

        for (final DrawerSection helpSection : mHelpDrawerSections) {
            if (position != helpSection.getPosition()) helpSection.unselect();
        }
    }

    // Unselects sections by instance of selected section
    protected void selectSection(final DrawerSection section) {
        selectSection(section.getPosition());
    }

    // Unselects current section If necessary
    protected void unselectSection(final DrawerSection section) {
        mCurrentSection = (mCurrentSection == section) ? null : mCurrentSection;
    }

    @Override
    public SectionedDrawerActivity scene() {
        return mScene;
    }

    private void addPrimarySection(final DrawerSection section) {
        section.withPosition(mPrimaryDrawerSections.size());
        mPrimaryDrawerSections.add(section);
        mPrimarySections.addView(section.selfView());
    }

    private void addHelpSection(final DrawerSection section) {
        section.withPosition(-mHelpDrawerSections.size() - 1);
        mHelpDrawerSections.add (section);
        mHelpSections.addView (section.selfView ());
        notifySectionsChanged ();
    }

    private void notifySectionsChanged() {
        mHelpSectionsSeparator.setVisibility (mHelpDrawerSections.isEmpty () ? View.GONE : View.VISIBLE);
    }

    private LayoutInflater getInflater() {
        return LayoutInflater.from(mScene);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        if (mScene.sectionInitializer().isTransparentable()) {
            // Change alpha by offset
            mUserCoverArea.setAlpha(slideOffset);
            mDrawerView.setBackgroundColor(ToolColor.withAlpha(DRAWER_COLOR, (1 - slideOffset) * 100));
        }
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        // Do nothing
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        // Do nothing
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (null != mCurrentSection) {
            mCurrentSection.onDrawerClosed(drawerView);
        }
    }

    // See AppCompatActivity#onSaveInstanceState
    public void saveState(final Bundle state) {
        if (null != mCurrentSection) {
            state.putInt (DrawerSection.ExtraKey.POSITION, mCurrentSection.getPosition ());
        }
    }

    // See AppCompatActivity#onRestoreInstanceState
    public void restoreState(final Bundle state) {
        // Find & select section from saved state if necessary
        if (null == mCurrentSection && !anyPresented()) {
            final DrawerSection restoredSection = section(null != state ? state.getInt(DrawerSection.ExtraKey.POSITION, 0) : 0);
            if (null != restoredSection) restoredSection.select(true).openTarget(true);
        }
    }

    public void investigateFragmentsStack() {
        // Get current
        final DrawerSection investigatedSection = section(ToolFragments.topFragment(scene()));

        if (null != mCurrentSection && mCurrentSection != investigatedSection) {
            // Investigated section is preferable to current
            mCurrentSection.unselect();
        }

        if (null != investigatedSection && null == mCurrentSection) {
            // Just select via internal action because it's already here
            investigatedSection.select(true);
        }
    }

    @Override
    public void updateUserInfo (UserInfoApi userInfo) {
        if (null == userInfo || TextUtils.isEmpty (userInfo.email)) {
            mUserName.setText (ToolStrings.EMPTY);
            mUserEmail.setText (ToolStrings.EMPTY);
        } else {
            mUserName.setText (R.string.drawer_user_data_name_greetings);
            mUserEmail.setText (userInfo.email);
        }
    }

    @Override
    public void onAuthSucceeded (UserInfoApi userInfo) {
        updateUserInfo (userInfo);
    }

    @Override
    public void onUnauthSucceeded () {
        updateUserInfo (new UserInfoApi (ToolStrings.EMPTY));
    }

}
