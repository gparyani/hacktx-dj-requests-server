package cs371m.godj;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;


import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rebeccal on 11/10/16.
 */

public class AlbumItemAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<AlbumSimple> albumList;
    private Handler myHandler;

    public AlbumItemAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        myHandler = new Handler();
    }

    @Override
    public int getCount() {
        if (albumList != null) {
            return albumList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (albumList != null) {
            return albumList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (albumList != null) {
            return albumList.get(position).hashCode();
        } else {
            return 0;
        }
    }

    public void bindView(AlbumSimple data, final View view, ViewGroup parent) {
        ImageView albumImage = (ImageView) view.findViewById(R.id.album_pic);
        TextView albumName = (TextView) view.findViewById(R.id.name_of_album);
        TextView albumID = (TextView) view.findViewById(R.id.album_id);

        List<Image> images = data.images;
        if (images.size() > 0) {
            String imageURL = images.get(0).url;
            Picasso.with(view.getContext()).load(imageURL).into(albumImage);
        } else {
            albumImage.setImageResource(R.drawable.music_record);
        }

        albumName.setText(data.name);
        albumID.setText(data.id);

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        spotify.getAlbum(data.id, new Callback<Album>() {
            @Override
            public void success(Album album, Response response) {
                myHandler.post(new AlbumSelected(album, view));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private class AlbumSelected implements Runnable {

        private Album album;
        private View view;

        public AlbumSelected(Album album, View view) {
            this.album = album;
            this.view = view;
        }

        @Override
        public void run() {
            TextView artistName = (TextView) view.findViewById(R.id.name_of_artist);
            ArtistSimple artist = album.artists.get(0);
            artistName.setText(artist.name);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumSimple data = (AlbumSimple) getItem(position);
        if (data == null) {
            throw new IllegalStateException("this should be called when list is not null");
        }

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.album_item, parent, false);
        }
        bindView(data, convertView, parent);
        return convertView;
    }

    public void changeList(List<AlbumSimple> newList) {
        albumList = newList;
    }
}
