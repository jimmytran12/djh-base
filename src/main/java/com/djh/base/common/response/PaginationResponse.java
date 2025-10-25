package com.djh.base.common.response;

import java.util.Collection;
import java.util.Collections;

public class PaginationResponse<E> {
    long total = 0;
    Collection<E> items;

    public PaginationResponse<E> setItems(Collection<E> items) {
        this.items = items == null ? Collections.emptyList() : items;
        return this;
    }

    public Collection<E> getItems() {
        return items;
    }

    public PaginationResponse<E> setTotal(long total) {
        this.total = total;
        return this;
    }

    public long getTotal() {
        return total == 0 ? items.size() : total;
    }
}
