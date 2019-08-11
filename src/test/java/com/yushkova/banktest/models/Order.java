package com.yushkova.banktest.models;

public class Order {
  private String orderAmount;
  private int orderAmountInt;
  private String returnUrl;
  private String userName;
  private String password;
  private String orderNumber;
  private String orderId;
  private int amountAfterRefund = 0;

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

  public int getAmountAfterRefund() {
    return amountAfterRefund;
  }

  public Order withOrderId(String orderId) {
    this.orderId = orderId;
    return this;
  }

  public Order withAmountAfterRefund(int amountAfterRefund) {
    this.amountAfterRefund = amountAfterRefund;
    return this;
  }

  public Order withOrderAmount(String orderAmount) {
    this.orderAmount = orderAmount;
    return this;
  }

  public Order withOrderAmountInt(int orderAmountInt) {
    this.orderAmountInt = orderAmountInt;
    return this;
  }

  public Order withReturnUrl(String returnUrl) {
    this.returnUrl = returnUrl;
    return this;
  }

  public Order withUserName(String userName) {
    this.userName = userName;
    return this;
  }

  public Order withPassword(String password) {
    this.password = password;
    return this;
  }

  public Order withOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
    return this;
  }
}
