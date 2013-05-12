package org.kootox.episodesmanager.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * User: couteau
 * Date: 16 mai 2010
 */
public class AddShow extends Activity {

    static final int DIALOG_SEARCH = 0;
    static final int DIALOG_ADD = 1;

    final Handler uiThreadCallback = new Handler();

    private TVRageAPI tvrageService = new TVRageAPI();

    private DatabaseHelper dbHelper;

    // TODO JC20100516 Bug on adding show

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(this);

        tvrageService.init(dbHelper);

        showDialog(DIALOG_SEARCH);
    }

    @Override
    public void onResume(){
        super.onResume();
        dbHelper.init(this);
        tvrageService.init(dbHelper);
    }

    @Override
    public void onPause(){
        super.onPause();
        dbHelper.close();
    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch (id) {
            case DIALOG_SEARCH:
                dialog = new Dialog(this);

                dialog.setContentView(R.layout.search);
                dialog.setTitle(R.string.searchTitle);

                final EditText searchShowTitle = (EditText) dialog.findViewById(R.id.searchShowTitle);

                searchShowTitle.setText("");

                //The button calculate the score of the set and add a row with it
                //Then calculate totals and update them
                Button validate = (Button) dialog.findViewById(R.id.searchButton);
                validate.setOnClickListener(new Button.OnClickListener() {

                    public void onClick(View v) {

                        //Get the title to search
                        final String searchString = searchShowTitle.getText().toString();

                        dismissDialog(DIALOG_SEARCH);

                        final Dialog waiting = ProgressDialog.show(
                                AddShow.this, "",
                                "Loading. Please wait...", true);

                        final Runnable runInUIThread = new Runnable() {
                            public void run() {
                                showDialog(DIALOG_ADD);
                            }
                        };

                        new Thread() {
                            @Override
                            public void run() {
                                tvrageService.search(searchString);
                                waiting.dismiss();
                                uiThreadCallback.post(runInUIThread);
                            }
                        }.start();

                    }
                });
                break;
            case DIALOG_ADD:
                final String[] items = tvrageService.getLastResults();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.chooseShow);
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, final int item) {
                        dismissDialog(DIALOG_ADD);
                        removeDialog(DIALOG_ADD);

                        final Dialog waiting = ProgressDialog.show(
                                AddShow.this, "",
                                "Loading. Please wait...", true);

                        final Runnable runInUIThread = new Runnable() {
                            public void run() {
                                end();
                            }
                        };

                        new Thread() {
                            @Override
                            public void run() {
                                tvrageService.add(items[item]);
                                waiting.dismiss();
                                uiThreadCallback.post(runInUIThread);
                            }
                        }.start();
                    }
                });
                dialog = builder.create();
                break;
            default:
                dialog = null;
        }
        return dialog;
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DIALOG_SEARCH:

                final EditText searchShowTitle = (EditText) dialog.findViewById(R.id.searchShowTitle);
                searchShowTitle.setText("");
                break;
            default:
                break;
        }
    }


    private void end() {
        Intent mIntent = new Intent();
        setResult(RESULT_OK, mIntent);
        finish();
    }

}
