package uk.ac.aston.cs3ip.outfitapp.ui.calendar;
import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("id")
    private int id;

    @SerializedName("icon")
    private String icon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
