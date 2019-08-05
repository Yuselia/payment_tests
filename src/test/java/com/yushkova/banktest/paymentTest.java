package com.yushkova.banktest;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class paymentTest {
  ChromeDriver wd;

  @BeforeMethod
  public void setUp() throws Exception {
    wd = new ChromeDriver();
    wd.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
  }

  @Test
  public void registerAndPayment() throws Exception {
    //urls
    String mainUrl = "https://web.rbsdev.com/alfapayment-release/";
    String partOfRegisterUrl = "rest/register.do?";
    String partOfPaymentUrl = "merchants/rbs/payment_ru.html?";

    //test values for register.do
    Order order = new Order("022018", "http://ya.ru/", "task-yushkova-api", "020819", "7623574274527");

    //test values for payment
    Card card = new Card("4111111111111111", "2019", "Декабрь", "Test", "123", "12345678");
    String email = "test@test.ru";
    String phone = "9270130570";

    //get register request
    String registerRequest = getRegisterRequestUrl(mainUrl, partOfRegisterUrl, order);

    //get register response
    String response = request(registerRequest);
    String mdOrder = getOrderId(response);

    //get payment URL
    String paymentUrl = getPaymentUrl(mainUrl, partOfPaymentUrl, mdOrder);

    //payment
    payment(paymentUrl, card, email, phone);
    confirmPayment(card.getCodeFromSms());
  }

  private String getRegisterRequestUrl(String mainUrl, String partOfRegisterUrl, Order order) {
    //get register request
    return mainUrl + partOfRegisterUrl
            + "userName=" + order.getUserName() + "&password=" + order.getPassword()
            + "&orderId=&amount=" + order.getOrderAmount()
            + "&orderNumber=" + order.getOrderNumber()
            + "&returnUrl=" + order.getReturnUrl();
  }

  private String getPaymentUrl(String mainUrl, String partOfPaymentUrl, String mdOrder) {
    //get payment URL
    return mainUrl + partOfPaymentUrl + "mdOrder=" + mdOrder;
  }

  private void confirmPayment(String codeFromSms) {
    //confirm payment
    wd.findElement(By.cssSelector("[name=password]")).click();
    wd.findElement(By.name("password")).sendKeys(codeFromSms);
    wd.findElement(By.cssSelector("[type=submit]" )).click();
  }

  private void payment(String paymentURL, Card card, String email, String phone) {
    wd.get(paymentURL);
    wd.findElement(By.id("pan_visible")).click();
    wd.findElement(By.id("pan_visible")).clear();
    wd.findElement(By.id("pan_visible")).sendKeys(card.getCardNumber());
    wd.findElement(By.id("month-button")).click();
    wd.findElement(By.xpath("//li[text() = '" + card.getExpMonth() + "']")).click();
    wd.findElement(By.id("year-button")).click();
    wd.findElement(By.xpath("//li[text() = '" + card.getExtYear() + "']")).click();
    wd.findElement(By.id("iTEXT")).click();
    wd.findElement(By.id("iTEXT")).clear();
    wd.findElement(By.id("iTEXT")).sendKeys(card.getOwner());
    wd.findElement(By.id("iCVC")).click();
    wd.findElement(By.id("iCVC")).clear();
    wd.findElement(By.id("iCVC")).sendKeys(card.getCvv2());
    wd.findElement(By.id("email")).click();
    wd.findElement(By.id("email")).clear();
    wd.findElement(By.id("email")).sendKeys(email);
    wd.findElement(By.id("phoneInput")).click();
    wd.findElement(By.id("phoneInput")).clear();
    wd.findElement(By.id("phoneInput")).sendKeys(phone);
    wd.findElement(By.id("buttonPayment")).click();
  }

  private static String request(String url) throws Exception {
    URL obj = new URL(url);
    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
    con.setRequestMethod("GET");
    int responseCode = con.getResponseCode();
    assertEquals(200, responseCode);
    BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    return response.toString();
  }

  private static String getOrderId(String response) {
    JSONObject myResponse = new JSONObject(response.toString());
    String orderId = myResponse.getString("orderId");
    String formUrl = myResponse.getString("formUrl");
    assertTrue(formUrl.contains(orderId));
    return orderId;
  }

  @AfterMethod
  public void tearDown() {
    wd.quit();
  }

}