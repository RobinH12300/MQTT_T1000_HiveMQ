package com.example.mqtt_t1000_hivemq;

import android.util.Log;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PCL {
    private String isConnect;
    private String isSubscribed;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void setConnect(String newValue) {
        String oldValue = isConnect;
        isConnect = newValue;

        support.firePropertyChange("ConnectionState", oldValue, newValue);
        Log.d("Fired connectionstate", newValue);
    }

    public void setTopic(String newValue){
        String oldValue = this.isSubscribed;
        this.isSubscribed = newValue;

        support.firePropertyChange("TopicState", oldValue, newValue);
        Log.d("Fired topic", newValue);
    }
}

