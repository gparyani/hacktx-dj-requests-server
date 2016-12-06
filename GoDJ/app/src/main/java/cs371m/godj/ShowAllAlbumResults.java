package cs371m.godj;

import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.TrackSimple;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jasmine on 11/10/2016.
 */

public class ShowAllAlbumResults extends Fragment {


    private  AlbumItemAdapter albumItemAdapter;
    private ListView listView;
    private List<AlbumSimple> albums;
    private Handler myHandler;
    private String formatTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.all_results_layout, container, false);

        String searchTerm = getArguments().getString("searchTerm");
        if (searchTerm == null) {
            formatTitle = "All Albums";
        } else {
            formatTitle = "\"" + searchTerm + "\"" + " in Songs";
        }
        EditText et = (EditText) getActivity().findViewById(R.id.searchTerm);
        et.setText("");
        et.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(formatTitle);

        myHandler = new Handler();

        listView = (ListView) v.findViewById(R.id.show_all_list_view);
        albums = getArguments().getParcelableArrayList("list");
        albumItemAdapter = new AlbumItemAdapter(getContext());
        listView.setAdapter(albumItemAdapter);
        albumItemAdapter.changeList(albums);
        albumItemAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.album_id);
                String albumID = tv.getText().toString();

                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();

                spotify.getAlbum(albumID, new Callback<Album>() {
                    @Override
                    public void success(Album album, Response response) {
                        myHandler.post(new AlbumSelected(album));
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        });

        return v;
    }


    private class AlbumSelected implements Runnable {

        Album album;

        public AlbumSelected(Album a) {
            album = a;
        }

        @Override
        public void run() {
            AlbumPageActivity albumPageActivity = new AlbumPageActivity();
            ((HomePage) getActivity()).activeFrags.add(albumPageActivity);
            System.out.println("active frags: " + ((HomePage) getActivity()).activeFrags.size());




            List<Image> albumImages = album.images;
            String albumName = album.name;
            String albumID = album.id;
            List<TrackSimple> albumTracks = album.tracks.items;
            List<ArtistSimple> albumArtists = album.artists;

            Bundle b = new Bundle();

            b.putParcelableArrayList("albumImages", (ArrayList) albumImages);
            b.putString("albumName", albumName);
            b.putString("albumID", albumID);
            b.putParcelableArrayList("albumTracks", (ArrayList) albumTracks);
            b.putParcelableArrayList("albumArtists", (ArrayList) albumArtists);

            albumPageActivity.setArguments(b);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_frame, albumPageActivity)
                    .hide(ShowAllAlbumResults.this)
                    .addToBackStack(null)
                    .commit();
        }
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
