package org.kootox.episodesmanager.android;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.kootox.episodesmanager.android.utility.EpisodesViewBinder;

/**
 * User: couteau
 * Date: 15 août 2010
 */
public class MainScreen extends EpisodeManagerListView {

    public void fillData() {
        final Dialog waiting = ProgressDialog.show(
                MainScreen.this, "",
                "Loading. Please wait...", true);

        final Runnable runInUIThread = new Runnable() {
            public void run() {
                String[] from = new String[]{
                        DatabaseHelper.EPISODE_SHOW,
                        DatabaseHelper.EPISODE_SEASON,
                        DatabaseHelper.EPISODE_NUMBER,
                        DatabaseHelper.EPISODE_TITLE,
                        DatabaseHelper.EPISODE_AIRINGDATE,
                        DatabaseHelper.EPISODE_WATCHED,
                        DatabaseHelper.EPISODE_ACQUIRED};

                int[] to = new int[]{
                        R.id.show,
                        R.id.season,
                        R.id.number,
                        R.id.title,
                        R.id.airingDate,
                        R.id.row_status1,
                        R.id.row_status2
                };

                // Now create an array adapter and set it to display using our row
                SimpleCursorAdapter mainScreenAdapter =
                        new SimpleCursorAdapter(MainScreen.this,
                                R.layout.main_screen_row, episodes,
                                from, to);

                //Create a view binder to display airing date correctly
                SimpleCursorAdapter.ViewBinder viewBinder =
                        new EpisodesViewBinder(episodes, MainScreen.this);

                mainScreenAdapter.setViewBinder(viewBinder);
                setListAdapter(mainScreenAdapter);
            }
        };

        new Thread() {
            @Override
            public void run() {

                // Get all of the shows from the database and create the item list
                episodes = dbHelper.fetchMainScreenEpisodes();
                startManagingCursor(episodes);

                uiThreadCallback.post(runInUIThread);
                waiting.dismiss();
            }
        }.start();

    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        dbHelper.treatEpisode(id);
        fillData();
    }
}
