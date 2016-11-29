package cs371m.godj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by rebeccal on 11/7/16.
 */

public class ArtistItemAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Artist> artistList;

    public ArtistItemAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (artistList != null) {
            return artistList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (artistList != null) {
            return artistList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (artistList != null) {
            return artistList.get(position).hashCode();
        } else {
            return 0;
        }
    }

    public void bindView(Artist data, View view, ViewGroup parent) {
        ImageView artistImg = (ImageView) view.findViewById(R.id.artist_pic);
        TextView artistName = (TextView) view.findViewById(R.id.artist);
        TextView artistID = (TextView) view.findViewById(R.id.artist_id);
        TextView artistURL = (TextView) view.findViewById(R.id.artist_image_url);

        List<Image> images = data.images;
        if (images.size() > 0) {
            String imageURL = images.get(0).url;
            artistURL.setText(imageURL);
            Picasso.with(view.getContext()).load(imageURL).into(artistImg);
        } else {
            artistURL.setText("");
            artistImg.setImageResource(R.drawable.microphone);
        }

        artistName.setText(data.name);
        artistID.setText(data.id);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Artist data = (Artist) getItem(position);
        if (data == null) {
            throw new IllegalStateException("this should be called when list is not null");
        }

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.artist_item, parent, false);
        }
        bindView(data, convertView, parent);
        return convertView;
    }

    public void changeList(List<Artist> newList) {
        artistList = newList;
    }
}
