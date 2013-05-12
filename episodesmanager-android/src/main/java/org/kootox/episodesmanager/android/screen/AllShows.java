package org.kootox.episodesmanager.android.screen;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import org.kootox.episodesmanager.android.DatabaseHelper;
import org.kootox.episodesmanager.android.EpisodeManagerListView;
import org.kootox.episodesmanager.android.R;
import org.kootox.episodesmanager.android.utility.ShowViewBinder;

/**
 * User: couteau
 * Date: 25 sept. 2010
 */
public class AllShows extends EpisodeManagerListView {

    public void fillData() {
        final Dialog waiting = ProgressDialog.show(
                AllShows.this, "",
                "Loading. Please wait...", true);

        final Runnable runInUIThread = new Runnable() {
            public void run() {
                String[] from = new String[]{
                        DatabaseHelper.SHOW_TITLE, DatabaseHelper.SHOW_ID,
                        DatabaseHelper.SHOW_ID
                };

                int[] to = new int[]{
                        R.id.show, R.id.acquireShow, R.id.watchShow
                };

                // Now create an adapter and set it to display using our row
                SimpleCursorAdapter mainScreenAdapter =
                        new SimpleCursorAdapter(AllShows.this,
                                R.layout.all_shows_row, episodes,
                                from, to);

                //Create a view binder to display airing date correctly
                SimpleCursorAdapter.ViewBinder viewBinder =
                        new ShowViewBinder(episodes, AllShows.this, dbHelper,
                                mainScreenAdapter);

                mainScreenAdapter.setViewBinder(viewBinder);

                setListAdapter(mainScreenAdapter);
            }
        };

        new Thread() {
            @Override
            public void run() {

                // Get all of the shows from the database and create the item list
                episodes = dbHelper.getAllShows();
                startManagingCursor(episodes);

                waiting.dismiss();
                uiThreadCallback.post(runInUIThread);
            }
        }.start();

    }

    protected void onListItemClick(ListView l, View v, int position, long id) {

        String title = dbHelper.getShowTitle(id);

        //get back watch checkbox
        CheckBox watch = (CheckBox)v.findViewById(R.id.watchShow);

        //get back acquire checkbox
        CheckBox acquire = (CheckBox)v.findViewById(R.id.acquireShow);

        /*if (!dbHelper.isWatchedShow(title)&&watch.isChecked()){
            dbHelper.watchShow(id);
        }

        if (!dbHelper.isAcquiredShow(title) && acquire.isChecked()) {
            dbHelper.acquireShow(id);
        }

        if (dbHelper.isAcquiredShow(title) && !acquire.isChecked()) {
            dbHelper.unacquireShow(id);
        }*/
        
        fillData();

    }

}