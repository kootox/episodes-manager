package org.kootox.episodesmanager.android.utility;

import android.app.Activity;
import android.app.Application;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.util.Date;
import org.kootox.episodesmanager.android.R;

/**
 * User: couteau
 * Date: 25 sept. 2010
 */
public class EpisodesViewBinder implements SimpleCursorAdapter.ViewBinder {

    Cursor episodes;

    Activity activity;

    Application app;

    java.text.DateFormat format;

    public EpisodesViewBinder(Cursor episodes, Activity activity) {
        this.episodes = episodes;
        this.activity = activity;
        this.app = this.activity.getApplication();
        this.format = DateFormat.getDateFormat(app);
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor,
                                int columnIndex) {
        if (!cursor.isAfterLast() && !cursor.isBeforeFirst()
                && columnIndex != -1) {
            if (view.getId() == R.id.airingDate) {
                try {
                    Date date = new Date(episodes.getLong(columnIndex));
                    ((TextView) view).setText(format.format(date));
                } catch (CursorIndexOutOfBoundsException eee) {
                    ((TextView) view).setText("");
                }
                return true;
            } else if (view.getId() == R.id.row_status1 ||
                    view.getId() == R.id.row_status2) {
                if (cursor.getInt(6) == 0 &&
                        cursor.getInt(7) == 1) {
                    view.setBackgroundResource(R.color.green);
                } else if (cursor.getInt(7) == 0 &&
                        cursor.getLong(5) < new Date().getTime()) {
                    view.setBackgroundResource(R.color.blue);
                } else {
                    view.setBackgroundResource(R.color.red);
                }
                return true;
            } else {
                ((TextView) view).setText(cursor.getString(columnIndex));
                return true;
            }
        }
        return false;
    }
}
