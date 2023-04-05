package com.capstone.foodify.shipper.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderDetail implements Serializable {
    private int id;
    @SerializedName("product")
    private Food food;
    private String quantity;
    private float subtotal;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }
}
