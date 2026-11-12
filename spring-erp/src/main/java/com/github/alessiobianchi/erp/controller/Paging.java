package com.github.alessiobianchi.erp.controller;

final class Paging {

    // The frontend asks for 50; a cap stops a single request from loading a whole table.
    static final int MAX_PAGE_SIZE = 100;

    private Paging() {
    }
}
