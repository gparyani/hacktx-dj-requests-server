package cs371m.godj;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.TrackSimple;

/**
 * Created by Jasmine on 11/10/2016.
 */

public class AlbumPageActivity extends Fragment {

    private ListView listView;
    private AlbumPageAdapter albumPageAdapter;
    private ImageView albumImage;
    private TextView albumNameTV;

    private List<Image> images;
    private List<TrackSimple> tracks;
    private List<ArtistSimple> artists;
    private String albumName;
    private String albumID;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.album_page_layout, container, false);


        EditText et = (EditText) getActivity().findViewById(R.id.searchTerm);
        et.setText("");
        et.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        images = getArguments().getParcelableArrayList("albumImages");
        tracks = getArguments().getParcelableArrayList("albumTracks");
        artists = getArguments().getParcelableArrayList("albumArtists");
        albumName = getArguments().getString("albumName");
        albumID = getArguments().getString("albumID");


        albumImage = (ImageView) v.findViewById(R.id.album_page_album_art);
        albumNameTV = (TextView) v.findViewById(R.id.album_page_album_name);

        final String imgURL = images.get(0).url;
        Picasso.with(getContext()).load(imgURL).into(albumImage);
        albumNameTV.setText(albumName);

        listView = (ListView) v.findViewById(R.id.album_page_list_view);
        albumPageAdapter = new AlbumPageAdapter(getContext());
        listView.setAdapter(albumPageAdapter);
        albumPageAdapter.changeList(tracks);
        albumPageAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TrackPageFragment trackPageFragment = new TrackPageFragment();

                ((HomePage) getActivity()).activeFrags.add(trackPageFragment);
                System.out.println("active frags: " + ((HomePage) getActivity()).activeFrags.size());

                Bundle b = new Bundle();

                TextView trackName = (TextView) view.findViewById(R.id.track_name);
                TextView artistName = (TextView) view.findViewById(R.id.artist_name);


                TextView trackURI = (TextView) view.findViewById(R.id.track_uri);

                b.putString("trackName", trackName.getText().toString());
                b.putString("artistName", artistName.getText().toString());
                b.putString("imageURL", imgURL);
                b.putString("albumName", albumName);
                b.putString("trackURI", trackURI.getText().toString());

                trackPageFragment.setArguments(b);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_frame, trackPageFragment)
                        .hide(AlbumPageActivity.this)
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
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        }
    }

}
