package com.example.a3d5bmusicapp.ui.host;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HostViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HostViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Host");


    }

    public LiveData<String> getText() {
        return mText;
    }
}