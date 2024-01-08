package org.example.classes;

public class Info {
    private  String type;
    private  String value;
    private int[] linksIds;

    public int[] getLinksIds() {
        return linksIds;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setLinksIds(int[] linksIds) {
        this.linksIds = linksIds;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
