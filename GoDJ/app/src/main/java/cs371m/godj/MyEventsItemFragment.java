package cs371m.godj;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Jasmine on 12/4/2016.
 */

public class MyEventsItemFragment extends DialogFragment {

    private int option;
    private int itemPos;
    private String eventNm;
    private boolean hosting;
    private boolean remove;
    private boolean currentEvent;
    public String[] options1 = {"Attend Event", "View Requested Songs", "Cancel Event"};
    public String[] options2 = {"Attend Event", "View Requested Songs", "Remove from Saved"};
    public String[] options3 = {"View Requested Songs", "Leave this Event"};
    public final int ATTEND = 0;
    public final int REQUESTED_SONGS = 1;
    protected static final int DELETE_CANCEL = 2;

    public interface MyDialogCloseListener
    {
        public void handleDialogClose(int option, int pos, boolean hosting, boolean currentEvent, boolean remove, String eventNm);//or whatever args you want
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        HomePage.mef.handleDialogClose(option, itemPos, hosting, currentEvent, remove, eventNm);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        option = -1;
        itemPos = getArguments().getInt("pos");
        eventNm = getArguments().getString("eventNm");
        remove = false;
        currentEvent = getArguments().getBoolean("currentEvent");
        String eventHost = getArguments().getString("eventHost");
        long startTime = getArguments().getLong("startTime");
        long endTime = getArguments().getLong("endTime");
        final String key = getArguments().getString("key");
        String hostUser = getArguments().getString("hostUser");

        final EventObject eventObject = new EventObject(eventNm, eventNm.toLowerCase(), eventHost, startTime, endTime, key, hostUser);

        hosting = (hostUser.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));


        final String[] choices;
        if (hosting && !currentEvent) {
            choices = options1;
        } else if (currentEvent) {
            choices = options3;
        } else {
            choices = options2;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            builder.setTitle("Options")
                    .setItems(choices, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            option = which;
                            if(option == ATTEND && !currentEvent) {

                                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                final String thisuserName = userName.replaceAll("\\.", "@");
                                final FragmentManager manager = getActivity().getSupportFragmentManager();

                                FirebaseDatabase.getInstance().getReference()
                                        .child("events")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String eventKey = eventObject.getKey();
                                                if(dataSnapshot.hasChild(eventKey)) {
                                                    // event exists in database

                                                    FirebaseDatabase.getInstance().getReference("users").child(thisuserName).child("eventAttending").setValue(eventObject.getKey());
                                                } else {
                                                    // event has been removed from the database

                                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            switch (which){
                                                                case DialogInterface.BUTTON_NEGATIVE:
                                                                    //No button clicked
                                                                    break;

                                                                case DialogInterface.BUTTON_POSITIVE:
                                                                    //Yes button clicked
                                                                    HomePage.mef.handleDialogClose(option, itemPos, hosting, currentEvent, true, eventNm);
                                                                    break;
                                                            }
                                                        }
                                                    };
                                                    alert.setTitle("Event No Longer Exists");
                                                    alert.setMessage("Would you like to remove it from your list?")
                                                            .setNegativeButton("No", dialogClickListener)
                                                            .setPositiveButton("Yes", dialogClickListener).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            } else if((option == REQUESTED_SONGS && !currentEvent) || (currentEvent && option == ATTEND)) {
                                ShowSongRequest showSongRequests = new ShowSongRequest();
                                Bundle b = new Bundle();
                                b.putBoolean("hosting", hosting);
                                b.putString("key", key);
                                b.putInt("pos", itemPos);
                                showSongRequests.setArguments(b);
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.main_frame, showSongRequests, "showSongRequests");
                                ft.addToBackStack("showSongRequests");
                                ft.commit();
                            }
                        }
                    });
        return builder.create();
    }
}