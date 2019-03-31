package samarel.extrafx;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by SamarEL on 11-Aug-18.
 */

public class ApiManager {

    public static String GetExchangeRates() {

        String OPERATION_NAME = "Currency_Prices";

        String WSDL_TARGET_NAMESPACE = "http://albarasi.a2hosted.com/";

        String SOAP_ADDRESS = "http://albarasi.a2hosted.com/Extra_Ex_Mobile.asmx";

        String SOAP_ACTION = "http://albarasi.a2hosted.com/Currency_Prices";
        SoapObject req = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);

        SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelop.dotNet = true;
        envelop.setOutputSoapObject(req);

        HttpTransportSE aht = new HttpTransportSE(SOAP_ADDRESS);
        Object response = null;
        try {
            aht.call(SOAP_ACTION, envelop);
            response = envelop.getResponse();
        } catch (Exception exception) {
            return null;
        }
        return response.toString();
    }

    public static void ProcessExchangeRates(Database db, String result, Context context) {

        if (result == null) {
            Toast.makeText(context, "للحصول على أخر التحديثات، يرجى التأكد من اتصالك بالانترنت.", Toast.LENGTH_LONG).show();
        } else {
            db.DeleteAllData();
            try {
                JSONArray array = new JSONArray(result);
                for (int j = 0; j < array.length(); j++) {
                    JSONObject currentObject = array.getJSONObject(j);
                    String from = currentObject.getString("CFrom");
                    String to = currentObject.getString("CTo");
                    Double buy_price = currentObject.getDouble("Buy_Price");
                    Double sell_price = currentObject.getDouble("Sell_Price");
                    String date = currentObject.getString("Price_Date");
                    int id1 = db.AddCurrency(from);
                    int id2 = db.AddCurrency(to);
                    db.AddExchangeRate(id1, id2, sell_price, buy_price, date);
                }
            } catch (Exception ex) {
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
