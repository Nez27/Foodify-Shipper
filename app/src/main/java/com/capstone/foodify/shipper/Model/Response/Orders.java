package com.capstone.foodify.shipper.Model.Response;

import com.capstone.foodify.shipper.Model.Order;
import com.capstone.foodify.shipper.Model.Page;

import java.util.List;

public class Orders {
    private List<Order> orders;
    private Page page;

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
