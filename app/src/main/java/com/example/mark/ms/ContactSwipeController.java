package com.example.mark.ms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import static android.renderscript.ScriptIntrinsicBLAS.LEFT;
import static android.view.Gravity.RIGHT;


public class ContactSwipeController extends ItemTouchHelper.Callback {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private boolean clickable;
    Context mContext;
    LayoutInflater mInflater;
    Drawable deleteIcon;
    Drawable editIcon;
    HashMap<String, String> contactCopy;
    Resources r;
    SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd:kk:mm");
    final ContactInfoAdapter cia = (ContactInfoAdapter) ContactInfoFragment.recyclerView.getAdapter();



    ContactSwipeController(Context context, LayoutInflater inflater) {
        mContext = context;
        if(android.os.Build.VERSION.SDK_INT <= 20){
            deleteIcon = ContextCompat.getDrawable(mContext,R.drawable.ic_session_delete_old);
            editIcon = ContextCompat.getDrawable(mContext,R.drawable.ic_session_edit_old);
        }
        else {
            deleteIcon = ContextCompat.getDrawable(mContext, R.drawable.session_delete);
            editIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_mode_edit_white_24dp);
        }
        mInflater = inflater;
        r = mContext.getResources();

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, RIGHT | LEFT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getBottom() - itemView.getTop();
        boolean dialogHasBeenShown = false;

        if (dX < 0) {

            if (dX > -mContext.getResources().getDisplayMetrics().widthPixels / 3) {
                drawEditTab(itemView, dX, itemHeight, c);

            } else {
                dX = (-mContext.getResources().getDisplayMetrics().widthPixels / 3) - 1;
                drawEditTab(itemView, dX, itemHeight, c);
            }
        } else {

            ColorDrawable deleteBackground = new ColorDrawable(Color.RED);
            Rect deleteBounds = new Rect(itemView.getLeft(),
                    itemView.getTop(),
                    itemView.getLeft() + (int) dX,
                    itemView.getBottom()
            );
            deleteBackground.setBounds(deleteBounds);
            deleteBackground.draw(c);

            int deleteIconTop = itemView.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
            int deleteIconMargin = (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
            int deleteIconLeft = itemView.getLeft() + deleteIconMargin;
            int deleteIconRight = (itemView.getLeft() + deleteIconMargin + deleteIcon.getIntrinsicWidth());
            int deleteIconBottom = deleteIconTop + deleteIcon.getIntrinsicHeight();

            deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            deleteIcon.draw(c);

        }


        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

        contactCopy = (HashMap<String, String>) Login.userContacts.get(viewHolder.getAdapterPosition()).clone();

        if (direction == RIGHT) {

            clickable = true;

            final ContactInfoAdapter cia = (ContactInfoAdapter) ContactInfoFragment.recyclerView.getAdapter();
            final int copyPosition = viewHolder.getAdapterPosition();
            final HashMap<String, String> copyMap = Login.userContacts.get(copyPosition);

            final Snackbar snackbar = Snackbar
                    .make(ContactInfoFragment.contactView, "Contact deleted.", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickable) {
                        Login.userContacts.add(copyPosition, copyMap);
                        cia.notifyItemInserted(copyPosition);
                        clickable = false;
                        firestore.collection(r.getString(R.string.STUDENTS))
                                .document(Login.studentNum)
                                .update(r.getString(R.string.CONTACTS), Login.userContacts)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, "Couldn't re-add contact at this time", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                }
            });

            snackbar.setActionTextColor(Color.rgb(25, 172, 172));

            if (viewHolder.getAdapterPosition() != -1) {
                Login.userContacts.remove(copyPosition);
                cia.notifyItemRemoved(copyPosition);

                firestore.collection(r.getString(R.string.STUDENTS))
                        .document(Login.studentNum)
                        .update(r.getString(R.string.CONTACTS), Login.userContacts)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, "Could not delete contact at this time", Toast.LENGTH_SHORT).show();
                            }
                        });


                snackbar.show();
            }
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            View dialogView = mInflater.inflate(R.layout.add_contact, null);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            final int index = viewHolder.getAdapterPosition();

            final EditText contactDescription = dialogView.findViewById(R.id.contactDescription);
            final EditText contactDetails = dialogView.findViewById(R.id.contactDetails);
            contactDescription.setText(Login.userContacts.get(index).keySet().toArray()[0].toString());
            contactDetails.setText(Login.userContacts.get(index).values().toArray()[0].toString());


            Button confirm = dialogView.findViewById(R.id.contactConfirm);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Login.userContacts.get(index).clear();
                    Login.userContacts.get(index)
                            .put(contactDescription.getText().toString()
                                    ,contactDetails.getText().toString());

                    cia.notifyItemChanged(index);

                    dialog.dismiss();
                    firestore.collection(r.getString(R.string.STUDENTS))
                            .document(Login.studentNum)
                            .update(r.getString(R.string.CONTACTS), Login.userContacts);
                    cia.notifyItemChanged(index);

                }
            });

            Button cancel = dialogView.findViewById(R.id.contactCancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                    cia.notifyItemChanged(index);

                }
            });

            dialog.show();
        }

    }

    public void drawEditTab(View itemView, float dX, int itemHeight, Canvas c) {
        ColorDrawable editBackground = new ColorDrawable(Color.BLUE);


        // Draw the red delete background
        Rect editBounds = new Rect(itemView.getRight() + (int) dX,
                itemView.getTop(),
                itemView.getRight(),
                itemView.getBottom());
        editBackground.setBounds(editBounds);
        editBackground.draw(c);

        // Calculate position of delete icon
        int editIconTop = itemView.getTop() + (itemHeight - editIcon.getIntrinsicHeight()) / 2;
        int editIconMargin = (itemHeight - editIcon.getIntrinsicHeight()) / 2;
        int editIconLeft = itemView.getRight() - editIconMargin - editIcon.getIntrinsicWidth();
        int editIconRight = itemView.getRight() - editIconMargin;
        int editIconBottom = editIconTop + editIcon.getIntrinsicHeight();
        // Draw the delete icon
        editIcon.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom);
        editIcon.draw(c);
    }
}
