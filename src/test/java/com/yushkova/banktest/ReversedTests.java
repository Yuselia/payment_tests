package com.yushkova.banktest;

import com.yushkova.banktest.TestBase;
import com.yushkova.banktest.models.Card;
import com.yushkova.banktest.models.Order;
import org.testng.annotations.Test;

import static com.yushkova.banktest.ApplicationManager.sendRequest;

public class ReversedTests extends TestBase {

  //test values for register.do
  Order order = new Order("022018", Integer.parseInt("022018"), "https://ya.ru/", "task-yushkova-api", "020819", "7623574274527", "");
  //there are correct card values
  Card card = new Card("5555 5555 5555 5599", "2019", "Декабрь", "Test", "123", "");
  String email = "";
  String phone = "";

  @Test
  public void reverseTest() throws Exception {
    //register
    String registerRequest = app.getRegisterRequestUrl(order);
    String registerResponse = sendRequest(registerRequest);
    String[] valuesOfRegisterParameters = app.getParametersFromResponse(registerResponse, app.namesOfRegisterParameters);
    order.setOrderId(valuesOfRegisterParameters[0]);

    //payment
    String paymentUrl = app.getPaymentUrl(valuesOfRegisterParameters);
    app.payment(paymentUrl, card, email, phone, order);

    //assert Order Status
    String orderStatusUrlRequest = app.getOrderStatusRequestUrl(order);
    String orderStatusResponse = sendRequest(orderStatusUrlRequest);
    String[] valuesOfOrderStatusParameters = app.getParametersFromResponse(orderStatusResponse, app.namesOfOrderStatusParameters);
    String[] valuesOfPaymentAmountInfo = app.getParametersFromResponse(valuesOfOrderStatusParameters[4], app.namesOfPaymentAmountInfo);
    app.assertOrderStatus(order, valuesOfOrderStatusParameters, valuesOfPaymentAmountInfo, "DEPOSITED");

    //reverse
    String reverseRequest = app.getReverseUrl(order);
    String reverseResponse = sendRequest(reverseRequest);
    String[] valuesOfReverseParameters = app.getParametersFromResponse(reverseResponse, app.namesOfReverseParameters);
    app.assertReverseStatus(order, valuesOfReverseParameters);
  }
}