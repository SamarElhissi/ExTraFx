package samarel.extrafx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ExchangeRates extends AppCompatActivity {
    private TableLayout mTableLayout;
    Database DB;
    ScrollView container;
    TextView date;
    public SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange_rates);
        // setup the table
        mTableLayout = findViewById(R.id.ratesTable);

        mTableLayout.setStretchAllColumns(true);
        date = findViewById(R.id.date);
        DB = new Database(this);
        container = findViewById(R.id.container);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(getApplicationContext(), ConvertAmount.class);
                startActivity(intent);
            }
        });
        swipeContainer = findViewById(R.id.swipeContainer);

        DrawTable();

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                R.color.yellow,
                android.R.color.white,
                R.color.yellow,
                android.R.color.white);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startLoadData();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.notification) {
            Intent intent = new Intent(getApplicationContext(), NotificationDetails.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.contact) {
            Intent intent = new Intent(getApplicationContext(), Contact.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void startLoadData() {
        new LoadDataTask().execute(0);

    }

    public void DrawTable() {
        container.setVisibility(View.INVISIBLE);
        mTableLayout.removeAllViews();
        drawHeader();

        Cursor rates = DB.ExchangeRates();
        int i = 0;
        while (rates.moveToNext()) {
            i++;
            String fromCurrencyName = rates.getString(rates.getColumnIndex("FromCurrencyName"));
            String toCurrencyName = rates.getString(rates.getColumnIndex("ToCurrencyName"));
            String sellExchangeRate = rates.getString(rates.getColumnIndex("SellExchangeRate"));
            String buyExchangeRate = rates.getString(rates.getColumnIndex("BuyExchangeRate"));
            String enteredOn = rates.getString(rates.getColumnIndex("EnteredOn"));
            date.setText("("+enteredOn+")");

            drawRow(fromCurrencyName, toCurrencyName, sellExchangeRate, buyExchangeRate, rates.getCount() != i);
        }


        container.setVisibility(View.VISIBLE);
        swipeContainer.setRefreshing(false);
    }

    private void drawRow(String fromCurrencyName, String toCurrencyName, String sellExchangeRate, String buyExchangeRate, boolean setBorder) {

        final TextView tv = drawCell(sellExchangeRate);
        final TextView tv2 = drawCell(buyExchangeRate);
        final TextView tv3 = drawCell(fromCurrencyName + " - " + toCurrencyName);

        // add table row
        final TableRow tr = new TableRow(this);
        //  tr.setId(1);
        TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        //  trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
        tr.setPadding(0, 0, 0, 0);
        trParams.setMargins(5, 0, 5, 10);
        tr.setLayoutParams(trParams);

        tr.addView(tv);
        tr.addView(tv2);
        tr.addView(tv3);
        if (setBorder)
            tr.setBackgroundResource(R.drawable.row);
        mTableLayout.addView(tr, trParams);

    }

    private TextView drawCell(String st) {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        tv.setGravity(Gravity.CENTER);

        tv.setPadding(0, 25, 0, 25);
        tv.setText(st);
        tv.setTextAppearance(getApplicationContext(), R.style.textRegualr);
        TextViewCompat.setTextAppearance(tv, R.style.textRegualr);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

        return tv;
    }

    private void drawHeader() {

        final TextView tv = drawHeaderTextView("سعر البيع");
        final TextView tv2 = drawHeaderTextView("سعر الشراء");
        final TextView tv3 = drawHeaderTextView("العملة");

        // add table row
        final TableRow tr = new TableRow(this);
        //  tr.setId(1);
        TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        //  trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
        tr.setPadding(0, 0, 0, 0);
        trParams.setMargins(5, 5, 5, 10);
        tr.setLayoutParams(trParams);

        tr.addView(tv);
        tr.addView(tv2);
        tr.addView(tv3);

        mTableLayout.addView(tr, trParams);
    }

    private TextView drawHeaderTextView(String s) {

        TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        tv.setGravity(Gravity.CENTER);
        tv.setTextAppearance(getApplicationContext(), R.style.textBold);
        TextViewCompat.setTextAppearance(tv, R.style.textBold);

        tv.setPadding(0, 25, 0, 25);
        tv.setText(s);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        tv.setBackgroundResource(R.color.yellow);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        return tv;
    }

    //////////////////////////////////////////////////////////////////////////////

    public class LoadDataTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {

            return ApiManager.GetExchangeRates();
        }

        @Override
        protected void onPostExecute(String result) {
            ApiManager.ProcessExchangeRates(DB, result, getApplicationContext());
            DrawTable();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
}
