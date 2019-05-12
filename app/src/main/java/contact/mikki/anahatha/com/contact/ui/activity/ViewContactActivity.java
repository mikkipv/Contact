package contact.mikki.anahatha.com.contact.ui.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import contact.mikki.anahatha.com.contact.R;
import contact.mikki.anahatha.com.contact.model.Contact;
import contact.mikki.anahatha.com.contact.ui.shapes.CircleTransform;

public class ViewContactActivity extends AppCompatActivity {

    ImageView contactImageView;
    ListView contactDetails;
    FloatingActionButton callBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);

        contactDetails = findViewById(R.id.listviewDetails);
        contactImageView = findViewById(R.id.contact_image);
        callBtn = findViewById(R.id.callActionButton);

        Intent intent = getIntent();

        String name = intent.getStringExtra("contactName");
        final String phone = intent.getStringExtra("contactPhone");
        Picasso.with(ViewContactActivity.this).load(intent.getStringExtra("contactImageURI")).placeholder(R.drawable.ic_profile).resize(120, 120).transform(new CircleTransform()).into(contactImageView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getContactDetails(name, phone));
        contactDetails.setAdapter(adapter);

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));//change the number
                if (ActivityCompat.checkSelfPermission(ViewContactActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ViewContactActivity.this,"Please add permission to call",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    startActivity(callIntent);
                }
            }
        });

    }

    //function to get Single contact detail for display

    public List<String> getContactDetails(String name, String phone)
    {
        List<String> contactDetails=new ArrayList<>();

        final ContentResolver cr = getContentResolver();
        String[] projection = new String[] {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Website.URL,ContactsContract.CommonDataKinds.Identity._ID};
        final Cursor cursor = cr.query(ContactsContract.Data.CONTENT_URI, projection, null, null, null);


        if (cursor!=null && cursor.getCount() > 0) {

            Log.d("cursor ok",name+"1");

            if (cursor.moveToFirst()) {

                String  contactId = cursor.getString(3);

                contactDetails.add("Name:"+"\t\t\t\t"+name);
                        contactDetails.add("Phone:"+"\t\t\t\t"+phone);
                String email=cursor.getString(3);
                Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI, Uri.encode("bob"));
                Cursor c = getContentResolver().query(uri,
                        new String[]{ContactsContract.CommonDataKinds.Email.DISPLAY_NAME, ContactsContract.CommonDataKinds.Email.DATA},
                        null, null, null);
                while (c.moveToNext()) {
                    // This would allow you get several email addresses
                    email = c.getString(
                            c.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                }

                c.close();
                        if(email!=null &&!email.isEmpty())
                        {
                            Log.d("email",email);
                            if(email.contains("@"))
                            {
                                contactDetails.add("Email:"+"\t\t\t\t"+email);
                            }
                            else
                            {
                                contactDetails.add("Email:"+"\t\t\t\t");
                            }
                        }

                        String address="";
                        Uri postal_uri = StructuredPostal.CONTENT_URI;
                        Cursor postal_cursor  = getContentResolver().query(postal_uri,null,  ContactsContract.Data.CONTACT_ID + "="+contactId.toString(), null,null);
                        while(postal_cursor.moveToNext())
                        {
                            String street = postal_cursor.getString(postal_cursor.getColumnIndex(StructuredPostal.STREET));
                            String city = postal_cursor.getString(postal_cursor.getColumnIndex(StructuredPostal.CITY));
                            String country = postal_cursor.getString(postal_cursor.getColumnIndex(StructuredPostal.COUNTRY));
                            address=street+", "+city+", "+country;
                        }
                        postal_cursor.close();
                        if(address!=null && !address.isEmpty())
                            contactDetails.add("Address:"+"\t\t\t\t"+address);
                        String website=cursor.getString(2);
                        if(website!=null && !website.isEmpty())
                            contactDetails.add("Website:"+"\t\t\t\t"+address);



            }
        }

        return contactDetails;
    }

}
