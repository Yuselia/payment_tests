package com.yushkova.banktest.appmanager;

import com.yushkova.banktest.models.Card;
import com.yushkova.banktest.models.Order;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class ApplicationManager {
  private final OrderAssertHelper orderAssertHelper = new OrderAssertHelper();
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

  public void stop() {
    if (wd != null) {
      wd.quit();
      wd = null;
    }
    else return;
  }

  public GetURLHelper getGetURLHelper() {
    return orderAssertHelper.getURLHelper;
  }

  public OrderAssertHelper getOrderAssertHelper() {
    return orderAssertHelper;
  }
}
