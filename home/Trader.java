import java.util.*;
import java.io.*;

public class Trader {
    
    private String traderId;
    private double traderBalance;
    private Map<String, Double> productAmountMap = new HashMap<String, Double>();

    private static List<Trader> allTraders = new ArrayList<Trader>();


    public Trader(String id, double balance) {
        if (id != null && balance >= 0){
            this.traderId = id;
            this.traderBalance = balance;
            allTraders.add(this);
        }      
    }

    public String getID() {
        return this.traderId;
    }

    public double getBalance() {
        return this.traderBalance;
    }

    public double importProduct(String product, double amount) {
        // check for null
        if (product == null || amount <= 0){
            return -1.0;
        }

        //add product to map
        if (this.productAmountMap.containsKey(product)){
            this.productAmountMap.put(product,this.productAmountMap.get(product)+ amount);
        } else {
            this.productAmountMap.put(product,amount);
        }
        
        return this.productAmountMap.get(product);
    }

    public double exportProduct(String product, double amount) {
        // check for null
        if (product == null || amount <= 0 || this.productAmountMap.containsKey(product) == false || amount > this.productAmountMap.get(product)){
            return -1.0;
        }
        //remove product amount from map
        this.productAmountMap.put(product,this.productAmountMap.get(product)-amount);
        if (this.productAmountMap.get(product) == 0) {
            this.productAmountMap.remove(product);
            return 0.0;
        }

        return this.productAmountMap.get(product);
    }

    public double getAmountStored(String product) {
        // check for null
        if (product == null){
            return -1.0;
        }
        if (this.productAmountMap.containsKey(product)){
            return this.productAmountMap.get(product);
        } else {
            return 0.0;
        }
    }

    public List<String> getProductsInInventory() {
        List<String> keyList = new ArrayList<String>();

        keyList.addAll(this.productAmountMap.keySet());

        Collections.sort(keyList);
        return keyList;
    }

    public double adjustBalance(double change) {
        this.traderBalance += change;
        return this.traderBalance;
    }

    public String toString() {
        String retString = this.getID()+ ": $" + String.format("%.2f",this.traderBalance) + " {";
        for (String product: this.getProductsInInventory()){
            retString = retString + product + ": " + String.format("%.2f",this.productAmountMap.get(product)) + ", "; 
        }
        if (this.getProductsInInventory().size() > 0){
            retString = retString.substring(0,retString.length()-2) + "}";
        } else {
            retString = retString + "}";
        }
         
        return retString;
    }

    public static void writeTraders(List<Trader> traders, String path) {
        if (traders == null || path == null){
            return;
        }
        try {
            File f = new File(path);
            PrintWriter write = new PrintWriter(f);

            for (Trader x:traders){
                write.println(x.toString());
            }
            write.close();
        } catch (FileNotFoundException e) {
            return;
        } catch (Exception e){
            return;
        }
    }

    public static void writeTradersBinary(List<Trader> traders, String path) {
        if (traders == null || path == null){
            return;
        }
        try {
            FileOutputStream f = new FileOutputStream(path);
            DataOutputStream write = new DataOutputStream(f);

            for (Trader x:traders){
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
