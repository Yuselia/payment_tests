package com.yushkova.banktest;

import com.yushkova.banktest.models.Card;
import com.yushkova.banktest.models.Order;
import org.testng.annotations.Test;

import static com.yushkova.banktest.ApplicationManager.sendRequest;

public class DeclineTests extends TestBase {

  @Test
  public void declineBankAuthorization() throws Exception {
    int tryCount = 3;
    //test values for register.do
    Order order2 = new Order().withOrderAmount("022018").withOrderAmountInt(Integer.parseInt("022018")).withReturnUrl("https://ya.ru/").withUserName("task-yushkova-api").withPassword("020819").withOrderNumber("7623574274527");
    //Here should be wrong code from sms
    Card card2 = new Card().withCardNumber("4111 1111 1111 1111").withExtYear("2019").withExpMonth("Декабрь").withOwner("Test").withCvv2("123").withCodeFromSms("1456");
    String email = "test@test.ru";
    String phone = "9270130570";

    //register
    String registerRequest = app.getRegisterRequestUrl(order2);
    String registerResponse = sendRequest(registerRequest);
    String[] valuesOfRegisterParameters = app.getParametersFromResponse(registerResponse, app.namesOfRegisterParameters);
    order2.withOrderId(valuesOfRegisterParameters[0]);

    //payment
    String paymentUrl = app.getPaymentUrl(valuesOfRegisterParameters);
    while (tryCount != 0) {
      app.payment(paymentUrl, card2, email, phone, order2);
      app.afterClickPaymentButton(order2, card2, false);
      tryCount --;
    }

    //assert Order Status
    String orderStatusUrlRequest = app.getOrderStatusRequestUrl(order2);
    String orderStatusResponse = sendRequest(orderStatusUrlRequest);
    String[] valuesOfOrderStatusParameters = app.getParametersFromResponse(orderStatusResponse, app.namesOfOrderStatusParameters);
    String[] valuesOfPaymentAmountInfo = app.getParametersFromResponse(valuesOfOrderStatusParameters[4], app.namesOfPaymentAmountInfo);
    app.assertOrderStatus(order2, valuesOfOrderStatusParameters, valuesOfPaymentAmountInfo, "DECLINED");
  }

  @Test
  public void tryThreeTimes() throws Exception {
    int tryCount = 3;
    //test values for register.do
    Order order2 = new Order().withOrderAmount("022018").withOrderAmountInt(Integer.parseInt("022018")).withReturnUrl("https://ya.ru/").withUserName("task-yushkova-api").withPassword("020819").withOrderNumber("7623574274527");
    //Here should be wrong code from sms
    Card card2 = new Card().withCardNumber("4111 1111 1111 1111").withExtYear("2019").withExpMonth("Декабрь").withOwner("Test").withCvv2("124").withCodeFromSms("12345678");
    String email = "test@test.ru";
    String phone = "9270130570";

    //register
    String registerRequest = app.getRegisterRequestUrl(order2);
    String registerResponse = sendRequest(registerRequest);
    String[] valuesOfRegisterParameters = app.getParametersFromResponse(registerResponse, app.namesOfRegisterParameters);
    order2.withOrderId(valuesOfRegisterParameters[0]);

    //payment
    String paymentUrl = app.getPaymentUrl(valuesOfRegisterParameters);
    while (tryCount != 0) {
      app.payment(paymentUrl, card2, email, phone, order2);
      app.afterClickPaymentButton(order2, card2, false);
      tryCount --;
    }

    //assert Order Status
    String orderStatusUrlRequest = app.getOrderStatusRequestUrl(order2);
    String orderStatusResponse = sendRequest(orderStatusUrlRequest);
    String[] valuesOfOrderStatusParameters = app.getParametersFromResponse(orderStatusResponse, app.namesOfOrderStatusParameters);
    String[] valuesOfPaymentAmountInfo = app.getParametersFromResponse(valuesOfOrderStatusParameters[4], app.namesOfPaymentAmountInfo);
    app.assertOrderStatus(order2, valuesOfOrderStatusParameters, valuesOfPaymentAmountInfo, "DECLINED");
  }

  @Test
  public void declineTimeOutAfterOpenedPage() throws Exception {
    int sessionTimeoutSecs = 30;
    //test values for register.do
    Order order1 = new Order().withOrderAmount("022018").withOrderAmountInt(Integer.parseInt("022018")).withReturnUrl("https://ya.ru/").withUserName("task-yushkova-api").withPassword("020819").withOrderNumber("7623574274527");
    //test values for payment

    //register
    String registerRequest = app.getRegisterRequestUrl(order1, sessionTimeoutSecs);
    String registerResponse = sendRequest(registerRequest);
    String[] valuesOfRegisterParameters = app.getParametersFromResponse(registerResponse, app.namesOfRegisterParameters);
    order1.withOrderId(valuesOfRegisterParameters[0]);

    //payment
    String paymentUrl = app.getPaymentUrl(valuesOfRegisterParameters);
    app.openPaymentPage(paymentUrl);
    app.waitReturnUrl(order1);

    //assert Order Status
    app.assertOrder(order1, "DECLINED");
  }

  @Test
  public void declineAfterOneTryAndTimeOut() throws Exception {
    int sessionTimeoutSecs = 30;
    //test values for register.do
    Order order2 = new Order().withOrderAmount("022018").withOrderAmountInt(Integer.parseInt("022018")).withReturnUrl("https://ya.ru/").withUserName("task-yushkova-api").withPassword("020819").withOrderNumber("7623574274527");
    //Here should be not valid values of card
    Card card2 = new Card().withCardNumber("4111 1111 1111 1111").withExtYear("2019").withExpMonth("Декабрь").withOwner("Test").withCvv2("124").withCodeFromSms("12345678");
    String email = "test@test.ru";
    String phone = "9270130570";

    //register
    String registerRequest = app.getRegisterRequestUrl(order2, sessionTimeoutSecs);
    String registerResponse = sendRequest(registerRequest);
    String[] valuesOfRegisterParameters = app.getParametersFromResponse(registerResponse, app.namesOfRegisterParameters);
    order2.withOrderId(valuesOfRegisterParameters[0]);

    //payment
    String paymentUrl = app.getPaymentUrl(valuesOfRegisterParameters);
    app.payment(paymentUrl, card2, email, phone, order2);
    app.afterClickPaymentButton(order2, card2, false);
    app.waitReturnUrl(order2);

    //assert Order Status
    app.assertOrder(order2, "DECLINED");
  }
}