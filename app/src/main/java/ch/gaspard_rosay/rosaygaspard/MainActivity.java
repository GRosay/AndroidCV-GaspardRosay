package ch.gaspard_rosay.rosaygaspard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

        LayoutInflater mInflater;

        mInflater = LayoutInflater.from(getApplicationContext());


        LinearLayout layout = (LinearLayout) this.findViewById(R.id.Experiences);

        View childView;
        TextView tJobTitle;
        TextView tJobSociety;
        TextView tJobDates;
        TextView tJobDescr;

        // JobInfo

        childView = mInflater.inflate(R.layout.job_card, null);

        tJobTitle = (TextView) childView.findViewById(R.id.cardJobTitle);
        tJobTitle.setText("Titre");

        tJobSociety = (TextView) childView.findViewById(R.id.cardJobSociety);
        tJobSociety.setText("Entreprise");

        tJobDates = (TextView) childView.findViewById(R.id.cardJobDates);
        tJobDates.setText("Dates");

        tJobDescr = (TextView) childView.findViewById(R.id.cardJobDescr);
        tJobDescr.setText("Description");

        layout.addView(childView);

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
}
