package cs371m.godj;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jasmine on 12/1/2016.
 */



public class MyEventsFragment extends Fragment {

    protected List<EventObject> hostedEvents;
    protected List<EventObject> savedEvents;

    protected ListView hostedEventsLV;
    protected ListView savedEventsLV;

    protected EventItemAdapter hostAdapter;
    protected EventItemAdapter savedAdapter;

    private static Handler handler = new Handler();

    static MyEventsFragment newInstance() {
        MyEventsFragment myEventsFragment = new MyEventsFragment();
        return myEventsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_events_layout, container, false);
        hostedEvents = new ArrayList<>();
        savedEvents = new ArrayList<>();

        final String thisUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().replaceAll("\\.", "@");
        System.out.println("SEARCHING");
        FirebaseDatabase.getInstance().getReference("users")
                .child(thisUserName)
                .child("savedEvents")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot eventSnapshot: dataSnapshot.getChildren()) {
                            String key = eventSnapshot.getKey();
                            EventObject eventObject = eventSnapshot.getValue(EventObject.class);
                            eventObject.setKey(key);
                            Log.d("eventByName ", eventObject.getEventName());
                            savedEvents.add(eventObject);
                        }

                        savedEventsLV = (ListView) getActivity().findViewById(R.id.saved_events_lv);


                        TextView savedHeader = new TextView(getActivity());
                        savedHeader.setText("Saved Events");
                        savedHeader.setTextSize(20);
                        savedHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        savedHeader.setPadding(0, 0, 0, 50);
                        savedHeader.setGravity(0x01);
                        savedHeader.setTypeface(savedHeader.getTypeface(), 1);

                        savedEventsLV.addHeaderView(savedHeader, null, false);
                        savedAdapter = new EventItemAdapter(getActivity());
                        savedEventsLV.setAdapter(savedAdapter);

                        savedAdapter.changeList(savedEvents);
                        UserMainActivity.ListUtils.setDynamicHeight(savedEventsLV);
                        savedAdapter.notifyDataSetChanged();

                        savedEventsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                int pos = position - 1;
                                String eventNm = savedEvents.get(pos).getEventName();
                                String eventHost = savedEvents.get(pos).getHostName();
                                long startTime = savedEvents.get(pos).getStartTime();
                                long endTime = savedEvents.get(pos).getEndTime();
                                String key = savedEvents.get(pos).getKey();
                                EventItemOptionsFragment eiof = new EventItemOptionsFragment();
                                Bundle b = new Bundle();
                                b.putString("eventNm", eventNm);
                                b.putString("eventHost", eventHost);
                                b.putLong("startTime", startTime);
                                b.putLong("endTime", endTime);
                                b.putString("key", key);
                                b.putInt("pos", pos);
                                eiof.setArguments(b);
                                eiof.show(getFragmentManager(), "options");

                            }
                        });



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("users")
                .child(thisUserName)
                .child("hostedEvents")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot eventSnapshot: dataSnapshot.getChildren()) {
                            String key = eventSnapshot.getKey();
                            EventObject eventObject = eventSnapshot.getValue(EventObject.class);
                            eventObject.setKey(key);
                            Log.d("eventByName ", eventObject.getEventName());
                            hostedEvents.add(eventObject);
                        }
                        hostedEventsLV = (ListView) getActivity().findViewById(R.id.hosted_events_lv);



                        TextView hostHeader = new TextView(getActivity());
                        hostHeader.setText("Events I'm Hosting");
                        hostHeader.setTextSize(20);
                        hostHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        hostHeader.setPadding(0, 0, 0, 50);
                        hostHeader.setGravity(0x01);
                        hostHeader.setTypeface(hostHeader.getTypeface(), 1);

                        hostedEventsLV.addHeaderView(hostHeader, null, false);

                        hostAdapter = new EventItemAdapter(getActivity());

                        hostedEventsLV.setAdapter(hostAdapter);

                        hostAdapter.changeList(hostedEvents);
                        UserMainActivity.ListUtils.setDynamicHeight(hostedEventsLV);
                        hostAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        return v;
    }

    protected void updateSavedAdapter() {

    }

    public static class EventItemOptionsFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String eventNm = getArguments().getString("eventNm");
            String eventHost = getArguments().getString("eventHost");
            long startTime = getArguments().getLong("startTime");
            long endTime = getArguments().getLong("endTime");
            String key = getArguments().getString("key");
            final int pos = getArguments().getInt("pos");
            final EventObject eventObject = new EventObject(eventNm, eventNm.toLowerCase(), eventHost, startTime, endTime, key);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final String[] options = {"Attend Event", "Delete"};
            builder.setTitle("Options")
                    .setItems(options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            if(which == 0) {
                                String thisUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                thisUserName = thisUserName.replaceAll("\\.", "@");
                                FirebaseDatabase.getInstance().getReference("users").child(thisUserName).child("eventAttending").setValue(eventObject.getKey());
                                /*TODO: ADD TOAST OR SNACKBAR*/
                            } else if(which == 1) {
                                //handler.post(new OptionsHelper(pos));

                                /*TODO: ADD TOAST OR SNACKBAR*/
                            }
                        }
                    });
            return builder.create();
        }
    }

    class OptionsHelper implements Runnable {

        private int position;

        public OptionsHelper(int pos) {
            position = pos;
        }

        @Override
        public void run() {
            String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            userName = userName.replaceAll("\\.", "@");
            EventObject eventObject = savedEvents.get(position - 1);
            savedEvents.remove(position - 1);
            FirebaseDatabase.getInstance().getReference("users").child(userName)
                    .child("savedEvents").child(eventObject.getKey()).removeValue();
            UserMainActivity.ListUtils.setDynamicHeight(savedEventsLV);
            savedAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
