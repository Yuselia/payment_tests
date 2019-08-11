package com.yushkova.banktest.tests;

import com.yushkova.banktest.models.Card;
import com.yushkova.banktest.models.Order;
import org.testng.annotations.Test;

public class ReversedTests extends TestBase {

  //test values for register.do
  Order order = new Order().withOrderAmount("022018").withOrderAmountInt(Integer.parseInt("022018")).withReturnUrl("https://ya.ru/").withUserName("task-yushkova-api").withPassword("020819").withOrderNumber("7623574274527");
  //there are correct card values
  Card card = new Card().withCardNumber("5555 5555 5555 5599").withExtYear("2019").withExpMonth("Декабрь").withOwner("Test").withCvv2("123");
  String email = "";
  String phone = "";

  @Test
  public void reverseTest() throws Exception {
    //register
    String registerRequest = app.getGetURLHelper().getRegisterRequestUrl(order);
    String registerResponse = app.getOrderAssertHelper().sendRequest(registerRequest);
    String[] valuesOfRegisterParameters = app.getOrderAssertHelper().getParametersFromResponse(registerResponse, namesOfRegisterParameters);
    order.withOrderId(valuesOfRegisterParameters[0]);

    //payment
    String paymentUrl = app.getGetURLHelper().getPaymentUrl(valuesOfRegisterParameters);
    app.payment(paymentUrl, card, email, phone, order);
    app.afterClickPaymentButton(order, card, true);
    app.waitReturnUrl(order);

    //assert Order Status
    app.getOrderAssertHelper().assertOrder(order, "DEPOSITED", namesOfOrderStatusParameters, namesOfPaymentAmountInfo);

    //reverse
    String reverseRequest = app.getGetURLHelper().getReverseUrl(order);
    String reverseResponse = app.getOrderAssertHelper().sendRequest(reverseRequest);
    String[] valuesOfReverseParameters = app.getOrderAssertHelper().getParametersFromResponse(reverseResponse, namesOfReverseAndRefundParameters);
    app.getOrderAssertHelper().assertRequestStatus(order, valuesOfReverseParameters);

    //assert Order Status again
    app.getOrderAssertHelper().assertOrder(order, "REVERSED", namesOfOrderStatusParameters, namesOfPaymentAmountInfo);
  }
}