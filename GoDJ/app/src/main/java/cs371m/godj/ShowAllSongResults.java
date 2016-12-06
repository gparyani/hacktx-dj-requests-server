package cs371m.godj;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Jasmine on 11/8/2016.
 */

public class ShowAllSongResults extends Fragment {

    private SpotifyItemAdapter spotifyItemAdapter;
    private ListView listView;
    private List<Track> tracks;
    private String formatTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.all_results_layout, container, false);

        String searchTerm = getArguments().getString("searchTerm");

        if (searchTerm == null) {
            formatTitle = "All Popular Songs";
        } else {
            formatTitle = "\"" + searchTerm + "\"" + " in Songs";
        }

        EditText et = (EditText) getActivity().findViewById(R.id.searchTerm);
        et.setText("");
        et.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(formatTitle);

        listView = (ListView) v.findViewById(R.id.show_all_list_view);
        tracks = getArguments().getParcelableArrayList("list");
        spotifyItemAdapter = new SpotifyItemAdapter(getContext());
        listView.setAdapter(spotifyItemAdapter);
        spotifyItemAdapter.changeList(tracks);
        spotifyItemAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TrackPageFragment trackPageFragment = new TrackPageFragment();

                ((HomePage) getActivity()).activeFrags.add(trackPageFragment);
                System.out.println("active frags: " + ((HomePage) getActivity()).activeFrags.size());

                TextView trackName = (TextView) view.findViewById(R.id.track_name);
                TextView artistName = (TextView) view.findViewById(R.id.artist_name);
                TextView imageURL = (TextView) view.findViewById(R.id.album_art_url);
                TextView albumName = (TextView) view.findViewById(R.id.album_name);
                TextView trackURI = (TextView) view.findViewById(R.id.track_uri);

                Bundle b = new Bundle();

                b.putString("trackName", trackName.getText().toString());
                b.putString("artistName", artistName.getText().toString());
                b.putString("imageURL", imageURL.getText().toString());
                b.putString("albumName", albumName.getText().toString());
                b.putString("trackURI", trackURI.getText().toString());

                trackPageFragment.setArguments(b);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_frame, trackPageFragment)
                        .hide(ShowAllSongResults.this)
                        .addToBackStack(null)
                        .commit();
            }

        });

        return v;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!isHidden()) {
            EditText et = (EditText) getActivity().findViewById(R.id.searchTerm);
            et.setText("");
            et.setVisibility(View.GONE);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(formatTitle);
        }
    }
}
