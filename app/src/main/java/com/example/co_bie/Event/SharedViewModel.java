package com.example.co_bie.Event;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<Bundle> bundleLocation = new MutableLiveData<>();
    private MutableLiveData<Bundle> bundleDataFields = new MutableLiveData<>();

    public void setLocationBundle(Bundle bundle) {
        bundleLocation.setValue(bundle);
    }

    public LiveData<Bundle> getLocationBundle() {
        return bundleLocation;
    }

    public void setDataFields(Bundle bundle) {
        bundleDataFields.setValue(bundle);
    }

    public LiveData<Bundle> getDataFields() {
        return bundleDataFields;
    }
}

