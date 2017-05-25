package com.oodles.apprtcandroidoodles.login;

import android.net.Uri;

import java.util.Comparator;

/**
 * Created by ankita on 20/4/17.
 */

public class Contact implements Comparator<Contact> {

    public Uri profilePic;
    public String name;

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public long contactId;
    public String number;

    public String online = "false";

    public Uri getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Uri profilePic) {
        this.profilePic = profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }




    @Override
    public int compare(Contact o1, Contact o2) {
        return o2.getOnline().compareTo(o1.getOnline());
    }
}
