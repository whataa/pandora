package tech.linjiang.pandora.ui.connector;

import android.view.MenuItem;

/**
 * Created by linjiang on 07/06/2018.
 */

public class SimpleOnActionExpandListener implements MenuItem.OnActionExpandListener {
    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        // true means menuView can expand
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        // true means menuView can collapse
        return true;
    }
}
