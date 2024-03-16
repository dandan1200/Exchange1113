import java.util.*;

public class MethodTesting {

    public static void TestTrader(){
        Trader t1 = new Trader("t1",100.0);
        System.out.println("ID: " + t1.getID());
        System.out.println("Balance: " + String.valueOf(t1.getBalance()));
        t1.importProduct("VAS", 20.0);
        System.out.println("Vas: " + t1.getAmountStored("VAS"));
        t1.importProduct("ABC", 10.5);
        System.out.println(t1.getProductsInInventory());
        System.out.println(t1.toString());
        t1.exportProduct("ABC", 10.5);
        System.out.println(t1.toString());
        List<Trader> traders = new ArrayList<Trader>();
        traders.add(t1);
        Trader.writeTraders(traders, "TradersListFile.txt");
        Trader.writeTraders(traders, "TradersListBin.bin");
    }

    public static void TestOrder() {
        Trader t1 = new Trader("t1",100.0);
        Order o1 = new Order("VAS",false,10.0,12.5,t1,"01");
        System.out.println(o1.toString());
        o1.adjustAmount(-2.5);
        System.out.println(o1.toString());
    }
    
    public static void TestMarket(){
        Trader t1 = new Trader("t1",100.0);
        Trader t2 = new Trader("t2",1000.0);
        t1.importProduct("VAS", 100);
        Order o1 = new Order("VAS",false,10.0,12.5,t1,"s1");
        // Order o2 = new Order("VAS",false,10.0,10.5,t1,"02");
        // Order o3 = new Order("VAS",false,10.0,20.5,t1,"03");
        // Order o4 = new Order("VAS",false,10.0,10.5,t1,"04");


        Order bo1 = new Order("VAS",true,2,14,t2,"1");
        Market mkt = new Market();
        System.out.println(mkt.placeSellOrder(o1));
        // System.out.println(mkt.placeSellOrder(o2).size());
        // System.out.println(mkt.placeSellOrder(o3).size());
        // System.out.println(mkt.placeSellOrder(o4).size());
        System.out.println(mkt.getSellBook());

        System.out.println(mkt.placeBuyOrder(bo1));
        System.out.println(mkt.getBuyBook());
        System.out.println(mkt.getSellBook());
        // System.out.println(mkt.cancelSellOrder("02"));
        // System.out.println(mkt.getSellBook());


    }
    public static void main(String[] args){
        TestMarket();
    }
}
