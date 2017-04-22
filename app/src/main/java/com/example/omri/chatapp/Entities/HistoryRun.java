package com.example.omri.chatapp.Entities;

import java.util.ArrayList;

/**
 * Created by Omri on 07/04/2017.
 */

public class HistoryRun extends Run {
    private boolean like;
    private boolean marked;
    public HistoryRun() {
        super();
    }

    public HistoryRun(String creator,String creatorId, String name, String date, String time, String location, ArrayList<Question> preferences, String runDistance, boolean like, boolean marked) {
        super(creator,creatorId, name, date, time, location, preferences, runDistance);
        this.like= like;
        this.marked= marked;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        marked = marked;
    }
}
