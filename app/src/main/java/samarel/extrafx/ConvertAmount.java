package samarel.extrafx;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class ConvertAmount extends AppCompatActivity {
    Database DB;
    Spinner from, to, type;
    Button convert;
    EditText amount;
    TextView result, rateResult;
    ImageView exchange;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.convert_amount);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        layout = findViewById(R.id.convertLayout);
        from = findViewById(R.id.fromCurr);
        to = findViewById(R.id.toCurr);
        type = findViewById(R.id.type);
        DB = new Database(this);
        convert = findViewById(R.id.convert);
        amount = findViewById(R.id.amount);
        result = findViewById(R.id.result);
        rateResult = findViewById(R.id.rateResult);
        exchange = findViewById(R.id.exchange);
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.getSelectedItem() != null && to.getSelectedItem() != null) {
                    int selectedFrom = from.getSelectedItemPosition();
                    int selectedTo = to.getSelectedItemPosition();

                    from.setSelection(selectedTo);
                    to.setSelection(selectedFrom);
                }
            }
        });
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);

                if (from.getSelectedItem() != null && to.getSelectedItem() != null) {
                    String selectedFrom = from.getSelectedItem().toString();
                    String selectedTo = to.getSelectedItem().toString();
                    String selectedType = type.getSelectedItem().toString();
                    Double selectedAmount = 0.0;
                    String amountText = amount.getText().toString();
                    try {
                        selectedAmount = Double.parseDouble(amountText);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "أدخل مبلغ للتحويل.", Toast.LENGTH_LONG).show();

                    }


                    Cursor rate = DB.GetExchangeRate(selectedFrom, selectedTo);
                    Double amount = 0.0;
                    Double rateAmount = 0.0;
                    if (rate.getCount() > 0) {
                        while (rate.moveToNext()) {

                            Double sell = rate.getDouble(rate.getColumnIndex("SellExchangeRate"));
                            Double buy = rate.getDouble(rate.getColumnIndex("BuyExchangeRate"));
                            if (selectedType == "بيع") {
                                amount = sell * selectedAmount;
                                rateAmount = sell;
                            } else {
                                amount = buy * selectedAmount;
                                rateAmount = buy;
                            }
                        }
                    } else {
                        Cursor rateReverse = DB.GetExchangeRate(selectedTo, selectedFrom);
                        if (rateReverse.getCount() > 0) {
                            while (rateReverse.moveToNext()) {

                                Double sell = rateReverse.getDouble(rateReverse.getColumnIndex("SellExchangeRate"));
                                Double buy = rateReverse.getDouble(rateReverse.getColumnIndex("BuyExchangeRate"));
                                if (selectedType == "بيع") {
                                    amount = selectedAmount / sell;
                                    rateAmount = 1 / sell;
                                } else {
                                    amount = selectedAmount / buy;
                                    rateAmount = 1 / buy;
                                }
                            }
                        }
                    }

                    if (selectedFrom == selectedTo) {
                        amount = selectedAmount;
                        rateAmount = 1.0;
                    }
                    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                    DecimalFormat formatter = (DecimalFormat) nf;
                    formatter.applyPattern("##################0.#########");

                    result.setText(formatter.format(amount));
                    rateResult.setText(formatter.format(rateAmount));
                } else {
                    Toast.makeText(getApplicationContext(), "يرجى اختيار عملات للتحويل بينها.", Toast.LENGTH_LONG).show();
                }

            }
        });


        Cursor rate = DB.GetCurrencies();
        ArrayList<String> items = new ArrayList<String>();
        while (rate.moveToNext()) {
            items.add(rate.getString(rate.getColumnIndex("Name")));

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, items);
        from.setAdapter(adapter);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, items);
        to.setAdapter(adapter2);
        String[] types = new String[]{"بيع", "شراء"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, types);
        type.setAdapter(adapter3);
        int dollar = items.indexOf("دولار");
        int sheikl = items.indexOf("شيكل");

        from.setSelection(dollar);
        to.setSelection(sheikl);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
