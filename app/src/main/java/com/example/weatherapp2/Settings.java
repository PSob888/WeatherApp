package com.example.weatherapp2;

public class Settings {
    boolean useImperial;
    int refreshTime;

    public Settings(boolean useImperial, int refreshTime) {
        this.useImperial = useImperial;
        this.refreshTime = refreshTime;
    }

    public boolean isUseImperial() {
        return useImperial;
    }

    public void setUseImperial(boolean useImperial) {
        this.useImperial = useImperial;
    }

    public int getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(int refreshTime) {
        this.refreshTime = refreshTime;
    }
}
