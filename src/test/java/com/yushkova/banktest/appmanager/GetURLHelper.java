package com.yushkova.banktest.appmanager;

import com.yushkova.banktest.models.Order;

import static org.testng.AssertJUnit.assertEquals;

public class GetURLHelper {
  //urls
  private String mainUrl = "https://web.rbsdev.com/alfapayment-release/";
  private String partOfRegisterUrl = "rest/register.do?";
  private String partOfPaymentUrl = "merchants/rbs/payment_ru.html?";
  private String partOfOrderStatusUrl = "rest/getOrderStatusExtended.do?";
  private String partOfReverseUrl = "rest/reverse.do?language=ru";
  private String partOfRefundUrl = "rest/refund.do?";

  public String getRegisterRequestUrl(Order order) {
    //get register sendRequest
    return mainUrl + partOfRegisterUrl
            + "userName=" + order.getUserName() + "&password=" + order.getPassword()
            + "&orderId=&amount=" + order.getOrderAmount()
            + "&orderNumber=" + order.getOrderNumber()
            + "&returnUrl=" + order.getReturnUrl();
  }

  public String getRegisterRequestUrl(Order order, int sessionTimeoutSecs) {
    //get register sendRequest
    return mainUrl + partOfRegisterUrl
            + "userName=" + order.getUserName() + "&password=" + order.getPassword()
            + "&orderId=&amount=" + order.getOrderAmount()
            + "&orderNumber=" + order.getOrderNumber()
            + "&returnUrl=" + order.getReturnUrl()
            + "&sessionTimeoutSecs=" + String.valueOf(sessionTimeoutSecs);
  }

  public String getPaymentUrl(String[] valuesOfRegisterParameters) {
    //get payment URL
    String paymentUrl = mainUrl + partOfPaymentUrl + "mdOrder=" + valuesOfRegisterParameters[0];
    assertEquals(paymentUrl, valuesOfRegisterParameters[1]);
    return paymentUrl;
  }

  public String getOrderStatusRequestUrl(Order order) {
    //get register sendRequest
    return mainUrl + partOfOrderStatusUrl
            + "userName=" + order.getUserName() + "&password=" + order.getPassword()
            + "&orderId=" + order.getOrderId()
            + "&language=ru"
            + "merchantOrderNumber=" + order.getOrderNumber();
  }

  public String getReverseUrl(Order order) {
    return mainUrl + partOfReverseUrl
            + "&orderId=" + order.getOrderId()
            + "&password=" + order.getPassword()
            + "&userName=" + order.getUserName();
  }

  public String getRefundUrl(Order order, String amount) {
    return mainUrl + partOfRefundUrl
            + "amount=" + amount
            + "&currency=810&language=ru"
            + "&orderId=" + order.getOrderId()
            + "&password=" + order.getPassword()
            + "&userName=" + order.getUserName();
  }
}
