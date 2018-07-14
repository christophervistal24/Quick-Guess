package com.example.michellebiol.sampleapp.Models;

public class CategoriesItem {

    private String head;
    private String desc;


    public CategoriesItem(String head , String desc)
    {
        this.head = head;
        this.desc = desc;
    }

    public String getHead() {
        return head;
    }

    public String getDesc() {
        return desc;
    }
}
