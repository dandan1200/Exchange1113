import java.util.*;
import java.io.*;

public class Trade {
    private String product;
    private double amount;
    private double price;
    private Order sellOrder;
    private Order buyOrder;

    private static List<Trade> allTrades = new ArrayList<Trade>();


    public Trade(String product, double amount, double price, Order sellOrder, Order buyOrder) {
        this.product = product;
        this.amount = amount;
        this.price = price;
        this.sellOrder = sellOrder;
        this.buyOrder = buyOrder;


        //make the trade
        //adjust balances
        this.sellOrder.getTrader().adjustBalance(this.price*this.amount);
        this.buyOrder.getTrader().adjustBalance(-this.price*this.amount);

        //import product
        this.buyOrder.getTrader().importProduct(this.product, this.amount);
        
        allTrades.add(this);
    }

    public String getProduct() {
        return this.product;
    }

    public double getAmount() {
        return this.amount;
    }

    public Order getSellOrder() {
        return this.sellOrder;
    }

    public Order getBuyOrder() {
        return this.buyOrder;
    }

    public double getPrice() {
        return this.price;
    }

    public String toString() {
        String retString = this.getSellOrder().getTrader().getID() + "->" + this.getBuyOrder().getTrader().getID() + ": " + String.format("%.2f",this.getAmount()) + "x" + this.getProduct() + " for $" + String.format("%.2f",this.getPrice()) + ".";
        return retString;
    }

    public boolean involvesTrader(Trader trader) {
        if (trader.getID().equals(this.getBuyOrder().getTrader().getID()) || trader.getID().equals(this.getSellOrder().getTrader().getID())) {
            return true;
        } else {
            return false;
        }
    }

    public static void writeTrades(List<Trade> trades, String path) {
        if (trades == null || path == null){
            return;
        }
        try {
            File f = new File(path);
            PrintWriter write = new PrintWriter(f);

            for (Trade x:trades){
                write.println(x.toString());
            }
            write.close();
        } catch (FileNotFoundException e) {
            return;
        } catch (Exception e){
            return;
        }
    }

    public static void writeTradesBinary(List<Trade> trades, String path) {
        if (trades == null || path == null){
            return;
        }
        try {
            FileOutputStream f = new FileOutputStream(path);
            DataOutputStream write = new DataOutputStream(f);

            for (Trade x:trades){
                write.writeUTF(x.toString());
                write.writeUTF("\u001f");
            }
            write.close();
        } catch (FileNotFoundException e) {
            return;
        } catch (Exception e){
            return;
        }
    }
}

