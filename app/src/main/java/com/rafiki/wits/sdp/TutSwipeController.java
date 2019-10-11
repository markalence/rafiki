package com.rafiki.wits.sdp;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.renderscript.ScriptIntrinsicBLAS.LEFT;
import static android.view.Gravity.RIGHT;


public class TutSwipeController extends ItemTouchHelper.Callback {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private boolean clickable;
    Context mContext;
    LayoutInflater mInflater;
    Drawable deleteIcon;
    Drawable editIcon;
    HashMap<String, Object> sessionCopy;
    Resources r;
    SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd:kk:mm");


    TutSwipeController(Context context, LayoutInflater inflater) {
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

        sessionCopy = (HashMap<String, Object>) LoginActivity.upcomingTuts.get(viewHolder.getAdapterPosition()).clone();
        sessionCopy.remove("id");

        if (direction == ItemTouchHelper.RIGHT) {

            clickable = true;

            final TutAdapter tutAdapter = (TutAdapter) TutActivity.recyclerView.getAdapter();
            final int copyPosition = viewHolder.getAdapterPosition();
            final HashMap<String, Object> copyMap = tutAdapter.mDataset.get(copyPosition);
            copyMap.remove("id");

            final Snackbar snackbar = Snackbar
                    .make(TutActivity.sessionView, "Session canceled.", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickable) {
                        LoginActivity.upcomingTuts.add(copyPosition, copyMap);
                        tutAdapter.notifyItemInserted(copyPosition);
                        clickable = false;
                        firestore.collection("schedule")
                                .add(copyMap)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {

                                } else {
                                    Toast.makeText(tutAdapter.mContext, "Couldn't re-add session at this time", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

            snackbar.setActionTextColor(Color.rgb(25, 172, 172));

            if (viewHolder.getAdapterPosition() != -1) {
                ((TutAdapter) TutActivity.recyclerView.getAdapter()).mDataset.remove(copyPosition);
                ((TutAdapter) TutActivity.recyclerView.getAdapter()).notifyItemRemoved(copyPosition);
                Query query = firestore.collection("schedule")
                        .whereEqualTo("courseCode", copyMap.get("courseCode"))
                        .whereEqualTo("date", copyMap.get("date"));

                query.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (int i = 0; i < task.getResult().size(); ++i) {
                                if (i == 0) {
                                    firestore.collection("schedule")
                                            .document(task.getResult().getDocuments().get(0).getId())
                                            .delete();
                                }
                            }
                        }
                    }
                });

                snackbar.show();
            }
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            View dialogView = mInflater.inflate(R.layout.edit_session_dialog, null);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Spinner timeSpinner = dialogView.findViewById(R.id.startTimeSpinner);
            Spinner hourSpinner = dialogView.findViewById(R.id.endTimeSpinner);

            final int index = viewHolder.getAdapterPosition();
            final Timestamp initialTimestamp = (Timestamp) LoginActivity.upcomingTuts.get(index).get("date");
            Date initialDate = initialTimestamp.toDate();

            final HashMap<String,Object> copyDay = (HashMap<String, Object>) LoginActivity.upcomingTuts.get(index).clone();

            String initialDateString = sdf.format(initialDate);
            final String initialDay = initialDateString.split(":")[0];

            timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String item = (String) parent.getItemAtPosition(position);
                    String newHours = item.split(":")[0];
                    String newMinutes = item.split(":")[1];
                    String newDateString = initialDay + ":" + newHours + ":" + newMinutes;
                    ParsePosition parse = new ParsePosition(0);
                    Date newDate = sdf.parse(newDateString, parse);
                    Timestamp newTimestamp = new Timestamp(newDate);

                    copyDay.put("date", newTimestamp);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                    int position = 0;
                    String item = (String) parent.getItemAtPosition(position);
                    String newHours = item.split(":")[0];
                    String newMinutes = item.split(":")[1];
                    String newDateString = initialDay + ":" + newHours + ":" + newMinutes;
                    ParsePosition parse = new ParsePosition(0);
                    Date newDate = sdf.parse(newDateString, parse);
                    Timestamp newTimestamp = new Timestamp(newDate);

                    LoginActivity.upcomingTuts.get(index).put("date", newTimestamp);

                }
            });
            hourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    copyDay.put("hours", parent.getItemAtPosition(position));

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    int position = 0;
                   copyDay.put("hours", parent.getItemAtPosition(position));
                }
            });

            Button confirm = dialogView.findViewById(R.id.editConfirm);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    LoginActivity.upcomingTuts.get(index).clear();
                    LoginActivity.upcomingTuts.get(index).putAll(copyDay);
                    firestore.collection(r.getString(R.string.SCHEDULE))
                            .whereEqualTo(r.getString(R.string.USERNAME), LoginActivity.studentNum)
                            .whereEqualTo(r.getString(R.string.DATE), initialTimestamp)
                            .get(Source.SERVER)
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (int i = 0; i < task.getResult().size(); ++i) {
                                        if (i == 0) {
                                            firestore.collection(r.getString(R.string.SCHEDULE))
                                                    .document(task.getResult().getDocuments().get(0).getId())
                                                    .update(LoginActivity.upcomingTuts.get(index));
                                        }
                                    }
                                }
                            });
                    TutActivity.tutAdapter.notifyItemChanged(index);
                }
            });

            Button cancel = dialogView.findViewById(R.id.editCancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    TutActivity.tutAdapter.notifyItemChanged(index);
                    LoginActivity.upcomingTuts.get(index).put(r.getString(R.string.HOURS), sessionCopy.get(r.getString(R.string.HOURS)));

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
