package bikecruzer.aau.dk.bikecruizer;

/**
 * Created by michael on 14-05-2016.
 */
public class POIRatings {
    private int dining = 0;
    private int barsandpubs = 0;
    private int attractions = 0;
    private int sights = 0;
    private int museums = 0;
    private int kids = 0;

    public POIRatings(int dining, int barsandpubs, int attractions, int sights, int museums, int kids){
        this.dining = dining;
        this.barsandpubs = barsandpubs;
        this.attractions = attractions;
        this.sights = sights;
        this.museums = museums;
        this.kids = kids;
    }

    public int getDining(){return this.dining;}
    public int getBarsandpubs(){return this.barsandpubs;}
    public int getAttractions(){return this.attractions;}
    public int getSights(){return this.sights;}
    public int getMuseums(){return this.museums;}
    public int getKids(){return this.kids;}

    public Integer[] getRatingsArray (){
        Integer[] arr = new Integer[] {getDining(), getBarsandpubs(), getAttractions(), getSights(), getMuseums(), getKids()};
        return arr;
    }
}
