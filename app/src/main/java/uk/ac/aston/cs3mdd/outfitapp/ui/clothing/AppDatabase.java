package uk.ac.aston.cs3mdd.outfitapp.ui.clothing;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.CalendarDao;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.Date;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.DateDao;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.DateOutfitJoin;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.DateOutfitJoinDao;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.Event;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.EventConverters;


@Database(entities = {ClothingItem.class, Outfit.class, OutfitClothingItemJoin.class, Date.class, Event.class, DateOutfitJoin.class}, version = 1, exportSchema = true)
@TypeConverters({ClothingItemConverters.class, EventConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ClothingDao clothingDao();
    public abstract OutfitDao outfitDao();
    public abstract OutfitClothingJoinDao outfitClothingJoinDao();
    public abstract CalendarDao calendarDao();
    public abstract DateOutfitJoinDao dateOutfitJoinDao();
    public abstract DateDao dateDao();
}
