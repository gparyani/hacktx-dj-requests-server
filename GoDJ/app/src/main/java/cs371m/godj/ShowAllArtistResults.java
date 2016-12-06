package cs371m.godj;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Jasmine on 11/8/2016.
 */

public class ShowAllArtistResults extends Fragment {

    private ArtistItemAdapter artistItemAdapter;
    private ListView listView;
    private List<Artist> artists;
    private String formatTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.all_results_layout, container, false);

        String searchTerm = getArguments().getString("searchTerm");

        if (searchTerm == null) {
            formatTitle = "All Related Artists";
        } else {
            formatTitle = "\"" + searchTerm + "\"" + " in Artists";
        }

        EditText et = (EditText) getActivity().findViewById(R.id.searchTerm);
        et.setText("");
        et.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(formatTitle);

        listView = (ListView) v.findViewById(R.id.show_all_list_view);
        artists = getArguments().getParcelableArrayList("list");
        artistItemAdapter = new ArtistItemAdapter(getContext());
        listView.setAdapter(artistItemAdapter);
        artistItemAdapter.changeList(artists);
        artistItemAdapter.notifyDataSetChanged();

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