package com.comp301.a08shopping;

import com.comp301.a08shopping.events.*;
import com.comp301.a08shopping.exceptions.OutOfStockException;
import com.comp301.a08shopping.exceptions.ProductNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreImpl implements Store {

  private String name;
  private List<StoreObserver> observer;
  private List<Product> prods;
  private Map<Product, Integer> products;
  private Map<Product, Double> sales;

  public StoreImpl(String name) {
    if (name == null) {
      throw new IllegalArgumentException();
    }
    this.name = name;
    observer = new ArrayList<StoreObserver>();
    products = new HashMap<Product, Integer>();
    sales = new HashMap<Product, Double>();
    prods = new ArrayList<Product>();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void addObserver(StoreObserver observer) {
    if (observer == null) {
      throw new IllegalArgumentException();
    }
    this.observer.add(observer);
  }

  @Override
  public void removeObserver(StoreObserver observer) {
    this.observer.remove(observer);
  }

  @Override
  public List<Product> getProducts() {
    return new ArrayList<>(products.keySet());
  }

  @Override
  public Product createProduct(String name, double basePrice, int inventory) {
    if (name == null || basePrice <= 0 || inventory < 0) {
      throw new IllegalArgumentException();
    }
    Product prod = new ProductImpl(name, basePrice);
    products.put(prod, inventory);
    sales.put(prod, 0.0);
    prods.add(prod);
    return prod;
  }

  @Override
  public ReceiptItem purchaseProduct(Product product) {
    if (product == null) {
      throw new IllegalArgumentException();
    }
    if (!products.containsKey(product)) {
      throw new ProductNotFoundException();
    }
    if (products.get(product) == 0) {
      throw new OutOfStockException();
    }
    products.put(product, products.get(product) - 1);

    if (products.get(product) == 0) {
      for (StoreObserver s : observer) {
        s.update(new OutOfStockEvent(product, this));
      }
    }
    for (StoreObserver s : observer) {
      s.update(new PurchaseEvent(product, this));
    }

    return new ReceiptItemImpl(product.getName(), getSalePrice(product), name);
  }

  @Override
  public void restockProduct(Product product, int numItems) {
    boolean wasOutOfStock;
    if (product == null || numItems < 0) {
      throw new IllegalArgumentException();
    }
    if (!products.containsKey(product)) {
      throw new ProductNotFoundException();
    }
    if (products.get(product) < 0) {
      throw new IllegalArgumentException();
    }

    if (products.get(product) == 0) {
      wasOutOfStock = true;
    } else {
      wasOutOfStock = false;
    }

    products.put(product, products.get(product) + numItems);

    if (wasOutOfStock) {
      for (StoreObserver s : observer) {
        s.update(new BackInStockEvent(product, this));
      }
    }
  }

  @Override
  public void startSale(Product product, double percentOff) {
    if (product == null) {
      throw new IllegalArgumentException();
    }
    if (!products.containsKey(product)) {
      throw new ProductNotFoundException();
    }
    if (percentOff < 0 || percentOff > 1) {
      throw new IllegalArgumentException();
    }
    sales.put(product, percentOff);
    for (StoreObserver s : observer) {
      s.update(new SaleStartEvent(product, this));
    }
  }

  @Override
  public void endSale(Product product) {
    if (product == null) {
      throw new IllegalArgumentException();
    }
    if (!products.containsKey(product)) {
      throw new ProductNotFoundException();
    }
    sales.put(product, null);
    for (StoreObserver s : observer) {
      s.update(new SaleEndEvent(product, this));
    }
  }

  @Override
  public int getProductInventory(Product product) {
    if (product == null) {
      throw new IllegalArgumentException();
    }
    if (!products.containsKey(product)) {
      throw new ProductNotFoundException();
    }
    return products.get(product);
  }

  @Override
  public boolean getIsInStock(Product product) {
    if (product == null) {
      throw new IllegalArgumentException();
    }
    if (!products.containsKey(product)) {
      throw new ProductNotFoundException();
    }
    if (products.get(product) == 0) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  public double getSalePrice(Product product) {
    if (product == null) {
      throw new IllegalArgumentException();
    }
    if (!products.containsKey(product)) {
      throw new ProductNotFoundException();
    }
    if (sales.get(product) == null) {
      return product.getBasePrice();
    } else {
      return product.getBasePrice() * (1 - sales.get(product));
    }
  }

  @Override
  public boolean getIsOnSale(Product product) {
    if (product == null) {
      throw new IllegalArgumentException();
    }
    if (!products.containsKey(product)) {
      throw new ProductNotFoundException();
    }
    if (product.getBasePrice() > getSalePrice(product)) {
      return true;
    } else {
      return false;
    }
  }
}
