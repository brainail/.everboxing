package org.brainail.Everboxing.ui.activities;

import android.os.Bundle;
import android.view.View;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.ui.drawer.DrawerSectionsControllerFactory;
import org.brainail.Everboxing.ui.drawer.DrawerSection;
import org.brainail.Everboxing.ui.drawer.IDrawerSectionsController;
import org.brainail.Everboxing.utils.tool.ToolColor;

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
public class SectionedDrawerActivity extends BaseDrawerActivity {

    private IDrawerSectionsController mDrawerSectionsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrawerSectionsController = DrawerSectionsControllerFactory.create(this);
    }

    public void addDrawerSection(final DrawerSection section) {
        mDrawerSectionsController.addSection(section);
    }

    public void addDrawerDivider() {
        mDrawerSectionsController.addDivider();
    }

    public void addDrawerSubheader(final String title) {
        mDrawerSectionsController.addSubheader(title);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        super.onDrawerSlide(drawerView, slideOffset);
        // FIXME#brainail: move to controller?
        int DRAWER_COLOR = ToolColor.by(R.color.menu_drawer_default);
        drawerView.setBackgroundColor(ToolColor.withAlpha(DRAWER_COLOR, (1 - slideOffset) * 100));
        drawerView.findViewById(R.id.drawer_menu_user_cover).setAlpha(slideOffset);
    }

}
