package org.brainail.Everboxing.ui.views;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import org.brainail.Everboxing.R;
import org.brainail.Everboxing.utils.ThemeManager;

/**
 * This file is part of Everboxing modules. <br/><br/>
 *
 * &copy; 2014 brainail <br/><br/>
 *
 * This program is free software: you can redistribute it and/or modify <br/>
 * it under the terms of the GNU General Public License as published by <br/>
 * the Free Software Foundation, either version 3 of the License, or <br/>
 * (at your option) any later version. <br/><br/>
 *
 * This program is distributed in the hope that it will be useful, <br/>
 * but WITHOUT ANY WARRANTY; without even the implied warranty of <br/>
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the <br/>
 * GNU General Public License for more details. <br/>
 *
 * You should have received a copy of the GNU General Public License <br/>
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
public class ThemeChooser extends DialogFragment implements MaterialDialog.ListCallback {

    public static final String MANAGER_TAG = "org.brainail.Everboxing.ThemeChooserTag";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.settings_change_theme_dialog_title))
                .items(ThemeManager.AppTheme.themes(getActivity()))
                .itemsCallback(this)
                .build();
    }

    @Override
    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
        ThemeManager.changeTheme(getActivity(), ThemeManager.AppTheme.values() [which]);
    }

}