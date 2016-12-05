package cs371m.godj;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    public void handleDialogClose(int pos, int option, boolean hosting) {
        /*TODO: this is upvoting when i click away, fix it*/
        if(option == TrackItemOptionsFragment.PLAY_UPVOTE && !hosting) {
            System.out.println("pos is : " + pos);
            int priority = tracks.get(pos).getPriority() + 1;
            tracks.get(pos).setPriority(priority);
            songRequestAdapter.notifyDataSetChanged();

            FirebaseDatabase.getInstance().getReference("eventPlaylists")
                    .child(key).child(tracks.get(pos).getTrackURI()).setValue(tracks.get(pos), 0 - priority); //TODO: workaround for firebase database ordering
            Collections.sort(tracks, new TrackCompare());
            songRequestAdapter.notifyDataSetChanged();
        } else if(option == TrackItemOptionsFragment.REMOVE_SAVE && hosting) {
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
