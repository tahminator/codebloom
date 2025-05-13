package com.patina.codebloom.common.page;

public class Page<T> {
    private boolean hasNextPage;
    private T items;
    private int pages;

    private int pageSize;

    public Page(final boolean hasNextPage, final T items, final int pages, final int pageSize) {
        this.hasNextPage = hasNextPage;
        this.items = items;
        this.pages = pages;
        this.pageSize = pageSize;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(final boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public T getItems() {
        return items;
    }

    public void setItems(final T items) {
        this.items = items;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(final int pages) {
        this.pages = pages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }
}
