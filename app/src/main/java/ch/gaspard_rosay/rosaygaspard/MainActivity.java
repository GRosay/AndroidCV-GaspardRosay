package ch.gaspard_rosay.rosaygaspard;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        if(savedInstanceState == null){
            new RequestTask(this).execute("http://gaspard-rosay.ch/cv/getExperience.php");
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

    class RequestTask extends AsyncTask<String, String, String> {
        private WeakReference<MainActivity> mActivity;
        public RequestTask(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
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
                LayoutInflater mInflater;

                mInflater = LayoutInflater.from(getApplicationContext());

                MainActivity activity = mActivity.get();

                LinearLayout layout = (LinearLayout) activity.findViewById(R.id.Experiences);

                View childView;
                TextView tJobTitle, tJobSociety, tJobDates, tJobDescr;
                String sDates, sDateFrom, sDateTo;
                Long iDateFrom, iDateTo;
                SimpleDateFormat f = new SimpleDateFormat("MMM yyyy");
                Calendar cDateFrom = Calendar.getInstance(), cDateTo = Calendar.getInstance();

                // On traite le JSON !! {} = OBJET JSON et [] = TABLEAU JSON. Dans notre cas,
                // le format est : {array[{},{},{}]}
                JSONObject jsonObject = new JSONObject(result);

                // On récupère le tableau "array"
                JSONArray jsArray = jsonObject.getJSONArray("array");

                // On parcours notre tableau json et on affiche les lignes
                for(int i = 0; i < jsArray.length(); i++) {
                    JSONObject tempJson = jsArray.getJSONObject(i);

                    sDateFrom = (String) tempJson.get("date_from");
                    try {
                        iDateFrom = Long.parseLong(sDateFrom);
                    } catch(NumberFormatException nfe) {
                        iDateFrom = 0L;
                    }
                    cDateFrom.setTimeInMillis(iDateFrom*1000);
                    Log.d("DateFrom: ", ""+iDateFrom);
                    sDateFrom = f.format(cDateFrom.getTime());

                    sDateTo = (String) tempJson.get("date_to");
                    try {
                        iDateTo = Long.parseLong(sDateTo);
                    } catch(NumberFormatException nfe) {
                        iDateTo = 0L;
                    }
                    cDateTo.setTimeInMillis(iDateTo*1000);
                    Log.d("DateTo: ", ""+iDateTo);
                    sDateTo = f.format(cDateTo.getTime());

                    sDates = "De " + sDateFrom + (iDateTo == 0 ? " à aujourd'hui" : " au " + sDateTo);

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


            } catch (JSONException e) {
                //En cas d'erreur...
                throw new RuntimeException(e);
            }
        }
    }
}
