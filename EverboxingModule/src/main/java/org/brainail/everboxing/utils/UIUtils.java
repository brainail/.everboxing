package org.brainail.Everboxing.utils;

import net.simonvt.menudrawer.MenuDrawer;

/**
 * User: brainail<br/>
 * Date: 06.07.14<br/>
 * Time: 16:19<br/>
 */
public final class UIUtils {

    public static boolean isMenuDrawerOpened(final int drawerState) {
        return drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING;
    }

}
