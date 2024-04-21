package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.util.List;
import java.util.Map;


public class KimiParameter {
String model;
List<Map> messages;
float temperature;
boolean stream;

    public List<Map> getMessages() {
        return messages;
    }

    public void setMessages(List<Map> messages) {
        this.messages = messages;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
