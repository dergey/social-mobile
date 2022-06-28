package com.sergey.zhuravlev.mobile.social.ui.common;

import java.time.LocalDate;

public abstract class Item<T> {

    public static class RepoItem<T> extends Item<T> {
        private final T model;

        public RepoItem(T model) {
            this.model = model;
        }

        public T getModel() {
            return model;
        }

    }

    public static class DateSeparatorItem<T> extends Item<T> {
        private final LocalDate date;

        public DateSeparatorItem(LocalDate date) {
            this.date = date;
        }

        public LocalDate getDate() {
            return date;
        }

    }

}
