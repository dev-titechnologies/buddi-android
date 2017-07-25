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

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "Buddy";

    // Contacts table name
    private static final String TABLE_CATEGORY = "Category";

    private static final String Category_Sub = "Category_Sub";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";

    private static final String CAT_ID = "cat_id";
    private static final String CAT_NAME = "cat_name";
    private static final String CAT_IMAGE = "cat_image";
    private static final String CAT_DESC = "cat_desc";
    private static final String CAT_STATUS = "cat_status";


    private static final String SUB_CAT_ID = "sub_cat_id";
    private static final String SUBCAT_NAME = "sub_cat_name";

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

        db.execSQL(CREATE_SUB_CAT_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + Category_Sub);

        // Create tables again
        onCreate(db);
    }



    public void insertSubCategory(String cat_id,JSONArray category){



        SQLiteDatabase db = this.getWritableDatabase();

        for (int i=0;i<category.length();i++) {
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



    public void insertCategory(JSONArray category){

        deleteCat();
        deleteSubContact();



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
            // Inserting main cat
            db.insert(TABLE_CATEGORY, null, values);
                // Inserting sub cat

                insertSubCategory(item.getString("category_id"),item.getJSONArray("sub_categories"));

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

    public JSONArray getAllCAT() {
     JSONArray categoryList = new JSONArray();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY+" WHERE "+CAT_STATUS+" = 1";

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

    public HashSet getSubCat(ArrayList selectedID) {
        SQLiteDatabase db = this.getReadableDatabase();

        HashSet<String> subCategoryList=new HashSet<String>();
//        JSONArray subCategoryList = new JSONArray();

        for (int i =0;i< selectedID.size();i++) {

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



                            subCategoryList.add( cursor.getString(1));



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

        JSONObject sub= new JSONObject();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Category_Sub, new String[] { CAT_ID,
                        SUB_CAT_ID, SUBCAT_NAME }, SUB_CAT_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        try {
            sub.put("subCat_id",cursor.getString(1));

            sub.put("subCat_name",cursor.getString(2));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // return contact
        return sub;
    }


}