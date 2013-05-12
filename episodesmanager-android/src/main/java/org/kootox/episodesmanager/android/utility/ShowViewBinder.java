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
 * Date: 25 sept. 2010
 */
public class ShowViewBinder implements SimpleCursorAdapter.ViewBinder {

    final Cursor shows;

    EpisodeManagerListView activity;

    DatabaseHelper helper;

    final SimpleCursorAdapter adapter;

    public ShowViewBinder(Cursor shows, EpisodeManagerListView activity,
                          DatabaseHelper helper, SimpleCursorAdapter adapter) {
        this.shows = shows;
        this.activity = activity;
        this.helper = helper;
        this.adapter = adapter;
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor,
                                int columnIndex) {

        final Long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.SHOW_ID));

        //Deal with the acquire CheckBox
        if (view.getId() == R.id.acquireShow) {

            CheckBox box = (CheckBox) view;

            box.setChecked(helper.isAcquiredShow(id));
            box.setOnClickListener(new CompoundButton.OnClickListener(){
                public void onClick(View v){

                    CheckBox checkbox = (CheckBox) v;

                    if (checkbox.isChecked() && !helper.isAcquiredShow(id)) {
                        helper.acquireShow(id);
                        adapter.notifyDataSetChanged();
                    } else if (!checkbox.isChecked() && helper.isAcquiredShow(id)) {
                        helper.unacquireShow(id);
                        adapter.notifyDataSetChanged();
                    }
                }
            });

            return true;
        }

        //Deal with the watch CheckBox
        if (view.getId() == R.id.watchShow) {

            CheckBox box = (CheckBox) view;

            box.setChecked(helper.isWatchedShow(id));

            //Set onClickListener to set the watch/unwatch procedure
            box.setOnClickListener(new CompoundButton.OnClickListener() {
                public void onClick(View v) {

                    CheckBox checkbox = (CheckBox)v;

                    if (checkbox.isChecked() && !helper.isWatchedShow(id)) {
                        helper.watchShow(id);
                        adapter.notifyDataSetChanged();
                    } else if (!checkbox.isChecked() && helper.isWatchedShow(id)) {
                        helper.unwatchShow(id);
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
