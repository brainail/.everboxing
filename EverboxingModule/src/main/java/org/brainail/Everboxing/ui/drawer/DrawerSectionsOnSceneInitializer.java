package org.brainail.Everboxing.ui.drawer;

import com.malinskiy.materialicons.Iconify;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.ui.activities.SectionedDrawerActivity;
import org.brainail.Everboxing.ui.activities.SettingsActivity;
import org.brainail.Everboxing.ui.fragments.FragmentChromeCustomTabs;
import org.brainail.Everboxing.ui.fragments.FragmentSex;
import org.brainail.Everboxing.ui.fragments.FragmentUnderlay;
import org.brainail.Everboxing.ui.views.BaseIcon;
import org.brainail.Everboxing.utils.tool.ToolColor;
import org.brainail.Everboxing.utils.tool.ToolResources;

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
public final class DrawerSectionsOnSceneInitializer {

    public interface IDrawerSectionInitializer {
        void initialize(final SectionedDrawerActivity scene);
        boolean isTransparentable();
    }

    public static final IDrawerSectionInitializer HOME = new IDrawerSectionInitializer() {

        @Override public void initialize(final SectionedDrawerActivity scene) {
            scene
                    // Section
                    .addDrawerSection (
                            new DrawerSection (scene, DrawerSection.LayoutType.NORMAL)
                                    .withTitle (ToolResources.string (R.string.fr_over_fr))
                                    .withName (ToolResources.string (R.string.fr_over_fr))
                                    .withIcon (BaseIcon.defIcon (scene, Iconify.IconValue.md_android))
                                    .withTarget (new FragmentUnderlay ())
                                    .withSectionColor (ToolResources.retrievePrimaryColor (scene))
                                    .withNotifications (10)
                                    .withNotificationsLimit (20)
                    )
                    // Divider
                    // .addDrawerDivider ()
                    // Subheader
                    .addDrawerSubheader ("Sample subheader ± section")
                    // Section
                    .addDrawerSection (
                            new DrawerSection (scene, DrawerSection.LayoutType.NORMAL)
                                    .withTitle (ToolResources.string (R.string.fr_sex))
                                    .withName (ToolResources.string (R.string.fr_sex))
                                    .withIcon (BaseIcon.defIcon (scene, Iconify.IconValue.md_people))
                                    .withTarget (new FragmentSex ())
                                    .withSectionColor (ToolColor.by (R.color.md_blue_400))
                                    .withNotifications (20)
                                    .withNotificationsLimit (18)
                    )
                    // Custom tabs
                    .addDrawerSubheader ("Chrome custom tabs")
                    .addDrawerSection (
                            new DrawerSection (scene, DrawerSection.LayoutType.NORMAL)
                                    .withTitle ("Chrome custom tabs")
                                    .withName ("Chrome custom tabs")
                                    .withIcon (BaseIcon.defIcon (scene, Iconify.IconValue.md_laptop_chromebook))
                                    .withTarget (new FragmentChromeCustomTabs ())
                                    .withSectionColor (ToolResources.retrievePrimaryColor (scene))
                    )
                    // New window section
                    // App settings
                    .addDrawerSection (
                            new DrawerSection (scene, DrawerSection.LayoutType.NORMAL)
                                    .withName (ToolResources.string (R.string.scene_settings))
                                    .withLocationType (DrawerSection.LocationType.HELP)
                                    .withIcon (BaseIcon.defIcon (scene, Iconify.IconValue.md_settings))
                                    .withTarget (SettingsActivity.class)
                    );
        }

        @Override public boolean isTransparentable() {
            return false;
        }

    };

}
