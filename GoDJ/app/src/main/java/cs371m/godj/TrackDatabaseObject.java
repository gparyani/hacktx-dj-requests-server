package cs371m.godj;

/**
 * Created by Jasmine on 11/30/2016.
 */

public class TrackDatabaseObject {

    private String trackName;
    private String artistName;
    private String albumName;
    private String trackURI;
    private int priority;

   public TrackDatabaseObject() {

   }

    public TrackDatabaseObject(String track, String artist, String album, String uri, int pri) {
        trackName = track;
        artistName = artist;
        albumName = album;
        trackURI = uri;
        priority = pri;
    }

    public String getTrackName() { return trackName; }
    public String getArtistName() {
        return artistName;
    }

    public String getAlbumName() { return albumName; }
    public String getTrackURI(){ return trackURI; }

    public int getPriority() {
        return priority;
    }



    public void setTrackName(String name) {
        trackName = name;
    }

    public void setArtistName(String name) {
        artistName = name;
    }

    public void setAlbumName(String name) {
        albumName = name;
    }
    public void setTrackURI(String uri){
        trackURI = uri;
    }

    public void setPriority(int pri) {
        priority = pri;
    }


}
