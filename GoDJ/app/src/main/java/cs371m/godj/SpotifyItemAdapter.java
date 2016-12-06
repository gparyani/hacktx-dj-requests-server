package cs371m.godj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Jasmine on 10/23/2016.
 */

public class SpotifyItemAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Track> trackList;

    public SpotifyItemAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (trackList != null) {
            return trackList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (trackList != null) {
            return trackList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (trackList != null) {
            return trackList.get(position).hashCode();
        } else {
            return 0;
        }
    }

    public void bindView(Track data, View view, ViewGroup parent) {
        TextView trackName = (TextView) view.findViewById(R.id.track_name);
//        System.out.println("This is the track name: " + trackName.getText());



        TextView artistName = (TextView) view.findViewById(R.id.artist_name);
        TextView albumName = (TextView) view.findViewById(R.id.album_name);
        TextView middleDot = (TextView) view.findViewById(R.id.middle_dot);
        TextView imageURL = (TextView) view.findViewById(R.id.album_art_url);
        TextView trackURI = (TextView) view.findViewById(R.id.track_uri);

        String artists = "";
        for(int i = 0; i < data.artists.size(); i++) {
            artists += data.artists.get(i).name;
            if(i + 1 < data.artists.size()) {
                artists += ", ";
            }
        }

        trackName.setText(data.name);
        artistName.setText(artists);
        trackURI.setText(data.uri);
        albumName.setText(data.album.name);
        char c = '\u00b7';
        middleDot.setText("" + c);
        String artURL = data.album.images.get(0).url;
        imageURL.setText(artURL);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Track data = (Track) getItem(position);
        if (data == null) {
            throw new IllegalStateException("this should be called when list is not null");
        }

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.spotify_item, parent, false);
        }
        bindView(data, convertView, parent);
        return convertView;
    }

    public void changeList(List<Track> newList) {
        trackList = newList;
    }
}
