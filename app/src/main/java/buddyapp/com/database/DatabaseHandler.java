package buddyapp.com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Buddy";

    // Contacts table name
    private static final String TABLE_CATEGORY = "Category";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";

    private static final String CAT_ID = "cat_id";
    private static final String CAT_NAME = "cat_name";
    private static final String CAT_IMAGE = "cat_image";
    private static final String CAT_DESC = "cat_desc";
    private static final String CAT_STATUS = "cat_status";



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




        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }



    public void insertCategory(JSONArray category){

        deleteContact();

        SQLiteDatabase db = this.getWritableDatabase();

        for (int i=0;i<category.length();i++) {
            try {
                JSONObject item = category.getJSONObject(i);



            ContentValues values = new ContentValues();


            values.put(CAT_ID, item.getString("category_id"));
            values.put(CAT_NAME, item.getString("category_name"));
                values.put(CAT_DESC, item.getString("category_desc"));
            values.put(CAT_IMAGE, item.getString("category_image"));
            values.put(CAT_STATUS,("1"));
            // Inserting Row
            db.insert(TABLE_CATEGORY, null, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        db.close(); // Closing database connection
    }

    public void deleteContact() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORY, null,
                null);
        db.close();
    }

    public JSONArray getAllCAT() {
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
                    contact.put("category_id",cursor.getString(1));

                contact.put("category_name",cursor.getString(2));
                contact.put("category_image",cursor.getString(3));


                    categoryList.put(contact);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }

        // return contact list
        return categoryList;
    }




}