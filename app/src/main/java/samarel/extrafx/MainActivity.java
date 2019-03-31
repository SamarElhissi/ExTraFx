package samarel.extrafx;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Database DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DB = new Database(this);
        FirebaseMessaging.getInstance().subscribeToTopic("extrafx");
        CheckIntent(getIntent());

    }

    public void CheckIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String action = extras.getString("action");
            if (action != null && action.equals("OpenNotification")) {
                String title = extras.getString("title");
                String body = extras.getString("text");
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                Date today = Calendar.getInstance().getTime();
                String today_date = df.format(today);

                DB.DeleteNotification();
                DB.AddNotification(title, body, today_date);

                Intent NotificationIntent = new Intent(getApplicationContext(), NotificationDetails.class);
                startActivity(NotificationIntent);
            } else {
                new LoadDataTask().execute(0);
            }
        } else {
            new LoadDataTask().execute(0);
        }
    }

    public class LoadDataTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {

            return ApiManager.GetExchangeRates();
        }

        @Override
        protected void onPostExecute(String result) {
            ApiManager.ProcessExchangeRates(DB, result, getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), ExchangeRates.class);
            startActivity(intent);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
}
