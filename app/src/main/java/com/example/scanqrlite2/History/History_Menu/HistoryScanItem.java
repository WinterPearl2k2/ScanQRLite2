package com.example.scanqrlite2.History.History_Menu;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scan_item")
public class HistoryScanItem {
    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "title_scan")
    String title;
    @ColumnInfo(name = "content_scan")
    String content;
    @ColumnInfo(name = "result_scan")
    String result;
    @ColumnInfo(name = "security_scan")
    String security;
    @ColumnInfo(name = "password_scan")
    String password;
    @ColumnInfo(name = "type_scan")
    String typeScan;

    public HistoryScanItem(String title, String content, String result) {
        this.title = title;
        this.content = content;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public String getTypeScan() {
        return typeScan;
    }

    public void setTypeScan(String typeScan) {
        this.typeScan = typeScan;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
