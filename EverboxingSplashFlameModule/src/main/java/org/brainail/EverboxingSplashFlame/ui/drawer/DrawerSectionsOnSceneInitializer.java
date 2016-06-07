package org.brainail.EverboxingSplashFlame.ui.drawer;

import com.malinskiy.materialicons.Iconify;

import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame.ui.activities.SectionedDrawerActivity;
import org.brainail.EverboxingSplashFlame.ui.activities.SettingsActivity;
import org.brainail.EverboxingSplashFlame.ui.fragments.FavouriteFragment;
import org.brainail.EverboxingSplashFlame.ui.fragments.FlamePropertiesFragment;
import org.brainail.EverboxingSplashFlame.ui.views.BaseIcon;
import org.brainail.EverboxingSplashFlame.utils.tool.ToolResources;
import org.brainail.EverboxingTools.utils.tool.ToolFragments;

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

    public static final int LUCKY_SECTION_POSITION = 2;

    public interface IDrawerSectionInitializer {
        void initialize (final SectionedDrawerActivity scene);
        boolean isTransparentable ();
    }

    public static final IDrawerSectionInitializer HOME = new IDrawerSectionInitializer () {

        @Override
        public void initialize (final SectionedDrawerActivity scene) {
            // Flamer
            scene.addDrawerSection (
                    new DrawerSection (scene, DrawerSection.LayoutType.NORMAL)
                            .withTitle (ToolResources.string (R.string.section_flamer))
                            .withName (ToolResources.string (R.string.section_flamer))
                            .withIcon (BaseIcon.defIcon (scene, Iconify.IconValue.zmdi_fire))
                            .withTarget (ToolFragments.FragmentCreator.from (FlamePropertiesFragment.class))
                            .withSectionColor (ToolResources.retrievePrimaryColor (scene))
            );

            // Favourite
            scene.addDrawerSection (
                    new DrawerSection (scene, DrawerSection.LayoutType.NORMAL)
                            .withTitle (ToolResources.string (R.string.section_favourite))
                            .withName (ToolResources.string (R.string.section_favourite))
                            .withIcon (BaseIcon.defIcon (scene, Iconify.IconValue.zmdi_favorite))
                            .withTarget (ToolFragments.FragmentCreator.from (FavouriteFragment.class))
                            .withSectionColor (ToolResources.retrievePrimaryColor (scene))
            );

            // Settings
            scene.addDrawerSection (
                    new DrawerSection (scene, DrawerSection.LayoutType.NORMAL)
                            .withName (ToolResources.string (R.string.settings_activity_title))
                            .withLocationType (DrawerSection.LocationType.HELP)
                            .withIcon (BaseIcon.controlIcon (scene, Iconify.IconValue.zmdi_settings))
                            .withTarget (SettingsActivity.class)
            );
        }

        @Override
        public boolean isTransparentable () {
            return false;
        }

    };

}
