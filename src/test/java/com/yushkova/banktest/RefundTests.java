package com.yushkova.banktest;

import com.yushkova.banktest.models.Card;
import com.yushkova.banktest.models.Order;
import org.testng.annotations.Test;

import static com.yushkova.banktest.ApplicationManager.sendRequest;

public class RefundTests extends TestBase {

  //test values for register.do
  Order order = new Order("022018", Integer.parseInt("022018"), "http://ya.ru/", "task-yushkova-api", "020819", "7623574274527", "");
  //there are correct card values
  Card card = new Card("5555 5555 5555 5599", "2019", "Декабрь", "Test", "123", "");
  String email = "";
  String phone = "";
  String amounRefund = "010010";

  @Test
  public void refundTest() throws Exception {
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

    //refund
    String refundRequest = app.getRefundUrl(order, amounRefund);
    String refundResponse = sendRequest(refundRequest);
    String[] valuesOfRefundParameters = app.getParametersFromResponse(refundResponse, app.namesOfRefundParameters);
    app.assertRefundStatus(order, valuesOfRefundParameters);
  }
}