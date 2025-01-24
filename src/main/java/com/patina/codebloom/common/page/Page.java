package com.patina.codebloom.common.page;

public class Page<T> {
    private boolean hasNextPage;
    private T data;
    private int pages;

    public Page(boolean hasNextPage, T data, int pages) {
        this.hasNextPage = hasNextPage;
        this.data = data;
        this.pages = pages;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
