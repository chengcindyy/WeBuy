package com.csis4495_cmk.webuy.viewmodels;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SharedGroupInventoryListViewModel extends ViewModel {
    private final MutableLiveData<List<Inventory>> inventoryList = new MutableLiveData<>();

    private final MutableLiveData<String> groupId = new MutableLiveData<>();

    private final MutableLiveData<Group> group = new MutableLiveData<>();

    private final MutableLiveData<Map<String,String>> inventoryIdMap = new MutableLiveData<>();

    public void setinventoryIdMap(Map<String, String> list) {

        inventoryIdMap.setValue(list);
    }

    public LiveData<Map<String, String>> getinventoryIdMap() {
        return inventoryIdMap;
    }

    public void setGroup(Group g) {
        group.setValue(g);
    }

    public LiveData<Group> getGroup() {
        return group;
    }

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
