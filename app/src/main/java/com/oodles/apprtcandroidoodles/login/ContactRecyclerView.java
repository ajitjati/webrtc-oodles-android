package com.oodles.apprtcandroidoodles.login;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oodles.apprtcandroidoodles.R;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ankita on 28/4/17.
 */

public class ContactRecyclerView extends RecyclerView.Adapter<ContactRecyclerView.ContactHolder> {
    Context mContext;
    OnViewClickListener onViewClickListener;
    ArrayList<Contact> contactsArray;
    ArrayList<Contact> originalContactArray = new ArrayList<Contact>();

    public ContactRecyclerView(Context context, OnViewClickListener onViewClickListener, ArrayList<Contact> contactsArray) {
        this.mContext = context;
        this.contactsArray = contactsArray;
        originalContactArray.addAll(contactsArray);
        this.onViewClickListener = onViewClickListener;
    }

    @Override
    public ContactRecyclerView.ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_row, parent, false);
        return new ContactRecyclerView.ContactHolder(listItemView);
    }

    @Override
    public void onBindViewHolder(ContactRecyclerView.ContactHolder holder, final int position) {
        Contact mContact = contactsArray.get(position);
        holder.userNumber.setText(mContact.getNumber());
        holder.mLabel.setText(mContact.getName());
        holder.cardViewMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClickListener.setOnViewClickListner(v, position);
            }
        });
        if (mContact.getOnline().equalsIgnoreCase("true")) {
            holder.offlineOnlineUser.setImageDrawable(mContext.getResources().getDrawable(R.drawable.online_drawable));
        } else {
            holder.offlineOnlineUser.setImageDrawable(mContext.getResources().getDrawable(R.drawable.offline_drawable));
        }
        Uri uri = ContentUris.withAppendedId(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, mContact.getContactId());
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            holder.mImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void performFilter(String str) {
        contactsArray.clear();
        for (Contact contact : originalContactArray) {
            Log.e("android", "value" + contact.getName());
            if (contact.getNumber().toLowerCase().startsWith(str.toString().trim().toLowerCase())) {
                Log.e("contactValue",contact.getName());
                contactsArray.add(contact);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contactsArray.size();
    }


    public class ContactHolder extends RecyclerView.ViewHolder {
        public UserAvatar mImage;
        public TextView mLabel, userNumber;
        ImageView offlineOnlineUser;
        CardView cardViewMain;

        public ContactHolder(View itemView) {
            super(itemView);
            mImage = (UserAvatar) itemView.findViewById(R.id.userImage);
            mLabel = (TextView) itemView.findViewById(R.id.userName);
            userNumber = (TextView) itemView.findViewById(R.id.userNumber);
            cardViewMain = (CardView) itemView.findViewById(R.id.cardViewMain);
            offlineOnlineUser = (ImageView) itemView.findViewById(R.id.offlineOnlineUser);
        }

    }
}
