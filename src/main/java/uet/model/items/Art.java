package uet.model.items;
public class Art extends Item {
    private String artist;     // họa sĩ
    private int yearCreated;
    public Art(String name, String description, double startingPrice, String imageUrl,String artist,int yearCreated){
        super(name, description, startingPrice, imageUrl);
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
