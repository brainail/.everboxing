package org.brainail.EverboxingLexis.ui.drawer;

import android.support.graphics.drawable.AnimatedVectorDrawableCompat;

import com.malinskiy.materialicons.Iconify;

import org.brainail.EverboxingLexis.R;
import org.brainail.EverboxingLexis.ui.activities.SectionedDrawerActivity;
import org.brainail.EverboxingLexis.ui.activities.SettingsActivity;
import org.brainail.EverboxingLexis.ui.views.BaseIcon;
import org.brainail.EverboxingTools.utils.tool.ToolFragments;
import org.brainail.EverboxingLexis.utils.tool.ToolResources;

import itkach.aard2.ui.fragments.LexisBookmarksFragment;
import itkach.aard2.ui.fragments.LexisDictionariesFragment;
import itkach.aard2.ui.fragments.LexisHistoryFragment;
import itkach.aard2.ui.fragments.LexisLookupFragment;

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

    public static final int LUCKY_SECTION_POSITION = 4;

    public static final class SectionTag {
        public static final String FEEDBACK_RATING = "feedback/rating";
    }

    public interface IDrawerSectionInitializer {
        void initialize (final SectionedDrawerActivity scene);
        boolean isTransparentable ();
    }

    public static final IDrawerSectionInitializer HOME = new IDrawerSectionInitializer () {

        @Override
        public void initialize (final SectionedDrawerActivity scene) {
            // Lookup
            scene.addDrawerSection (
                    new DrawerSection (scene, DrawerSection.LayoutType.NORMAL)
                            .withTitle ("\u00A0")
                            .withName (ToolResources.string (R.string.section_lookup))
                            .withIcon (BaseIcon.defIcon (scene, Iconify.IconValue.zmdi_search))
                            .withTarget (ToolFragments.FragmentCreator.from (LexisLookupFragment.class))
                            .withSectionColor (ToolResources.retrievePrimaryColor (scene))
            );

            // Bookmarks
            scene.addDrawerSection (
                    new DrawerSection (scene, DrawerSection.LayoutType.NORMAL)
                            .withTitle (ToolResources.string (R.string.section_bookmarks))
                            .withName (ToolResources.string (R.string.section_bookmarks))
                            .withIcon (BaseIcon.defIcon (scene, Iconify.IconValue.zmdi_bookmark_outline))
                            .withTarget (ToolFragments.FragmentCreator.from (LexisBookmarksFragment.class))
                            .withSectionColor (ToolResources.retrievePrimaryColor (scene))
            );

            // History
            scene.addDrawerSection (
                    new DrawerSection (scene, DrawerSection.LayoutType.NORMAL)
                            .withTitle (ToolResources.string (R.string.section_history))
                            .withName (ToolResources.string (R.string.section_history))
                            .withIcon (BaseIcon.defIcon (scene, Iconify.IconValue.zmdi_time_restore))
                            .withTarget (ToolFragments.FragmentCreator.from (LexisHistoryFragment.class))
                            .withSectionColor (ToolResources.retrievePrimaryColor (scene))
            );

            // Dictionaries
            scene.addDrawerSection (
                    new DrawerSection (scene, DrawerSection.LayoutType.NORMAL)
                            .withTitle (ToolResources.string (R.string.section_dictionaries))
                            .withName (ToolResources.string (R.string.section_dictionaries))
                            .withIcon (BaseIcon.defIcon (scene, Iconify.IconValue.zmdi_collection_text))
                            .withTarget (ToolFragments.FragmentCreator.from (LexisDictionariesFragment.class))
                            .withSectionColor (ToolResources.retrievePrimaryColor (scene))
            );

            // Random (position 4!)
            scene.addDrawerSection (
                    new DrawerSection (scene, DrawerSection.LayoutType.NORMAL)
                            .withName (ToolResources.string (R.string.section_lucky))
                            .withIcon (BaseIcon.controlIcon (scene, Iconify.IconValue.zmdi_shuffle))
                            .withTarget ((DrawerSectionCallback) scene)
            );

            // Feedback/Rating
            scene.addDrawerSection (
                    new DrawerSection (scene, DrawerSection.LayoutType.NORMAL)
                            .withName (ToolResources.string (R.string.drawer_item_help_us))
                            .withLocationType (DrawerSection.LocationType.HELP)
                            .withIcon (AnimatedVectorDrawableCompat.create(scene, R.drawable.ic_star_rating))
                            .withTag (SectionTag.FEEDBACK_RATING)
                            .withTarget ((DrawerSectionCallback) scene)
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
