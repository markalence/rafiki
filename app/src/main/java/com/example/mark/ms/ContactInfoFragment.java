package com.example.mark.ms;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class ContactInfoFragment extends Fragment {

    static RecyclerView recyclerView;
    ContactInfoAdapter cia;
    HashMap<String, String> newContact;
    FirebaseFirestore firestore;
    Resources r;
    StringUtils utils;
    ContactSwipeController swipeController;
    static View contactView;

    public ContactInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firestore = FirebaseFirestore.getInstance();
        r = getContext().getResources();
        newContact = new HashMap<>();
        utils = new StringUtils();
        final View RootView = inflater.inflate(R.layout.fragment_contact_info, container, false);
        contactView = RootView.findViewById(R.id.contactInfoConstraintLayout);
        recyclerView = RootView.findViewById(R.id.contactRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cia = new ContactInfoAdapter();
        recyclerView.setAdapter(cia);
        swipeController = new ContactSwipeController(getContext(),getLayoutInflater());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = RootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getLayoutInflater().inflate(R.layout.add_contact, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Button cancel = view.findViewById(R.id.contactCancel);
                Button confirm = view.findViewById(R.id.contactConfirm);
                final EditText contactDescription = view.findViewById(R.id.contactDescription);
                final EditText contactDetails = view.findViewById(R.id.contactDetails);

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cDescription = contactDescription.getText().toString();
                        String cDetails = contactDetails.getText().toString();

                        if (cDescription.isEmpty() || cDetails.isEmpty()) {
                            Toast.makeText(getContext(), "Fill in all fields", Toast.LENGTH_SHORT).show();
                        } else {
                            newContact.put(utils.capitalize(cDescription), utils.capitalize(cDetails));
                            Login.userContacts.add(newContact);
                            cia.notifyItemChanged(Login.userDays.size()-1);
                            firestore.collection(r.getString(R.string.STUDENTS))
                                    .document(Login.studentNum)
                                    .update(r.getString(R.string.CONTACTS), Login.userContacts)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Could not add contact at this time", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            dialog.dismiss();
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        newContact.clear();
                    }
                });

                dialog.show();
            }
        });

        return RootView;
    }


}
