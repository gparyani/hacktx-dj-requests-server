package cs371m.godj;

/**
 * Created by Jasmine on 11/29/2016.
 */

public class EventObject {

    private String eventName;
    private String eventQueryName;
    private String eventLocation;
    private String hostName;
    private String hostUserName;
    private String key;
    private long startTime;
    private long endTime;

    public EventObject() {

    }


    public EventObject(String name, String queryName, String host, long start, long end, String uniqueKey) {
        eventName = name;
        eventQueryName = queryName;
        hostName = host;
        startTime = start;
        endTime = end;
        key = uniqueKey;
    }

    public String getEventName() { return eventName; }
    public String getEventQueryName() {return eventQueryName; }
    public String getLocation() { return eventLocation; }
    public String getHostName() {
        return hostName;
    }
    public String getHostUserName() {
        return hostUserName;
    }
    public String getKey() { return key; }
    public long getStartTime() { return startTime; }
    public long getEndTime(){ return endTime; }



    public void setEventName(String name) {
        eventName = name;
    }
    public void setEventQueryName(String queryName) { eventQueryName = queryName; }
    public void setLocation(String loc) {
        eventLocation = loc;
    }
    public void setHostName(String name) {
        hostName = name;
    }
    public void setKey(String name) {
        key = name;
    }
    public void setHostUserName(String name) {
        hostUserName = name;
    }
    public void setStartTime(long start) {
        startTime = start;
    }
    public void setEndTime(long end){
        endTime = end;
    }

}
