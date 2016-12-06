package cs371m.godj;

import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TrackSimple;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by rebeccal on 11/17/16.
 */

public class ArtistPageActivity extends Fragment {

    private ImageView artistImage;
    private TextView artistName;

    private TextView popularTitle;
    private TextView albumTitle;
    private TextView relatedTitle;

    private TextView popTrackFooter;
    private TextView albumFooter;
    private TextView relatedArtistFooter;

    private ListView popularTrackListView;
    private ListView albumsListView;
    private ListView relatedArtistListView;

    private List<Track> popularTracks;
    private List<AlbumSimple> albums;
    private List<Artist> relatedArtists;

    private List<Track> fullPopTrackList;
    private List<AlbumSimple> fullAlbumList;
    private List<Artist> fullRelatedArtistList;

    private SpotifyItemAdapter popTracksAdapter;
    private AlbumItemAdapter albumsItemAdapter;
    private ArtistItemAdapter relatedArtistsAdapter;

    private Handler myHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.artist_page_layout, container, false);

        EditText et = (EditText) getActivity().findViewById(R.id.searchTerm);
        et.setText("");
        et.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        artistImage = (ImageView) v.findViewById(R.id.artist_image);
        artistName = (TextView) v.findViewById(R.id.artist_nm);
        popularTrackListView = (ListView) v.findViewById(R.id.popular_tracks);
        albumsListView = (ListView) v.findViewById(R.id.albums);
        relatedArtistListView = (ListView) v.findViewById(R.id.related_artists);
        myHandler = new Handler();

        popularTitle = new TextView(getContext());
        popularTitle.setText("Popular");
        popularTitle.setTextColor(0xffffffff);
        popularTitle.setTextSize(20);
        popularTitle.setTypeface(popularTitle.getTypeface(), 1);
        popularTitle.setPadding(350,35,0,50);
        popularTrackListView.addHeaderView(popularTitle, null, false);
        popularTitle.setVisibility(View.INVISIBLE);

        popTrackFooter = new TextView(getContext());
        popTrackFooter.setText("See more popular songs");
        popTrackFooter.setTextColor(0xffffffff);
        popTrackFooter.setPadding(0, 15, 0, 0);
        popularTrackListView.addFooterView(popTrackFooter);
        popTrackFooter.setVisibility(View.INVISIBLE);

        albumTitle = new TextView(getContext());
        albumTitle.setText("Albums");
        albumTitle.setTextColor(0xffffffff);
        albumTitle.setTextSize(20);
        albumTitle.setTypeface(albumTitle.getTypeface(), 1);
        albumTitle.setPadding(350,0,0,50);
        albumsListView.addHeaderView(albumTitle, null, false);
        albumTitle.setVisibility(View.INVISIBLE);

        albumFooter = new TextView(getContext());
        albumFooter.setText("See more albums");
        albumFooter.setTextColor(0xffffffff);
        albumFooter.setPadding(0, 15, 0, 0);
        albumsListView.addFooterView(albumFooter);
        albumFooter.setVisibility(View.INVISIBLE);

        relatedTitle = new TextView(getContext());
        relatedTitle.setText("Related Artists");
        relatedTitle.setTextColor(0xffffffff);
        relatedTitle.setTextSize(20);
        relatedTitle.setTypeface(relatedTitle.getTypeface(), 1);
        relatedTitle.setPadding(270,0,0,50);
        relatedArtistListView.addHeaderView(relatedTitle, null, false);
        relatedTitle.setVisibility(View.INVISIBLE);

        relatedArtistFooter = new TextView(getContext());
        relatedArtistFooter.setText("See more related artists");
        relatedArtistFooter.setTextColor(0xffffffff);
        relatedArtistFooter.setPadding(0, 15, 0, 0);
        relatedArtistListView.addFooterView(relatedArtistFooter);
        relatedArtistFooter.setVisibility(View.INVISIBLE);

        popularTracks = new ArrayList<>();
        albums = new ArrayList<>();
        relatedArtists = new ArrayList<>();

        fullPopTrackList = new ArrayList<>();
        fullAlbumList = new ArrayList<>();
        fullRelatedArtistList = new ArrayList<>();

        popTracksAdapter = new SpotifyItemAdapter(getContext());
        albumsItemAdapter = new AlbumItemAdapter(getContext());
        relatedArtistsAdapter = new ArtistItemAdapter(getContext());

        popularTrackListView.setAdapter(popTracksAdapter);
        albumsListView.setAdapter(albumsItemAdapter);
        relatedArtistListView.setAdapter(relatedArtistsAdapter);

        callServices();
        onClickListeners();
        return v;
    }


    class TopTracks implements Runnable {

        @Override
        public void run() {
            popularTitle.setVisibility(View.VISIBLE);
            popTracksAdapter.notifyDataSetChanged();
            popTrackFooter.setVisibility(View.VISIBLE);
            popTracksAdapter.notifyDataSetChanged();
            popTracksAdapter.changeList(popularTracks);
            UserMainFragment.ListUtils.setDynamicHeight(popularTrackListView);
            popTracksAdapter.notifyDataSetChanged();
        }
    }

    class ArtistAlbums implements Runnable {

        @Override
        public void run() {
            albumTitle.setVisibility(View.VISIBLE);
            albumsItemAdapter.notifyDataSetChanged();
            albumFooter.setVisibility(View.VISIBLE);
            albumsItemAdapter.notifyDataSetChanged();
            albumsItemAdapter.changeList(albums);
            UserMainFragment.ListUtils.setDynamicHeight(albumsListView);
            albumsItemAdapter.notifyDataSetChanged();

        }
    }

    class RelatedArtists implements Runnable {

        @Override
        public void run() {
            relatedTitle.setVisibility(View.VISIBLE);
            relatedArtistsAdapter.notifyDataSetChanged();
            relatedArtistFooter.setVisibility(View.VISIBLE);
            relatedArtistsAdapter.notifyDataSetChanged();
            relatedArtistsAdapter.changeList(relatedArtists);
            UserMainFragment.ListUtils.setDynamicHeight(relatedArtistListView);
            relatedArtistsAdapter.notifyDataSetChanged();

        }
    }

    private class AlbumSelected implements Runnable {

        private Album album;

        public AlbumSelected(Album a) {
            album = a;
        }

        @Override
        public void run() {
            AlbumPageActivity albumPageActivity = new AlbumPageActivity();
            ((HomePage) getActivity()).activeFrags.add(albumPageActivity);


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
                    .hide(ArtistPageActivity.this)
                    .addToBackStack(null)
                    .commit();
        }
    }


    protected void onClickListeners() {

        popularTrackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int footerPos = parent.getCount() - 1;

                if (footerPos == position) {
                    ShowAllSongResults showAllSongResults = new ShowAllSongResults();
                    ((HomePage) getActivity()).activeFrags.add(showAllSongResults);
                    System.out.println("active frags: " + ((HomePage) getActivity()).activeFrags.size());
                    Bundle b = new Bundle();
                    b.putParcelableArrayList("list", (ArrayList) fullPopTrackList);
                    showAllSongResults.setArguments(b);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_frame, showAllSongResults)
                            .addToBackStack(null)
                            .commit();
                } else {


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
                            .hide(ArtistPageActivity.this)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        albumsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int footerPos = parent.getCount() - 1;

                if (footerPos == position) {
                    ShowAllAlbumResults showAllAlbumResults = new ShowAllAlbumResults();
                    ((HomePage) getActivity()).activeFrags.add(showAllAlbumResults);

                    Bundle b = new Bundle();
                    b.putParcelableArrayList("list", (ArrayList) fullAlbumList);
                    showAllAlbumResults.setArguments(b);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.main_frame, showAllAlbumResults)
                            .hide(ArtistPageActivity.this)
                            .addToBackStack(null)
                            .commit();
                } else {

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

            }
        });

        relatedArtistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int footerPos = parent.getCount() - 1;

                if (footerPos == position) {

                    ShowAllArtistResults showAllArtistResults = new ShowAllArtistResults();

                    ((HomePage) getActivity()).activeFrags.add(showAllArtistResults);
                    System.out.println("active frags: " + ((HomePage) getActivity()).activeFrags.size());

                    Bundle b = new Bundle();
                    b.putParcelableArrayList("list", (ArrayList) fullRelatedArtistList);
                    showAllArtistResults.setArguments(b);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.main_frame, showAllArtistResults)
                            .hide(ArtistPageActivity.this)
                            .addToBackStack(null)
                            .commit();
                } else {

                    TextView artistID = (TextView) view.findViewById(R.id.artist_id);
                    TextView artistName = (TextView) view.findViewById(R.id.artist);
                    TextView artistURL = (TextView) view.findViewById(R.id.artist_image_url);


                    ArtistPageActivity artistPageActivity = new ArtistPageActivity();
                    ((HomePage) getActivity()).activeFrags.add(artistPageActivity);

                    Bundle b = new Bundle();
                    b.putString("artistID", artistID.getText().toString());
                    b.putString("artistName", artistName.getText().toString());
                    b.putString("artistURL", artistURL.getText().toString());
                    artistPageActivity.setArguments(b);


                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.main_frame, artistPageActivity)
                            .hide(ArtistPageActivity.this)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

    }

    protected void callServices() {
        String artistID = getArguments().getString("artistID");
        Picasso.with(getContext()).load(getArguments().getString("artistURL")).into(artistImage);
        artistName.setText(getArguments().getString("artistName"));

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        spotify.getArtistTopTrack(artistID, "US", new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                fullPopTrackList = tracks.tracks;

                int displaySize = Math.min(fullPopTrackList.size(), 5);
                for (int i = 0; i < displaySize; i++) {
                    popularTracks.add(fullPopTrackList.get(i));
                }

                myHandler.post(new TopTracks());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        spotify.getArtistAlbums(artistID, new Callback<Pager<Album>>() {
            @Override
            public void success(Pager<Album> albumPager, Response response) {
                List<Album> albumList = albumPager.items;

                for (Album a: albumList) {
                    fullAlbumList.add(a);
                }

                int displaySize = Math.min(albumList.size(), 5);
                for (int i = 0; i < displaySize; i++) {
                    albums.add(albumList.get(i));
                }

                myHandler.post(new ArtistAlbums());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        spotify.getRelatedArtists(artistID, new Callback<Artists>() {
            @Override
            public void success(Artists artists, Response response) {
                fullRelatedArtistList = artists.artists;

                int displaySize = Math.min(fullRelatedArtistList.size(), 5);
                for (int i = 0; i < displaySize; i++) {
                    relatedArtists.add(fullRelatedArtistList.get(i));
                }

                myHandler.post(new RelatedArtists());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
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
