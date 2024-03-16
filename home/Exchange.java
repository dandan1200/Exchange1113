import java.util.*;

public class Exchange {
    private Market mkt = new Market();
    private List<Trader> tradersInExch = new ArrayList<Trader>();
    private String orderIDIncrement = "0000";

    private void incrementOrderID(){
        int value = Integer.parseInt(orderIDIncrement, 16);
        value++;
        orderIDIncrement = Integer.toHexString(value);
        if (orderIDIncrement.length() == 1){
            orderIDIncrement = "000" + orderIDIncrement;
        } else if (orderIDIncrement.length() == 2){
            orderIDIncrement = "00" + orderIDIncrement;
        } else if (orderIDIncrement.length() == 3){
            orderIDIncrement = "0" + orderIDIncrement;
        }

    }

    public String addCmd(String[] cmd){
        
        //Check args
        if (cmd.length != 3){
            return "Error: incorrect arguments.";
        }
        
        
        String id = cmd[1];
        Boolean dupFlag = false;
        double balance;
        //Check balance    
        try {
            balance = Double.parseDouble(cmd[2]);
            if (balance < 0){
                return "Initial balance cannot be negative.";
            }
        } catch (Exception e){
            return "Error: invalid balance ";
        }
        //Check for duplicate ID
        for (Trader x: tradersInExch){
            if (x.getID().equals(id)){
                dupFlag = true;
            }
        }
            
        if (dupFlag == false) {
            tradersInExch.add(new Trader(id,balance));
            return "Success.";
        } else {
            return "Trader with given ID already exists.";
        }
         
    }

    public String balanceCmd(String[] cmd){
        //Check args
        if (cmd.length != 2){
            return "Error: incorrect arguments.";
        }

        String id = cmd[1];

        for (Trader x: tradersInExch){
            if (x.getID().equals(id)){
                return String.format("$%.2f",x.getBalance());
            }
        }
        return "No such trader in the market.";
    }

    public String inventoryCmd(String[] cmd) {
        //Check args
        if (cmd.length != 2){
            return "Error: incorrect arguments.\n";
        }
        String retString = "";
        String id = cmd[1];
        for (Trader x: tradersInExch){
            if (x.getID().equals(id)){
                if (x.getProductsInInventory().size() == 0) {
                    return "Trader has an empty inventory.\n";
                }
                for (String p: x.getProductsInInventory()){
                    retString = retString + p + "\n";
                }
                return retString;
            }
        }
        return "No such trader in the market.\n";
    }

    public String amountCmd(String[] cmd){
        //Check args
        if (cmd.length != 3) {
            return "Error: incorrect arguments.";
        }
        String id = cmd[1];
        String product = cmd[2];

        for (Trader x: tradersInExch){
            if (x.getID().equals(id)){
                if (x.getAmountStored(product) > 0) {
                    return String.format("%.2f",x.getAmountStored(product));
                } else {
                    return "Product not in inventory.";
                }
            }
        }
        return "No such trader in the market.";
    }

    public String sellCmd(String[] cmd){
        //Check args
        if (cmd.length != 5){
            return "Error: incorrect arguments.\n";
        }

        String id = cmd[1];
        String product = cmd[2];
        double amount = Double.parseDouble(cmd[3]);
        double price = Double.parseDouble(cmd[4]);

        for (Trader x: tradersInExch){
            if (x.getID().equals(id)){
                if (x.getAmountStored(product) < amount) {
                    return "Order could not be placed onto the market.\n";
                }

                Order o = new Order(product, false, amount, price, x,this.orderIDIncrement);
                List<Trade> tradesReturned = mkt.placeSellOrder(o);
                incrementOrderID();
                String retString = "";

                if (tradesReturned.size() == 0){
                    return "No trades could be made, order added to sell book.\n";
                } else {
                    if (o.isClosed()){
                        retString = "Product sold in entirety, trades as follows:\n";
                    } else {
                        retString = "Product sold in part, trades as follows:\n";
                    }

                    for (Trade y: tradesReturned){
                        retString = retString + y.toString() + "\n";
                    }
                    return retString;
                }
            }
        }
        return "No such trader in the market.\n";

    }

    public String buyCmd(String[] cmd) {
        //Check args
        if (cmd.length != 5){
            return "Error: incorrect arguments.\n";
        }

        String id = cmd[1];
        String product = cmd[2];
        double amount = Double.parseDouble(cmd[3]);
        double price = Double.parseDouble(cmd[4]);

        for (Trader x: tradersInExch){
            if (x.getID().equals(id)){
                // if (x.getBalance() < amount*price) {
                //     return "Order could not be placed onto the market.\n";
                // }

                Order o = new Order(product, true, amount, price, x,this.orderIDIncrement);
                List<Trade> tradesReturned = mkt.placeBuyOrder(o);
                incrementOrderID();
                String retString = "";

                if (tradesReturned.size() == 0){
                    return "No trades could be made, order added to buy book.\n";
                } else {
                    if (o.isClosed()){
                        retString = "Product bought in entirety, trades as follows:\n";
                    } else {
                        retString = "Product bought in part, trades as follows:\n";
                    }

                    for (Trade y: tradesReturned){
                        retString = retString + y.toString() + "\n";
                    }
                    return retString;
                }
            }
        }
        return "No such trader in the market.\n";


    }
    
    public String importCmd(String[] cmd){
        if (cmd.length != 4){
            return "Error: incorrect arguments.";
        }
        String id = cmd[1];
        String product = cmd[2];
        double amount = Double.parseDouble(cmd[3]);

        if (amount <= 0) {
            return "Could not import product into market.";
        }

        for (Trader t: tradersInExch){
            if (t.getID().equals(id)){
                double current = t.importProduct(product,amount);
                if (current == 0) {
                    return String.format("Trader now has no units of %s.", product);
                }
                return String.format("Trader now has %.2f units of %s.", current, product);
            }
        }
        return "No such trader in the market.";


    }
    
    public String exportCmd(String[] cmd){
        if (cmd.length != 4){
            return "Error: incorrect arguments.";
        }
        String id = cmd[1];
        String product = cmd[2];
        double amount = Double.parseDouble(cmd[3]);

        if (amount <= 0) {
            return "Could not export product out of market.";
        }

        for (Trader t: tradersInExch){
            if (t.getID().equals(id)){
                if (t.getAmountStored(product) >= amount){
                    double current = t.exportProduct(product,amount);
                    if (current == 0) {
                        return String.format("Trader now has no units of %s.", product);
                    }
                    return String.format("Trader now has %.2f units of %s.", current, product);
                } else {
                    return "Could not export product out of market.";
                }
            }
        }
        return "No such trader in the market.";
 
    }

    public String cancelSellCmd(String[] cmd){
        if (cmd.length != 3){
            return "Error: incorrect arguments.";
        }
        String order = cmd[2];

        boolean cancelled = mkt.cancelSellOrder(order);

        if (cancelled == true) {
            return "Order successfully cancelled.";
        } else {
            return "No such order in sell book.";
        }

    }

    public String cancelBuyCmd(String[] cmd){
        if (cmd.length != 3){
            return "Error: incorrect arguments.";
        }
        String order = cmd[2];

        boolean cancelled = mkt.cancelBuyOrder(order);

        if (cancelled == true) {
            return "Order successfully cancelled.";
        } else {
            return "No such order in buy book.";
        }

    }

    public String orderCmd(String[] cmd){
        if (cmd.length != 2){
            return "Error: incorrect arguments.";
        }

        String orderID = cmd[1];

        if (mkt.getBuyBook().size() == 0 && mkt.getSellBook().size() == 0){
            return "No orders in either book in the market.";
        }

        for (Order o: mkt.getBuyBook()){
            if (o.getID().equals(orderID)){
                return o.toString();
            }
        }

        for (Order o: mkt.getSellBook()){
            if (o.getID().equals(orderID)){
                return o.toString();
            }
        }

        return "Order is not present in either order book.";


    }

    public String tradersCmd(String[] cmd){
        if (cmd.length != 1){
            return "Error: incorrect arguments.\n";
        }
        
        if (tradersInExch.size() == 0){
            return "No traders in the market.\n";
        }

        String retString = "";
        
        ArrayList<String> traderIds = new ArrayList<String>();
        for (Trader x: tradersInExch){
            traderIds.add(x.getID());
        }
        Collections.sort(traderIds);
        for (String s: traderIds){
            retString = retString + s + "\n";
        }
        return retString;
    }

    public String tradesCmd(String[] cmd){

        String retString = "";

        if (mkt.getTrades().size() > 0){
            for (Trade t: mkt.getTrades()){
                retString = retString + t.toString() + "\n";
            }
            return retString;
        } else {
            return "No trades have been completed.\n";
        }
        
    }

    public String tradesTraderCmd(String[] cmd){
        if (cmd.length != 3){
            return "Error: incorrect arguments.\n";
        }

        String id = cmd[2];
        String retString = "";

        for (Trader t: tradersInExch){
            if (t.getID().equals(id)){
                for (Trade tr: Market.filterTradesByTrader(mkt.getTrades(), t)){
                    retString = retString + tr.toString() + "\n";
                }
                if (retString.equals("")){
                    return "No trades have been completed by trader.\n";
                } else{
                    return retString;
                }    
            }
        }
        return "No such trader in the market.\n";

    }

    public String tradesProductCmd(String[] cmd){
        if (cmd.length != 3){
            return "Error: incorrect arguments.\n";
        }
        String product = cmd[2];
        String retString = "";

        for (Trade tr: Market.filterTradesByProduct(mkt.getTrades(), product)){
            retString = retString + tr.toString() + "\n";
        }
        if (retString.equals("")){
            return "No trades have been completed with given product.\n";
        } else{
            return retString;
        }

    }

    public String bookSellCmd(String[] cmd){
        if (cmd.length != 2){
            return "Error: incorrect arguments.\n";
        }
        String retString = "";

        for (Order o:mkt.getSellBook()){
            retString = retString + o.toString() + "\n";
        }
        if (retString.equals("")){
            return "The sell book is empty.\n";
        } else {
            return retString;
        }
    }
    
    public String bookBuyCmd(String[] cmd){
        if (cmd.length != 2){
            return "Error: incorrect arguments.\n";
        }
        String retString = "";

        for (Order o:mkt.getBuyBook()){
            retString = retString + o.toString() + "\n";
        }
        if (retString.equals("")){
            return "The buy book is empty.\n";
        } else {
            return retString;
        }
    }

    public String saveCmd(String[] cmd){
        if (cmd.length != 3) {
            return "Error: incorrect arguments.";
        }

        String traderPath = cmd[1];
        String tradesPath = cmd[2];

        Trade.writeTrades(mkt.getTrades(), tradesPath);
        Trader.writeTraders(this.tradersInExch, traderPath);
        return "Success.";
    }

    public String binaryCmd(String[] cmd){
        if (cmd.length != 3) {
            return "Error: incorrect arguments.";
        }

        String traderPath = cmd[1];
        String tradesPath = cmd[2];

        Trade.writeTradesBinary(mkt.getTrades(), tradesPath);
        Trader.writeTradersBinary(this.tradersInExch, traderPath);
        return "Success.";
    }

    public void run() {
        Scanner keyboard = new Scanner(System.in);


        System.out.print("$ ");
        String[] cmdIn = keyboard.nextLine().split(" ");
        while (cmdIn[0].toUpperCase().equals("EXIT") == false){
            if (cmdIn.length >= 2){
                if (cmdIn[0].toUpperCase().equals("CANCEL") && cmdIn[1].toUpperCase().equals("SELL")){
                    System.out.println(cancelSellCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("CANCEL") && cmdIn[1].toUpperCase().equals("BUY")){
                    System.out.println(cancelBuyCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("TRADES") && cmdIn[1].toUpperCase().equals("TRADER")){
                    System.out.print(tradesTraderCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("TRADES") && cmdIn[1].toUpperCase().equals("PRODUCT")){
                    System.out.print(tradesProductCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("BOOK") && cmdIn[1].toUpperCase().equals("SELL")){
                    System.out.print(bookSellCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("BOOK") && cmdIn[1].toUpperCase().equals("BUY")){
                    System.out.print(bookBuyCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("BALANCE")){
                    System.out.println(balanceCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("INVENTORY")){
                    System.out.print(inventoryCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("AMOUNT")){
                    System.out.println(amountCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("SELL")){
                    System.out.print(sellCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("BUY")){
                    System.out.print(buyCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("IMPORT")){
                    System.out.println(importCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("EXPORT")){
                    System.out.println(exportCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("ORDER")){
                    System.out.println(orderCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("SAVE")){
                    System.out.println(saveCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("BINARY")){
                    System.out.println(binaryCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("ADD")){
                    System.out.println(addCmd(cmdIn));
                }
            } else {
                if (cmdIn[0].toUpperCase().equals("TRADERS")){
                System.out.print(tradersCmd(cmdIn));
                } else if (cmdIn[0].toUpperCase().equals("TRADES") && cmdIn.length == 1){
                System.out.print(tradesCmd(cmdIn));
                }
            }
            

            System.out.print("$ ");
            cmdIn = keyboard.nextLine().split(" ");
        }
        System.out.println("Have a nice day.");
        keyboard.close();
        
        return;
    }


    public static void main(String[] args) {
        Exchange exchange = new Exchange();
        exchange.run();
    }
}
