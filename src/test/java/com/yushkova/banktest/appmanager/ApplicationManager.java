package com.yushkova.banktest.appmanager;

import com.yushkova.banktest.models.Card;
import com.yushkova.banktest.models.Order;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlContains;
import static org.testng.AssertJUnit.assertEquals;

public class ApplicationManager {
  private final GetURLHelper getURLHelper = new GetURLHelper();
  protected ChromeDriver wd;
  protected WebDriverWait wait;


  public void init() {
    wd = new ChromeDriver();
    wait = new WebDriverWait(wd, 80);
  }

  public void openPaymentPage(String URL) {
    wd.get(URL);
    wait.until(titleIs("Альфа-Банк"));
  }

  public void payment(String paymentURL, Card card, String email, String phone, Order order) {
    openPaymentPage(paymentURL);
    wait.until(presenceOfElementLocated(By.id("pan_visible")));
    type(By.id("pan_visible"), card.getCardNumber());
    wd.findElement(By.id("month-button")).click();
    wait.until(presenceOfElementLocated(By.xpath("//li[text() = '" + card.getExpMonth() + "']")));
    wd.findElement(By.xpath("//li[text() = '" + card.getExpMonth() + "']")).click();
    wd.findElement(By.id("year-button")).click();
    wait.until(presenceOfElementLocated(By.xpath("//li[text() = '" + card.getExtYear() + "']")));
    wd.findElement(By.xpath("//li[text() = '" + card.getExtYear() + "']")).click();
    type(By.id("iTEXT"), card.getOwner());
    type(By.id("iCVC"), card.getCvv2());
    if(wd.findElement(By.id("email")).isDisplayed()) {
      type(By.id("email"), email);
      type(By.id("phoneInput"), phone);
    }
    wd.findElement(By.id("buttonPayment")).click();
  }

  private void setConfirmationCode(String code) {
    wait.until(titleIs("Payment confirmation"));
    type(By.name("password"), code);
    wd.findElement(By.cssSelector("[type=submit]")).click();
  }

  public void afterClickPaymentButton(Order order, Card card, boolean paymentShouldBePassed) {
    if (!paymentShouldBePassed) {
      if ((isElementPresent(wd, By.xpath("//*[text() = 'Срок действия карты указан неверно']"))) ||
              (isElementPresent(wd, By.xpath("//*[text() = 'Владелец карты указан неверно']")))) {
        return;
      }
    }
    if (card.getCodeFromSms() != null) {
      setConfirmationCode(card.getCodeFromSms());
    }
    if (!paymentShouldBePassed) {
      if (isElementPresent(
              wd, By.xpath("//*[text() = 'Введен неправильный или просроченный код. Для получения нового кода, пожалуйста, нажмите кнопку «Отправить код еще раз».']"))) {
        return;
      }
      return;
    }
  }

  public void waitReturnUrl(Order order) {
    wait.until(urlContains(order.getReturnUrl()));
  }

  private void type(By locator, String text) {
    wd.findElement(locator).click();
    wd.findElement(locator).clear();
    wd.findElement(locator).sendKeys(text);
  }

  boolean isElementPresent(WebDriver driver, By locator) {
    return driver.findElements(locator).size() > 0;
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

  public void assertOrder(Order order, String assertPaymentState, String[] namesOfOrderStatusParameters, String[] namesOfPaymentAmountInfo) throws Exception {
    String orderStatusUrlRequest = getURLHelper.getOrderStatusRequestUrl(order);
    String orderStatusResponse = sendRequest(orderStatusUrlRequest);
    String[] valuesOfOrderStatusParameters = getParametersFromResponse(orderStatusResponse, namesOfOrderStatusParameters);
    String[] valuesOfPaymentAmountInfo = getParametersFromResponse(valuesOfOrderStatusParameters[4], namesOfPaymentAmountInfo);
    assertOrderStatus(order, valuesOfOrderStatusParameters, valuesOfPaymentAmountInfo, assertPaymentState);
  }

  public void assertOrder(Order order, String assertPaymentState, int amountAfterRefund, String[] namesOfOrderStatusParameters, String[] namesOfPaymentAmountInfo) throws Exception {
    String orderStatusUrlRequest = getURLHelper.getOrderStatusRequestUrl(order);
    String orderStatusResponse = sendRequest(orderStatusUrlRequest);
    String[] valuesOfOrderStatusParameters = getParametersFromResponse(orderStatusResponse, namesOfOrderStatusParameters);
    String[] valuesOfPaymentAmountInfo = getParametersFromResponse(valuesOfOrderStatusParameters[4], namesOfPaymentAmountInfo);
    assertOrderStatus(order, valuesOfOrderStatusParameters, valuesOfPaymentAmountInfo, assertPaymentState, amountAfterRefund);
  }

  public void assertOrderStatus(Order order, String[] valuesOfOrderStatusParameters, String[] valuesOfPaymentAmountInfo, String assertPaymentState) {
    assertEquals("0", valuesOfOrderStatusParameters[0]);
    assertEquals("Успешно", valuesOfOrderStatusParameters[1]);
    assertEquals(String.valueOf(order.getOrderAmountInt()), valuesOfOrderStatusParameters[3]);
    assertEquals(assertPaymentState, valuesOfPaymentAmountInfo[0]);
    switch (assertPaymentState) {
      case  ("DEPOSITED"):
        assertEquals("2", valuesOfOrderStatusParameters[2]);
        assertEquals(String.valueOf(order.getOrderAmountInt()), valuesOfPaymentAmountInfo[1]);
        assertEquals(String.valueOf(order.getOrderAmountInt()), valuesOfPaymentAmountInfo[2]);
        assertEquals("0", valuesOfPaymentAmountInfo[3]);;
        break;
      case ("CREATED"):
        assertEquals("0", valuesOfOrderStatusParameters[2]);
        assertEquals("0", valuesOfPaymentAmountInfo[1]);
        assertEquals("0", valuesOfPaymentAmountInfo[2]);
        assertEquals("0", valuesOfPaymentAmountInfo[3]);;
        break;
      case ("DECLINED"):
        assertEquals("6", valuesOfOrderStatusParameters[2]);
        assertEquals("0", valuesOfPaymentAmountInfo[1]);
        assertEquals("0", valuesOfPaymentAmountInfo[2]);
        assertEquals("0", valuesOfPaymentAmountInfo[3]);;
        break;
      case ("REVERSED"):
        assertEquals("3", valuesOfOrderStatusParameters[2]);
        assertEquals(String.valueOf(order.getOrderAmountInt()), valuesOfPaymentAmountInfo[1]);
        assertEquals("0", valuesOfPaymentAmountInfo[2]);
        assertEquals("0", valuesOfPaymentAmountInfo[3]);;
        break;
      default:
        break;
    }
  }

  public void assertOrderStatus(Order order, String[] valuesOfOrderStatusParameters, String[] valuesOfPaymentAmountInfo, String assertPaymentState, int amountAfterRefund) {
    assertEquals("0", valuesOfOrderStatusParameters[0]);
    assertEquals("Успешно", valuesOfOrderStatusParameters[1]);
    assertEquals(String.valueOf(order.getOrderAmountInt()), valuesOfOrderStatusParameters[3]);
    assertEquals(assertPaymentState, valuesOfPaymentAmountInfo[0]);
    assertEquals("4", valuesOfOrderStatusParameters[2]);
    assertEquals(String.valueOf(order.getOrderAmountInt()), valuesOfPaymentAmountInfo[1]);
    assertEquals(String.valueOf(amountAfterRefund), valuesOfPaymentAmountInfo[2]);
    assertEquals(String.valueOf(order.getOrderAmountInt() - amountAfterRefund), valuesOfPaymentAmountInfo[3]);;
  }

  public void assertRequestStatus(Order order, String[] valuesOfParameters) {
    assertEquals("0", valuesOfParameters[0]);
    assertEquals("Успешно", valuesOfParameters[1]);
  }

  public void assertWrongRequest(Order order, String[] valuesOfParameters) {
    assertEquals("7", valuesOfParameters[0]);
    assertEquals("Сумма возврата превышает сумму списания", valuesOfParameters[1]);
  }

  public void stop() {
    if (wd != null) {
      wd.quit();
      wd = null;
    }
    else return;
  }

  public GetURLHelper getGetURLHelper() {
    return getURLHelper;
  }
}