package cs371m.godj;

/**
 * Created by Jasmine on 9/22/2016.
 * Used to keep track of current song time/remaining time
 */

public class TimeStamp {


    private int minsLeft; // MM: left in song
    private int secsLeft; // :SS left in song
    private int songDuration; // length of song in seconds
    private int currentTimePos; // current position of song in seconds


    public TimeStamp(int duration) {
        songDuration = duration;
        minsLeft = duration / 60;
        secsLeft = duration % 60;
        currentTimePos = 0;
    }

    // update the current song position time text
    public String updateTimeProgress(int duration) {
        String time = "";
        currentTimePos = duration;
        int currentMinutes = duration / 60;
        int currentSeconds = duration % 60;
        time += (currentMinutes < 10) ? 0 + (currentMinutes + ":") : currentMinutes + ":";
        time += (currentSeconds < 10) ? 0 + "" + currentSeconds : currentSeconds;
        return time;
    }

    // update the remaining time text
    public String updateTimeLeft() {
        String time = "";
        int timeLeft = songDuration - currentTimePos;
        minsLeft = timeLeft / 60;
        secsLeft = timeLeft % 60;
        time += (minsLeft < 10) ? 0 + (minsLeft + ":") : minsLeft + ":";
        time += (secsLeft < 10) ? 0 + "" + secsLeft : secsLeft;
        return time;
    }
}
