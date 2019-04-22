package tech.linjiang.pandora.ui.connector;

import androidx.appcompat.widget.SearchView;

/**
 * Created by linjiang on 07/06/2018.
 */

public class SimpleOnQueryTextListener implements SearchView.OnQueryTextListener {
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
