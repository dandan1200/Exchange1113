import java.util.*;

public class Market {

    private ArrayList<Order> buyBook = new ArrayList<Order>();
    private ArrayList<Order> sellBook = new ArrayList<Order>();
    private List<Trade> allTrades = new ArrayList<Trade>();

    public Market() {
    }

    private static ArrayList<Order> sortOptions(ArrayList<Order> orders,boolean rev){
        // This method performs a bubble sort sorting into the best order for the time-price matching algorithm.
        
        Order tempVar;
        if (rev == false) {
            for (int i = 0; i < orders.size()-1; i++) {
                for (int j = 0; j < orders.size()-i-1;j++){
                    if (orders.get(j).getPrice() < orders.get(j+1).getPrice()){
                        tempVar = orders.get(j+1);
                        orders.set(j+1,orders.get(j));
                        orders.set(j,tempVar);
                    } else if (orders.get(j).getPrice() == orders.get(j+1).getPrice()){
                        // if (orders.get(j).getTimeIndex() > orders.get(j+1).getTimeIndex()){
                        //     tempVar = orders.get(j+1);
                        //     orders.set(j+1,orders.get(j));
                        //     orders.set(j,tempVar);
                        // }
                    }
                }
            }
        } else {
            for (int i = 0; i < orders.size()-1; i++) {
                for (int j = 0; j < orders.size()-i-1;j++){
                    if (orders.get(j).getPrice() > orders.get(j+1).getPrice()){
                        tempVar = orders.get(j+1);
                        orders.set(j+1,orders.get(j));
                        orders.set(j,tempVar);
                    } else if (orders.get(j).getPrice() == orders.get(j+1).getPrice()){
                        // if (orders.get(j).getTimeIndex() > orders.get(j+1).getTimeIndex()){
                        //     tempVar = orders.get(j+1);
                        //     orders.set(j+1,orders.get(j));
                        //     orders.set(j,tempVar);
                        // }
                    }
                }
            }
        }
        return orders;

    }

    public List<Trade> placeSellOrder(Order order) {
        ArrayList<Trade> completedTrades = new ArrayList<Trade>();
        ArrayList<Order> potentialTrades = new ArrayList<Order>();
        int timeIndex = 0;
        if (order == null || order.isBuy() == true){
            return null;
        }
        //check trader

        if (order.getTrader().getProductsInInventory().contains(order.getProduct()) == false){
            return null;
        }
        if (order.getTrader().getAmountStored(order.getProduct()) < order.getAmount()){
            
            return null;
        }
        order.getTrader().exportProduct(order.getProduct(),order.getAmount());

        if (this.getBuyBook().size() == 0){
            sellBook.add(order);
            return completedTrades;
        } 

        //Find orders for same product
        for (Order buySearch:this.getBuyBook()){
            if (order.getProduct().equals(buySearch.getProduct())){
                buySearch.setTimeIndex(timeIndex);
                potentialTrades.add(buySearch);
            }
            timeIndex++;
        }

        //Sort potential trades list:
        potentialTrades = Market.sortOptions(potentialTrades, false);
        

        //Search through sorted orders for best order to trade
        for (Order x:potentialTrades){
            if (order.getPrice() <= x.getPrice()){
                if (order.getAmount() == x.getAmount()){

                    //New full swap trade, both orders closed.
                    Trade newTrade = new Trade(order.getProduct(),order.getAmount(),x.getPrice(),order,x);
                    completedTrades.add(newTrade);
                    allTrades.add(newTrade);

                    order.close();
                    x.close();

                    buyBook.remove(x);

                    return completedTrades;
                } else if (order.getAmount() < x.getAmount()){

                    Trade newTrade = new Trade(order.getProduct(),order.getAmount(),x.getPrice(),order,x);
                    completedTrades.add(newTrade);
                    allTrades.add(newTrade);

                    x.adjustAmount(-order.getAmount());
                    order.close();
                    return completedTrades;
                } else {

                    Trade newTrade = new Trade(order.getProduct(),x.getAmount(),x.getPrice(),order,x);
                    completedTrades.add(newTrade);
                    allTrades.add(newTrade);

                    order.adjustAmount(-x.getAmount());
                    x.close();
                    buyBook.remove(x);
                }
            }
        }
        sellBook.add(order);
        return completedTrades;


    }

    public List<Trade> placeBuyOrder(Order order) {
        ArrayList<Trade> completedTrades = new ArrayList<Trade>();
        ArrayList<Order> potentialTrades = new ArrayList<Order>();
        int timeIndex = 0;
        
        if (order == null || order.isBuy() == false){
            return null;
        }

        if (this.getSellBook().size() == 0){
            buyBook.add(order);
            return completedTrades;
        } 

        // if (order.getTrader().getBalance() < order.getAmount()*order.getPrice()){
        //     return null;
        // }
        
        
        //Find orders for same product
        for (Order sellSearch:this.getSellBook()){
            if (order.getProduct().equals(sellSearch.getProduct())){
                sellSearch.setTimeIndex(timeIndex);
                potentialTrades.add(sellSearch);
            }
            timeIndex++;
        }

        //Sort potential trades list:
        potentialTrades = Market.sortOptions(potentialTrades, true);

        

        //Search through sorted orders for best order to trade
        for (Order x:potentialTrades){
            if (order.getPrice() >= x.getPrice()){
                if (order.getAmount() == x.getAmount()){
                    //New full swap trade, both orders closed.
                    Trade newTrade = new Trade(order.getProduct(),order.getAmount(),x.getPrice(),x,order);
                    completedTrades.add(newTrade);
                    allTrades.add(newTrade);
                    
                    order.close();
                    x.close();
                    sellBook.remove(x);
                    return completedTrades;
                } else if (order.getAmount() < x.getAmount()){
                    Trade newTrade = new Trade(order.getProduct(),order.getAmount(),x.getPrice(),x,order);
                    completedTrades.add(newTrade);
                    allTrades.add(newTrade);

                    x.adjustAmount(-order.getAmount());
                    order.close();
                    return completedTrades;
                    
                } else {
                    Trade newTrade = new Trade(order.getProduct(),x.getAmount(),x.getPrice(),x,order);
                    completedTrades.add(newTrade);
                    allTrades.add(newTrade);

                    order.adjustAmount(-x.getAmount());
                    x.close();
                    sellBook.remove(x);
                    
                }
            }
        }
        buyBook.add(order);
        return completedTrades;

    }

    public boolean cancelBuyOrder(String order) {
        if (order == null) {
            return false;
        } 
        boolean found = false;

        for (Order x: this.getBuyBook()){
            if (x.getID().equals(order)){
                found = true;
                this.buyBook.remove(x);
                break;
            }
        }
        return found;
        
    }

    public boolean cancelSellOrder(String order) {
        if (order == null) {
            return false;
        } 
        boolean found = false;

        for (Order x: this.getSellBook()){
            if (x.getID().equals(order)){
                found = true;
                this.sellBook.remove(x);
                break;
            }
        }
        return found;
    }

    public List<Order> getSellBook() {
        return this.sellBook;
    }

    public List<Order> getBuyBook() {
        return this.buyBook;
    }

    public List<Trade> getTrades() {
        return this.allTrades;
    }

    public static List<Trade> filterTradesByTrader(List<Trade> trades, Trader trader) {
        List<Trade> filtered = new ArrayList<Trade>();
        
        if (trades == null || trader == null){
            return null;
        }
        
        for (Trade x: trades){
            if (trader.getID() == x.getSellOrder().getTrader().getID() || trader.getID() == x.getBuyOrder().getTrader().getID() ) {
                filtered.add(x);
            }
        }

        return filtered;
    }

    public static List<Trade> filterTradesByProduct(List<Trade> trades, String product) {
        List<Trade> filtered = new ArrayList<Trade>();
        
        if (trades == null || product == null){
            return null;
        }
        
        for (Trade x: trades){
            if (x.getProduct().equals(product)) {
                filtered.add(x);
            }
        }

        return filtered;
    }
}
