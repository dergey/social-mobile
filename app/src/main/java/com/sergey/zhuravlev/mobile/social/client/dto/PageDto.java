package com.sergey.zhuravlev.mobile.social.client.dto;

import java.util.List;

public class PageDto<T> {

    private Integer size;

    private Integer number;

    private Integer totalPages;

    private Integer totalElements;

    private Boolean hasNext;

    private List<T> content;

    public PageDto() {
    }

    public PageDto(Integer size, Integer number, Integer totalPages, Integer totalElements, Boolean hasNext, List<T> content) {
        this.size = size;
        this.number = number;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
        this.content = content;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "PageDto{" +
                "size=" + size +
                ", number=" + number +
                ", totalPages=" + totalPages +
                ", totalElements=" + totalElements +
                ", hasNext=" + hasNext +
                ", content=" + content +
                '}';
    }
}
