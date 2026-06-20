package com.litvin.backrooms.capability;

public interface ISanity {
    float getSanity();
    void setSanity(float sanity);
    void addSanity(float amount);
    boolean isHidden();
    void setHidden(boolean hidden);
    void copyFrom(ISanity source);
}
