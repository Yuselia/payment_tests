package com.yushkova.banktest;

import com.sun.tools.javac.util.Convert;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.testng.AssertJUnit.assertEquals;

public class ApplicationManager {
  ChromeDriver wd;
  //urls
  String mainUrl = "https://web.rbsdev.com/alfapayment-release/";
  String partOfRegisterUrl = "rest/register.do?";
  String partOfPaymentUrl = "merchants/rbs/payment_ru.html?";
  String partOfOrderStatusUrl = "rest/getOrderStatusExtended.do?";

  String[] namesOfRegisterParameters = {"orderId", "formUrl"};
  String[] namesOfOrderStatusParameters = {"errorCode", "errorMessage", "orderStatus", "amount", "paymentAmountInfo"};
  String[] namesOfPaymentAmountInfo = {"paymentState", "approvedAmount", "depositedAmount", "refundedAmount"};

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

  public void init() {
    wd = new ChromeDriver();
    wd.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
  }

  public String[] getParametersFromResponse(String response, String[] namesOfParameters) {
    JSONObject myResponse = new JSONObject(response.toString());
    String[] valuesOfParameters = new String[namesOfParameters.length];
    for (int i = 0; i < namesOfParameters.length; i++) {
      valuesOfParameters[i] = (myResponse.get(namesOfParameters[i])).toString();
    }
    return valuesOfParameters;
  }

  public String getPaymentUrl(String mdOrder) {
    //get payment URL
    return mainUrl + partOfPaymentUrl + "mdOrder=" + mdOrder;
  }

  public void confirmPayment(String codeFromSms) {
    //confirm payment
    wd.findElement(By.cssSelector("[name=password]")).click();
    wd.findElement(By.name("password")).sendKeys(codeFromSms);
    wd.findElement(By.cssSelector("[type=submit]" )).click();
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
  }

  private void type(By locator, String text) {
    wd.findElement(locator).click();
    wd.findElement(locator).clear();
    wd.findElement(locator).sendKeys(text);
  }

  public String getPaymentUrl(String[] valuesOfRegisterParameters) {
    //get payment URL
    String paymentUrl = getPaymentUrl(valuesOfRegisterParameters[0]);
    assertEquals(paymentUrl, valuesOfRegisterParameters[1]);
    return paymentUrl;
  }

  /*public String register() throws Exception {
    //get register sendRequest
    //String registerRequest = getRegisterRequestUrl(order);

    //get register response
    return sendRequest(registerRequest);
  }*/

  public String getOrderStatus(Order order) throws Exception{
    String getOrderStatusRequest = getOrderStatusRequestUrl(order);
    //return getOrderStatusRequest;
   return sendRequest(getOrderStatusRequest);
  }

  public String getRegisterRequestUrl(Order order) {
    //get register sendRequest
    return mainUrl + partOfRegisterUrl
            + "userName=" + order.getUserName() + "&password=" + order.getPassword()
            + "&orderId=&amount=" + order.getOrderAmount()
            + "&orderNumber=" + order.getOrderNumber()
            + "&returnUrl=" + order.getReturnUrl();
  }

  private String getOrderStatusRequestUrl(Order order) {
    //get register sendRequest
    return mainUrl + partOfOrderStatusUrl
            + "userName=" + order.getUserName() + "&password=" + order.getPassword()
            + "&orderId=" + order.getOrderId()
            + "&language=ru"
            + "merchantOrderNumber=" + order.getOrderNumber();
  }

  public void stop() {
    wd.quit();
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
}
