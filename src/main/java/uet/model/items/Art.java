package uet.model.items;

import java.math.BigDecimal;

public class Art extends Item {
    private String artist;     // họa sĩ
    private int yearCreated;
    public Art(long sellerId,String name, String description, BigDecimal startingPrice, String imageUrl, String artist, int yearCreated){
        super(sellerId,name, description, startingPrice, imageUrl);
        this.artist = artist;
        this.yearCreated = yearCreated;
    }
    //getter
    public int getYearCreated() {
        return yearCreated;
    }
    public String getArtist() {
        return artist;
    }
    // setter
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public void setYearCreated(int yearCreated) {
        this.yearCreated = yearCreated;
    }
    @Override
    public String getType() {
        return "Art";
    }
}
