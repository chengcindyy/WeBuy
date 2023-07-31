package com.csis4495_cmk.webuy.viewmodels;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.csis4495_cmk.webuy.models.Inventory;

import java.util.ArrayList;
import java.util.List;

public class SharedGroupInventoryListViewModel extends ViewModel {
    private final MutableLiveData<List<Inventory>> inventoryList = new MutableLiveData<>();

    private final MutableLiveData<String> groupId = new MutableLiveData<>();

    public void setGroupId(String value) {
        groupId.setValue(value); // This will work, since myString is MutableLiveData
    }

    public LiveData<String> getGroupId() {
        return groupId;
    }

    public void setInventoryList(List<Inventory> list) {

        inventoryList.setValue(list);
    }

    public LiveData<List<Inventory>> getInventoryList() {

        return inventoryList;
    }
}
