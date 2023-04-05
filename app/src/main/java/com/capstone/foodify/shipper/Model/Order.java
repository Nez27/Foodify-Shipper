package com.capstone.foodify.shipper.Model;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private int id;
    private String orderTrackingNumber;
    private String paymentMethod;
    private float shippingCost;
    private float productCost;
    private String status;
    private String address;
    private User user;
    private Shipper shipper;
    private List<OrderDetail> orderDetails;
    private String orderTime;
    private float  total;
    private double lat;
    private double lng;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Order(String orderTrackingNumber) {
        this.orderTrackingNumber = orderTrackingNumber;
    }
    public String getOrderTrackingNumber() {
        return orderTrackingNumber;
    }

    public void setOrderTrackingNumber(String orderTrackingNumber) {
        this.orderTrackingNumber = orderTrackingNumber;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public float getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(float shippingCost) {
        this.shippingCost = shippingCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public List<OrderDetail> getListOrderDetail() {
        return orderDetails;
    }

    public void setListOrderDetail(List<OrderDetail> listOrderDetail) {
        this.orderDetails = listOrderDetail;
    }

    public float getProductCost() {
        return productCost;
    }

    public void setProductCost(float productCost) {
        this.productCost = productCost;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Shipper getShipper() {
        return shipper;
    }

    public void setShipper(Shipper shipper) {
        this.shipper = shipper;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
