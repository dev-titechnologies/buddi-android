package buddyapp.com.activity.Fragment;


import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

import buddyapp.com.ContactModel;
import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

/**
 * A simple {@link Fragment} subclass.
 */

public class InviteFriends extends Fragment {
    ListView contactListView;
    JSONArray contactJsonArray;
    ContactAdapter contactAdapter;
    public static int SELECTED_POSITION;
    static ArrayList<String> map = new ArrayList<String>();
    EditText searchContact;
    ArrayList<ContactModel> modelarraylist = new ArrayList<ContactModel>();
    static Button invite;

    public InviteFriends() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_invite_friends, container, false);
        contactListView = (ListView) view.findViewById(R.id.contact_list);
        searchContact = (EditText) view.findViewById(R.id.search_contact);
        new GetContactTask().execute();
        invite = (Button) view.findViewById(R.id.invite);

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new sendInvite().execute();

            }
        });
        searchContact.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                contactAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        return view;
    }

    class GetContactTask extends AsyncTask<String, String, JSONArray> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(getActivity());
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            contactJsonArray = ReadPhoneContacts(getActivity());

            return contactJsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray contactJsonArray) {
            super.onPostExecute(contactJsonArray);
            CommonCall.hideLoader();

            for (int i = 0; i < contactJsonArray.length(); i++) {
                JSONObject jsonObejct = null;
                try {
                    jsonObejct = contactJsonArray.getJSONObject(i);
                    ContactModel cm = new ContactModel(jsonObejct.getString("name"), jsonObejct.getString("number"));
                    modelarraylist.add(cm);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            contactAdapter = new ContactAdapter(modelarraylist, getActivity());
            contactListView.setAdapter(contactAdapter);
        }
    }


    public static JSONArray ReadPhoneContacts(Context cntx) //This Context parameter is nothing but your Activity class's Context
    {

        JSONArray CONTACTS = new JSONArray();

        Cursor cursor = cntx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        Integer contactsCount = cursor.getCount(); // get how many contacts you have in your contacts list


        String userMobile = "";
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();



        /*
        *
        *
        * fetching country code list for matching the local num
        *
        * */
        JSONArray country_codes;

        if (contactsCount > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    //the below cursor will give you details for multiple contacts
                    Cursor pCursor = cntx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    // continue till this cursor reaches to all phone numbers which are associated with a contact in the contact list

                    while (pCursor.moveToNext()) {
                        int phoneType = pCursor.getInt(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        //String isStarred        = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED));
                        String phoneNo = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                        phoneNo = phoneNo.replace(" ", "").replace("(", "").replace(")", "").replaceAll("-", "").replaceFirst("^0+(?!$)", "");

                        JSONObject contacts_item = new JSONObject();
                        try {

                            contacts_item.put("name", contactName);


                            //checking the local contact number with the user registerd number ..--> if the number is
                            // local then it will add contry code with that number

                            if (userMobile.contains("-")) {

                                if (phoneNo.trim().length() == userMobile.substring(userMobile.lastIndexOf("-") + 1).trim().length()


                                        ) {


//                                    CommonCall.PrintLog("NEW NUMBER", userMobile.substring(0, userMobile.indexOf('-')) + phoneNo);

                                    contacts_item.put("number", userMobile.substring(0, userMobile.indexOf('-')) + "-" + phoneNo);

                                } else {

/*
*
*
* checking number with country code and not '-' symbol
*
*
*
* */
                                    contacts_item.put("number", phoneNo);

                                }

                            } else {
                                contacts_item.put("number", phoneNo);
                            }
                            CONTACTS.put(contacts_item);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    pCursor.close();
                }
            }
            cursor.close();
        }
        return CONTACTS;
    }

    public static class ContactAdapter extends BaseAdapter implements Filterable {
        ArrayList<ContactModel> contactArray;
        ArrayList<ContactModel> selectedList;
        Context context;
        private ItemFilter mFilter = new ItemFilter();

        CustomViewHolder holder = null;

        public ContactAdapter(ArrayList<ContactModel> contactArray, Context context) {
            this.contactArray = contactArray;
            this.selectedList = contactArray;
            this.context = context;
        }

        @Override
        public int getCount() {
            return this.contactArray.size();
        }

        @Override
        public Object getItem(int i) {
            return contactArray.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.contact_list_item, viewGroup, false);

            holder = new CustomViewHolder();
            holder.name = (TextView) view.findViewById(R.id.contact_name);
//            holder.number = (TextView) view.findViewById(R.id.contact_number);
            view.setTag(holder);
            holder.name.setId(i);
            holder.name.setText(contactArray.get(i).getName()+"\n"+contactArray.get(i).getNumber());
            view.setTag(R.string.name, contactArray.get(i).getName());
//            holder.number.setText(contactArray.get(i).getNumber());
            view.setTag(R.string.number, contactArray.get(i).getNumber());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String temp = null;
                    if(view.getTag(R.string.number).toString().contains("+91")){
                        temp = view.getTag(R.string.number).toString();
                    }else if(view.getTag(R.string.number).toString().contains("91")){
                        temp = "+"+view.getTag(R.string.number).toString();
                     }
                    else{
                        temp = "+91"+view.getTag(R.string.number).toString();
                    }
                    if (!map.contains(temp)) {
                        map.add(validateNumber(temp).toString());
//                        holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick, 0);
                        ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick, 0);
                    } else {
                        map.remove(temp);
                        ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_circle_outline, 0);

                    }
                    CommonCall.PrintLog("map---", map.toString());
                    if(map.size()>0){
                        invite.setVisibility(View.VISIBLE);
                    }else{
                        invite.setVisibility(View.GONE);
                    }
                }
            });
            return view;
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        public class CustomViewHolder {
            TextView name, number;
        }

        public class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();
                ArrayList<ContactModel> modellist = new ArrayList<ContactModel>();

                final List<ContactModel> list = selectedList;

                int count = list.size();
                final ArrayList<String> nlist = new ArrayList<String>(count);
                final ArrayList<String> numlist = new ArrayList<String>(count);

                String filterableString, filterableNumber;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i).getName();
                    filterableNumber = list.get(i).getNumber();
                    if (filterableString.toLowerCase().contains(filterString)) {
                        ContactModel cm = new ContactModel(filterableString, filterableNumber);
                        modellist.add(cm);
                        nlist.add(filterableString);
                    }
                }

                results.values = modellist;
                results.count = nlist.size();

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contactArray = (ArrayList<ContactModel>) results.values;
                notifyDataSetChanged();
            }

        }

        /**********
         * add country code if the number doesn't contain country code
         * @param number
         * @return
         **********/
        public String validateNumber(String number) {
            String validnumber = null;
            try {

                String scountrycode = "91";
                String smobilenumber = number;

                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(smobilenumber, "IN");
                CommonCall.PrintLog("Phone number++", swissNumberProto + "");
                Boolean isValid = phoneUtil.isValidNumber(swissNumberProto); // returns true
                if (isValid) {
                    CommonCall.PrintLog("Phone number", swissNumberProto.getNationalNumber() + "");
                    validnumber = "+" + scountrycode + swissNumberProto.getNationalNumber();
                } else
                    validnumber = number;
            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString());
            }
            return validnumber;
        }

    }

    class sendInvite extends AsyncTask<String, String, String> {

        JSONObject reqData = new JSONObject();
        String response;

        @Override
        protected void onPreExecute() {
            CommonCall.showLoader(getActivity());


        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONArray jsonArray = new JSONArray(map);
                reqData.put("mobile_array", jsonArray);
                reqData.put("invited_mobile", PreferencesUtils.getData(Constants.mobile, getActivity(), ""));
                response = NetworkCalls.POST(Urls.getInviteURL(), reqData.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    map.clear();
                    invite.setVisibility(View.GONE);

                } else if (obj.getInt("status") == 2) {
                    Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                } else if (obj.getInt("status") == 3) {
                    CommonCall.sessionout(getActivity());
                } else {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
