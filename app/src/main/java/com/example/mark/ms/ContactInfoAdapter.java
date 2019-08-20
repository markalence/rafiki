package com.example.mark.ms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactInfoAdapter extends RecyclerView.Adapter<ContactInfoAdapter.ViewHolder> {

    ArrayList<HashMap<String,String>> mDataset;
    public Context mContext;



    @NonNull
    @Override
    public ContactInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        ContactInfoAdapter.ViewHolder vh = new ContactInfoAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactInfoAdapter.ViewHolder holder, int position) {

        holder.setItem(position);

    }

    @Override
    public int getItemCount() {
        return Login.userContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView contactDescription;
        EditText contactInfo;
        public ViewHolder(View itemView) {
            super(itemView);
            contactDescription = itemView.findViewById(R.id.contactDescription);
            contactInfo = itemView.findViewById(R.id.contactInfo);
        }

        public void setItem(int position){
            System.out.println(Login.userContacts);
            String description = Login
                    .userContacts
                    .get(position)
                    .keySet()
                    .toArray()[0]
                    .toString();


            contactDescription.setText(description);
            contactInfo.setText(Login.userContacts.get(position).get(description));
        }


    }
}