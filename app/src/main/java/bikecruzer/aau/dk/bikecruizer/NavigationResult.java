package bikecruzer.aau.dk.bikecruizer;

/**
 * Created by michael on 11-05-2016.
 */
public class NavigationResult {
    private String mResult = null;
    private int mId = 0;
    private POI mPOI = null;

    public NavigationResult(String result, int id, POI poi){
        this.mResult = result;
        this.mId = id;
        this.mPOI = poi;
    }

    public String getmResult(){
        return this.mResult;
    }
    public int getmId(){
        return this.mId;
    }
    public POI getmPOI(){
        return this.mPOI;
    }


}
