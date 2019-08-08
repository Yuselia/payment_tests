package com.yushkova.banktest;

import com.yushkova.banktest.models.Card;
import com.yushkova.banktest.models.Order;
import org.testng.annotations.Test;

import static com.yushkova.banktest.ApplicationManager.sendRequest;

public class DeclineTests extends TestBase {

  @Test
  public void declineStateAfterOpenPageAndTimeOut() throws Exception {
    //test values for register.do
    Order order1 = new Order("022018", Integer.parseInt("022018"), "http://ya.ru/", "task-yushkova-api", "020819", "7623574274527", "");
    //test values for payment
    Card card1 = new Card("4111 1111 1111 1111", "2019", "Декабрь", "Test", "123", "12345678");
    String email = "test@test.ru";
    String phone = "9270130570";

    //register
    String registerRequest = app.getRegisterRequestUrl(order1);
    String registerResponse = sendRequest(registerRequest);
    String[] valuesOfRegisterParameters = app.getParametersFromResponse(registerResponse, app.namesOfRegisterParameters);
    order1.setOrderId(valuesOfRegisterParameters[0]);

    //payment
    String paymentUrl = app.getPaymentUrl(valuesOfRegisterParameters);
    app.openPage(paymentUrl);
    wait(1200000);


    //assert Order Status
    String orderStatusUrlRequest = app.getOrderStatusRequestUrl(order1);
    String orderStatusResponse = sendRequest(orderStatusUrlRequest);
    String[] valuesOfOrderStatusParameters = app.getParametersFromResponse(orderStatusResponse, app.namesOfOrderStatusParameters);
    String[] valuesOfPaymentAmountInfo = app.getParametersFromResponse(valuesOfOrderStatusParameters[4], app.namesOfPaymentAmountInfo);
    app.assertOrderStatus(order1, valuesOfOrderStatusParameters, valuesOfPaymentAmountInfo, "DECLINED");
  }

  @Test
  public void declineStateAfterOneTryAndTimeOut() throws Exception {
    //test values for register.do
    Order order2 = new Order("022018", Integer.parseInt("022018"), "http://ya.ru/", "task-yushkova-api", "020819", "7623574274527", "");
    //this is card with wrong cvv2
    Card card2 = new Card("4111 1111 1111 1111", "2019", "Декабрь", "Test", "124", "12345678");
    String email = "test@test.ru";
    String phone = "9270130570";

    //register
    String registerRequest = app.getRegisterRequestUrl(order2);
    String registerResponse = sendRequest(registerRequest);
    String[] valuesOfRegisterParameters = app.getParametersFromResponse(registerResponse, app.namesOfRegisterParameters);
    order2.setOrderId(valuesOfRegisterParameters[0]);

    //payment
    String paymentUrl = app.getPaymentUrl(valuesOfRegisterParameters);
    app.payment(paymentUrl, card2, email, phone);
    wait(1200000);

    //assert Order Status
    String orderStatusUrlRequest = app.getOrderStatusRequestUrl(order2);
    String orderStatusResponse = sendRequest(orderStatusUrlRequest);
    String[] valuesOfOrderStatusParameters = app.getParametersFromResponse(orderStatusResponse, app.namesOfOrderStatusParameters);
    String[] valuesOfPaymentAmountInfo = app.getParametersFromResponse(valuesOfOrderStatusParameters[4], app.namesOfPaymentAmountInfo);
    app.assertOrderStatus(order2, valuesOfOrderStatusParameters, valuesOfPaymentAmountInfo, "DECLINED");
  }
}