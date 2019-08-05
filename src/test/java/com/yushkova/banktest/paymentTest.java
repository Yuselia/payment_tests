package com.yushkova.banktest;

import org.testng.annotations.Test;

public class paymentTest extends TestBase {

  @Test
  public void registerAndPayment() throws Exception {
    //register
    String response = register();
    String[] valuesOfRegisterParameters = getParametersFromResponse(response, namesOfRegisterParameters);

    //payment
    String paymentUrl = getPaymentUrl(valuesOfRegisterParameters);
    payment(paymentUrl, card, email, phone);
    confirmPayment(card.getCodeFromSms());
  }
}