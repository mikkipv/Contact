package contact.mikki.anahatha.com.contact.model;

import android.media.Image;

/**
 * Created by Swathi on 11/05/19.
 */

public class Contact {

    String name;
    String phone;
    String address;
    String email;
    String mobile;

    String contactImageUri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getContactImage() {
        return contactImageUri;
    }

    public void setContactImage(String contactImageUri) {
        this.contactImageUri = contactImageUri;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }




}
