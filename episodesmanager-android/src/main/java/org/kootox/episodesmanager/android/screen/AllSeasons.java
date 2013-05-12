package org.kootox.episodesmanager.android.screen;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import org.kootox.episodesmanager.android.DatabaseHelper;
import org.kootox.episodesmanager.android.EpisodeManagerListView;
import org.kootox.episodesmanager.android.R;
import org.kootox.episodesmanager.android.utility.SeasonViewBinder;

/**
 * User: couteau
 * Date: 30 sept. 2010
 */
public class AllSeasons extends EpisodeManagerListView {

    private final Long id;

    public AllSeasons(Long id){
        super();
        this.id = id;
    }

    public void fillData() {
        final Dialog waiting = ProgressDialog.show(
                AllSeasons.this, "",
                "Loading. Please wait...", true);

        final Runnable runInUIThread = new Runnable() {
            public void run() {
                String[] from = new String[]{
                        DatabaseHelper.SHOW_TITLE, DatabaseHelper.SHOW_ID,
                        DatabaseHelper.SHOW_ID
                };

                int[] to = new int[]{
                        R.id.seasonName, R.id.acquireSeason, R.id.watchSeason
                };

                // Now create an adapter and set it to display using our row
                SimpleCursorAdapter mainScreenAdapter =
                        new SimpleCursorAdapter(AllSeasons.this,
                                R.layout.all_seasons_row, episodes,
                                from, to);

                //Create a view binder to display airing date correctly
                SimpleCursorAdapter.ViewBinder viewBinder =
                        new SeasonViewBinder(episodes, AllSeasons.this, dbHelper,
                                mainScreenAdapter);

                mainScreenAdapter.setViewBinder(viewBinder);

                setListAdapter(mainScreenAdapter);
            }
        };

        new Thread() {
            @Override
            public void run() {

                // Get all of the shows from the database and create the item list
                episodes = dbHelper.getAllSeasons(id);
                startManagingCursor(episodes);

                waiting.dismiss();
                uiThreadCallback.post(runInUIThread);
            }
        }.start();

    }

    protected void onListItemClick(ListView l, View v, int position, long id) {

        String title = dbHelper.getShowTitle(id);

        //get back watch checkbox
        CheckBox watch = (CheckBox) v.findViewById(R.id.watchShow);

        //get back acquire checkbox
        CheckBox acquire = (CheckBox) v.findViewById(R.id.acquireShow);

        if (!dbHelper.isWatchedShow(title) && watch.isChecked()) {
            dbHelper.watchShow(id);
        }

        fillData();

    }

}