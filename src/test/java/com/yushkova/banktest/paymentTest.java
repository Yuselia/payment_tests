package com.yushkova.banktest;

import org.testng.annotations.Test;

public class paymentTest extends TestBase {

  @Test
  public void registerAndPayment() throws Exception {
    //register
    String response = app.register();
    String[] valuesOfRegisterParameters = app.getParametersFromResponse(response, app.namesOfRegisterParameters);

    //payment
    String paymentUrl = app.getPaymentUrl(valuesOfRegisterParameters);
    app.payment(paymentUrl, app.card, app.email, app.phone);
    app.confirmPayment(app.card.getCodeFromSms());
  }
}