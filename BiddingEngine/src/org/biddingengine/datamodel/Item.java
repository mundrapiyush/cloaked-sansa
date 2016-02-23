package org.biddingengine.datamodel;

import java.util.TreeSet;
import java.util.UUID;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties({"bidList"})
public class Item {
	
	private final UUID itemID;
    private String name;    
    private String description;
    private double startPrice;
    private final long creationTime;
    private final String sellerUID;
    private String buyerID;
    private float soldPrice;
    private boolean isActive;
    private TreeSet<Bid> bidList;

    public Item(String name, String description, 
    			double startPrice, long creationTime,
    			String sellerUID, boolean isActive) {
        this.name = name;
        this.description = description;
        this.startPrice = startPrice;
        this.creationTime = creationTime;
        this.itemID = UUID.randomUUID();
        this.sellerUID = sellerUID;
        this.isActive = isActive;
        this.setBuyerID(null);
        this.setSoldPrice(-1.0f);
        this.setBidList(new TreeSet<Bid>(new SimpleItemComparator()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(double startPrice) {
        this.startPrice = startPrice;
    }
    
	public UUID getItemID() {
		return itemID;
	}
	
	public String getSellerUID() {
		return sellerUID;
	}
	
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public TreeSet<Bid> getBidList() {
		return bidList;
	}

	public void setBidList(TreeSet<Bid> bidList) {
		this.bidList = bidList;
	}
	
	public long getCreationTime() {
		return creationTime;
	}
	
	public String getBuyerID() {
		return buyerID;
	}

	public void setBuyerID(String buyerID) {
		this.buyerID = buyerID;
	}
	
	public float getSoldPrice() {
		return soldPrice;
	}

	public void setSoldPrice(float soldPrice) {
		this.soldPrice = soldPrice;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof Item){
			if(this.itemID == ((Item)obj).itemID)
				return true;
			else
				return false;
		}
		return false;
	}

	public int hashCode(){
		return this.itemID.hashCode();
	}
}
