package com.rafiki.wits.sdp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import org.apache.commons.lang3.ArrayUtils;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class SessionSwipeController extends ItemTouchHelper.Callback {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private boolean clickable;
    Context mContext;
    LayoutInflater mInflater;
    Drawable deleteIcon;
    Drawable editIcon;
    HashMap<String, Object> sessionCopy;
    Resources r;
    SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd:kk:mm");
    boolean rightSwiped = false;
    boolean leftSwiped = false;


    SessionSwipeController(Context context, LayoutInflater inflater) {
        mContext = context;
        if (android.os.Build.VERSION.SDK_INT <= 20) {
            deleteIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_white_24dp);
            editIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_edit_white_24dp);
        } else {
            deleteIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_white_24dp);
            editIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_edit_white_24dp);
        }
        mInflater = inflater;
        r = mContext.getResources();

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
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

   handleSwipe(viewHolder,direction);

    }

    public boolean handleSwipe(final RecyclerView.ViewHolder viewHolder, int direction) {
        final int copyPosition;
        if(viewHolder.getAdapterPosition() == -1){
            sessionCopy = LoginActivity.upcomingTuts.get(0);
            copyPosition = 0;
        }
        else {
            sessionCopy = (HashMap<String, Object>) LoginActivity.upcomingTuts.get(viewHolder.getAdapterPosition()).clone();
            copyPosition = viewHolder.getAdapterPosition();
        }
        sessionCopy.remove("id");
        System.out.println(direction);
        if (direction == ItemTouchHelper.RIGHT) {

            clickable = true;
            rightSwiped = true;
            final TutorScheduleAdapter tutorScheduleAdapter = (TutorScheduleAdapter) TutorScheduleActivity.recyclerView.getAdapter();
            final HashMap<String, Object> copyMap = tutorScheduleAdapter.mDataset.get(copyPosition);
            copyMap.remove("id");

            final Snackbar snackbar = Snackbar
                    .make(TutorScheduleActivity.sessionView, "Session canceled.", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickable) {
                        LoginActivity.upcomingTuts.add(copyPosition, copyMap);
                        tutorScheduleAdapter.notifyItemInserted(copyPosition);
                        clickable = false;
                        firestore.collection("schedule")
                                .add(copyMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {

                                    LoginActivity.upcomingTuts.get(copyPosition).put("id", task.getResult().getId());

                                } else {
                                    Toast.makeText(tutorScheduleAdapter.mContext, "Couldn't re-add session at this time", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

            snackbar.setActionTextColor(Color.rgb(25, 172, 172));

            if (viewHolder.getAdapterPosition() != -1) {
                ((TutorScheduleAdapter) TutorScheduleActivity.recyclerView.getAdapter()).mDataset.remove(copyPosition);
                ((TutorScheduleAdapter) TutorScheduleActivity.recyclerView.getAdapter()).notifyItemRemoved(copyPosition);
                Query query = firestore.collection("schedule")
                        .whereEqualTo(r.getString(R.string.COURSE_CODE), copyMap.get(r.getString(R.string.COURSE_CODE)))
                        .whereEqualTo("startTime", copyMap.get("startTime"));

                query.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (int i = 0; i < task.getResult().size(); ++i) {
                                firestore.collection("schedule")
                                        .document(task.getResult().getDocuments().get(i).getId())
                                        .delete();
                            }
                        }
                    }
                });

                snackbar.show();
            }
        } else {
            final int index;
            if(viewHolder.getAdapterPosition() == -1){
                index = 0;
            }
            else{
                index = viewHolder.getAdapterPosition();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            View dialogView = mInflater.inflate(R.layout.edit_session_dialog, null);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Spinner startTimeSpinner = dialogView.findViewById(R.id.startTimeSpinner);
            Spinner endTimeSpinner = dialogView.findViewById(R.id.endTimeSpinner);
            System.out.println("TUTS " + LoginActivity.upcomingTuts);
            final Timestamp initialTimestamp = (Timestamp) LoginActivity.upcomingTuts.get(index).get("startTime");
            Date initialDate = initialTimestamp.toDate();
            final HashMap<String, Object> copyDay = (HashMap<String, Object>) LoginActivity.upcomingTuts.get(index).clone();
            String initialDateString = sdf.format(initialDate);
            final String initialDay = initialDateString.split(":")[0];
            final Timestamp endTimestamp = (Timestamp) LoginActivity.upcomingTuts.get(index).get("endTime");
            Date endDate = endTimestamp.toDate();
            String endDateString = sdf.format(endDate);
            final String endDay = initialDateString.split(":")[0];

            startTimeSpinner.setSelection(ArrayUtils.indexOf(r.getStringArray(R.array.weekDayTimeArray), initialDateString.split(":")[1] + ":" + initialDateString.split(":")[2]));
            endTimeSpinner.setSelection(ArrayUtils.indexOf(r.getStringArray(R.array.weekDayTimeArray), endDateString.split(":")[1] + ":" + endDateString.split(":")[2]));
            startTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String item = (String) parent.getItemAtPosition(position);
                    String newHours = item.split(":")[0];
                    String newMinutes = item.split(":")[1];
                    String newDateString = initialDay + ":" + newHours + ":" + newMinutes;
                    ParsePosition parse = new ParsePosition(0);
                    Date newDate = sdf.parse(newDateString, parse);
                    Timestamp newTimestamp = new Timestamp(newDate);

                    copyDay.put("startTime", newTimestamp);

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

                    LoginActivity.upcomingTuts.get(index).put("endTime", newTimestamp);

                }
            });
            endTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String item = (String) parent.getItemAtPosition(position);
                    String newHours = item.split(":")[0];
                    String newMinutes = item.split(":")[1];
                    String newDateString = endDay + ":" + newHours + ":" + newMinutes;
                    ParsePosition parse = new ParsePosition(0);
                    Date newDate = sdf.parse(newDateString, parse);
                    Timestamp newTimestamp = new Timestamp(newDate);

                    copyDay.put("endTime", newTimestamp);

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

                    LoginActivity.upcomingTuts.get(index).put("endTime", newTimestamp);

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
                            .whereEqualTo(r.getString(R.string.COURSE_CODE), LoginActivity.upcomingTuts.get(index).get(r.getString(R.string.COURSE_CODE)))
                            .whereEqualTo("startTime", initialTimestamp)
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
                    TutorScheduleActivity.sessionAdapter.notifyItemChanged(index);
                }
            });

            Button cancel = dialogView.findViewById(R.id.editCancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    TutorScheduleActivity.sessionAdapter.notifyItemChanged(index);
//                    LoginActivity.upcomingTuts.get(index).put(r.getString(R.string.HOURS), sessionCopy.get(r.getString(R.string.HOURS)));

                }
            });

            dialog.show();
            leftSwiped = true;
        }
        return true;
    }

    public boolean drawEditTab(View itemView, float dX, int itemHeight, Canvas c) {
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
        return true;
    }
}
