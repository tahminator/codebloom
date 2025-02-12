package com.patina.codebloom.common.page;

public class Page<T> {
    private boolean hasNextPage;
    private T data;
    private int pages;

    public Page(final boolean hasNextPage, final T data, final int pages) {
        this.hasNextPage = hasNextPage;
        this.data = data;
        this.pages = pages;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(final boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public T getData() {
        return data;
    }

    public void setData(final T data) {
        this.data = data;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(final int pages) {
        this.pages = pages;
    }
}
