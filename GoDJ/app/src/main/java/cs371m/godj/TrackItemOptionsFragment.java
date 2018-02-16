package cs371m.godj;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Jasmine on 12/5/2016.
 */

public class TrackItemOptionsFragment extends DialogFragment {

    private int pos;
    private int thisOption;
    private boolean hosting;
    private boolean selectedOption;
    private String name;
    private String artist;
    private String image;
    private String album;

    public final static int PLAY_UPVOTE = 0;
    public final static int REMOVE_SAVE = 1;

    public interface MyDialogCloseListener {
        public void handleDialogClose(int pos, int option, boolean hosting, boolean selectedOption);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if((getActivity().getSupportFragmentManager().
                findFragmentByTag("showSongRequests")) != null) {
            ((ShowSongRequest) getActivity().getSupportFragmentManager().
                    findFragmentByTag("showSongRequests")).handleDialogClose(pos, thisOption, hosting, selectedOption);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        pos = getArguments().getInt("pos");
        selectedOption = false;
        final String uri = getArguments().getString("uri");
        final String eventKey = getArguments().getString("eventKey");
        name = getArguments().getString("trackName");
        artist = getArguments().getString("artistName");
        album = getArguments().getString("albumName");
        image = getArguments().getString("imageURL");
        hosting = getArguments().getBoolean("hosting");
        AlertDialog.Builder builder;
        if(hosting) {
            builder = new AlertDialog.Builder(getActivity());
            final String[] options = {"Play Song", "Remove"};
            builder.setTitle("Options")
                    .setItems(options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            selectedOption = true;
                            thisOption = which;
                            // The 'which' argument contains the index position
                            // of the selected item
                            if (which == PLAY_UPVOTE) {

//                                getFragmentManager().popBackStack();
//                                Intent showTrackPage = new Intent(getContext(), TrackPageActivity.class);
//
//                                showTrackPage.putExtra("trackName", name);
//                                showTrackPage.putExtra("artistName", artist);
//                                showTrackPage.putExtra("imageURL", image);
//                                showTrackPage.putExtra("albumName", album);
//                                showTrackPage.putExtra("trackURI", uri);
//
//
//
//                                startActivity(showTrackPage);

                                try {
                                    Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                                    intent.setData(Uri.parse(uri));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    FirebaseDatabase.getInstance().getReference("eventPlaylists")
                                            .child(eventKey).child(uri).removeValue();
                                } catch(ActivityNotFoundException activityNotFound) {
                                    String appID = "com.spotify.music";
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("market://details?id=" + appID));
                                        startActivity(intent);
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + appID));
                                        startActivity(intent);
                                    }
                                }
                            } else if(which == REMOVE_SAVE) {

                            }
                        }
                    });
        } else {
            builder = new AlertDialog.Builder(getActivity());
            final String[] options = {"Upvote", "Save Song"};
            builder.setTitle("Options")
                    .setItems(options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            selectedOption = true;
                            thisOption = which;
                            // The 'which' argument contains the index position
                            // of the selected item
                            if (which == 0) {

                                /*TODO: ADD TOAST OR SNACKBAR*/
                            }
                        }
                    });
        }
        return builder.create();
    }

}
