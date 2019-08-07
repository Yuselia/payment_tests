package com.yushkova.banktest;

import org.testng.annotations.Test;

import static com.yushkova.banktest.ApplicationManager.sendRequest;

public class paymentTest extends TestBase {

  //test values for register.do
  Order order = new Order("022018", Integer.parseInt("022018"), "http://ya.ru/", "task-yushkova-api", "020819", "7623574274527", "");
  //test values for payment
  Card card = new Card("4111 1111 1111 1111", "2019", "Декабрь", "Test", "123", "12345678");
  String email = "test@test.ru";
  String phone = "9270130570";

  @Test
  public void registerAndPayment() throws Exception {
    //register
    String registerRequest = app.getRegisterRequestUrl(order);
    String registerResponse = sendRequest(registerRequest);
    String[] valuesOfRegisterParameters = app.getParametersFromResponse(registerResponse, app.namesOfRegisterParameters);
    order.setOrderId(valuesOfRegisterParameters[0]);

    //payment
    String paymentUrl = app.getPaymentUrl(valuesOfRegisterParameters);
    app.payment(paymentUrl, card, email, phone);

    //assert Order Status
    String orderStatusResponse = app.getOrderStatus(order);
    String[] valuesOfOrderStatusParameters = app.getParametersFromResponse(orderStatusResponse, app.namesOfOrderStatusParameters);
    String[] valuesOfPaymentAmountInfo = app.getParametersFromResponse(valuesOfOrderStatusParameters[4], app.namesOfPaymentAmountInfo);
    app.assertOrderStatus(order, valuesOfOrderStatusParameters, valuesOfPaymentAmountInfo, "DEPOSITED");
  }
}