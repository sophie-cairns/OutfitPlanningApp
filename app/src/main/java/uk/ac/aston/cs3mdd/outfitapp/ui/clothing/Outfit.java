package uk.ac.aston.cs3mdd.outfitapp.ui.clothing;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "outfits")
public class Outfit implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "outfitId")
    private long id;

    private List<String> tags;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
