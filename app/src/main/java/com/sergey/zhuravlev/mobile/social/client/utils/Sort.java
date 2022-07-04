package com.sergey.zhuravlev.mobile.social.client.utils;

public class Sort {

    private final String sortBy;
    private final Direction direction;

    public Sort(String sortBy, Direction direction) {
        this.sortBy = sortBy;
        this.direction = direction;
    }

    public String getSortBy() {
        return sortBy;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return sortBy + "," + direction.name();
    }
}
