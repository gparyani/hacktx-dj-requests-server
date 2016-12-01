package cs371m.godj;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    static MyEventsFragment newInstance() {
        MyEventsFragment myEventsFragment = new MyEventsFragment();
        return myEventsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_events_layout, container, false);
        hostedEvents = new ArrayList<>();
        savedEvents = new ArrayList<>();

        String thisUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().replaceAll("\\.", "@");
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
