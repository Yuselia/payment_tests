package com.yushkova.banktest;

public class Order {
  private final String orderAmount;
  private final String returnUrl;
  private final String userName;
  private final String password;
  private final String orderNumber;

  public Order(String orderAmount, String returnUrl, String userName, String password, String orderNumber) {
    this.orderAmount = orderAmount;
    this.returnUrl = returnUrl;
    this.userName = userName;
    this.password = password;
    this.orderNumber = orderNumber;
  }

  public String getOrderAmount() {
    return orderAmount;
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
}
