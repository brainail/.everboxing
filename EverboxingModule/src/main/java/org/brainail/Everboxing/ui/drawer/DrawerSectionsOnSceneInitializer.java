package org.brainail.Everboxing.ui.drawer;

import android.graphics.Color;

import org.brainail.Everboxing.ui.activities.SectionedDrawerActivity;
import org.brainail.Everboxing.ui.activities.SettingsActivity;

import static org.brainail.Everboxing.ui.drawer.DrawerSection.LayoutType;
import static org.brainail.Everboxing.ui.drawer.DrawerSection.LocationType;

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
public class DrawerSectionsOnSceneInitializer {

    public static void initHome(final SectionedDrawerActivity scene) {
        scene.addDrawerSection(new DrawerSection(scene).withTitle("Section One"));
        scene.addDrawerSection(new DrawerSection(scene).withTitle("Section Two"));

        scene.addDrawerDivider();

        scene.addDrawerSection(
                new DrawerSection(scene, LayoutType.NORMAL)
                        .withTitle("Sender")
                        .withIcon(scene.getResources().getDrawable(android.R.drawable.ic_menu_send))
                        .withNotifications(10)
        );

        scene.addDrawerSection(
                new DrawerSection(scene, LayoutType.NORMAL)
                        .withTitle("Today")
                        .withIcon(scene.getResources().getDrawable(android.R.drawable.ic_menu_today))
                        .withNotifications(1000)
                        .withSectionColor(Color.parseColor("#2196f3"))
        );

        scene.addDrawerSubheader("Privacy");

        scene.addDrawerSection(
                new DrawerSection(scene)
                        .withTitle("Sex everywhere")
                        .withNotifications(20)
                        .withNotificationsLimit(18)
                        .withSectionColor(Color.parseColor("#ff9800"))
        );

        scene.addDrawerSection(
                new DrawerSection(scene, LayoutType.NORMAL)
                        .withTitle("Settings")
                        .withLocationType(LocationType.HELP)
                        .withIcon(scene.getResources().getDrawable(android.R.drawable.ic_menu_manage))
                        .withTarget(SettingsActivity.class)
        );
    }

}
