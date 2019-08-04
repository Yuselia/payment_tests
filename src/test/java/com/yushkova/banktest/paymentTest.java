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
  public void paymentTest() throws Exception {
    //urls
    String mainUrl = "https://web.rbsdev.com/alfapayment-release/";
    String partOfRegisterUrl = "rest/register.do?";
    String partOfPaymentUrl = "merchants/rbs/payment_ru.html?";

    //test values for register.do
    String orderAmount = "022018";
    String returnUrl = "http://ya.ru/";
    String userName = "task-yushkova-api";
    String password = "020819";
    String orderNumber = "7623574274527";

    //get register request
    String registerRequest = mainUrl + partOfRegisterUrl
            + "userName=" + userName + "&password=" + password
            + "&orderId=&amount=" + orderAmount
            + "&orderNumber=" + orderNumber
            + "&returnUrl=" + returnUrl;

    //get register response
    String response = request(registerRequest);
    String mdOrder = getOrderId(response);

    //test values for payment
    String cardNumber = "4111111111111111";
    String extYear = "2019";
    String expMonth = "Декабрь";
    String owner = "Test";
    String cvv2 = "123";
    String codeFromSms = "12345678";
    String email = "test@test.ru";
    String phone = "9270130570";

    //payment
    payment(mainUrl, partOfPaymentUrl, mdOrder, cardNumber, extYear, expMonth, owner, cvv2, email, phone);

    //confirm payment
    wd.findElement(By.cssSelector("[name=password]")).click();
    wd.findElement(By.name("password")).sendKeys(codeFromSms);
    wd.findElement(By.cssSelector("[type=submit]" )).click();
  }

  private void payment(String mainUrl, String partOfPaymentUrl, String mdOrder, String cardNumber, String extYear, String expMonth, String owner, String cvv2, String email, String phone) {
    wd.get(mainUrl + partOfPaymentUrl + "mdOrder=" + mdOrder);
    wd.findElement(By.id("pan_visible")).click();
    wd.findElement(By.id("pan_visible")).sendKeys(cardNumber);
    wd.findElement(By.id("month-button")).click();
    wd.findElement(By.xpath("//li[text() = '" + expMonth + "']")).click();
    wd.findElement(By.id("year-button")).click();
    wd.findElement(By.xpath("//li[text() = '" + extYear + "']")).click();
    wd.findElement(By.id("iTEXT")).click();
    wd.findElement(By.id("iTEXT")).sendKeys(owner);
    wd.findElement(By.id("iCVC")).click();
    wd.findElement(By.id("iCVC")).sendKeys(cvv2);
    wd.findElement(By.id("email")).click();
    wd.findElement(By.id("email")).sendKeys(email);
    wd.findElement(By.id("phoneInput")).click();
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