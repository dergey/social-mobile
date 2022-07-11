package com.sergey.zhuravlev.mobile.social.database.paggeble;

import androidx.room.Ignore;

public class Pageable {

    private Integer page;

    public Pageable() {
    }

    @Ignore
    public Pageable(Integer page) {
        this.page = page;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

}
