package org.kootox.episodesmanager.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.kootox.episodesmanager.android.utility.EpisodesManagerException;

/**
 * User: couteau
 * Date: 14 mai 2010
 */
public class DatabaseHelper {

    private static String TAG = "EpisodesManager";

    private static final String DATABASE_NAME = "episodesmanager.db";

    private Context context;

    private static final int DATABASE_VERSION = 4;

    public static final String EPISODES_TABLE = "episodes";

    public static final String SHOWS_TABLE = "shows";

    public static final String SHOW_TITLE = "title";
    public static final String SHOW_TVRAGEID = "tvRageId";
    public static final String SHOW_ID = "_id";

    public static final String EPISODE_TITLE = "title";
    public static final String EPISODE_AIRINGDATE = "airingDate";
    public static final String EPISODE_ACQUIRED = "acquired";
    public static final String EPISODE_WATCHED = "watched";
    public static final String EPISODE_NUMBER = "number";
    public static final String EPISODE_SEASON = "season";
    public static final String EPISODE_SHOW = "show";
    public static final String EPISODE_ID = "_id";

    public static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private static SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        OpenHelper openHelper = new OpenHelper(context);
        try {
            db = openHelper.getWritableDatabase();
        } catch (NullPointerException eee){
            Log.d(TAG, "No openHelper");
            //don't know what to do
        }
    }

    public void init(Context context) {
        this.context = context;
        OpenHelper openHelper = new OpenHelper(context);
        db = openHelper.getWritableDatabase();

    }

    public void close(){
        db.close();
    }

    public long storeEpisode(Long id, String title, Date airingDate,
                                    Boolean acquired, Boolean watched,
                                    int number, int season, String show)
            throws EpisodesManagerException {

        Log.i(TAG, title);

        if (id != null) {

            if (episodeExistsById(id)) {
                //if shows exist update
                updateEpisode(id, title, airingDate, acquired, watched, number,
                        season, show);
            } else {
                //if not present in db, insert it
                insertEpisode(title, airingDate, acquired, watched, number,
                        season, show);
            }

        } else {

            //if not present in db, insert it
            insertEpisode(title, airingDate, acquired, watched, number,
                    season, show);
        }

        Long newId = findEpisodeByTitle(title);

        if (newId == null) {
            throw new EpisodesManagerException("An error occurred while trying to save show " + title);
        }

        return newId;
        
    }

    private void updateEpisode(Long id, String title, Date airingDate,
                                     Boolean acquired, Boolean watched,
                                     int number, int season, String show){

        ContentValues values = new ContentValues();

        values.put(EPISODE_TITLE, title);
        values.put(EPISODE_AIRINGDATE, airingDate.getTime());
        values.put(EPISODE_ACQUIRED, acquired);
        values.put(EPISODE_WATCHED, watched);
        values.put(EPISODE_NUMBER, number);
        values.put(EPISODE_SEASON, season);
        values.put(EPISODE_SHOW, show);

        db.update(EPISODES_TABLE, values, EPISODE_ID + "=" + id, null);
    }

    private void insertEpisode(String title, Date airingDate,
                                      Boolean acquired, Boolean watched,
                                      int number, int season, String show){

        ContentValues values = new ContentValues();

        values.put(EPISODE_TITLE, title);
        values.put(EPISODE_AIRINGDATE, airingDate.getTime());
        values.put(EPISODE_ACQUIRED, acquired);
        values.put(EPISODE_WATCHED, watched);
        values.put(EPISODE_NUMBER, number);
        values.put(EPISODE_SEASON, season);
        values.put(EPISODE_SHOW, show);

        db.insert(EPISODES_TABLE, null, values);
    }


    /**
     *
     * Create or update show in database
     *
     * @param id the id of the show if already created, null if not or do not know
     * @param title the title of the show
     * @param tvRageId the tvRageId of the show
     * @return the id of the show
     * @throws EpisodesManagerException if an error occur
     */
    public long storeShow(Long id, String title, int tvRageId)
            throws EpisodesManagerException{

        if (id != null) {

            boolean exists = showExistsById(id);

            if (exists){
                //if shows exist update
                updateShow(id, title, tvRageId);
            } else {
                //try to find same show with other id
                Long otherId = findShowByTitle(title);

                if (otherId == null) {
                    //if not present in db, insert it
                    insertShow(title, tvRageId);
                } else {
                    //if already present update it
                    updateShow(otherId, title, tvRageId);
                }
            }

        } else {

            Long oldId = findShowByTitle(title);

            if (oldId == null) {
                //if not present in db, insert it
                insertShow(title, tvRageId);
            } else {
                //if already present update it
                updateShow(oldId, title, tvRageId);
            }

        }

        Long newId = findShowByTitle(title);

        if (newId==null){
            throw new EpisodesManagerException("An error occurred while trying to save show " + title);
        }

        return newId;
    }

    private void insertShow(String title, int tvRageId){
        ContentValues values = new ContentValues();
        values.put(SHOW_TITLE, title);
        values.put(SHOW_TVRAGEID, tvRageId);
        db.insert(SHOWS_TABLE, null, values);
    }

    private void updateShow(long id, String title, int tvRageId){
        ContentValues values = new ContentValues();
        values.put(SHOW_TITLE, title);
        values.put(SHOW_TVRAGEID, tvRageId);

        db.update(SHOWS_TABLE, values, SHOW_ID + "=" + id, null);
    }

    public String getShowTitle(Long id){

        String title = null;

        String[] columns = {SHOW_TITLE};

        String where = SHOW_ID + "=" + id;

        if (!db.isOpen()){
            Log.d(TAG, "Database closed, reopen it");
            init(context);
        }

        //try to find a show with the same title
        Cursor answer = db.query(SHOWS_TABLE, columns, where,  null, null, null,
                null, null);

        //get its id if exists
        if ((answer != null) && (answer.moveToFirst())) {
            int column = answer.getColumnIndex(SHOW_TITLE);
            do {
                title = answer.getString(column);
            } while (answer.moveToNext());
            answer.close();
        }

        return title;
    }

    /**
     * Find a show id by its title
     * @param title the show title
     * @return the show id, null if not found
     */
    public Long findShowByTitle(String title){
        Long oldId = null;

        String[] columns = {SHOW_ID};

        if (!db.isOpen()){
            Log.d(TAG, "Database closed, reopen it");
            init(context);
        }

        //try to find a show with the same title
        Cursor answer = db.query(SHOWS_TABLE, columns,
                    SHOW_TITLE + "=\'" + title.replaceAll("'", "''") + "\'",
                    null, null, null, null, null);

        //get its id if exists
        if ((answer!= null) && (answer.moveToFirst())) {
            int idColumn = answer.getColumnIndex(SHOW_ID);
            do {
                oldId = answer.getLong(idColumn);
            } while (answer.moveToNext());
        }

        answer.close();

        return oldId;
    }

    public Boolean showExistsById(long id){

        String[] columns = {SHOW_ID};

        //try to find a show with the same title
        Cursor answer = db.query(SHOWS_TABLE, columns, SHOW_ID + "=" + id, null, null, null, null, null);

        //return true if elements where returned
        Boolean returnValue = answer.moveToFirst();

        answer.close();

        return returnValue;
    }

    public Boolean episodeExistsById(long id) {

        String[] columns = {EPISODE_ID};

        //try to find a show with the same title
        Cursor answer = db.query(EPISODES_TABLE, columns, EPISODE_ID + "=" + id, null, null, null, null, null);

        //return true if elements where returned
        Boolean returnValue = answer.moveToFirst();

        answer.close();

        return returnValue;
    }

    /**
     * Find a show id by its title
     *
     * @param title the show title
     * @return the show id, null if not found
     */
    public Long findEpisodeByTitle(String title) {
        Long oldId = null;

        String[] columns = {EPISODE_ID};

        //try to find a show with the same title
        Cursor answer = db.query(EPISODES_TABLE, columns, EPISODE_TITLE + "=\'" + title.replaceAll("'", "''") + "\'", null, null, null, null, null);

        //get its id if exists
        if (answer.moveToFirst()) {
            int idColumn = answer.getColumnIndex(EPISODE_ID);
            do {
                oldId = answer.getLong(idColumn);
            } while (answer.moveToNext());
        }

        answer.close();

        return oldId;
    }

    public void acquireEpisode(long id){
        
        ContentValues values = new ContentValues();

        values.put(EPISODE_ACQUIRED, true);

        try {
            db.update(EPISODES_TABLE, values, EPISODE_ID + "=" + id, null);
        } catch (IllegalStateException eee) {
            Log.d(TAG, "Database closed, reopen it");
            init(context);
            db.update(EPISODES_TABLE, values, EPISODE_ID + "=" + id, null);
        }
    }

    public void watchEpisode(long id) {

        ContentValues values = new ContentValues();

        values.put(EPISODE_WATCHED, true);

        try {
            db.update(EPISODES_TABLE, values, EPISODE_ID + "=" + id, null);
        } catch (IllegalStateException eee) {
            Log.d(TAG, "Database closed, reopen it");
            init(context);
            db.update(EPISODES_TABLE, values, EPISODE_ID + "=" + id, null);
        }
    }

    public Cursor fetchMainScreenEpisodes() {

        //Init the null airingdate
        Date nullDate = new Date();
        try {
            nullDate = format.parse("0000-00-00");
        } catch (ParseException eee) {
            Log.e(TAG, "Could not initiate the comparison date", eee);
        }

        // The columns to return
        String[] returnedColumns = {EPISODE_ID, EPISODE_TITLE,
                EPISODE_SHOW, EPISODE_NUMBER, EPISODE_SEASON,
                EPISODE_AIRINGDATE, EPISODE_WATCHED, EPISODE_ACQUIRED};

        // The toAcquire clauses
        String whereToAcquire = EPISODE_ACQUIRED + "=0 AND " +
                EPISODE_AIRINGDATE + "<" + new Date().getTime() + " AND " +
                EPISODE_AIRINGDATE + "<>" + nullDate.getTime() + " AND " +
                EPISODE_AIRINGDATE + " = (select min(" + EPISODE_AIRINGDATE +
                ") FROM " + EPISODES_TABLE +
                " as e where e.show=episodes.show AND " + EPISODE_ACQUIRED + "=0)";

        // The toWatch clauses
        String whereToWatch = EPISODE_ACQUIRED + "=1 AND " +
                EPISODE_WATCHED + "=0 AND " +
                EPISODE_AIRINGDATE + "<>" + nullDate.getTime() + " AND " +
                //EPISODE_AIRINGDATE + "<" + new Date().getTime() + " AND " +
                EPISODE_AIRINGDATE + " = (select min(" + EPISODE_AIRINGDATE +
                ") FROM " + EPISODES_TABLE +
                " as e where e.show=episodes.show AND " + EPISODE_ACQUIRED + "=1 AND " +
                EPISODE_WATCHED + "=0)";

        //The toBroadcast clauses
        String whereToBroadcast = EPISODE_ACQUIRED + "=0 AND " +
                EPISODE_AIRINGDATE + ">" + new Date().getTime() + " AND " +
                EPISODE_AIRINGDATE + "<>" + nullDate.getTime() + " AND " +
                EPISODE_AIRINGDATE + " = (select min(" + EPISODE_AIRINGDATE +
                ") FROM " + EPISODES_TABLE +
                " as e where e.show=episodes.show AND " + EPISODE_ACQUIRED + "=0)";

        // The order by clause
        String orderBy = EPISODE_ACQUIRED + " ASC," +
                EPISODE_WATCHED + " ASC," +
                EPISODE_AIRINGDATE + " ASC";

        //The toAcquire query
        String toAcquireQuery = SQLiteQueryBuilder.buildQueryString(true,
                EPISODES_TABLE, returnedColumns,
                whereToAcquire, null, null, null, null);

        //The toWatch query
        String toWatchQuery = SQLiteQueryBuilder.buildQueryString(true,
                EPISODES_TABLE, returnedColumns,
                whereToWatch, null, null, null, null);

        //The toBroadcast query
        String toBroadcastQuery = SQLiteQueryBuilder.buildQueryString(true,
                EPISODES_TABLE, returnedColumns,
                whereToBroadcast, null, null, null, null);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        //Final query
        String finalQuery = queryBuilder.buildUnionQuery(new String[]{toAcquireQuery,
                toWatchQuery, toBroadcastQuery}, orderBy, null);

        //Try to get a cursor
        Cursor result;
        try {
            result = db.rawQuery(finalQuery, null);
        } catch (IllegalStateException eee){
            Log.d(TAG, "Database closed, reopen it");
            init(context);
            result = db.rawQuery(finalQuery, null);
        }

        return result;
    }

    /**
     * Get all the shows in database
     * @return a cursor containing all shows present on database
     */
    public Cursor getAllShows(){
        String[] returnedColumns = {SHOW_ID, SHOW_TITLE};

        String orderBy = SHOW_TITLE + " ASC";

        checkDBState();

        return db.query(SHOWS_TABLE, returnedColumns, null, null, null, null,
                orderBy);
    }

    /**
     * Get all the seasons of a show
     * @param id the show id
     * @return a cursor containing all the seasons number
     */
    public Cursor getAllSeasons(Long id) {

        String show = getShowTitle(id);

        String[] returnedColumns = {EPISODE_SEASON};

        String where = EPISODE_SEASON + " = " + show;

        String groupBy = EPISODE_SEASON;

        String orderBy = EPISODE_SEASON + " ASC";

        checkDBState();

        return db.query(EPISODES_TABLE, returnedColumns, where, null, groupBy,
                null, orderBy);
    }

    public void treatEpisode(long id) {

        String[] returnedColumns = {EPISODE_ID, EPISODE_AIRINGDATE,
                EPISODE_WATCHED, EPISODE_ACQUIRED};

        String where = EPISODE_ID + "=" + id;

        checkDBState();

        Cursor cursor = db.query(EPISODES_TABLE, returnedColumns,
                where, null, EPISODE_SHOW,
                null, null);

        cursor.moveToFirst();
        int watched = cursor.getInt(2);
        int acquired = cursor.getInt(3);

        if (acquired == 0){
            acquireEpisode(id);
        } else if (watched == 0){
            watchEpisode(id);
        }
    }

    /**
     * Check if a show is acquired
     *
     * @param show the show title
     * @return true if all the show episodes have been acquired
     */
    public boolean isAcquiredShow(String show) {
        String[] returnColumns = {EPISODE_ID};
        String where = EPISODE_SHOW + " = \"" + show + "\" AND " + EPISODE_ACQUIRED +
                " = 0";

        checkDBState();

        Cursor cursor = db.query(EPISODES_TABLE, returnColumns, where, null,
                null, null, null);

        cursor.moveToFirst();

        return (cursor.getCount() == 0);
    }

    /**
     * Check if a show is acquired
     *
     * @param id the show id
     * @return true if all the show episodes have been acquired
     */
    public boolean isAcquiredShow(Long id) {

        String show = getShowTitle(id);

        return isAcquiredShow(show);
    }

    /**
     * Acquire and watch show
     *
     * @param id the show id
     */
    public void watchShow(Long id){

        String show = getShowTitle(id);

        ContentValues values = new ContentValues();

        values.put(EPISODE_ACQUIRED, true);
        values.put(EPISODE_WATCHED, true);

        checkDBState();

        while (!isWatchedShow(id)){
            db.update(EPISODES_TABLE, values, EPISODE_SHOW + "=\"" + show + "\"",
                   null);
        }
    }

    /**
     * unwatch a show
     *
     * @param id the show id
     */
    public void unwatchShow(Long id) {

        String show = getShowTitle(id);

        ContentValues values = new ContentValues();

        values.put(EPISODE_WATCHED, false);

        checkDBState();

        db.update(EPISODES_TABLE, values, EPISODE_SHOW + "=\"" + show + "\"",
                null);
    }

    /**
     * acquire a show
     *
     * @param id the show id
     */
    public void acquireShow(Long id) {

        String show = getShowTitle(id);

        ContentValues values = new ContentValues();

        values.put(EPISODE_ACQUIRED, true);

        checkDBState();

        db.update(EPISODES_TABLE, values, EPISODE_SHOW + "=\"" + show + "\"",
                null);
    }

    /**
     * unwatch and unaquire a show
     *
     * @param id the show id
     */
    public void unacquireShow(Long id) {

        String show = getShowTitle(id);

        ContentValues values = new ContentValues();

        values.put(EPISODE_WATCHED, false);
        values.put(EPISODE_ACQUIRED, false);

        checkDBState();

        db.update(EPISODES_TABLE, values, EPISODE_SHOW + "=\"" + show + "\"",
                null);
    }

    /**
     * Method that check database state. Reopen database with previous context
     * if database is closed.
     */
    protected void checkDBState() {
        if (!db.isOpen()){
            Log.d(TAG, "Database is closed, reopen it");
            init(context);
        }
    }

    /**
     * Check if a show is watched
     *
     * @param show the show title
     * @return true if all the show episodes have been watched
     */
    public boolean isWatchedShow(String show) {
        String[] returnColumns = {EPISODE_ID};
        String where = EPISODE_SHOW + " = \"" + show + "\" AND " +
                EPISODE_WATCHED + " = 0";

        checkDBState();

        Cursor cursor = db.query(EPISODES_TABLE, returnColumns, where, null,
                null, null, null);

        cursor.moveToFirst();

        return (cursor.getCount() == 0);
    }

    /**
     * Check if a show is watched
     *
     * @param id the show id
     * @return true if all the show episodes have been watched
     */
    public boolean isWatchedShow(Long id) {

        String show = getShowTitle(id);

        return isWatchedShow(show);
    }

    /**
     * Check if a season is acquired
     *
     * @param show the show title
     * @param seasonNumber the season number
     * @return true if all the show episodes have been acquired
     */
    public boolean isAcquiredSeason(String show, int seasonNumber) {
        String[] returnColumns = {EPISODE_ID};
        String where = EPISODE_SHOW + " = \"" + show + "\" AND "
                + EPISODE_ACQUIRED + " = 0 AND "
                + EPISODE_SEASON + " = " + seasonNumber;

        checkDBState();

        Cursor cursor = db.query(EPISODES_TABLE, returnColumns, where, null,
                null, null, null);

        cursor.moveToFirst();

        return (cursor.getCount() == 0);
    }

    /**
     * Check if a season is acquired
     *
     * @param id the show id
     * @param seasonNumber the season number
     * @return true if all the show episodes have been acquired
     */
    public boolean isAcquiredSeason(Long id, int seasonNumber) {

        String show = getShowTitle(id);

        return isAcquiredSeason(show, seasonNumber);
    }

    /**
     * Acquire and watch a season
     *
     * @param id the show id
     * @param seasonNumber the season number
     */
    public void watchSeason(Long id, int seasonNumber) {

        String show = getShowTitle(id);

        ContentValues values = new ContentValues();

        values.put(EPISODE_ACQUIRED, true);
        values.put(EPISODE_WATCHED, true);

        String where = EPISODE_SHOW + "=\"" + show + "\" AND " +
                EPISODE_SEASON + " = " + seasonNumber;

        checkDBState();

        while (!isWatchedShow(id)) {
            db.update(EPISODES_TABLE, values, where, null);
        }
    }

    /**
     * unwatch a season
     *
     * @param id the show id
     * @param seasonNumber the season number
     */
    public void unwatchSeason(Long id, int seasonNumber) {

        String show = getShowTitle(id);

        ContentValues values = new ContentValues();

        values.put(EPISODE_WATCHED, false);

        String where = EPISODE_SHOW + "=\"" + show + "\" AND " +
                EPISODE_SEASON + " = " + seasonNumber;

        checkDBState();

        db.update(EPISODES_TABLE, values, where, null);
    }

    /**
     * acquire a season
     *
     * @param id the show id
     * @param seasonNumber the season number
     */
    public void acquireSeason(Long id, int seasonNumber) {

        String show = getShowTitle(id);

        ContentValues values = new ContentValues();

        values.put(EPISODE_ACQUIRED, true);

        String where = EPISODE_SHOW + "=\"" + show + "\" AND " +
                EPISODE_SEASON + " = " + seasonNumber;

        checkDBState();

        db.update(EPISODES_TABLE, values, where, null);
    }

    /**
     * unwatch and unaquire a season
     *
     * @param id the show id
     * @param seasonNumber the season number
     */
    public void unacquireSeason(Long id, int seasonNumber) {

        String show = getShowTitle(id);

        ContentValues values = new ContentValues();

        values.put(EPISODE_WATCHED, false);
        values.put(EPISODE_ACQUIRED, false);

        String where = EPISODE_SHOW + "=\"" + show + "\" AND " +
                EPISODE_SEASON + " = " + seasonNumber;

        checkDBState();

        db.update(EPISODES_TABLE, values, where, null);
    }

    /**
     * Check if a show is watched
     *
     * @param show the show title
     * @param seasonNumber the season number
     * @return true if all the show episodes have been watched
     */
    public boolean isWatchedSeason(String show, int seasonNumber) {
        String[] returnColumns = {EPISODE_ID};
        String where = EPISODE_SHOW + " = \"" + show + "\" AND " +
                EPISODE_WATCHED + " = 0 AND " +
                EPISODE_SEASON + " = " + seasonNumber;

        checkDBState();

        Cursor cursor = db.query(EPISODES_TABLE, returnColumns, where, null,
                null, null, null);

        cursor.moveToFirst();

        return (cursor.getCount() == 0);
    }

    /**
     * Check if a show is watched
     *
     * @param id         the show id
     * @param seasonNumber the season number
     * @return true if all the show episodes have been watched
     */
    public boolean isWatchedSeason(Long id, int seasonNumber) {

        String show = getShowTitle(id);

        return isWatchedSeason(show, seasonNumber);
    }


    private static class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + EPISODES_TABLE + "(" +
                    EPISODE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    EPISODE_TITLE + " TEXT, " +
                    EPISODE_AIRINGDATE + " LONG, " +
                    EPISODE_ACQUIRED + " BOOLEAN, " +
                    EPISODE_WATCHED + " BOOLEAN, " +
                    EPISODE_NUMBER + " INTEGER," +
                    EPISODE_SEASON + " INTEGER," +
                    EPISODE_SHOW + " TEXT);");

            db.execSQL("CREATE TABLE " + SHOWS_TABLE + "(" +
                    SHOW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SHOW_TITLE + " TEXT, " +
                    SHOW_TVRAGEID + " INTEGER);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Example", "Upgrading database, this will drop tables and recreate.");
            db.execSQL("DROP TABLE IF EXISTS " + EPISODES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SHOWS_TABLE);

            onCreate(db);
        }

    }

}
