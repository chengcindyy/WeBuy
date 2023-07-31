package com.csis4495_cmk.webuy.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

public class SharedICheckInventoryViewModel extends ViewModel {

    private final MutableLiveData<Map<String, Boolean>> selectedInventory = new MutableLiveData<>();

    public void select(Map<String, Boolean> item){
        selectedInventory.setValue(item);
    }

    public LiveData<Map<String, Boolean>> getselectedInventory(){
        return selectedInventory;
    }
}
