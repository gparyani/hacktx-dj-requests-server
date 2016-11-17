package cs371m.godj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import kaaes.spotify.webapi.android.models.TrackSimple;

/**
 * Created by Jasmine on 11/11/2016.
 */

public class AlbumPageAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<TrackSimple> trackList;

    public AlbumPageAdapter(Context context) {
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

    public void bindView(TrackSimple data, View view, ViewGroup parent) {
        TextView trackName = (TextView) view.findViewById(R.id.track_name);
        TextView artistName = (TextView) view.findViewById(R.id.artist_name);
        TextView trackURI = (TextView) view.findViewById(R.id.track_uri);

        trackName.setText(data.name);
        String artists = "";
        for(int i = 0; i < data.artists.size(); i++) {
            artists += data.artists.get(i).name;
            if(i + 1 < data.artists.size()) {
                artists += ", ";
            }
        }
        artistName.setText(artists);
        trackURI.setText(data.uri);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TrackSimple data = (TrackSimple) getItem(position);
        if (data == null) {
            throw new IllegalStateException("this should be called when list is not null");
        }

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.spotify_item, parent, false);
        }
        bindView(data, convertView, parent);
        return convertView;
    }

    public void changeList(List<TrackSimple> newList) {
        trackList = newList;
    }
}
