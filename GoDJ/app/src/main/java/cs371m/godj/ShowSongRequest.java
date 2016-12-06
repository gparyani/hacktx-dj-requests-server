package cs371m.godj;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static android.support.design.widget.Snackbar.make;
import static cs371m.godj.TrackPageActivity.ONE_HOUR;
import static cs371m.godj.TrackPageActivity.REQ_LIMIT;

/**
 * Created by Jasmine on 12/4/2016.
 */

public class ShowSongRequest extends Fragment implements TrackItemOptionsFragment.MyDialogCloseListener {

    protected ListView listView;
    protected List<TrackDatabaseObject> tracks;
    protected SongRequestAdapter songRequestAdapter;
    //protected boolean hosting;
    protected int limit;
    protected String key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.favorite_layout, container, false);


        listView = (ListView) v.findViewById(R.id.fav_list_view);
        tracks = new ArrayList<>();
        songRequestAdapter = new SongRequestAdapter(getContext());
        key = getArguments().getString("key");
        final boolean hosting = getArguments().getBoolean("hosting");

        /*TODO: set limit for dj view vs. user view*/
        limit = (hosting) ? 20 : 50;

        //final String thisUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().replaceAll("\\.", "@");
        System.out.println("SEARCHING: " + key);
//        FirebaseDatabase.getInstance().getReference("eventPlaylists")
//                .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot trackSnapshot: dataSnapshot.getChildren()) {
//                    //String key = eventSnapshot.getKey();
//                    TrackDatabaseObject trackDatabaseObject = trackSnapshot.getValue(TrackDatabaseObject.class);
//                    //eventObject.setKey(key);
//                    Log.d("eventByName ", trackDatabaseObject.getTrackName());
//                    tracks.add(trackDatabaseObject);
//                }
//
//
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        TextView songHeader = new TextView(getActivity());
        songHeader.setText("Song Requests");
        songHeader.setTextSize(20);
        songHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        songHeader.setPadding(0, 0, 0, 50);
        songHeader.setGravity(0x01);
        songHeader.setTypeface(songHeader.getTypeface(), 1);

        listView.addHeaderView(songHeader, null, false);
        listView.setAdapter(songRequestAdapter);
        songRequestAdapter.changeList(tracks);
        songRequestAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TrackDatabaseObject track = tracks.get(position - 1);
                String uri = track.getTrackURI();
                TrackItemOptionsFragment trackItemOptionsFragment = new TrackItemOptionsFragment();
                Bundle b = new Bundle();
                b.putString("uri", uri);
                b.putBoolean("hosting", hosting);
                b.putString("artistName", track.getArtistName());
                b.putString("trackName", track.getTrackName());
                b.putInt("pos", position - 1);
                trackItemOptionsFragment.setArguments(b);
                trackItemOptionsFragment.show(getActivity().getSupportFragmentManager(), "options");
            }
        });

        /*TODO: seems to be updating real time correctly and displaying in general, if breaks come here*/
        String thisUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        thisUserName = thisUserName.replaceAll("\\.", "@");
        FirebaseDatabase.getInstance().getReference("users").child(thisUserName).child("eventAttending").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = (String)dataSnapshot.getValue();
                System.out.println("event attend val: " + key);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference("eventPlaylists").child(key).orderByPriority().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TrackDatabaseObject trackDatabaseObject = dataSnapshot.getValue(TrackDatabaseObject.class);
                tracks.add(trackDatabaseObject);
                //Collections.sort(tracks, new TrackCompare());
                songRequestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println("onChildChanged");
                TrackDatabaseObject trackDatabaseObject = dataSnapshot.getValue(TrackDatabaseObject.class);
                /*TODO: Inefficient while loop*/
                boolean found = false;
                int index = 0;
                while(!found && index < tracks.size()) {
                    if(tracks.get(index).getTrackURI().equals(trackDatabaseObject.getTrackURI())) {
                        found = true;
                        if(tracks.get(index).getPriority() != trackDatabaseObject.getPriority()) {
                            tracks.get(index).setPriority(trackDatabaseObject.getPriority());
                            Collections.sort(tracks, new TrackCompare());
                            songRequestAdapter.notifyDataSetChanged();
                        }
                    }
                    index++;
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }


    @Override
    public void handleDialogClose(int pos, int option, boolean hosting, boolean selectedOption) {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
        if(selectedOption && option == TrackItemOptionsFragment.PLAY_UPVOTE && !hosting) {

            final int position = pos;

            String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            final String thisUserName = userName.replaceAll("\\.", "@");
            final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            db.child("users").child(thisUserName).child("eventAttending")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String currEvent = (String) dataSnapshot.getValue();
                            if (!currEvent.equals("none")) {
                                final DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference()
                                        .child("events");
                                eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(currEvent)) {
                                            eventsRef.child(currEvent).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    EventObject eventObject = dataSnapshot.getValue(EventObject.class);
                                                    long eventStart = eventObject.getStartTime();
                                                    long eventEnd = eventObject.getEndTime();
                                                    long currTime = System.currentTimeMillis();

                                                    final String eventName = eventObject.getEventName();


                                                    if(currTime > eventStart && currTime < eventEnd) {
                                                        final DatabaseReference reqRef = FirebaseDatabase.getInstance()
                                                                .getReference("upvotes")
                                                                .child(thisUserName).child(currEvent);
                                                        reqRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                RequestObject requestObject = dataSnapshot.getValue(RequestObject.class);
                                                                if(requestObject != null) {
                                                                    if (requestObject.getNumRequests() > 0) {
                                                                        if (requestObject.getNumRequests() == REQ_LIMIT) {
                                                                            long currTime = System.currentTimeMillis();
                                                                            if (currTime > requestObject.getNextAvailableRequest()) {
                                                                                requestObject.setNumRequests(1);
                                                                                requestObject.setFirstRequestTime(currTime);
                                                                                requestObject.setNextAvailableRequest(currTime + ONE_HOUR);
                                                                                reqRef.setValue(requestObject);
                                                                            } else {
                                                                        /*TODO: Not quite on the dot timewise*/
                                                                                System.out.println("REQ_LIMIT REACHED");

                                                                                Date d = new Date(requestObject.getNextAvailableRequest());
                                                                                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                                                                                String nextTime = sdf.format(d);
                                                                                Snackbar snack = Snackbar.make(viewGroup, "Your upvote limit has been reached. Upvotes available again at " + nextTime + ".", Snackbar.LENGTH_LONG);
                                                                                View view = snack.getView();
                                                                                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                                                                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                                                snack.show();
                                                                            }
                                                                        } else if (requestObject.getNumRequests() < REQ_LIMIT) {
                                                                            int priority = tracks.get(position).getPriority() + 1;
                                                                            tracks.get(position).setPriority(priority);
                                                                            songRequestAdapter.notifyDataSetChanged();

                                                                            FirebaseDatabase.getInstance().getReference("eventPlaylists")
                                                                                    .child(key).child(tracks.get(position).getTrackURI()).setValue(tracks.get(position), 0 - priority); //TODO: workaround for firebase database ordering
                                                                            Collections.sort(tracks, new TrackCompare());
                                                                            songRequestAdapter.notifyDataSetChanged();
                                                                            int reqs = requestObject.getNumRequests() + 1;
                                                                            requestObject.setNumRequests(reqs);
                                                                            reqRef.setValue(requestObject);
                                                                            Snackbar snack = Snackbar.make(viewGroup, "Song Upvoted!", Snackbar.LENGTH_LONG);
                                                                            View view = snack.getView();
                                                                            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                                                            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                                            snack.show();
                                                                        }
                                                                    }
                                                                } else {
                                                                    long startTime = System.currentTimeMillis();
                                                                    RequestObject requestObject1 = new RequestObject(1,
                                                                            startTime, startTime + ONE_HOUR);
                                                                    reqRef.setValue(requestObject1);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });

                                                    } else {
                                                        String message = (currTime > eventStart) ? "Upvote Failed. " + eventName + " has already ended." : "Request Failed. " + eventName + " has not yet started.";
                                                        Snackbar snack = Snackbar.make(viewGroup, message, Snackbar.LENGTH_LONG);
                                                        View view = snack.getView();
                                                        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                        snack.show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                        } else {
                                            Snackbar snack = make(viewGroup, "Cannot upvote. The event you are attending no longer exists.", Snackbar.LENGTH_LONG);
                                            View view = snack.getView();
                                            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                            snack.show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });











        } else if(selectedOption && option == TrackItemOptionsFragment.REMOVE_SAVE && hosting) {
            FirebaseDatabase.getInstance().getReference("eventPlaylists")
                    .child(key).child(tracks.get(pos).getTrackURI()).removeValue();
            tracks.remove(pos);
            songRequestAdapter.notifyDataSetChanged();
        }
    }

    class TrackCompare implements Comparator<TrackDatabaseObject> {
        @Override
        public int compare(TrackDatabaseObject o1, TrackDatabaseObject o2) {
            return o2.getPriority() - o1.getPriority();
        }
    }
}
