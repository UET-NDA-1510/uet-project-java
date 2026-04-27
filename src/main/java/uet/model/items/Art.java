package uet.model.items;

import java.math.BigDecimal;

public class Art extends Item {
    private String artist;// họa sĩ
    public Art(){
        super();
    };
    public Art(long sellerId,String name, String description, BigDecimal startingPrice, String imageUrl, String artist){
        super(sellerId,name, description, startingPrice, imageUrl);
        this.artist = artist;
    }
    //getter
    public String getArtist() {
        return artist;
    }
    // setter
    public void setArtist(String artist) {
        this.artist = artist;
    }
    @Override
    public String getType() {
        return "Art";
    }
}
