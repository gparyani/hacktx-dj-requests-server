package cs371m.godj;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;


/**
 * Created by Jasmine on 9/22/2016.
 * Class used to control media playback
 */

public class PlayerStatus {


    private Context context; // Context we're running in
    //private MediaPlayer player; // MediaPlayer to play songs
    private Handler myHandler; // Handler used for Runnable
    private TimeStamp ts; // used to update song time info
    private SeekBar mySeekBar; // seekbar for the app
    //private String[] songList; // songs in the playlist
    //private int[] songRes; // array of song resIDs
    private ImageButton[] userButs; // array of user control ImageButtons
    private TextView[] songTV; // array of useful TextViews
    //private int currSong; // songRes index for song currently playing
    //private int nextSong; // songRes index for song playing next
    //private int prevSong; // songRes index for previous song
    private boolean isPaused; // did the user press pause?
    private boolean isLooped; // is the song set to loop?



    private final Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            Log.d("Player","OK!");
        }

        @Override
        public void onError(Error error) {
            Log.d("Player","ERROR:" + error);
        }
    };

    private final Player.OperationCallback mOperationCallbackinit = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            Log.d("Player","OK!");
            System.out.println("Girl why you neva ready?: " + MainActivity.mPlayer.getMetadata().currentTrack);
            //myHandler.post(new PlayerInit());
            //while(MainActivity.mPlayer.getMetadata().currentTrack == null){}

        }

        @Override
        public void onError(Error error) {
            Log.d("Player","ERROR:" + error);
        }
    };


    public PlayerStatus(Context c, ImageButton[] buts, TextView[] tv, SeekBar seekBar, final String trackURI) {
        context = c;
        //songList = context.getResources().getStringArray(R.array.song_names);
        //songRes  = GetSongResources.getSongResources();
        isLooped = false;
        songTV = tv;
        userButs = buts;
        mySeekBar = seekBar;
        myHandler = new Handler();
        isPaused = true;


        MainActivity.mPlayer.addNotificationCallback(new Player.NotificationCallback() {
            @Override
            public void onPlaybackEvent(PlayerEvent playerEvent) {
                Log.d("PlayerStatus", "Playback event received: " + playerEvent.name());
                switch (playerEvent) {
                    // Handle event type as necessary
                    case kSpPlaybackNotifyTrackChanged:
                        initPlayer();
                    default:
                        break;
                }
            }

            @Override
            public void onPlaybackError(Error error) {

            }
        });

        if(MainActivity.mPlayer.getPlaybackState().isActiveDevice) {
            if(MainActivity.mPlayer.getPlaybackState().isPlaying) {
                if(!trackURI.equals(MainActivity.mPlayer.getMetadata().currentTrack.uri)) {
                    MainActivity.mPlayer.pause(mOperationCallback);
                } else {
                    userButs[TrackPageActivity.PLAY_PAUSE_BUT].setImageResource(R.drawable.pause);
                    initPlayer();
                }
            } else {
                if(trackURI.equals(MainActivity.mPlayer.getMetadata().currentTrack.uri)) {
                    initPlayer();
                }
            }
        }



//        if(MainActivity.mPlayer.getPlaybackState().isActiveDevice) {
//            if(!trackURI.equals(MainActivity.mPlayer.getMetadata().currentTrack.uri)) {
//                MainActivity.mPlayer.playUri(mOperationCallbackinit, trackURI, 0, 0);
//            } else {
//
//                if(MainActivity.mPlayer.getPlaybackState().isPlaying) {
//                    userButs[TrackPageActivity.PLAY_PAUSE_BUT].setImageResource(R.drawable.pause);
//                } else {
//                    userButs[TrackPageActivity.PLAY_PAUSE_BUT].setImageResource(R.drawable.play);
//                }
//                initPlayer();
//            }
//        }  else {
//            MainActivity.mPlayer.playUri(mOperationCallbackinit, trackURI, 0, 0);
//        }





        userButs[TrackPageActivity.PLAY_PAUSE_BUT].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(MainActivity.mPlayer.getPlaybackState().isActiveDevice &&
                    trackURI.equals(MainActivity.mPlayer.getMetadata().currentTrack.uri)) {
                    pausePlaySong(v);
                } else {
                    MainActivity.mPlayer.playUri(mOperationCallbackinit, trackURI, 0, 0);
                    System.out.println(trackURI);
                    userButs[TrackPageActivity.PLAY_PAUSE_BUT].setImageResource(R.drawable.pause);
                    isPaused = false;
                }



            }
        });
        userButs[TrackPageActivity.FORWARD_BUT].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // skipForward();
               // MainActivity.mPlayer.playUri(mOperationCallback, "spotify:track:44tDqFYrK86OVaxzLhUbVv", 0,0);
//                int currentPlayingIndex = SavedSongsFragment.queue.get(trackURI);
//                System.out.println(currentPlayingIndex);
//                MainActivity.mPlayer.playUri(mOperationCallback,
//                        SavedSongsFragment.tracks.get(currentPlayingIndex +1).getTrackURI(), 0,0);
            }
        });
        userButs[TrackPageActivity.BACK_BUT].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // skipBackwards();
            }
        });
        mySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    MainActivity.mPlayer.seekToPosition(mOperationCallback, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private class PlayerInit implements Runnable {



        public PlayerInit() {

        }

        @Override
        public void run() {
            initPlayer();
        }
    }

    private void returnToPlayer() {
        int duration = (int) MainActivity.mPlayer.getMetadata().currentTrack.durationMs;
        mySeekBar.setMax(duration);
        ts = new TimeStamp(duration / 1000);
        updateSeekProgress();
    }


    // initialize player to begin playing first song
    public void initPlayer() {
        //player = player.create(context, songRes[0]);
        int duration = (int) MainActivity.mPlayer.getMetadata().currentTrack.durationMs;
        mySeekBar.setMax(duration);
        ts = new TimeStamp(duration / 1000);

//        currSong = 0;
//        nextSong = currSong + 1;
//        prevSong = songRes.length - 1;
//        textStatusUpdate();
      //  player.start();
        updateSeekProgress();
       // MainActivity.mPlayer.setRepeat(mOperationCallback, true);
//        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                if(!mp.isLooping()) {
//                    updatePlayerState(nextSong);
//                    playSong(currSong, false);
//                }
//            }
//        });

    }


    // make runnable every sec so that seekbar updates as song plays
    public void updateSeekProgress() {
        myHandler.postDelayed(seekBarRunnable, 1000);
    }


    private Runnable seekBarRunnable = new Runnable() {
        public void run() {
            int currentPos = (int) MainActivity.mPlayer.getPlaybackState().positionMs;
            mySeekBar.setProgress(currentPos);
            songTV[TrackPageActivity.CURRENT_TIME].setText(ts.updateTimeProgress(currentPos / 1000));
            songTV[TrackPageActivity.REMAINING_TIME].setText(ts.updateTimeLeft());
            myHandler.postDelayed(this, 100);
        }
    };


    // play the song, will continuously play next song once current is
    // finished unless looping is on
    public void playSong(int song, boolean clickedSong) {
        //player.reset();
        //player = player.create(context, songRes[song]);
        int duration = (int) MainActivity.mPlayer.getMetadata().currentTrack.durationMs;
        mySeekBar.setMax(duration);
        ts = new TimeStamp(duration / 1000);
        //player.setLooping(isLooped);
        MainActivity.mPlayer.setRepeat(mOperationCallback, true);
        //textStatusUpdate();
        if(!isPaused) {
           // player.start();
            MainActivity.mPlayer.resume(mOperationCallback);
            updateSeekProgress();
            if(clickedSong && isPaused) {
                isPaused = false;
                userButs[TrackPageActivity.PLAY_PAUSE_BUT].setImageResource(R.drawable.pause);
            }
        }
       /* player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(!mp.isLooping()) {
                    updatePlayerState(nextSong);
                    playSong(currSong, false);
                }
            }
        });*/
    }


    // either pause or play the song, depends on current state of
    // play/pause button
    public void pausePlaySong(View view) {
        if(MainActivity.mPlayer.getPlaybackState().isPlaying) {
            isPaused = true;
            MainActivity.mPlayer.pause(mOperationCallback);
            ((ImageButton)view).setImageResource(R.drawable.play);
        } else {
            isPaused = false;
            MainActivity.mPlayer.resume(mOperationCallback);
            updateSeekProgress();
            ((ImageButton)view).setImageResource(R.drawable.pause);
        }
    }


    /*// skip to the next song
    public void skipForward() {
        prevSong = currSong;
        currSong = nextSong;
        nextSong = (currSong == songRes.length - 1) ? 0 : currSong + 1;
        playSong(currSong, false);
    }


    // go back to the previous song
    public void skipBackwards() {
        nextSong = currSong;
        currSong = prevSong;
        prevSong = (currSong == 0) ? songRes.length - 1 : currSong - 1;
        playSong(currSong, false);
    }


    // update the current, next, prev songs when user clicks on a
    // specific song
    public void updatePlayerState(int pos) {
        currSong = pos;
        nextSong = (currSong == songRes.length - 1) ? 0 : currSong + 1;
        prevSong = (currSong == 0) ? songRes.length - 1 : currSong - 1;
    }


    // update text to display current and next songs playing
    public void textStatusUpdate() {
        songTV[MainActivity.NOW_PLAYING].setText(songList[currSong]);
        songTV[MainActivity.NEXT_UP].setText(songList[nextSong]);
    }


    // set whether or not the song is looping
    public void setLooped(boolean isLooping) {
        isLooped = isLooping;
        player.setLooping(isLooping);
    }


    // returns this.player MediaPlayer
    public MediaPlayer getPlayer() {
        return player;
    }

*/
}
