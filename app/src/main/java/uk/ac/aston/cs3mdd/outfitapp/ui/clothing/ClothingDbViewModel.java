package uk.ac.aston.cs3mdd.outfitapp.ui.clothing;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.Date;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.DateOutfitJoin;
import uk.ac.aston.cs3mdd.outfitapp.ui.calendar.Event;

public class ClothingDbViewModel extends AndroidViewModel {
    private AppDatabase db;
    private LiveData<List<ClothingItem>> clothingItems;
    private LiveData<List<Outfit>> outfits;
    private LiveData<Long> lastInsertedOutfitId;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public ClothingDbViewModel(@NonNull Application application) {
        super(application);
        db = Room.databaseBuilder(application, AppDatabase.class, "clothing-database").build();
        clothingItems = db.clothingDao().getAllClothingItems();
        outfits = db.outfitDao().getAllOutfits();
        lastInsertedOutfitId = db.outfitDao().getLastInsertedOutfitId();
    }

    public LiveData<List<ClothingItem>> getAllClothingItems() {
        return clothingItems;
    }

    public LiveData<List<Outfit>> getAllOutfits() {
        return outfits;
    }

    public void insertClothingItem(ClothingItem clothingItem) {
        Log.i("SC", "clothing item being submitted");
        executor.submit(() -> {
            db.clothingDao().insert(clothingItem);
        });
    }

    public void deleteClothingItem(ClothingItem clothingItem) {
        Log.i("SC", "clothing item being deleted. clothing item: " + clothingItem.getId());
        executor.submit(() -> {
            List<Long> outfitIds = db.outfitClothingJoinDao().getOutfitsByClothingId(clothingItem.getId());
            db.clothingDao().delete(clothingItem);
            for (Long outfitId : outfitIds) {
                Log.i("SC", "outfitId: " + outfitId);
                int Count = db.outfitClothingJoinDao().getClothingOutfitJoinCount(outfitId);
                if (Count < 1) {
                    Log.i("SC", "outfit being deleted. outfitId: " + outfitId);
                    db.outfitDao().deleteOutfitById(outfitId);
                }
            }
        });
    }

        public long insertOutfit(Outfit outfit) {
            Log.i("SC", "outfit being submitted");
            try {

                Future<Long> future = executor.submit(new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {
                        return db.outfitDao().insert(outfit);
                    }
                });
                return future.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return -1;
            }
        }

        public void deleteOutfit(Outfit outfit) {
            Log.i("SC", "outfit being deleted");
            executor.submit(() -> {
                db.outfitDao().delete(outfit);
            });
        }

    public LiveData<List<ClothingItem>> getClothingItemsForOutfit(long outfitId) {
        return db.outfitClothingJoinDao().getClothingItemsForOutfit(outfitId);
    }

    public void insertOutfitClothingItemJoins(List<OutfitClothingItemJoin> joins) {
        for (OutfitClothingItemJoin join : joins) {
            executor.submit(() -> {
                        db.outfitClothingJoinDao().insert(join);
                    });
        }

    }

    public void deleteClothingItemsForOutfit(long outfitId) {
        Log.i("SC", "delete outfit clothing item joins");
        executor.submit(() -> {
            db.outfitClothingJoinDao().deleteClothingItemsForOutfit(outfitId);
        });
    }

    public void insertDate(Date date) {
        Log.i("SC", "date being submitted");
        executor.submit(() -> {
            db.dateDao().insert(date);
        });
    }

    public void insertDateOutfitJoins(List<DateOutfitJoin> joins) {
        Log.i("SC", "date outfit joins submitted");
        for (DateOutfitJoin join : joins) {
            Log.i("SC", "Join ID " + join.outfitId);
            executor.submit(() -> {
                db.dateOutfitJoinDao().insert(join);
            });
        }
    }

    public void deleteOutfitsForDate(LocalDate date) {
        Log.i("SC", "delete outfit date joins");
        executor.submit(() -> {
            db.dateOutfitJoinDao().deleteOutfitsForDate(date);
        });
    }

    public Date getDateByLocalDate(LocalDate localDate) {
        try {
            Future<Date> future = executor.submit(new Callable<Date>() {
                @Override
                public Date call() throws Exception {
                    return db.dateDao().getDateByValue(localDate);
                }
            });
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LiveData<List<Outfit>> getOutfitsForDate(LocalDate localDate) {
        return db.dateOutfitJoinDao().getOutfitsForDate(localDate);
    }

    public void insertEvent(Event event) {
        Log.i("SC", "event being submitted");
        executor.submit(() -> {
            db.calendarDao().insert(event);
        });
    }

    public LiveData<List<Event>> getEventsForDate(LocalDate localDate) {
        return db.calendarDao().getEventsForDate(localDate);
    }

    public List<String> getBrands() {
        try {
            Future<List<String>> future = executor.submit(new Callable<List<String>>() {
                @Override
                public List<String> call() throws Exception {
                    return db.clothingDao().getBrands();
                }
            });
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }

    }

}
