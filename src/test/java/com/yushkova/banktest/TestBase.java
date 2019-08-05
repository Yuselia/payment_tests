package com.yushkova.banktest;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.testng.AssertJUnit.assertEquals;

public class TestBase {
  ChromeDriver wd;

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
  String[] namesOfRegisterParameters = {"orderId", "formUrl"};

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

  @BeforeMethod
  public void setUp() throws Exception {
    wd = new ChromeDriver();
    wd.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
  }

  protected String register() throws Exception {
    //get register request
    String registerRequest = getRegisterRequestUrl(mainUrl, partOfRegisterUrl, order);

    //get register response
    return request(registerRequest);
  }

  public String[] getParametersFromResponse(String response, String[] namesOfParameters) {
    JSONObject myResponse = new JSONObject(response.toString());
    String[] valuesOfParameters = new String[namesOfParameters.length];
    for (int i = 0; i < namesOfParameters.length; i++) {
      valuesOfParameters[i] = myResponse.getString(namesOfParameters[i]);
    }
    return valuesOfParameters;
  }

  private String getRegisterRequestUrl(String mainUrl, String partOfRegisterUrl, Order order) {
    //get register request
    return mainUrl + partOfRegisterUrl
            + "userName=" + order.getUserName() + "&password=" + order.getPassword()
            + "&orderId=&amount=" + order.getOrderAmount()
            + "&orderNumber=" + order.getOrderNumber()
            + "&returnUrl=" + order.getReturnUrl();
  }

  protected String getPaymentUrl(String mainUrl, String partOfPaymentUrl, String mdOrder) {
    //get payment URL
    return mainUrl + partOfPaymentUrl + "mdOrder=" + mdOrder;
  }

  protected void confirmPayment(String codeFromSms) {
    //confirm payment
    wd.findElement(By.cssSelector("[name=password]")).click();
    wd.findElement(By.name("password")).sendKeys(codeFromSms);
    wd.findElement(By.cssSelector("[type=submit]" )).click();
  }

  protected void payment(String paymentURL, Card card, String email, String phone) {
    wd.get(paymentURL);
    type(By.id("pan_visible"), card.getCardNumber());
    wd.findElement(By.id("month-button")).click();
    wd.findElement(By.xpath("//li[text() = '" + card.getExpMonth() + "']")).click();
    wd.findElement(By.id("year-button")).click();
    wd.findElement(By.xpath("//li[text() = '" + card.getExtYear() + "']")).click();
    type(By.id("iTEXT"), card.getOwner());
    type(By.id("iCVC"), card.getCvv2());
    type(By.id("email"), email);
    type(By.id("phoneInput"), phone);
    wd.findElement(By.id("buttonPayment")).click();
  }

  private void type(By locator, String text) {
    wd.findElement(locator).click();
    wd.findElement(locator).clear();
    wd.findElement(locator).sendKeys(text);
  }

  protected String getPaymentUrl(String[] valuesOfRegisterParameters) {
    //get payment URL
    String paymentUrl = getPaymentUrl(mainUrl, partOfPaymentUrl, valuesOfRegisterParameters[0]);
    assertEquals(paymentUrl, valuesOfRegisterParameters[1]);
    return paymentUrl;
  }

  @AfterMethod
  public void tearDown() {
    wd.quit();
  }
}
