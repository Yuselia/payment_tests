package com.yushkova.banktest.models;

public class Order {
  private final String orderAmount;
  private final int orderAmountInt;
  private final String returnUrl;
  private final String userName;
  private final String password;
  private final String orderNumber;
  private String orderId;
  private int amountAfterRefund;

  public Order(String orderAmount, int orderAmountInt, String returnUrl, String userName, String password, String orderNumber, String orderId, int amountAfterRefund) {
    this.orderAmount = orderAmount;
    this.orderAmountInt = orderAmountInt;
    this.returnUrl = returnUrl;
    this.userName = userName;
    this.password = password;
    this.orderNumber = orderNumber;
    this.orderId = orderId;
    this.amountAfterRefund = amountAfterRefund;
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

  public int getAmountAfterRefund() {
    return amountAfterRefund;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public void setAmountAfterRefund(int amountAfterRefund) {
    this.amountAfterRefund = amountAfterRefund;
  }
}
