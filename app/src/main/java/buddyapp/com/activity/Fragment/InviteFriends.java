package buddyapp.com.activity.Fragment;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import buddyapp.com.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InviteFriends extends Fragment {
    ListView contactList;
    SimpleCursorAdapter adapter;
    Cursor cur;
    public InviteFriends() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_invite_friends, container, false);
        contactList = (ListView) view.findViewById(R.id.contact_list);
        cur = getContacts();


        final String[] fields = new String[] {ContactsContract.Data.DISPLAY_NAME};

        adapter=new SimpleCursorAdapter(getActivity(),
                        R.layout.contact_list_item,
                        cur,
                        fields,
                        new int[] {R.id.textbox});
        contactList.setAdapter(adapter);

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), cur.getString(i), Toast.LENGTH_SHORT).show();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }


    private Cursor getContacts() {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        String[] projection =
                new String[]{ ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = "upper("+ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC";
            return getActivity().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
    }

}
