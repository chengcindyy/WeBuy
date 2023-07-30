package com.csis4495_cmk.webuy.adapters;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> keywords = new MutableLiveData<String>();
    private final MutableLiveData<String> selectedLocation = new MutableLiveData<String>();
    private final MutableLiveData<String> selectedPriceRange = new MutableLiveData<String>();
    private final MutableLiveData<Integer> selectedStatus = new MutableLiveData<Integer>();
    private final MutableLiveData<Integer> selectedGroupType = new MutableLiveData<Integer>();

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

    public void selectPriceRange(String str) {
        selectedPriceRange.setValue(str);
    }

    public LiveData<String> getSelectedPriceRange() {
        return selectedPriceRange;
    }

    public void selectedStatus(int status) {
        selectedStatus.setValue(status);
    }

    public LiveData<Integer> getSelectedStatus() {
        return selectedStatus;
    }

    public void selectedGroupType(int groupType) {
        selectedGroupType.setValue(groupType);
    }

    public LiveData<Integer> getGroupType() {
        return selectedGroupType;
    }
}