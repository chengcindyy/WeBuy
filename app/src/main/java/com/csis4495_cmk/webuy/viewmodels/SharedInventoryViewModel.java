package com.csis4495_cmk.webuy.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

public class SharedInventoryViewModel extends ViewModel {

    private final MutableLiveData<Map<String, Boolean>> selectedInventory = new MutableLiveData<>();

    public void select(Map<String, Boolean> item){
        selectedInventory.setValue(item);
    }

    public LiveData<Map<String, Boolean>> getselectedInventory(){
        return selectedInventory;
    }
}
