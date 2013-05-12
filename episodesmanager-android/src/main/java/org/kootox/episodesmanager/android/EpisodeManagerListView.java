package org.kootox.episodesmanager.android;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import org.kootox.episodesmanager.android.screen.AllShows;

/**
 * User: couteau
 * Date: 18 mai 2010
 */
public class EpisodeManagerListView extends ListActivity {

    protected static final int ACTIVITY_ADD = 0;
    protected static final int ACTIVITY_BULK = 0;

    static final protected int MENU_UPDATE = 0;
    static final protected int MENU_ADD = 1;
    static final protected int MENU_QUIT = 2;
    protected static final int MENU_BULK = 3;


    protected TVRageAPI tvrageService = new TVRageAPI();

    protected final Handler uiThreadCallback = new Handler();

    protected Cursor episodes;

    protected DatabaseHelper dbHelper;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        dbHelper = new DatabaseHelper(this);

        tvrageService.init(dbHelper);

        setContentView(R.layout.main);
        fillData();
    }

    @Override
    public void onResume() {
        super.onResume();
        dbHelper.init(this);
        tvrageService.init(dbHelper);
    }

    @Override
    public void onStart() {
        super.onStart();
        dbHelper.init(this);
        tvrageService.init(dbHelper);
    }

    @Override
    public void onPause() {
        super.onPause();
        dbHelper.close();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        dbHelper.close();
        dbHelper.init(this);
        tvrageService.init(dbHelper);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    @Override
    public void onStop() {
        super.onStop();
        dbHelper.close();
    }

    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_UPDATE, 0, R.string.menuUpdate);
        menu.add(0, MENU_ADD, 0, R.string.menuAdd);
        menu.add(0, MENU_QUIT, 0, R.string.menuQuit);
        menu.add(0, MENU_BULK, 0, R.string.menuBulk);

        return super.onCreateOptionsMenu(menu);
    }

    /* Handles item selections */

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_UPDATE:
                return true;
            case MENU_ADD:
                search();
                return true;
            case MENU_BULK:
                openAllShowsActivity();
                return true;
            case MENU_QUIT:
                this.finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        dbHelper = new DatabaseHelper(this);
        tvrageService.init(dbHelper);
        
        switch (requestCode) {
            case ACTIVITY_ADD:
                fillData();
                break;
        }
    }

    public void search() {
        dbHelper.close();
        Intent i = new Intent(this, AddShow.class);
        startActivityForResult(i, ACTIVITY_ADD);
    }

    public void openAllShowsActivity() {
        dbHelper.close();
        Intent i = new Intent(this, AllShows.class);
        startActivityForResult(i, ACTIVITY_BULK);
    }

    public void fillData(){}


}
