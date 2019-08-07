package com.yushkova.banktest;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.testng.AssertJUnit.assertEquals;

public class ApplicationManager {
  private ChromeDriver wd;
  private WebDriverWait wait;

  //urls
  private String mainUrl = "https://web.rbsdev.com/alfapayment-release/";
  private String partOfRegisterUrl = "rest/register.do?";
  private String partOfPaymentUrl = "merchants/rbs/payment_ru.html?";
  private String partOfOrderStatusUrl = "rest/getOrderStatusExtended.do?";

  String[] namesOfRegisterParameters = {"orderId", "formUrl"};
  String[] namesOfOrderStatusParameters = {"errorCode", "errorMessage", "orderStatus", "amount", "paymentAmountInfo"};
  String[] namesOfPaymentAmountInfo = {"paymentState", "approvedAmount", "depositedAmount", "refundedAmount"};

  public void init() {
    wd = new ChromeDriver();
    wait = new WebDriverWait(wd, 100);
  }

  public static String sendRequest(String url) throws Exception {
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

  public String[] getParametersFromResponse(String response, String[] namesOfParameters) {
    JSONObject myResponse = new JSONObject(response.toString());
    String[] valuesOfParameters = new String[namesOfParameters.length];
    for (int i = 0; i < namesOfParameters.length; i++) {
      valuesOfParameters[i] = (myResponse.get(namesOfParameters[i])).toString();
    }
    return valuesOfParameters;
  }

  public String getRegisterRequestUrl(Order order) {
    //get register sendRequest
    return mainUrl + partOfRegisterUrl
            + "userName=" + order.getUserName() + "&password=" + order.getPassword()
            + "&orderId=&amount=" + order.getOrderAmount()
            + "&orderNumber=" + order.getOrderNumber()
            + "&returnUrl=" + order.getReturnUrl();
  }

  public String getPaymentUrl(String[] valuesOfRegisterParameters) {
    //get payment URL
    String paymentUrl = mainUrl + partOfPaymentUrl + "mdOrder=" + valuesOfRegisterParameters[0];
    assertEquals(paymentUrl, valuesOfRegisterParameters[1]);
    return paymentUrl;
  }

  private String getOrderStatusRequestUrl(Order order) {
    //get register sendRequest
    return mainUrl + partOfOrderStatusUrl
            + "userName=" + order.getUserName() + "&password=" + order.getPassword()
            + "&orderId=" + order.getOrderId()
            + "&language=ru"
            + "merchantOrderNumber=" + order.getOrderNumber();
  }

  public void payment(String paymentURL, Card card, String email, String phone) {
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
    if (isElementPresent(wd, By.cssSelector("form[name=form1]"))) {
      type(By.name("password"), card.getCodeFromSms());
      wd.findElement(By.cssSelector("[type=submit]")).click();
    }

  }

  public String getOrderStatus(Order order) throws Exception{
    String getOrderStatusRequest = getOrderStatusRequestUrl(order);
    return sendRequest(getOrderStatusRequest);
  }

  public void assertOrderStatus(Order order, String[] valuesOfOrderStatusParameters, String[] valuesOfPaymentAmountInfo, String assertPaymentState) {
    assertEquals("0", valuesOfOrderStatusParameters[0]);
    assertEquals("Успешно", valuesOfOrderStatusParameters[1]);
    assertEquals(String.valueOf(order.getOrderAmountInt()), valuesOfOrderStatusParameters[3]);
    assertEquals(assertPaymentState, valuesOfPaymentAmountInfo[0]);
    if(assertPaymentState == "DEPOSITED") {
      assertEquals("2", valuesOfOrderStatusParameters[2]);
      assertEquals(String.valueOf(order.getOrderAmountInt()), valuesOfPaymentAmountInfo[1]);
      assertEquals(String.valueOf(order.getOrderAmountInt()), valuesOfPaymentAmountInfo[2]);
      assertEquals("0", valuesOfPaymentAmountInfo[3]);
    }
  }

  private void type(By locator, String text) {
    wd.findElement(locator).click();
    wd.findElement(locator).clear();
    wd.findElement(locator).sendKeys(text);
  }

  boolean isElementPresent(WebDriver driver, By locator) {
    return driver.findElements(locator).size() > 0;
  }

  public void stop() {
    wd.quit();wd = null;
  }
}
