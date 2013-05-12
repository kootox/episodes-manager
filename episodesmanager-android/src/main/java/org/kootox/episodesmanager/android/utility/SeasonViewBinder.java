package org.kootox.episodesmanager.android.utility;

import android.database.Cursor;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SimpleCursorAdapter;
import org.kootox.episodesmanager.android.DatabaseHelper;
import org.kootox.episodesmanager.android.EpisodeManagerListView;
import org.kootox.episodesmanager.android.R;

/**
 * User: couteau
 * Date: 30 sept. 2010
 */
public class SeasonViewBinder implements SimpleCursorAdapter.ViewBinder {

    final Cursor shows;

    EpisodeManagerListView activity;

    DatabaseHelper helper;

    final SimpleCursorAdapter adapter;

    public SeasonViewBinder(Cursor shows, EpisodeManagerListView activity,
                          DatabaseHelper helper, SimpleCursorAdapter adapter) {
        this.shows = shows;
        this.activity = activity;
        this.helper = helper;
        this.adapter = adapter;
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor,
                                int columnIndex) {

        String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EPISODE_SHOW));
        final Long id = helper.findShowByTitle(title);
        final int seasonNumber = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.EPISODE_SEASON));

        //Deal with the acquire CheckBox
        if (view.getId() == R.id.acquireSeason) {

            CheckBox box = (CheckBox) view;

            box.setChecked(helper.isAcquiredSeason(id, seasonNumber));
            box.setOnClickListener(new CompoundButton.OnClickListener() {
                public void onClick(View v) {

                    CheckBox checkbox = (CheckBox) v;

                    if (checkbox.isChecked() && !helper.isAcquiredSeason(id, seasonNumber)) {
                        helper.acquireSeason(id, seasonNumber);
                        adapter.notifyDataSetChanged();
                    } else if (!checkbox.isChecked() && helper.isAcquiredSeason(id, seasonNumber)) {
                        helper.unacquireSeason(id, seasonNumber);
                        adapter.notifyDataSetChanged();
                    }
                }
            });

            return true;
        }

        //Deal with the watch CheckBox
        if (view.getId() == R.id.watchSeason) {

            CheckBox box = (CheckBox) view;

            box.setChecked(helper.isWatchedSeason(id, seasonNumber));

            //Set onClickListener to set the watch/unwatch procedure
            box.setOnClickListener(new CompoundButton.OnClickListener() {
                public void onClick(View v) {

                    CheckBox checkbox = (CheckBox) v;

                    if (checkbox.isChecked() && !helper.isWatchedSeason(id, seasonNumber)) {
                        helper.watchSeason(id, seasonNumber);
                        adapter.notifyDataSetChanged();
                    } else if (!checkbox.isChecked() && helper.isWatchedSeason(id, seasonNumber)) {
                        helper.unwatchSeason(id, seasonNumber);
                        adapter.notifyDataSetChanged();
                    }

                }
            });

            //return true as everything is set
            return true;
        }

        return false;
    }

}
