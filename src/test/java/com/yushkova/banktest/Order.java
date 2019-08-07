package com.yushkova.banktest;

public class Order {
  private final String orderAmount;
  private final int orderAmountInt;
  private final String returnUrl;
  private final String userName;
  private final String password;
  private final String orderNumber;
  private String orderId;

  public Order(String orderAmount, int orderAmountInt, String returnUrl, String userName, String password, String orderNumber, String orderId) {
    this.orderAmount = orderAmount;
    this.orderAmountInt = orderAmountInt;
    this.returnUrl = returnUrl;
    this.userName = userName;
    this.password = password;
    this.orderNumber = orderNumber;
    this.orderId = orderId;
  }

  public String getOrderAmount() {
    return orderAmount;
  }

  public int getOrderAmountInt() {
    return orderAmountInt;
  }

  public String getReturnUrl() {
    return returnUrl;
  }

  public String getUserName() {
    return userName;
  }

  public String getPassword() {
    return password;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }
}
