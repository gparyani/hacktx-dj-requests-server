package cs371m.godj;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

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
    public String[] options1 = {"Attend Event", "View Requested Songs", "Cancel Event"};
    public String[] options2 = {"Attend Event", "View Requested Songs", "Remove from Saved"};
    public final int ATTEND = 0;
    public final int REQUESTED_SONGS = 1;
    protected final int DELETE_CANCEL = 2;

    public interface MyDialogCloseListener
    {
        public void handleDialogClose(int option, int pos, boolean hosting, boolean remove, String eventNm);//or whatever args you want
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        System.out.println("activity: " + getActivity());
        System.out.println("getChildFragmentManager: " + getChildFragmentManager());
        ((MyDialogCloseListener) HomePage.mef).handleDialogClose(option, itemPos, hosting, remove, eventNm);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        option = -1;
        itemPos = getArguments().getInt("pos");
        eventNm = getArguments().getString("eventNm");
        hosting = getArguments().getBoolean("hosting");
        remove = false;
        String eventHost = getArguments().getString("eventHost");
        long startTime = getArguments().getLong("startTime");
        long endTime = getArguments().getLong("endTime");
        final String key = getArguments().getString("key");

        final EventObject eventObject = new EventObject(eventNm, eventNm.toLowerCase(), eventHost, startTime, endTime, key);



        final String[] choices = hosting ? options1 : options2;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            builder.setTitle("Options")
                    .setItems(choices, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            option = which;
                            if(option == ATTEND) {
                                String thisUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                thisUserName = thisUserName.replaceAll("\\.", "@");
                                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                final String thisuserName = userName.replaceAll("\\.", "@");

                                FirebaseDatabase.getInstance().getReference()
                                        .child("events")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String eventKey = eventObject.getKey();
                                                boolean found = false;
                                                if(dataSnapshot.hasChild(eventKey)) {
                                                    FirebaseDatabase.getInstance().getReference("users").child(thisuserName).child("eventAttending").setValue(eventObject.getKey());
                                                    Snackbar snack = Snackbar.make(HomePage.mef.getView(), "You are now attending " + eventNm, Snackbar.LENGTH_SHORT);
                                                    View view = snack.getView();
                                                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                    snack.show();
                                                } else {
                                                    System.out.println("event does not exist");
                                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            switch (which){
                                                                case DialogInterface.BUTTON_NEGATIVE:
                                                                    //No button clicked
                                                                    break;

                                                                case DialogInterface.BUTTON_POSITIVE:
                                                                    //Yes button clicked
                                                                    ((MyDialogCloseListener) HomePage.mef).handleDialogClose(option, itemPos, hosting, true, eventNm);
                                                                    break;
                                                            }
                                                        }
                                                    };


                                                    alert.setTitle("Event No Longer Exists");
                                                    alert.setMessage("Would you like to remove it from your list?")
                                                            .setNegativeButton("No", dialogClickListener)
                                                            .setPositiveButton("Yes", dialogClickListener).show();
                                                }
//                                        for(DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
//
//                                            if(eventSnapshot.getKey().equals(eventObject.getKey())) {
//                                            }
//                                        }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            } else if(option == REQUESTED_SONGS){
                                showSongRequests showSongRequests = new showSongRequests();
                                Bundle b = new Bundle();
                                b.putBoolean("hosting", hosting);
                                b.putString("key", key);
                                showSongRequests.setArguments(b);
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                                // Replace any other Fragment with our new Details Fragment with the right data
                                ft.replace(R.id.main_frame, showSongRequests);
                                // Let us come back
                                ft.addToBackStack(null);
                                // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                ft.commit();
                            }
                        }
                    });
        return builder.create();
    }
}