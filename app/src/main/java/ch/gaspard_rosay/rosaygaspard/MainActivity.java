package ch.gaspard_rosay.rosaygaspard;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Gestion des boutons
        FloatingActionButton mail = (FloatingActionButton) findViewById(R.id.mail);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Sending mail to gaspard@rosay.ch...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("application/octet-stream");

                String aEmailList[] = { "gaspard@rosay.ch" };

                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);

                startActivity(emailIntent);

            }
        });
        FloatingActionButton phone = (FloatingActionButton) findViewById(R.id.phone);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Calling Gaspard Rosay...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+41791384672"));
                try{
                    startActivity(intent);
                }
                catch(Exception e){
                    Snackbar.make(view, "Error when trying to call Gaspard Rosay!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                }
            }
        });

        MainDatabaseHelper mDbHelper = new MainDatabaseHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        boolean bNetwork = isNetworkAvailable();

        // On test la disponibilité réseau
        if(!bNetwork){ // Pas de réseau
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.main_layout), "Aucune connexion réseau disponible!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("", null);

            snackbar.show();

            // Variables
            LayoutInflater mInflater;
            mInflater = LayoutInflater.from(getApplicationContext());
            LinearLayout layout;
            View childView;
            String sDates, sDateFrom, sDateTo;
            Long iDateFrom, iDateTo;
            TextView tJobTitle, tJobSociety, tJobDates, tJobDescr;
            Cursor c;
            String sortOrder;

            // On définit une projection pour la table à utiliser
            String[] experience_proj = {
                    MainDatabase.ExperienceEntry._ID,
                    MainDatabase.ExperienceEntry.COLUMN_NAME_TITLE,
                    MainDatabase.ExperienceEntry.COLUMN_NAME_DATES,
                    MainDatabase.ExperienceEntry.COLUMN_NAME_SOCIETY,
                    MainDatabase.ExperienceEntry.COLUMN_NAME_DESCR
            };
            sortOrder =
                    MainDatabase.ExperienceEntry._ID + " ASC";

            c = db.query(
                    MainDatabase.ExperienceEntry.TABLE_NAME,  // The table to query
                    experience_proj,                               // The columns to return
                    null,                                     // The columns for the WHERE clause
                    null,                                    // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            layout = (LinearLayout) this.findViewById(R.id.Experiences);

            int i = 0;

            try {
                while (c.moveToNext()) {

                    sDates = c.getString(c.getColumnIndexOrThrow(MainDatabase.StudiesEntry.COLUMN_NAME_DATES));

                    // JobInfo
                    childView = mInflater.inflate(R.layout.job_card, null);

                    tJobTitle = (TextView) childView.findViewById(R.id.cardJobTitle);
                    tJobTitle.setText(c.getString(c.getColumnIndexOrThrow(MainDatabase.ExperienceEntry.COLUMN_NAME_TITLE)));


                    // On remplace le titre par le titre du poste actuel/dernier poste
                    if(i == 0){
                        TextView tProfilTitle = (TextView) this.findViewById(R.id.profile_title);
                        tProfilTitle.setText(c.getString(c.getColumnIndexOrThrow(MainDatabase.ExperienceEntry.COLUMN_NAME_TITLE)));
                    }

                    tJobSociety = (TextView) childView.findViewById(R.id.cardJobSociety);
                    tJobSociety.setText(c.getString(c.getColumnIndexOrThrow(MainDatabase.ExperienceEntry.COLUMN_NAME_SOCIETY)));

                    tJobDates = (TextView) childView.findViewById(R.id.cardJobDates);
                    tJobDates.setText(sDates);

                    tJobDescr = (TextView) childView.findViewById(R.id.cardJobDescr);
                    tJobDescr.setText(c.getString(c.getColumnIndexOrThrow(MainDatabase.ExperienceEntry.COLUMN_NAME_DESCR)));

                    layout.addView(childView);
                    i++;
                }
            } finally {
                c.close();
            }

            String[] studies_proj = {
                    MainDatabase.StudiesEntry._ID,
                    MainDatabase.StudiesEntry.COLUMN_NAME_DIPLOMA,
                    MainDatabase.StudiesEntry.COLUMN_NAME_DATES,
                    MainDatabase.StudiesEntry.COLUMN_NAME_SCHOOL,
                    MainDatabase.StudiesEntry.COLUMN_NAME_DESCR
            };
            sortOrder =
                    MainDatabase.StudiesEntry._ID + " ASC";

            c = db.query(
                    MainDatabase.StudiesEntry.TABLE_NAME,  // The table to query
                    studies_proj,                               // The columns to return
                    null,                                     // The columns for the WHERE clause
                    null,                                    // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            layout = (LinearLayout) this.findViewById(R.id.Studies);

            try {
                while (c.moveToNext()) {

                    sDates = c.getString(c.getColumnIndexOrThrow(MainDatabase.StudiesEntry.COLUMN_NAME_DATES));

                    // JobInfo
                    childView = mInflater.inflate(R.layout.job_card, null);

                    tJobTitle = (TextView) childView.findViewById(R.id.cardJobTitle);
                    tJobTitle.setText(c.getString(c.getColumnIndexOrThrow(MainDatabase.StudiesEntry.COLUMN_NAME_DIPLOMA)));

                    tJobSociety = (TextView) childView.findViewById(R.id.cardJobSociety);
                    tJobSociety.setText(c.getString(c.getColumnIndexOrThrow(MainDatabase.StudiesEntry.COLUMN_NAME_SCHOOL)));

                    tJobDates = (TextView) childView.findViewById(R.id.cardJobDates);
                    tJobDates.setText(sDates);

                    tJobDescr = (TextView) childView.findViewById(R.id.cardJobDescr);
                    tJobDescr.setText(c.getString(c.getColumnIndexOrThrow(MainDatabase.StudiesEntry.COLUMN_NAME_DESCR)));

                    layout.addView(childView);
                }
            } finally {
                c.close();
            }

        }
        else if(savedInstanceState == null){
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.main_layout), "Mise à jour...", Snackbar.LENGTH_LONG)
                    .setAction("", null);

            snackbar.show();
            new RequestTask(this, db, "Experiences").execute("http://gaspard-rosay.ch/cv/getExperience.php");
            new RequestTask(this, db, "Studies").execute("http://gaspard-rosay.ch/cv/getStudies.php");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getDateString(String sDateFrom, String sDateTo){
        String sDates;
        Long lDateFrom, lDateTo;
        SimpleDateFormat f = new SimpleDateFormat("MMM yyyy");
        Calendar cDateFrom = Calendar.getInstance(), cDateTo = Calendar.getInstance();

        try {
            lDateFrom = Long.parseLong(sDateFrom);
        } catch(NumberFormatException nfe) {
            lDateFrom = 0L;
        }
        cDateFrom.setTimeInMillis(lDateFrom*1000);
        sDateFrom = f.format(cDateFrom.getTime());

        try {
            lDateTo = Long.parseLong(sDateTo);
        } catch(NumberFormatException nfe) {
            lDateTo = 0L;
        }
        cDateTo.setTimeInMillis(lDateTo*1000);
        sDateTo = f.format(cDateTo.getTime());

        sDates = "De " + sDateFrom + (lDateTo == 0 ? " à aujourd'hui" : " à " + sDateTo);

        return sDates;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class RequestTask extends AsyncTask<String, String, String> {
        private WeakReference<MainActivity> mActivity;
        private String sChoice;
        private SQLiteDatabase db;
        public RequestTask(MainActivity activity, SQLiteDatabase mDb, String Choice) {
            mActivity = new WeakReference<MainActivity>(activity);
            db = mDb;
            sChoice = Choice;
        }

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // On traîte la valeure retournées par PHP

            try {

                // Variables communes
                LayoutInflater mInflater;
                mInflater = LayoutInflater.from(getApplicationContext());
                MainActivity activity = mActivity.get();
                LinearLayout layout;
                View childView;
                String sDates, sDateFrom, sDateTo;
                ContentValues values;


                // On traite le JSON !! {} = OBJET JSON et [] = TABLEAU JSON. Dans notre cas,
                // le format est : {array[{},{},{}]}
                JSONObject jsonObject = new JSONObject(result);

                // On récupère le tableau "array"
                JSONArray jsArray = jsonObject.getJSONArray("array");


                switch (sChoice) {
                    case "Experiences":

                        db.execSQL("delete from "+ MainDatabase.ExperienceEntry.TABLE_NAME);

                        layout = (LinearLayout) activity.findViewById(R.id.Experiences);

                        TextView tJobTitle, tJobSociety, tJobDates, tJobDescr;

                        // On parcours notre tableau json et on affiche les lignes
                        for(int i = 0; i < jsArray.length(); i++) {
                            JSONObject tempJson = jsArray.getJSONObject(i);

                            sDateFrom = (String) tempJson.get("date_from");
                            sDateTo = (String) tempJson.get("date_to");

                            sDates = getDateString(sDateFrom, sDateTo);

                            // Ajout dans la DB interne
                            // On crée un nouveau jeu de données
                            values = new ContentValues();
                            values.put(MainDatabase.ExperienceEntry.COLUMN_NAME_TITLE, (String) tempJson.get("title"));
                            values.put(MainDatabase.ExperienceEntry.COLUMN_NAME_DATES, sDates);
                            values.put(MainDatabase.ExperienceEntry.COLUMN_NAME_SOCIETY, (String) tempJson.get("society"));
                            values.put(MainDatabase.ExperienceEntry.COLUMN_NAME_DESCR, (String) tempJson.get("description"));

                            // On insère la nouvelle ligne
                            db.insert(
                                MainDatabase.ExperienceEntry.TABLE_NAME,
                                null, // auto-id
                                values);

                            // On remplace le titre par le titre du poste actuel/dernier poste
                            if(i == 0){
                                TextView tProfilTitle = (TextView) activity.findViewById(R.id.profile_title);
                                tProfilTitle.setText((String) tempJson.get("title"));
                            }


                            // JobInfo
                            childView = mInflater.inflate(R.layout.job_card, null);


                            tJobTitle = (TextView) childView.findViewById(R.id.cardJobTitle);
                            tJobTitle.setText((String) tempJson.get("title"));

                            tJobSociety = (TextView) childView.findViewById(R.id.cardJobSociety);
                            tJobSociety.setText((String) tempJson.get("society"));

                            tJobDates = (TextView) childView.findViewById(R.id.cardJobDates);
                            tJobDates.setText(sDates);

                            tJobDescr = (TextView) childView.findViewById(R.id.cardJobDescr);
                            tJobDescr.setText((String) tempJson.get("description"));

                            layout.addView(childView);

                        }
                        break;
                    case "Studies":
                        db.execSQL("delete from "+ MainDatabase.StudiesEntry.TABLE_NAME);
                        layout = (LinearLayout) activity.findViewById(R.id.Studies);
                        TextView tStudiesDiploma, tStudiesSchool, tSudiesDates, tStudiesDescr;

                        for (int i = 0; i < jsArray.length(); i++) {
                            JSONObject tempJson = jsArray.getJSONObject(i);


                            sDateFrom = (String) tempJson.get("date_from");
                            sDateTo = (String) tempJson.get("date_to");

                            sDates = getDateString(sDateFrom, sDateTo);

                            // Ajout dans la DB interne
                            // On crée un nouveau jeu de données
                            values = new ContentValues();
                            values.put(MainDatabase.StudiesEntry.COLUMN_NAME_DIPLOMA, (String) tempJson.get("diploma"));
                            values.put(MainDatabase.StudiesEntry.COLUMN_NAME_DATES, sDates);
                            values.put(MainDatabase.StudiesEntry.COLUMN_NAME_SCHOOL, (String) tempJson.get("school"));
                            values.put(MainDatabase.StudiesEntry.COLUMN_NAME_DESCR, (String) tempJson.get("description"));


                            // On insère la nouvelle ligne
                            db.insert(
                                    MainDatabase.StudiesEntry.TABLE_NAME,
                                    null, // auto-id
                                    values);


                            // JobInfo
                            childView = mInflater.inflate(R.layout.studies_card, null);

                            tStudiesDiploma = (TextView) childView.findViewById(R.id.cardStudiesTitle);
                            tStudiesDiploma.setText((String) tempJson.get("diploma"));

                            tStudiesSchool = (TextView) childView.findViewById(R.id.cardStudiesSchool);
                            tStudiesSchool.setText((String) tempJson.get("school"));

                            tSudiesDates = (TextView) childView.findViewById(R.id.cardStudiesDates);
                            tSudiesDates.setText(sDates);

                            tStudiesDescr = (TextView) childView.findViewById(R.id.cardStudiesDescr);
                            tStudiesDescr.setText((String) tempJson.get("description"));

                            layout.addView(childView);

                        }
                        break;
                    default:
                        break;
                }

            } catch (JSONException e) {
                //En cas d'erreur...
                throw new RuntimeException(e);
            }
        }
    }
}
