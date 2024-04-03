package uk.ac.aston.cs3mdd.outfitapp.ui.calendar;
import com.google.gson.annotations.SerializedName;

public class Temperature {
    @SerializedName("min")
    private double min;

    @SerializedName("max")
    private double max;

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setMax(double max) {
        this.max = max;
    }
}
