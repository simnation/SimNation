package org.simnation.agents.firm.common;


import org.simnation.agents.business.Demand;
import org.simnation.agents.business.Invoice;
import org.simnation.agents.business.Order;
import org.simnation.agents.business.Supply;
import org.simnation.agents.firm.manufacturer.Manufacturer;
import org.simnation.agents.market.GoodsMarketB2B;
import org.simnation.agents.market.MarketInfo;
import org.simnation.common.Batch;
import org.simnation.context.technology.Good;
import org.simnation.core.Message;



// handles orders (MtS products) and inquiries (MtO products) from the b2b market and assesses
// market info from the market.
// generates a production order either from stock level gap or from an order based on a specific
// inquiry.

public final class Sales {
	
	private final Manufacturer parent;
	
	public Sales(Manufacturer manufacturer) {
	    parent=manufacturer;
	}

	// for MtS products!
	public void offerSupplies() {
	    // offer all batches from stock at b2b market
	    for (Batch batch : parent.getState().getWarehouse().getOutputStorage().getStockOnHand()) {
	        batch.setPrice(findBestPrice(batch));
	        Supply<Good> supply=new Supply<Good>(parent.getAddress(),batch);
	        parent.sendMessage(new Message<Supply<Batch>>(parent.getAddress(),
	                GoodsMarketB2B.getStaticAddress(),supply));
	    }
	}

    public void handleOrder(Order<Batch> order) {
        // remove ware from stock
        Batch batch=parent.getState().getWarehouse().getOutputStorage().
            removeFromStock(order.getOfferedSupply().getBatch(),order.getOrderedAmount());
        // calc total price
        double price=calcAmountPayable(order);
        // calc due date
        double due=parent.getState().getCreditPeriod()+parent.getState().getTime();
        // generate invoice
        Invoice invoice=new Invoice(order,price,due);
        // send delivery, including batch and invoice
        Supply<Good> delivery=new Supply<Good>(batch,invoice);
        parent.sendMessage(new Message<Supply<Good>>(parent.getAddress(),order.getCustomerAddr(),delivery));
	}



    public void handleMarketInfo(MarketInfo info) {
	    
	}

	// for MtO products!!!
	public void handleInquiry(Demand<Good> content) {
	    // TODO Auto-generated method stub

	}
	
	   // generate a suggestion based on some kind of pricing strategy
    private float findBestPrice(Batch batch) {
        return 1.1f;
    }
    
    private double calcAmountPayable(Order<Batch> order) {
        double sum=order.getOfferedSupply().getMaxPrice()*order.getOrderedAmount();
        return(sum);
    }

}
