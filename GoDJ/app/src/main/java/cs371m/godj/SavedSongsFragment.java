package cs371m.godj;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jasmine on 12/6/2016.
 */

public class SavedSongsFragment extends Fragment {

    protected ListView listView;
    protected SongRequestAdapter songRequestAdapter;
    protected static List<TrackDatabaseObject> tracks;
    protected static HashMap<String, Integer> queue; /*TODO: Come back when working on playlist/queue*/




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.favorite_layout, container, false);

        tracks = new ArrayList<>();
        listView = (ListView) v.findViewById(R.id.fav_list_view);
        songRequestAdapter = new SongRequestAdapter(getContext());
        queue = new HashMap<>();


        TextView songHeader = new TextView(getActivity());
        songHeader.setText("Saved Songs");
        songHeader.setTextSize(20);
        songHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        songHeader.setPadding(0, 20, 0, 50);
        songHeader.setGravity(0x01);
        songHeader.setTypeface(songHeader.getTypeface(), 1);

        listView.addHeaderView(songHeader, null, false);
        listView.setAdapter(songRequestAdapter);


        String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String _userName = user.replaceAll("\\.", "@");
        FirebaseDatabase.getInstance().getReference().child("users").child(_userName)
                .child("savedSongs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot trackSnapshot: dataSnapshot.getChildren()) {

                    TrackDatabaseObject trackDatabaseObject = trackSnapshot.getValue(TrackDatabaseObject.class);
                    tracks.add(trackDatabaseObject);
                    queue.put(trackDatabaseObject.getTrackURI(), tracks.size() - 1);
                }
                songRequestAdapter.changeList(tracks);
                songRequestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView trackURI = (TextView) view.findViewById(R.id.track_uri);
                TextView trackName = (TextView) view.findViewById(R.id.track_name);
                TextView artistName = (TextView) view.findViewById(R.id.artist_name);
                TextView albumName = (TextView) view.findViewById(R.id.album_name);
                TextView imageURL = (TextView) view.findViewById(R.id.album_art_url);

                String uri = trackURI.getText().toString();
                String name = trackName.getText().toString();
                String artist = artistName.getText().toString();
                String album = albumName.getText().toString();
                String image = imageURL.getText().toString();

                /*try {
                    Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                    intent.setData(Uri.parse(uri));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch(ActivityNotFoundException activityNotFound) {
                    String appID = "com.spotify.music";
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=" + appID));
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException anfe) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + appID));
                        startActivity(intent);
                    }
                }*/








                    getFragmentManager().popBackStack();
                    Intent showTrackPage = new Intent(getContext(), TrackPageActivity.class);

                    showTrackPage.putExtra("trackName", name);
                    showTrackPage.putExtra("artistName", artist);
                    showTrackPage.putExtra("imageURL", image);
                    showTrackPage.putExtra("albumName", album);
                    showTrackPage.putExtra("trackURI", uri);

                    startActivity(showTrackPage);

            }
        });

        return v;
    }
}
