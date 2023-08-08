package com.csis4495_cmk.webuy.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.csis4495_cmk.webuy.models.Group;

import java.util.Map;

public class CustomerHomeFilterViewModel extends ViewModel {

    private final MutableLiveData<Map<String, Group>> filteredGroupMap = new MutableLiveData<>();
    private final MutableLiveData<String> keywords = new MutableLiveData<String>();
    private final MutableLiveData<String> selectedLocation = new MutableLiveData<String>();
    private final MutableLiveData<String> selectedPriceRange = new MutableLiveData<String>();
    private final MutableLiveData<Integer> selectedStatus = new MutableLiveData<Integer>();
    private final MutableLiveData<Integer> selectedGroupType = new MutableLiveData<Integer>();
    private final MutableLiveData<String> selectedTimeRange = new MutableLiveData<String>();

    public LiveData<Map<String, Group>> getFilteredGroupMap() {
        return filteredGroupMap;
    }

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

    public void selectedTimeRange(String timeRange) {
        selectedTimeRange.setValue(timeRange);
    }

    public LiveData<String> getSelectedTimeRange() {
        return selectedTimeRange;
    }
}
