package com.yushkova.banktest.tests;

import com.yushkova.banktest.models.Card;
import com.yushkova.banktest.models.Order;
import org.testng.annotations.Test;

import static com.yushkova.banktest.ApplicationManager.sendRequest;

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
    String registerRequest = app.getRegisterRequestUrl(order);
    String registerResponse = sendRequest(registerRequest);
    String[] valuesOfRegisterParameters = app.getParametersFromResponse(registerResponse, namesOfRegisterParameters);
    order.withOrderId(valuesOfRegisterParameters[0]);

    //payment
    String paymentUrl = app.getPaymentUrl(valuesOfRegisterParameters);
    app.payment(paymentUrl, card, email, phone, order);
    app.afterClickPaymentButton(order, card, true);
    app.waitReturnUrl(order);

    //assert Order Status
    app.assertOrder(order, "DEPOSITED", namesOfOrderStatusParameters, namesOfPaymentAmountInfo);

    //reverse
    String reverseRequest = app.getReverseUrl(order);
    String reverseResponse = sendRequest(reverseRequest);
    String[] valuesOfReverseParameters = app.getParametersFromResponse(reverseResponse, namesOfReverseAndRefundParameters);
    app.assertRequestStatus(order, valuesOfReverseParameters);

    //assert Order Status again
    app.assertOrder(order, "REVERSED", namesOfOrderStatusParameters, namesOfPaymentAmountInfo);
  }
}