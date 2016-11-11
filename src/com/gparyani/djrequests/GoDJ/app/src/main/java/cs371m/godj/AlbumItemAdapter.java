package cs371m.godj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import kaaes.spotify.webapi.android.models.AlbumSimple;

/**
 * Created by rebeccal on 11/10/16.
 */

public class AlbumItemAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<AlbumSimple> albumList;

    public AlbumItemAdapter(Context context) {
        inflater = LayoutInflater.from(context);
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

    public void bindView(AlbumSimple data, View view, ViewGroup parent) {
        ImageView albumImage = (ImageView) view.findViewById(R.id.album_pic);
        TextView albumName = (TextView) view.findViewById(R.id.name_of_album);
        // TextView artistName = (TextView) view.findViewById(R.id.name_of_artist);

        // change image
        albumImage.setImageResource(R.drawable.microphone);
        albumName.setText(data.name);



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
