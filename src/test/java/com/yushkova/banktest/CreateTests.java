package com.yushkova.banktest;

import com.yushkova.banktest.models.Card;
import com.yushkova.banktest.models.Order;
import org.testng.annotations.Test;

import static com.yushkova.banktest.ApplicationManager.sendRequest;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

public class CreateTests extends TestBase {

  @Test
  public void createStateAfterClosePaymentPage() throws Exception {
    //test values for register.do
    Order order1 = new Order("022018", Integer.parseInt("022018"), "http://ya.ru/", "task-yushkova-api", "020819", "7623574274527", "", 0);
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
    app.openPaymentPage(paymentUrl);
    app.stop();

    //assert Order Status
    app.assertOrder(order1, "CREATED");
  }

  @Test
  public void createStateAfterOnePaymentTry() throws Exception {
    //test values for register.do
    Order order2 = new Order("022018", Integer.parseInt("022018"), "https://ya.ru/", "task-yushkova-api", "020819", "7623574274527", "", 0);
    //Here should be not valid values of card
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
    app.payment(paymentUrl, card2, email, phone, order2);

    //assert Order Status
    app.assertOrder(order2, "CREATED");
  }
}