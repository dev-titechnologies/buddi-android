package buddyapp.com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import buddyapp.com.Controller;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "Buddy";

    // Contacts table name
    private static final String TABLE_CATEGORY = "Category";

    private static final String Category_Sub = "Category_Sub";

    private static final String TABLE_HISTORY = "History";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";

    private static final String CAT_ID = "cat_id";
    private static final String CAT_NAME = "cat_name";
    private static final String CAT_IMAGE = "cat_image";
    private static final String CAT_DESC = "cat_desc";
    private static final String CAT_STATUS = "cat_status";


    private static final String SUB_CAT_ID = "sub_cat_id";
    private static final String SUBCAT_NAME = "sub_cat_name";

    private static final String BOOKING_ID = "booking_id";
    private static final String TRAINEE_ID = "trainee_id";
    private static final String TRAINEE_NAME = "trainee_name";
    private static final String TRAINER_NAME = "trainer_name";
    private static final String TRAINER_ID = "trainer_id";
    private static final String CATEGORY = "category";
    private static final String TRAINING_STATUS = "training_status";
    private static final String PAYMENT_STATUS = "payment_status";
    private static final String LOCATION = "location";
    private static final String TRAINED_DATE = "trained_date";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + CAT_ID + " TEXT,"
                + CAT_NAME + " TEXT,"
                + CAT_IMAGE + " TEXT,"
                + CAT_DESC + " TEXT,"
                + CAT_STATUS + " TEXT" +
                ")";

        String CREATE_SUB_CAT_TABLE = "CREATE TABLE " + Category_Sub + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + CAT_ID + " TEXT,"
                + SUB_CAT_ID + " TEXT,"
                + SUBCAT_NAME + " TEXT" +
                ")";

        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + " ("
                + KEY_ID + " INTEGER PRIMARY KEY," + BOOKING_ID + " TEXT,"
                + TRAINEE_ID + " TEXT,"
                + TRAINEE_NAME + " TEXT,"
                + TRAINER_NAME + " TEXT,"
                + TRAINER_ID + " TEXT,"
                + CATEGORY + " TEXT,"
                + TRAINING_STATUS + " TEXT,"
                + PAYMENT_STATUS + " TEXT,"
                + LOCATION + " TEXT,"
                + TRAINED_DATE + " TEXT" + ")";


        db.execSQL(CREATE_SUB_CAT_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + Category_Sub);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        // Create tables again
        onCreate(db);
    }


    public void insertSubCategory(String cat_id, JSONArray category) {


        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < category.length(); i++) {
            try {
                JSONObject item = category.getJSONObject(i);


                ContentValues values = new ContentValues();


                values.put(CAT_ID, cat_id);
                values.put(SUB_CAT_ID, item.getString("subCat_id"));
                values.put(SUBCAT_NAME, item.getString("subCat_name"));


                // Inserting Row
                db.insert(Category_Sub, null, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

//        db.close(); // Closing database connection
    }


    public void insertCategory(JSONArray category) {

        deleteCat();
        deleteSubContact();

        ArrayList<String> arrayaproved = null,arraypending = null;
        SQLiteDatabase db = this.getWritableDatabase();
        try {

        JSONArray aproved = new JSONArray(PreferencesUtils.getData(Constants.approved, Controller.getAppContext(),"[]"));
        JSONArray pending = new JSONArray(PreferencesUtils.getData(Constants.pending, Controller.getAppContext(),"[]"));


        arrayaproved = new ArrayList(aproved.length());
        for(int i=0;i < aproved.length();i++){

                arrayaproved.add(aproved.get(i).toString());

        }

         arraypending = new ArrayList(pending.length());
        for(int i=0;i < pending.length();i++){

                arraypending.add(pending.get(i).toString());

        }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < category.length(); i++) {
            try {
                JSONObject item = category.getJSONObject(i);


                ContentValues values = new ContentValues();


                values.put(CAT_ID, item.getString("category_id"));
                values.put(CAT_NAME, item.getString("category_name"));
                values.put(CAT_DESC, item.getString("category_desc"));
                values.put(CAT_IMAGE, item.getString("category_image"));


                if (arrayaproved.contains(item.getString("category_id"))
                        
                      || arraypending.contains(item.getString("category_id"))  
                        
                        )

                values.put(CAT_STATUS, ("0"));
else
                    values.put(CAT_STATUS, ("1"));

                
                
                
                
                // Inserting main cat





                db.insert(TABLE_CATEGORY, null, values);
                // Inserting sub cat

                insertSubCategory(item.getString("category_id"), item.getJSONArray("sub_categories"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        db.close(); // Closing database connection
    }

    public void deleteSubContact() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Category_Sub, null,
                null);
        db.close();
    }


    public void deleteCat() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORY, null,
                null);
        db.close();
    }
    public long getcountSELECTEDCAT() {

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE " + CAT_STATUS + " = 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // return contact list
        return cursor.getCount();
    }
    public JSONArray getAllCAT() {
        JSONArray categoryList = new JSONArray();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE " + CAT_STATUS + " = 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                JSONObject contact = new JSONObject();

                try {


                    contact.put("category_id", cursor.getString(1));

                    contact.put("category_name", cursor.getString(2));
                    contact.put("category_image", cursor.getString(3));


                    categoryList.put(contact);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }

        // return contact list
        return categoryList;
    }
    public JSONArray getAllCATForTrainee() {
        JSONArray categoryList = new JSONArray();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                JSONObject contact = new JSONObject();

                try {


                    contact.put("category_id", cursor.getString(1));

                    contact.put("category_name", cursor.getString(2));
                    contact.put("category_image", cursor.getString(3));


                    categoryList.put(contact);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }

        // return contact list
        return categoryList;
    }

    public HashSet getSubCat(ArrayList selectedID) {
        SQLiteDatabase db = this.getReadableDatabase();

        HashSet<String> subCategoryList = new HashSet<String>();
//        JSONArray subCategoryList = new JSONArray();

        for (int i = 0; i < selectedID.size(); i++) {

            Cursor cursor = db.query(Category_Sub, new String[]{CAT_ID,
                            SUB_CAT_ID, SUBCAT_NAME}, CAT_ID + "=?",
                    new String[]{String.valueOf(selectedID.get(i))}, null, null, null, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
//                    JSONObject cat = new JSONObject();

                    try {
//                        cat.put("category_id", cursor.getString(0));
//
//                        cat.put("subCat_id", cursor.getString(1));
//                        cat.put("subCat_name", cursor.getString(2));


                        subCategoryList.add(cursor.getString(1));


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } while (cursor.moveToNext());
            }

        }


        // return contact
        return subCategoryList;
    }

    public JSONObject getSubCat(String id) {

        JSONObject sub = new JSONObject();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Category_Sub, new String[]{CAT_ID,
                        SUB_CAT_ID, SUBCAT_NAME}, SUB_CAT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        try {
            sub.put("subCat_id", cursor.getString(1));

            sub.put("subCat_name", cursor.getString(2));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // return contact
        return sub;
    }

    public void insertHistroy(JSONObject jsonObject) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues statement = new ContentValues();
        try {
            statement.put(BOOKING_ID, jsonObject.getString(BOOKING_ID));
            statement.put(TRAINEE_ID, jsonObject.getString(TRAINEE_ID));
            statement.put(TRAINEE_NAME, jsonObject.getString(TRAINEE_NAME));
            statement.put(TRAINER_NAME, jsonObject.getString(TRAINER_NAME));
            statement.put(TRAINER_ID, jsonObject.getString(TRAINER_ID));
            statement.put(CATEGORY, jsonObject.getString(CATEGORY));
            statement.put(TRAINING_STATUS, jsonObject.getString(TRAINING_STATUS));
            statement.put(PAYMENT_STATUS, jsonObject.getString(PAYMENT_STATUS));
            statement.put(LOCATION, jsonObject.getString(LOCATION));
            statement.put(TRAINED_DATE, jsonObject.getString(TRAINED_DATE));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        long count = database.insert(TABLE_HISTORY, null, statement);

    }

    public JSONArray getAllHistory() {
        JSONArray jsonArray = new JSONArray();
        String selectQuery = "SELECT  * FROM " + TABLE_HISTORY + "";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject obj = new JSONObject();
                try {
                    obj.put(BOOKING_ID, cursor.getString(0));
                    obj.put(TRAINEE_ID, cursor.getString(1));
                    obj.put(TRAINEE_NAME, cursor.getString(2));
                    obj.put(TRAINER_NAME, cursor.getString(3));
                    obj.put(TRAINER_ID, cursor.getString(4));
                    obj.put(CATEGORY, cursor.getString(5));
                    obj.put(TRAINING_STATUS, cursor.getString(6));
                    obj.put(PAYMENT_STATUS, cursor.getString(7));
                    obj.put(LOCATION, cursor.getString(8));
                    obj.put(TRAINED_DATE, cursor.getString(9));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }
        return jsonArray;
    }
}