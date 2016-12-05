package cs371m.godj;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Jasmine on 12/5/2016.
 */

public class TrackItemOptionsFragment extends DialogFragment {

    private int pos;
    private int option;
    private boolean hosting;

    public final static int PLAY_UPVOTE = 0;
    public final static int REMOVE_SAVE = 1;

    public interface MyDialogCloseListener {
        public void handleDialogClose(int pos, int option, boolean hosting);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        ((ShowSongRequest) getActivity().getSupportFragmentManager().
                findFragmentByTag("showSongRequests")).handleDialogClose(pos, option, hosting);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        pos = getArguments().getInt("pos");
        final String uri = getArguments().getString("uri");
        final String artistName = getArguments().getString("artistName");
        final String trackName = getArguments().getString("trackName");
        hosting = getArguments().getBoolean("hosting");
        AlertDialog.Builder builder;
        if(hosting) {
            builder = new AlertDialog.Builder(getActivity());
            final String[] options = {"Play Song", "Remove"};
            builder.setTitle("Options")
                    .setItems(options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            option = which;
                            // The 'which' argument contains the index position
                            // of the selected item
                            if (which == PLAY_UPVOTE) {
                                Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                                intent.setData(Uri.parse(uri));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                /*TODO: ADD TOAST OR SNACKBAR*/
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
                            option = which;
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
//
//        private void playPlayMusic() {
//            Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
//            i.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.internal.receiver.MediaButtonReceiver"));
//            i.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
//            //sendOrderedBroadcast(i, null);
//
//            i = new Intent(Intent.ACTION_MEDIA_BUTTON);
//            i.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.internal.receiver.MediaButtonReceiver"));
//            i.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
//            //sendOrderedBroadcast(i, null);
//        }
}
