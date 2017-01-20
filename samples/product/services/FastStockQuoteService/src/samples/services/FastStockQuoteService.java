/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package samples.services;
import java.util.Date;

import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

public class FastStockQuoteService {
	
		private final static OMElement response = createResponse();

		private static OMElement createResponse() {
			
			  OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace ns = fac.createOMNamespace("http://services.samples/xsd", "ns");
        OMElement getQuoteResponse = fac.createOMElement("getQuoteResponse ", ns);
        OMElement returnElt = fac.createOMElement("return", ns);
        
        OMElement change = fac.createOMElement("change", ns);        
        change.addChild(fac.createOMText(change, "-2.573165716892239"));
        returnElt.addChild(change);
        
        OMElement earnings = fac.createOMElement("earnings", ns);        
        earnings.addChild(fac.createOMText(earnings, "12.729598827258027"));
        returnElt.addChild(earnings);
        
        OMElement high = fac.createOMElement("high", ns);        
        high.addChild(fac.createOMText(high, "181.57938605633444"));
        returnElt.addChild(high);
        
        OMElement last = fac.createOMElement("last", ns);        
        last.addChild(fac.createOMText(last, "79.93167957835779"));
        returnElt.addChild(last);
        
        OMElement lastTradeTimestamp = fac.createOMElement("lastTradeTimestamp", ns);        
        lastTradeTimestamp.addChild(fac.createOMText(lastTradeTimestamp, "Thu Jan 25 17:39:12 IST 2007"));
        returnElt.addChild(lastTradeTimestamp);
        
        OMElement low = fac.createOMElement("low", ns);        
        low.addChild(fac.createOMText(low, "9.93167957835779"));
        returnElt.addChild(low);
        
        OMElement marketCap = fac.createOMElement("marketCap", ns);        
        marketCap.addChild(fac.createOMText(marketCap, "5.93167957835779"));
        returnElt.addChild(marketCap);
        
        OMElement name = fac.createOMElement("name", ns);        
        name.addChild(fac.createOMText(name, "IBM Company"));
        returnElt.addChild(name);
        
        OMElement open = fac.createOMElement("open", ns);        
        open.addChild(fac.createOMText(open, "15.93167957835779"));
        returnElt.addChild(open);
        
        OMElement peRatio = fac.createOMElement("peRatio", ns);        
        peRatio.addChild(fac.createOMText(peRatio, "24.283806785853777"));
        returnElt.addChild(peRatio);
        
        OMElement percentageChange = fac.createOMElement("percentageChange", ns);        
        percentageChange.addChild(fac.createOMText(percentageChange, "-2.334460572410184"));
        returnElt.addChild(percentageChange);
        
        OMElement prevClose = fac.createOMElement("prevClose", ns);        
        prevClose.addChild(fac.createOMText(prevClose, "-179.58650497565893"));
        returnElt.addChild(prevClose);
        
        OMElement symbol = fac.createOMElement("symbol", ns);        
        symbol.addChild(fac.createOMText(symbol, "IBM"));
        returnElt.addChild(symbol);
        
        OMElement volume = fac.createOMElement("volume", ns);        
        volume.addChild(fac.createOMText(volume, "7618"));
        returnElt.addChild(volume);
        
        getQuoteResponse.addChild(returnElt);
        return getQuoteResponse;
		}
		
    // in-out
    public OMElement getQuote(OMElement request) {
        //System.out.println(new Date() + " FastStockQuoteService :: Generating quote for : " + request.getSymbol());
        //return new GetQuoteResponse(request.getSymbol());
        return response;
    }

    // in only
    /*public void placeOrder(OMElement order) {
        System.out.println(new Date() + " FastStockQuoteService :: Accepted order for : " +
            order.getQuantity() + " stocks of " + order.getSymbol() +
            " at $ " + order.getPrice());
    }*/
}
