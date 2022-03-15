package com.comp301.a08shopping;

import com.comp301.a08shopping.events.*;

import java.util.ArrayList;
import java.util.List;

public class CustomerImpl implements Customer {

  private String name;
  private double budget;
  private List<ReceiptItem> history;

  public CustomerImpl(String name, double budget) {
    if (name == null || budget < 0) {
      throw new IllegalArgumentException();
    }
    this.name = name;
    this.budget = budget;
    history = new ArrayList<ReceiptItem>();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public double getBudget() {
    return budget;
  }

  @Override
  public void purchaseProduct(Product product, Store store) {
    if (product == null || store == null) {
      throw new IllegalArgumentException();
    }
    if (store.getSalePrice(product) > budget) {
      throw new IllegalStateException();
    }
    budget = budget - store.getSalePrice(product);
    history.add(store.purchaseProduct(product));
  }

  @Override
  public List<ReceiptItem> getPurchaseHistory() {
    return history;
  }

  @Override
  public void update(StoreEvent event) {
    if (event.getType() == 1) {
      System.out.println(
          event.getProduct().getName() + " is back in stock at " + event.getStore().getName());
    }
    if (event.getType() == 2) {
      System.out.println(
          event.getProduct().getName() + " is now out of stock at " + event.getStore().getName());
    }
    if (event.getType() == 3) {
      System.out.println(
          "Someone purchased "
              + event.getProduct().getName()
              + " at "
              + event.getStore().getName());
    }
    if (event.getType() == 4) {
      System.out.println(
          "The sale for "
              + event.getProduct().getName()
              + " at "
              + event.getStore().getName()
              + " has ended");
    }
    if (event.getType() == 5) {
      System.out.println(
          "New sale for "
              + event.getProduct().getName()
              + " at "
              + event.getStore().getName()
              + "!");
    }
  }
}
