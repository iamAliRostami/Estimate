package com.leon.estimate.Tables;

public class NoeVagozariDictionary {
    int id;
    boolean isDisabled;
    boolean isSelected;
    String title;

    public NoeVagozariDictionary(int id, boolean isDisabled, boolean isSelected, String title) {
        this.id = id;
        this.isDisabled = isDisabled;
        this.isSelected = isSelected;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
