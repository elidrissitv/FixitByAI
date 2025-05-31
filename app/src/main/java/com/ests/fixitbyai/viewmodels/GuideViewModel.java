package com.ests.fixitbyai.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.ests.fixitbyai.database.AppDatabase;
import com.ests.fixitbyai.database.Guide;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuideViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final ExecutorService executorService;
    private final MutableLiveData<List<Guide>> searchResults;

    public GuideViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        executorService = Executors.newSingleThreadExecutor();
        searchResults = new MutableLiveData<>();
    }

    public void searchGuides(String query) {
        executorService.execute(() -> {
            List<Guide> guides = database.guideDao().searchGuides(query).getValue();
            searchResults.postValue(guides);
        });
    }

    public LiveData<List<Guide>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Guide> getGuideById(int id) {
        return database.guideDao().getGuideById(id);
    }

    public void insertGuide(Guide guide) {
        executorService.execute(() -> {
            database.guideDao().insert(guide);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
} 