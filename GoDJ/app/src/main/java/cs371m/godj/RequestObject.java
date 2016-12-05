package cs371m.godj;

/**
 * Created by Jasmine on 12/5/2016.
 */

public class RequestObject {

    private int numRequests;
    private long firstRequestTime;
    private long nextAvailableRequest;

    public RequestObject() {

    }

    public RequestObject(int reqs, long firstReqTime, long nextAvailableTime) {
        numRequests = reqs;
        firstRequestTime = firstReqTime;
        nextAvailableRequest = nextAvailableTime;
    }

    public long getFirstRequestTime() {
        return firstRequestTime;
    }

    public void setFirstRequestTime(long firstRequestTime) {
        this.firstRequestTime = firstRequestTime;
    }

    public int getNumRequests() {
        return numRequests;
    }

    public void setNumRequests(int numRequests) {
        this.numRequests = numRequests;
    }

    public long getNextAvailableRequest() {
        return nextAvailableRequest;
    }

    public void setNextAvailableRequest(long nextAvailableRequest) {
        this.nextAvailableRequest = nextAvailableRequest;
    }




}
