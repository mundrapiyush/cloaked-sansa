package org.biddingengine.datamodel;

import java.util.Comparator;

public class SimpleItemComparator implements Comparator<Bid>{

	@Override
	public int compare(Bid bid1, Bid bid2) {
		if(bid1.getBidPrice() > bid2.getBidPrice())
			return -1;
		else if(bid1.getBidPrice() < bid2.getBidPrice())
			return 1;
		else
			return 0;
	}
}
