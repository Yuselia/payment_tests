package com.yushkova.banktest.tests;

import com.yushkova.banktest.models.Card;
import com.yushkova.banktest.models.Order;
import org.testng.annotations.Test;

public class RegisterAndPaymentOnly extends TestBase {

  //test values for register.do
  Order order = new Order().withOrderAmount("022018").withReturnUrl("https://ya.ru/").withUserName("task-yushkova-api").withPassword("020819").withOrderNumber("7623574274527");
  //there are correct card values
  Card card = new Card().withCardNumber("4111 1111 1111 1111").withExtYear("2019").withExpMonth("Декабрь").withOwner("Test").withCvv2("123").withCodeFromSms("12345678");
  String email = "test@test.ru";
  String phone = "9270130570";

  @Test
  public void smoke() throws Exception {
    order.withOrderAmountInt(Integer.parseInt(order.getOrderAmount()));
    //register
    String registerRequest = app.getGetURLHelper().getRegisterRequestUrl(order);
    String registerResponse = app.getOrderAssertHelper().sendRequest(registerRequest);
    String[] valuesOfRegisterParameters = app.getOrderAssertHelper().getParametersFromResponse(registerResponse, namesOfRegisterParameters);
    order.withOrderId(valuesOfRegisterParameters[0]);

    //payment
    String paymentUrl = app.getGetURLHelper().getPaymentUrl(valuesOfRegisterParameters);
    app.openPaymentPage(paymentUrl);
    app.payment(card, email, phone, order);
    app.afterClickPaymentButton(order, card, true);
    app.waitReturnUrl(order);

    //assert Order Status
    app.getOrderAssertHelper().assertOrder(order, "DEPOSITED", namesOfOrderStatusParameters, namesOfPaymentAmountInfo);
  }
}