package contact.mikki.anahatha.com.contact.ui.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import contact.mikki.anahatha.com.contact.R;

public class AddContactActivity extends AppCompatActivity {

    final int PERMISSION_ACCESS_FILES=1000;
    int RESULT_LOAD_IMAGE = 1;

    EditText edName,edPhone,edAddress,edMobile,edEmailId;
    ImageButton addImageButton;
    Button saveButton;
    String imageUri,name,address,phone,mobile,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        edName=findViewById(R.id.editTextName);
        edPhone=findViewById(R.id.editTextPhone);
        edAddress=findViewById(R.id.editTextAddress);
        edMobile=findViewById(R.id.editTextMobile);
        edEmailId=findViewById(R.id.editTextEmail);

        addImageButton=findViewById(R.id.addImageButton);
        saveButton=findViewById(R.id.saveButton);

        permissionsGranted();
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(permissionsGranted())
                {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        if(permissionsGranted()) {
            if (validateBasicFields()) {
                if (checkContactAlreadyExist(name, phone)) {
                    Toast.makeText(AddContactActivity.this, "Contact Already exists", Toast.LENGTH_SHORT).show();

                }
                else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, name);
                    contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);

                    if (address != null && !address.isEmpty())
                        contentValues.put(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, name);
                    //  if (mobile != null && !mobile.isEmpty())
                    //     contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, mobile);
                    if (email != null && !email.isEmpty())
                        contentValues.put(ContactsContract.CommonDataKinds.Email.ADDRESS, email);


                    if (imageUri != null && !imageUri.isEmpty())
                        contentValues.put(ContactsContract.CommonDataKinds.Phone.PHOTO_URI, imageUri);


                    Uri rawContactUri = getContentResolver().insert(ContactsContract.Contacts.CONTENT_URI, contentValues);

                    //long ret = ContentUris.parseId(rawContactUri);

                    if (rawContactUri != null) {
                        Toast.makeText(AddContactActivity.this, "Contact Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddContactActivity.this, "Contact Add failed", Toast.LENGTH_SHORT).show();

                    }


                }
            }
        }

            }
        });
    }

    public boolean validateBasicFields()
    {
        name=edName.getText().toString();
        phone=edPhone.getText().toString();
        if(name.isEmpty() || phone.isEmpty())
        {
            Toast.makeText(AddContactActivity.this,"Please enter name and phone fields to save"+name,Toast.LENGTH_SHORT).show();
            return false;
        }

        if(phone.length()<3)
        {
            Toast.makeText(AddContactActivity.this,"Please enter valid number",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean permissionsGranted() {
        if (ContextCompat.checkSelfPermission(AddContactActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(AddContactActivity.this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED ) {
            //  requesting the permission if
            // permission not granted
            ActivityCompat.requestPermissions(AddContactActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_CONTACTS,Manifest.permission.READ_CONTACTS},
                    PERMISSION_ACCESS_FILES);

        } else {
            // Permission has already been granted
            return true;

        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FILES: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   permissionsGranted();
                    return;

                } else {
                    Toast.makeText(AddContactActivity.this, "Permission required , please change settings ", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            addImageButton.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            imageUri = picturePath;

        }
    }

    public boolean checkContactAlreadyExist(String name,String phone) {
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    if (phoneNumber.equalsIgnoreCase(phone) && name.contains(contactName))
                        return true;

                } while (cursor.moveToNext());


            }
        }
        return false;
    }
}
