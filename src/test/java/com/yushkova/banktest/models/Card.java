package com.yushkova.banktest.models;

public class Card {
  private String cardNumber;
  private String extYear;
  private String expMonth;
  private String owner;
  private String cvv2;
  private String codeFromSms;

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

  public Card withCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
    return this;
  }

  public Card withExtYear(String extYear) {
    this.extYear = extYear;
    return this;
  }

  public Card withExpMonth(String expMonth) {
    this.expMonth = expMonth;
    return this;
  }

  public Card withOwner(String owner) {
    this.owner = owner;
    return this;
  }

  public Card withCvv2(String cvv2) {
    this.cvv2 = cvv2;
    return this;
  }

  public Card withCodeFromSms(String codeFromSms) {
    this.codeFromSms = codeFromSms;
    return this;
  }
}


