package com.csis4495_cmk.webuy.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

public class SharedEditStyleViewModel extends ViewModel{
    private final MutableLiveData<Map<String, Boolean>> selectedStyle = new MutableLiveData<>();

    public void select(Map<String, Boolean> item){
        selectedStyle.setValue(item);
    }

    public LiveData<Map<String, Boolean>> getSelectedStyle(){
        return selectedStyle;
    }
}
