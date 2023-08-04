package com.csis4495_cmk.webuy.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

public class SharedICheckInventoryViewModel extends ViewModel {

    private final MutableLiveData<Map<String, Integer>> selectedInventory = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRestockChecked = new MutableLiveData<>();
    public void select(Map<String, Integer> item){
        selectedInventory.setValue(item);
    }
    public LiveData<Map<String, Integer>> getselectedInventory(){
        return selectedInventory;
    }

    public boolean getIsRestockChecked(){
        return true;
    }
}
