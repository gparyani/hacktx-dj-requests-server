package cs371m.godj;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * Created by Jasmine on 12/4/2016.
 */

public class showSongRequests extends Fragment {

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
       // songRequestAdapter.ch
        key = getArguments().getString("key");
        final boolean hosting = getArguments().getBoolean("hosting");
        limit = (hosting) ? 20 : 50;

        final String thisUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().replaceAll("\\.", "@");
        System.out.println("SEARCHING: " + key);
        FirebaseDatabase.getInstance().getReference("eventPlaylists")
                .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot trackSnapshot: dataSnapshot.getChildren()) {
                    //String key = eventSnapshot.getKey();
                    TrackDatabaseObject trackDatabaseObject = trackSnapshot.getValue(TrackDatabaseObject.class);
                    //eventObject.setKey(key);
                    Log.d("eventByName ", trackDatabaseObject.getTrackName());
                    tracks.add(trackDatabaseObject);
                }

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
                        trackItemOptionsFragment.setArguments(b);
                        trackItemOptionsFragment.show(getActivity().getSupportFragmentManager(), "options");
                    }
                });


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

    public static class TrackItemOptionsFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String uri = getArguments().getString("uri");
            boolean hosting = getArguments().getBoolean("hosting");
            AlertDialog.Builder builder;
            if(hosting) {
                builder = new AlertDialog.Builder(getActivity());
                final String[] options = {"Play Song", "Delete"};
                builder.setTitle("Options")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                if (which == 0) {
                                    String uri = "spotify:track:<spotify uri>";
                                    Intent launcher = new Intent( Intent.ACTION_VIEW, Uri.parse(uri) );
                                    startActivity(launcher);
//                                    try {
//                                        final Intent intent = new Intent(Intent.ACTION_MAIN);
//                                        intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
//                                        intent.setComponent(new ComponentName("com.spotify.mobile.android.ui", "com.spotify.mobile.android.ui.Launcher"));
//                                        intent.putExtra(SearchManager.QUERY, artistName + " " + trackName );
//                                        context.startActivity(intent);
//                                    } catch ( ActivityNotFoundException e ) {
//                                        final Intent intent = new Intent(Intent.ACTION_MAIN);
//                                        intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
//                                        intent.setComponent(new ComponentName("com.spotify.mobile.android.ui", "com.spotify.mobile.android.ui.activity.MainActivity"));
//                                        intent.putExtra(SearchManager.QUERY, artistName + " " + trackName );
//                                        context.startActivity(intent);
//                                    }

                                /*TODO: ADD TOAST OR SNACKBAR*/
                                } else if(which == 1) {

                                }
                            }
                        });
            } else {
                builder = new AlertDialog.Builder(getActivity());
                final String[] options = {"Upvote", "Save Song"};
                builder.setTitle("Options")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                if (which == 0) {
                                /*TODO: ADD TOAST OR SNACKBAR*/
                                }
                            }
                        });
            }
            return builder.create();
        }
    }

}
