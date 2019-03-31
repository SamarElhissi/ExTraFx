package samarel.extrafx;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class NotificationDetails extends AppCompatActivity {
    Database DB;
    TextView title, body, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String instance = FirebaseInstanceId.getInstance().getToken();
        title = findViewById(R.id.title);
        body = findViewById(R.id.body);
        date = findViewById(R.id.date);

        DB = new Database(this);

        Cursor rate = DB.GetNotification();
        ArrayList<String> items = new ArrayList<String>();
        while (rate.moveToNext()) {
            String ntitle = rate.getString(rate.getColumnIndex("Title"));
            String nbody = rate.getString(rate.getColumnIndex("Body"));
            String ndate = rate.getString(rate.getColumnIndex("EnteredOn"));

            title.setText(ntitle);
            body.setText(nbody);
            date.setText(ndate);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), ExchangeRates.class);
                startActivity(intent);                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
