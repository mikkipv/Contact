package contact.mikki.anahatha.com.contact.ui.activity;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import contact.mikki.anahatha.com.contact.R;
import contact.mikki.anahatha.com.contact.model.Contact;
import contact.mikki.anahatha.com.contact.ui.adapter.ContactAdapter;

public class ContactActivity extends AppCompatActivity {

    final int PERMISSION_READ_CONTACT = 1000;

    TextView contactName;
    ImageView contactImage;
    RecyclerView contactsRecyclerView;
    FloatingActionButton floatingAddButton;
    EditText editTextSearch;

    ContactAdapter adapter;
    List<Contact> contacts;

    static int contactCount = 0;
    MyContentObserver contentObserver = new MyContentObserver();

    private class MyContentObserver extends ContentObserver {

        public MyContentObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Cursor cursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                    null, null);
            Log.d("count 1 :" + cursor.getCount(), " count 2:" + contactCount);

            if (cursor.getCount() > 0 && contactCount != cursor.getCount()) {
                runOnUiThread(new Runnable(){
                    public void run() {
                        // UI code goes here
                        loadContacts();
                        adapter.notifyDataSetChanged();         // updates recycler view with contact change
                    }
                });

            }

        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        contactName = findViewById(R.id.contact_name);
        contactImage = findViewById(R.id.contact_image_view);
        contactsRecyclerView = findViewById(R.id.contact_recycler_view);
        editTextSearch = findViewById(R.id.editTextSearch);
        //floatingAddButton = findViewById(R.id.floatingAddBtn);

        // Create intent filter, add filter action.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("contact_update");

        // Register receiver one to local broadcast manager.

        if (ContextCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            //  requesting the permission if
            // permission not granted
            ActivityCompat.requestPermissions(ContactActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,Manifest.permission.CALL_PHONE},
                    PERMISSION_READ_CONTACT);

        } else {
            // Permission has already been granted
            this.getApplicationContext().getContentResolver().registerContentObserver (ContactsContract.Contacts.CONTENT_URI, true, contentObserver);

            loadContacts();
            searchFilter();

        }

//        floatingAddButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent=new Intent(ContactActivity.this,AddContactActivity.class);
//                startActivity(intent);
//
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_CONTACT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.getApplicationContext().getContentResolver().registerContentObserver (ContactsContract.Contacts.CONTENT_URI, true, contentObserver);
                    loadContacts();
                    searchFilter();

                } else {
                    Toast.makeText(ContactActivity.this, "Permission required , please change settings ", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void loadContacts() {

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null,  "upper("+ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");


        if (cursor.getCount() > 0) {

            contactCount = cursor.getCount();
            contacts = new ArrayList<Contact>();
        }
        if (cursor.moveToFirst()) {
            do {
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String image_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                Contact currentContact = new Contact();
                currentContact.setName(name);
                currentContact.setContactImage(image_uri);
                currentContact.setPhone(phoneNumber);
                contacts.add(currentContact);

            } while (cursor.moveToNext());

            adapter = new ContactAdapter(contacts, ContactActivity.this);
            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            contactsRecyclerView.setHasFixedSize(true);
            contactsRecyclerView.setAdapter(adapter);
            cursor.close();
        }

    }

    public void searchFilter() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                  adapter = new ContactAdapter(contacts, ContactActivity.this);
                 contactsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                List<Contact> filteredContacts = adapter.filter(s.toString());
                if (filteredContacts != null) {
                    adapter = new ContactAdapter(filteredContacts, ContactActivity.this);
                    contactsRecyclerView.setAdapter(adapter);
                }

                if (s.toString().isEmpty()) {
                    adapter = new ContactAdapter(contacts, ContactActivity.this);
                    contactsRecyclerView.setAdapter(adapter);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

                List<Contact> filteredContacts = adapter.filter(s.toString());
                if (filteredContacts != null) {
                    adapter = new ContactAdapter(filteredContacts, ContactActivity.this);
                    contactsRecyclerView.setAdapter(adapter);
                }

            }

        });
    }
}




