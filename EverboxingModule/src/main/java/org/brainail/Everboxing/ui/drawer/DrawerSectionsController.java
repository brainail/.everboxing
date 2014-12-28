package org.brainail.Everboxing.ui.drawer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.tool.ToolView;

import java.util.LinkedList;

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

    private final LinearLayout mPrimarySections;
    private final LinearLayout mHelpSections;
    private final View mHelpSectionsSeparator;

    private final LinkedList<DrawerSection> mPrimaryDrawerSections = new LinkedList<DrawerSection>();
    private final LinkedList<DrawerSection> mHelpDrawerSections = new LinkedList<DrawerSection>();

    private final Context mContext;

    public DrawerSectionsController(final Activity scene) {
        mPrimarySections = (LinearLayout) scene.findViewById(R.id.drawer_menu_primary_sections);
        mHelpSections = (LinearLayout) scene.findViewById(R.id.drawer_menu_help_sections);
        mHelpSectionsSeparator = scene.findViewById(R.id.drawer_menu_help_sections_separator);
        mContext = scene;
    }

    @Override
    public void addDivider() {
        addDivider(R.layout.drawer_section_divider);
    }

    @Override
    public void addSubheader(final String titleText) {
        final View subheader = getInflater().inflate(R.layout.drawer_section_subheader, mPrimarySections, false);
        final TextView title = (TextView) subheader.findViewById(R.id.drawer_section_subheader_text);
        title.setText(titleText);

        addDivider(R.layout.drawer_section_divider_subheader);
        mPrimarySections.addView(subheader);
    }

    private void addDivider(final int layoutId) {
        mPrimarySections.addView(ToolView.linearWrapper(mContext, layoutId));
    }

    @Override
    public void addSection(final DrawerSection section) {
        section.withController(this);

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

    @Override
    public void selectSection(final DrawerSection section) {
        final int position = section.getPosition();

        for (final DrawerSection primarySection : mPrimaryDrawerSections) {
            if (position != primarySection.getPosition()) {
                primarySection.unselect();
            }
        }

        for (final DrawerSection helpSection : mHelpDrawerSections) {
            if (position != helpSection.getPosition()) {
                helpSection.unselect();
            }
        }
    }

    @Override
    public void unselectSection(final DrawerSection section) {
        // FIXME: Add some light implementation ..
    }

    private void addPrimarySection(final DrawerSection section) {
        section.withPosition(mPrimaryDrawerSections.size());
        mPrimaryDrawerSections.add(section);
        mPrimarySections.addView(section.selfView());
        notifySectionsChanged();
    }

    private void addHelpSection(final DrawerSection section) {
        section.withPosition(-mHelpDrawerSections.size() - 1);
        mHelpDrawerSections.add(section);
        mHelpSections.addView(section.selfView());
        notifySectionsChanged();
    }

    private void notifySectionsChanged() {
        mHelpSectionsSeparator.setVisibility(mHelpDrawerSections.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private LayoutInflater getInflater() {
        return LayoutInflater.from(mContext);
    }

}
