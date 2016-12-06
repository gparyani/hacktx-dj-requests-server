package cs371m.godj;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jasmine on 12/1/2016.
 */



public class MyEventsFragment extends Fragment implements MyEventsItemFragment.MyDialogCloseListener {

    protected List<EventObject> attendingEvent;
    protected List<EventObject> hostedEvents;
    protected List<EventObject> savedEvents;

    protected ListView attendingEventLV;
    protected ListView hostedEventsLV;
    protected ListView savedEventsLV;

    protected EventItemAdapter attendingAdapter;
    protected EventItemAdapter hostAdapter;
    protected EventItemAdapter savedAdapter;

    private static Handler handler = new Handler();

    protected String userName;

    static MyEventsFragment newInstance() {
        MyEventsFragment myEventsFragment = new MyEventsFragment();
        return myEventsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_events_layout, container, false);
        attendingEvent = new ArrayList<>();
        hostedEvents = new ArrayList<>();
        savedEvents = new ArrayList<>();

        hostAdapter = new EventItemAdapter(getActivity());
        savedAdapter = new EventItemAdapter(getActivity());
        attendingAdapter = new EventItemAdapter(getActivity());

        attendingEventLV = (ListView) v.findViewById(R.id.attending_event);
        savedEventsLV = (ListView) v.findViewById(R.id.saved_events_lv);
        hostedEventsLV = (ListView) v.findViewById(R.id.hosted_events_lv);

        TextView attendingEventHeader = new TextView(getActivity());
        attendingEventHeader.setText("Attending Event");
        attendingEventHeader.setTextSize(20);
        attendingEventHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        attendingEventHeader.setPadding(0, 0, 0, 50);
        attendingEventHeader.setGravity(0x01);
        attendingEventHeader.setTypeface(attendingEventHeader.getTypeface(), 1);
        attendingEventLV.addHeaderView(attendingEventHeader, null, false);
        attendingEventLV.setAdapter(attendingAdapter);



        /*TODO: use Homepage.userName in place of get user in other places as well... maybe*/
        userName = HomePage.userName;

        final String thisUserName = userName.replaceAll("\\.", "@");
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


                        TextView savedHeader = new TextView(getActivity());
                        savedHeader.setText("Saved Events");
                        savedHeader.setTextSize(20);
                        savedHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        savedHeader.setPadding(0, 0, 0, 50);
                        savedHeader.setGravity(0x01);
                        savedHeader.setTypeface(savedHeader.getTypeface(), 1);

                        savedEventsLV.addHeaderView(savedHeader, null, false);
                        savedEventsLV.setAdapter(savedAdapter);

                        savedAdapter.changeList(savedEvents);
                        UserMainFragment.ListUtils.setDynamicHeight(savedEventsLV);
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
                                MyEventsItemFragment eiof = new MyEventsItemFragment();
                                Bundle b = new Bundle();
                                b.putString("eventNm", eventNm);
                                b.putString("eventHost", eventHost);
                                b.putLong("startTime", startTime);
                                b.putLong("endTime", endTime);
                                b.putString("key", key);
                                b.putInt("pos", pos);
                                b.putBoolean("hosting", false);
                                b.putBoolean("currentEvent", false);
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

                        TextView hostHeader = new TextView(getActivity());
                        hostHeader.setText("Hosting Events");
                        hostHeader.setTextSize(20);
                        hostHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        hostHeader.setPadding(0, 0, 0, 50);
                        hostHeader.setGravity(0x01);
                        hostHeader.setTypeface(hostHeader.getTypeface(), 1);

                        hostedEventsLV.addHeaderView(hostHeader, null, false);


                        hostedEventsLV.setAdapter(hostAdapter);

                        hostAdapter.changeList(hostedEvents);
                        UserMainFragment.ListUtils.setDynamicHeight(hostedEventsLV);
                        hostAdapter.notifyDataSetChanged();

                        hostedEventsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                int pos = position - 1;
                                String eventNm = hostedEvents.get(pos).getEventName();
                                String eventHost = hostedEvents.get(pos).getHostName();
                                long startTime = hostedEvents.get(pos).getStartTime();
                                long endTime = hostedEvents.get(pos).getEndTime();
                                String key = hostedEvents.get(pos).getKey();
                                MyEventsItemFragment eiof = new MyEventsItemFragment();
                                Bundle b = new Bundle();
                                b.putString("eventNm", eventNm);
                                b.putString("eventHost", eventHost);
                                b.putLong("startTime", startTime);
                                b.putLong("endTime", endTime);
                                b.putString("key", key);
                                b.putInt("pos", pos);
                                b.putBoolean("hosting", true);
                                b.putBoolean("currentEvent", false);
                                eiof.setArguments(b);

                                eiof.show(getActivity().getSupportFragmentManager(), "options");
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("users")
                .child(thisUserName)
                .child("eventAttending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String eventKey = (String) dataSnapshot.getValue();
                        attendingEvent.clear();
                        UserMainFragment.ListUtils.setDynamicHeight(attendingEventLV);
                        attendingAdapter.notifyDataSetChanged();


                        FirebaseDatabase.getInstance().getReference("events")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            String eventSnapshotKey;
                                            EventObject eventObject;
                                            for(DataSnapshot eventSnapshot: dataSnapshot.getChildren()) {
                                                eventSnapshotKey = eventSnapshot.getKey();
                                                if (eventSnapshotKey.equals(eventKey)) {
                                                    eventObject = eventSnapshot.getValue(EventObject.class);
                                                    attendingEvent.add(eventObject);
                                                    attendingAdapter.changeList(attendingEvent);
                                                    UserMainFragment.ListUtils.setDynamicHeight(attendingEventLV);
                                                    attendingAdapter.notifyDataSetChanged();
                                                    break;
                                                }
                                            }

                                            attendingEventLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    int pos = position - 1;
                                                    String eventNm = attendingEvent.get(pos).getEventName();
                                                    String eventHost = attendingEvent.get(pos).getHostName();
                                                    long startTime = attendingEvent.get(pos).getStartTime();
                                                    long endTime = attendingEvent.get(pos).getEndTime();
                                                    String key = attendingEvent.get(pos).getKey();
                                                    MyEventsItemFragment eiof = new MyEventsItemFragment();
                                                    Bundle b = new Bundle();
                                                    b.putString("eventNm", eventNm);
                                                    b.putString("eventHost", eventHost);
                                                    b.putLong("startTime", startTime);
                                                    b.putLong("endTime", endTime);
                                                    b.putString("key", key);
                                                    b.putInt("pos", pos);
                                                    b.putBoolean("hosting", false);
                                                    b.putBoolean("currentEvent", true);
                                                    eiof.setArguments(b);
                                                    eiof.show(getFragmentManager(), "options");

                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        return v;
    }


    public void handleDialogClose(int option, int pos, boolean hosting, boolean remove, String eventNm) {
        if(remove) {
            String thisUserName = userName.replaceAll("\\.", "@");
            EventObject eventObject = savedEvents.get(pos);
            savedEvents.remove(pos);
            FirebaseDatabase.getInstance().getReference("users").child(thisUserName)
                    .child("savedEvents").child(eventObject.getKey()).removeValue();
            UserMainFragment.ListUtils.setDynamicHeight(savedEventsLV);
            savedAdapter.notifyDataSetChanged();

        } else if(option == 2) {
            if(hosting) {
                EventObject eventObject = hostedEvents.get(pos);
                System.out.println("canceling: " + eventObject.getKey());
                FirebaseDatabase.getInstance().getReference("events")
                        .child(eventObject.getKey()).removeValue();
                FirebaseDatabase.getInstance().getReference("eventPlaylists")
                        .child(eventObject.getKey()).removeValue();
                String thisUserName = userName.replaceAll("\\.", "@");
                FirebaseDatabase.getInstance().getReference("users").child(thisUserName)
                        .child("hostedEvents").child(eventObject.getKey()).removeValue();
                FirebaseDatabase.getInstance().getReference("eventPlaylists")
                        .child(eventObject.getKey()).removeValue();
                hostedEvents.remove(pos);
                UserMainFragment.ListUtils.setDynamicHeight(hostedEventsLV);
                hostAdapter.notifyDataSetChanged();

            } else {
                String thisUserName = userName.replaceAll("\\.", "@");
                EventObject eventObject = savedEvents.get(pos);
                savedEvents.remove(pos);
                FirebaseDatabase.getInstance().getReference("users").child(thisUserName)
                        .child("savedEvents").child(eventObject.getKey()).removeValue();
                UserMainFragment.ListUtils.setDynamicHeight(savedEventsLV);
                savedAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
