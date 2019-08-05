package com.yushkova.banktest;

public class Card {
  private final String cardNumber;
  private final String extYear;
  private final String expMonth;
  private final String owner;
  private final String cvv2;
  private final String codeFromSms;

  public Card(String cardNumber, String extYear, String expMonth, String owner, String cvv2, String codeFromSms) {
    this.cardNumber = cardNumber;
    this.extYear = extYear;
    this.expMonth = expMonth;
    this.owner = owner;
    this.cvv2 = cvv2;
    this.codeFromSms = codeFromSms;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public String getExtYear() {
    return extYear;
  }

  public String getExpMonth() {
    return expMonth;
  }

  public String getOwner() {
    return owner;
  }

  public String getCvv2() {
    return cvv2;
  }

  public String getCodeFromSms() {
    return codeFromSms;
  }
}
