package com.yushkova.banktest.tests;

import com.yushkova.banktest.models.Card;
import com.yushkova.banktest.models.Order;
import org.testng.annotations.Test;

public class RefundTests extends TestBase {

  //test values for register.do
  Order order = new Order().withOrderAmount("022018").withOrderAmountInt(Integer.parseInt("022018")).withReturnUrl("https://ya.ru/").withUserName("task-yushkova-api").withPassword("020819").withOrderNumber("7623574274527");
  //there are correct card values
  Card card = new Card().withCardNumber("5555 5555 5555 5599").withExtYear("2019").withExpMonth("Декабрь").withOwner("Test").withCvv2("123");
  String email = "";
  String phone = "";
  String amountRefundString = "010010";

  @Test
  public void refundTest() throws Exception {
    int amountRefund = Integer.parseInt(amountRefundString);
    int countCanRefund = order.getOrderAmountInt() / amountRefund;
    //register
    String registerRequest = app.getRegisterRequestUrl(order);
    String registerResponse = app.sendRequest(registerRequest);
    String[] valuesOfRegisterParameters = app.getParametersFromResponse(registerResponse, namesOfRegisterParameters);
    order.withOrderId(valuesOfRegisterParameters[0]);

    //payment
    String paymentUrl = app.getPaymentUrl(valuesOfRegisterParameters);
    app.payment(paymentUrl, card, email, phone, order);
    app.afterClickPaymentButton(order, card, true);
    app.waitReturnUrl(order);

    //assert Order Status
    app.assertOrder(order, "DEPOSITED", namesOfOrderStatusParameters, namesOfPaymentAmountInfo);
    String refundRequest = app.getRefundUrl(order, amountRefundString);

    //refund
    for (int i = 0; i < countCanRefund; i ++) {
      String refundResponse = app.sendRequest(refundRequest);
      String[] valuesOfRefundParameters = app.getParametersFromResponse(refundResponse, namesOfReverseAndRefundParameters);
      app.assertRequestStatus(order, valuesOfRefundParameters);
      amountRefund = amountRefund * (i+1);
      order.withAmountAfterRefund(order.getOrderAmountInt() - amountRefund);

      app.assertOrder(order, "REFUNDED", order.getAmountAfterRefund(), namesOfOrderStatusParameters, namesOfPaymentAmountInfo);
    }

    tryToRefundOneMore(amountRefund, refundRequest);
  }

  private void tryToRefundOneMore(int amountRefund, String refundRequest) throws Exception {
    String refundResponse = app.sendRequest(refundRequest);
    String[] valuesOfRefundParameters = app.getParametersFromResponse(refundResponse, namesOfReverseAndRefundParameters);
    app.assertWrongRequest(order, valuesOfRefundParameters);

    app.assertOrder(order, "REFUNDED", order.getAmountAfterRefund(), namesOfOrderStatusParameters, namesOfPaymentAmountInfo);
  }
}