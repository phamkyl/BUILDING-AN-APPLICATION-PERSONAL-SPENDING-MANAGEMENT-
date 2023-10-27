package com.savevvy.savvy.Model;

public class Data {

    private int amount;
    private String type;
    private String note;
    private String id;
    private String data;

    public Data(int amount, String type, String note, String id, String data) {
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.id = id;
        this.data = data;
    }



    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Data() {

    }



}
