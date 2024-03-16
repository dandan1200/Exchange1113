import java.util.*;
import java.io.*;

public class Order {

    private String product;
    private boolean buy;
    private double amount;
    private double price;
    private Trader trader;
    private String orderId;
    private boolean closed;
    private int timeIndex;



    public Order(String product, boolean buy, double amount, double price, Trader trader, String id) {
        if (product != null && amount > 0 && price > 0 && trader != null){
            this.product = product;
            this.buy = buy;
            this.amount = amount;
            this.price = price;
            this.trader = trader;
            this.orderId = id;
            this.closed = false;
        }
    }

    public void setTimeIndex(int t){
        this.timeIndex = t;
    }

    // public Integer getTimeIndex(){
    //     return this.timeIndex;
    // }

    public String getProduct() {
        return this.product;
    }

    public boolean isBuy() {
        return this.buy;
    }

    public double getAmount() {
        return this.amount;
    }

    public Trader getTrader() {
        return this.trader;
    }

    public void close() {
        this.closed = true;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public double getPrice() {
        return this.price;
    }

    public String getID() {
        return this.orderId;
    }

    public void adjustAmount(double change) {
        if (this.amount + change > 0){
            this.amount = this.amount + change;
        }
    }

    public String toString() {
        String retString = this.getID() + ": ";
        if (this.isBuy()){
            retString = retString + "BUY ";
        } else {
            retString = retString + "SELL ";
        }

        retString = retString + String.format("%.2f",this.getAmount()) + "x" + this.getProduct() + " @ $" + String.format("%.2f",this.getPrice()); 

        return retString;
    }
    
}
