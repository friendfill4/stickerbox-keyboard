package com.example.vi6.tabbed;

import java.io.Serializable;

/**
 * Created by vi6 on 03-Mar-17.
 */

public class allEmojisPOJO implements Serializable{
    String name, catID, folder, path,link;

    public allEmojisPOJO(String name, String catID, String folder, String path) {
        this.name = name;
        this.catID = catID;
        this.folder = folder;
        this.path = path;
    }

    public allEmojisPOJO(String name, String catID, String link) {
        this.name = name;
        this.catID = catID;
        this.link = link;
    }

    public allEmojisPOJO(String catID, String name) {
        this.catID = catID;
        this.name = name;
    }

    public allEmojisPOJO() {
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
