package com.yushkova.banktest.appmanager;

import com.yushkova.banktest.models.Order;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import static org.testng.AssertJUnit.assertEquals;

public class OrderAssertHelper {
  protected final GetURLHelper getURLHelper = new GetURLHelper();

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
    String orderStatusResponse = OrderAssertHelper.sendRequest(orderStatusUrlRequest);
    String[] valuesOfOrderStatusParameters = getParametersFromResponse(orderStatusResponse, namesOfOrderStatusParameters);
    String[] valuesOfPaymentAmountInfo = getParametersFromResponse(valuesOfOrderStatusParameters[4], namesOfPaymentAmountInfo);
    assertOrderStatus(order, valuesOfOrderStatusParameters, valuesOfPaymentAmountInfo, assertPaymentState);
  }

  public void assertOrder(Order order, String assertPaymentState, int amountAfterRefund, String[] namesOfOrderStatusParameters, String[] namesOfPaymentAmountInfo) throws Exception {
    String orderStatusUrlRequest = getURLHelper.getOrderStatusRequestUrl(order);
    String orderStatusResponse = OrderAssertHelper.sendRequest(orderStatusUrlRequest);
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
}
