package com.csis4495_cmk.webuy.adapters;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> keywords = new MutableLiveData<String>();
    private final MutableLiveData<String> selectedLocation = new MutableLiveData<String>();
    private final MutableLiveData<String> selectedCondition = new MutableLiveData<String>();

    public void enterKeywords(String str) {
        keywords.setValue(str);
    }

    public LiveData<String> getKeywords() {
        return keywords;
    }

    public void selectLocation(String str) {
        selectedLocation.setValue(str);
    }

    public LiveData<String> getSelectedLocation() {
        return selectedLocation;
    }

    public void selectCondition(String str) {
        selectedCondition.setValue(str);
    }

    public LiveData<String> getSelectedCondition() {
        return selectedCondition;
    }
}
