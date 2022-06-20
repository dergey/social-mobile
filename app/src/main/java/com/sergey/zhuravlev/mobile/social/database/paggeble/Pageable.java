package com.sergey.zhuravlev.mobile.social.database.paggeble;

public class Pageable {

    private Integer page;

    public Pageable() {
    }

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
