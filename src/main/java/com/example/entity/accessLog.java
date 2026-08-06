package com.example.entity;

// JSON name convention is camelCase
public class accessLog {
    //
    private long id_Column;
    private String pageName;
    private String accessDate;
    private String ipValue;

    //
    public accessLog(long id_Column, String pageName, String accessDate, String ipValue) {
        this.id_Column = id_Column;
        this.pageName = pageName;
        this.accessDate = accessDate;
        this.ipValue = ipValue;
    }

    // Getters and Setters
    public long getid_Column() {
        return id_Column;
    }

    public void setID_Columnn(long id_column) {
        this.id_Column = id_column;
    }

    public String getpageName() {
        return pageName;
    }

    public void setpageName(String pageName) {
        this.pageName = pageName;
    }

    public String getaccessDate() {
        return accessDate;
    }

    public void setaccessDate(String accessDate) {
        this.accessDate = accessDate;
    }

    public String getipValue() {
        return ipValue;
    }

    public void setIpValue(String ipValue) {
        this.ipValue = ipValue;
    }

}
