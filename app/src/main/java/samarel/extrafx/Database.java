package samarel.extrafx;

/**
 * Created by SamarEL on 09-Aug-18.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ExTraEx.db";

    public static final String Currency_Table_Name = "Currency";
    public static final String Currency_Name = "Name";

    public static final String Exchange_Rate_Table_Name = "ExchangeRate";
    public static final String From_Currency_Id = "FromCurrencyId";
    public static final String To_Currency_Id = "ToCurrencyId";
    public static final String Sell_Exchange_Rate = "SellExchangeRate";
    public static final String Buy_Exchange_Rate = "BuyExchangeRate";
    public static final String Entered_On = "EnteredOn";
    public static final String Notification_Table_Name = "Notification";
    public static final String Notification_Title = "Title";
    public static final String Notification_Body = "Body";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Currency_Table_Name + " (Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,Name TEXT)");
        db.execSQL("create table " + Exchange_Rate_Table_Name + " (Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,FromCurrencyId INTEGER,ToCurrencyId INTEGER,SellExchangeRate DOUBLE,BuyExchangeRate DOUBLE, EnteredOn TEXT)");
        db.execSQL("create table " + Notification_Table_Name + " (Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,Title TEXT,Body TEXT, EnteredOn TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Currency_Table_Name);
        db.execSQL("DROP TABLE IF EXISTS " + Exchange_Rate_Table_Name);
        db.execSQL("DROP TABLE IF EXISTS " + Notification_Table_Name);
        onCreate(db);
    }

    //-------------------------------------------------------type-----------------------------------------------
    public Cursor ExchangeRates() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT e.*,fromC.Name FromCurrencyName, toC.Name ToCurrencyName from " + Exchange_Rate_Table_Name +
                " as e join " + Currency_Table_Name + " as fromC on e." + From_Currency_Id + " = fromC.Id" +
                " join " + Currency_Table_Name + " as toC on e." + To_Currency_Id + " = toC.Id", null);
        return res;
    }

    public void DeleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Exchange_Rate_Table_Name, null, null);
        db.delete(Currency_Table_Name, null, null);
        db.close();
    }

    public void DeleteNotification() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Notification_Table_Name, null, null);
        db.close();
    }

    public int AddNotification(String title, String body, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Notification_Title, title);
        contentValues.put(Notification_Body, body);
        contentValues.put(Entered_On, date);

        return (int)db.insert(Notification_Table_Name, null, contentValues);
    }

    public int AddCurrency(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * from "+Currency_Table_Name+" WHERE Name = '" + name + "'" ,null);

        if (res.getCount() > 0){
            while (res.moveToNext()) {
                return res.getInt(res.getColumnIndex("Id"));
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(Currency_Name, name);

        return (int)db.insert(Currency_Table_Name, null, contentValues);
    }

    public void AddExchangeRate(int fromCurrencyId, int toCurrencyId, Double sellExchangeRate, Double buyExchangeRate, String enteredOn) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(From_Currency_Id, fromCurrencyId);
        contentValues.put(To_Currency_Id, toCurrencyId);
        contentValues.put(Sell_Exchange_Rate, sellExchangeRate);
        contentValues.put(Buy_Exchange_Rate, buyExchangeRate);
        contentValues.put(Entered_On, enteredOn);

        db.insert(Exchange_Rate_Table_Name, null, contentValues);
    }

    public Cursor GetExchangeRate(String from, String to) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("SELECT e.SellExchangeRate, e.BuyExchangeRate from " + Exchange_Rate_Table_Name +
                " as e join " + Currency_Table_Name + " as fromC on e." + From_Currency_Id + " = fromC.Id" +
                " join " + Currency_Table_Name + " as toC on e." + To_Currency_Id + " = toC.Id"+
                " Where fromC.Name = '"+from+"' and toC.Name = '"+to+"'", null);
        return res;
    }

    public Cursor GetCurrencies() {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("SELECT * from " + Currency_Table_Name , null);
        return res;
    }

    public Cursor GetNotification() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * from " + Notification_Table_Name , null);
        return res;
    }
}
