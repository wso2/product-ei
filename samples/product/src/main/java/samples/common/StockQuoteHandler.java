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

package samples.common;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.xpath.AXIOMXPath;

import javax.xml.namespace.QName;
import java.util.Random;
import java.util.List;
import java.util.Iterator;

/**
 * A class that can create messages to, and parse replies from our sample StockQuote service
 */
public class StockQuoteHandler {

    private static final Random RANDOM = new Random();

    /**
     * Create a new custom quote request with a body as follows
     * <m0:CheckPriceRequest xmlns:m0="http://services.samples">
     *   <m0:Code>symbol</m0:Code>
     * </m0:CheckPriceRequest>
     * @param symbol the stock symbol
     * @return OMElement for SOAP body
     */
    public static OMElement createCustomQuoteRequest(String symbol) {
        OMFactory factory   = OMAbstractFactory.getOMFactory();
        OMNamespace ns      = factory.createOMNamespace(
            "http://services.samples", "m0");
        OMElement chkPrice  = factory.createOMElement("CheckPriceRequest", ns);
        OMElement code      = factory.createOMElement("Code", ns);
        chkPrice.addChild(code);
        code.setText(symbol);
        return chkPrice;
    }

    /**
     * Create a new quote request with a body as follows
     *  <m:GetQuote xmlns:m="http://services.samples">
     *      <m:request>
     *          <m:symbol>IBM</m:symbol>
     *      </m:request>
     *  </m:GetQuote>
     * @param symbol the stock symbol
     * @return OMElement for SOAP body
     */
    public static OMElement createStandardQuoteRequest(String symbol, int itrCount) {
        OMFactory factory   = OMAbstractFactory.getOMFactory();
        OMNamespace ns      = factory.createOMNamespace("http://services.samples", "m0");
        OMElement getQuote  = factory.createOMElement("getQuote", ns);
        for (int i =0; i<itrCount; i++) {
            OMElement request   = factory.createOMElement("request", ns);
            OMElement symb      = factory.createOMElement("symbol", ns);
            request.addChild(symb);
            getQuote.addChild(request);
            symb.setText(symbol);
        }
        return getQuote;
    }

    /**
     * Create a new full quote request with a body as follows
     *  <m:GetFullQuote xmlns:m="http://services.samples">
     *      <m:request>
     *          <m:symbol>IBM</m:symbol>
     *      </m:request>
     *  </m:GetFullQuote>
     * @param symbol the stock symbol
     * @return OMElement for SOAP body
     */
    public static OMElement createFullQuoteRequest(String symbol) {
        OMFactory factory   = OMAbstractFactory.getOMFactory();
        OMNamespace ns      = factory.createOMNamespace("http://services.samples", "m0");
        OMElement getQuote  = factory.createOMElement("getFullQuote", ns);
        OMElement request   = factory.createOMElement("request", ns);
        OMElement symb      = factory.createOMElement("symbol", ns);
        request.addChild(symb);
        getQuote.addChild(request);
        symb.setText(symbol);
        return getQuote;
    }

    /**
     * Create a new market activity request with a body as follows
     *  <m:getMarketActivity xmlns:m="http://services.samples">
     *      <m:request>
     *          <m:symbol>IBM</m:symbol>
     *          ...
     *          <m:symbol>MSFT</m:symbol>
     *      </m:request>
     *  </m:getMarketActivity>
     * @return OMElement for SOAP body
     */
    public static OMElement createMarketActivityRequest() {
        OMFactory factory   = OMAbstractFactory.getOMFactory();
        OMNamespace ns      = factory.createOMNamespace("http://services.samples", "m0");
        OMElement getQuote  = factory.createOMElement("getMarketActivity", ns);
        OMElement request   = factory.createOMElement("request", ns);

        OMElement symb = null;
        for (int i=0; i<100; i++) {
            symb = factory.createOMElement("symbols", ns);
            symb.setText(randomString(3));
            request.addChild(symb);
        }

        getQuote.addChild(request);
        return getQuote;
    }

    /**
     * Create a new order for a quantiry of a stock at a given price
     * <m:placeOrder xmlns:m="http://services.samples">
     *	  <m:order>
     *	      <m:price>3.141593E0</m:price>
     *	      <m:quantity>4</m:quantity>
     *	      <m:symbol>IBM</m:symbol>
     *    </m:order>
     * 	</m:placeOrder>
     *
     * @param purchPrice the purchase price
     * @param qty the quantiry
     * @param symbol the stock
     * @return an OMElement payload for the order
     */
    public static OMElement createPlaceOrderRequest(double purchPrice, int qty, String symbol) {
        OMFactory factory   = OMAbstractFactory.getOMFactory();
        OMNamespace ns      = factory.createOMNamespace("http://services.samples", "m0");
        OMElement placeOrder= factory.createOMElement("placeOrder", ns);
        OMElement order     = factory.createOMElement("order", ns);
        OMElement price     = factory.createOMElement("price", ns);
        OMElement quantity  = factory.createOMElement("quantity", ns);
        OMElement symb      = factory.createOMElement("symbol", ns);
        price.setText(Double.toString(purchPrice));
        quantity.setText(Integer.toString(qty));
        symb.setText(symbol);
        order.addChild(price);
        order.addChild(quantity);
        order.addChild(symb);
        placeOrder.addChild(order);        
        return placeOrder;
    }

    /**
     * Digests the standard StockQuote response and extracts the last trade price
     * @param result
     * @return
     * @throws javax.xml.stream.XMLStreamException
     *
     *  <ns:getQuoteResponse xmlns:ns="http://services.samples">
     *      <ns:return>
     *          <ns:change>-2.3238706829151026</ns:change>
     *          ...
     *          <ns:symbol>IBM</ns:symbol>
     *          <ns:volume>17949</ns:volume>
     *      </ns:return>
     *  </ns:getQuoteResponse>
     */
    public static String parseStandardQuoteResponse(OMElement result) throws Exception {

        AXIOMXPath xPath = new AXIOMXPath("//ns:last");
        xPath.addNamespace("ns","http://services.samples/xsd");
        OMElement last = (OMElement) xPath.selectSingleNode(result);
        if (last != null) {
            return last.getText();
        } else {
            throw new Exception("Unexpected response : " + result);
        }
    }

    /**
     * <ns:getFullQuoteResponse xmlns:ns="http://services.samples">
            <ns:return>
               <tradeHistory xmlns="http://services.samples">
                  <day>0</day>
                  <quote>
                     <change>-2.367492989603466</change>
                     <earnings>13.14956711287784</earnings>
                     <high>-155.58844623078153</high>
                     <last>157.47582716569198</last>
                     <lastTradeTimestamp>Mon Apr 16 23:29:58 LKT 2007</lastTradeTimestamp>
                     <low>-155.31924118819015</low>
                     <marketCap>6373750.467022192</marketCap>
                     <name>IBM Company</name>
                     <open>-154.84071720443495</open>
                     <peRatio>-17.353258031353164</peRatio>
                     <percentageChange>-1.3910235348298898</percentageChange>
                     <prevClose>170.1979104108393</prevClose>
                     <symbol>IBM</symbol>
                     <volume>8935</volume>
                  </quote>
               </tradeHistory>
               <tradeHistory xmlns="http://services.samples">
                  <day>1</day>
                  <quote>
                     <change>3.794122022240518</change>
                     <earnings>-8.656536789776045</earnings>
                     <high>176.77136802352928</high>
                     <last>170.28677783945102</last>
                     <lastTradeTimestamp>Mon Apr 16 23:29:58 LKT 2007</lastTradeTimestamp>
                     <low>-166.64126635049223</low>
                     <marketCap>-6112014.916847887</marketCap>
                     <name>IBM Company</name>
                     <open>-168.30884678174925</open>
                     <peRatio>-18.644628475049693</peRatio>
                     <percentageChange>-2.29678289479374</percentageChange>
                     <prevClose>-165.19288918603885</prevClose>
                     <symbol>IBM</symbol>
                     <volume>5825</volume>
                  </quote>
               </tradeHistory>
               ...
            </ns:return>
         </ns:getFullQuoteResponse>
     *
     * @param result
     * @return
     * @throws Exception
     */
    public static String parseFullQuoteResponse(OMElement result) throws Exception {

        AXIOMXPath xPath = new AXIOMXPath("//ns:last");
        xPath.addNamespace("ns","http://services.samples/xsd");
        List lastNodes = xPath.selectNodes(result);

        if (lastNodes == null) {
            throw new Exception("Unexpected response : " + result);
        }

        double total = 0;
        int count = 0;

        Iterator iter = lastNodes.iterator();
        while (iter.hasNext()) {
            OMElement last = (OMElement) iter.next();
            total += Double.parseDouble(last.getText());
            count++;
        }

        return Double.toString(total/count);
    }

    /**
     * <ns:getMarketActivityResponse xmlns:ns="http://services.samples">
            <ns:return>
               <quotes xmlns="http://services.samples">
                  <change>4.183958555301184</change>
                  <earnings>-8.585281368244686</earnings>
                  <high>-158.70528805517333</high>
                  <last>160.83784480071603</last>
                  <lastTradeTimestamp>Tue Apr 17 02:21:30 LKT 2007</lastTradeTimestamp>
                  <low>-157.4950051860593</low>
                  <marketCap>5.9907588733164035E7</marketCap>
                  <name>EHM Company</name>
                  <open>-160.18368223376558</open>
                  <peRatio>24.0926205053427</peRatio>
                  <percentageChange>-2.6141745708181374</percentageChange>
                  <prevClose>-160.04893483420904</prevClose>
                  <symbol>EHM</symbol>
                  <volume>6319</volume>
               </quotes>
               <quotes xmlns="http://services.samples">
                  ....
                  <volume>7613</volume>
               </quotes>
               ...
            </ns:return>
        <ns:getMarketActivityResponse>
     * @param result
     * @return the average last price for each stock symbol
     * @throws Exception
     */
    public static String parseMarketActivityResponse(OMElement result) throws Exception {

        AXIOMXPath xPath = new AXIOMXPath("//ns:last");
        xPath.addNamespace("ns","http://services.samples/xsd");
        List lastNodes = xPath.selectNodes(result);

        if (lastNodes == null) {
            throw new Exception("Unexpected response : " + result);
        }

        double total = 0;
        int count = 0;

        Iterator iter = lastNodes.iterator();
        while (iter.hasNext()) {
            OMElement last = (OMElement) iter.next();
            total += Double.parseDouble(last.getText());
            count++;
        }

        return Double.toString(total/count);
    }

    /**
     * Digests the custom quote response and extracts the last trade price
     * @param result
     * @return
     * @throws javax.xml.stream.XMLStreamException
     *
     *      <CheckPriceResponse xmlns="http://ws.invesbot.com/" >
     *          <Code>IBM</Code>
     *          <Price>82.90</Price>
     *      </CheckPriceResponse>
     */
    public static String parseCustomQuoteResponse(OMElement result) throws Exception {

        AXIOMXPath xPath = new AXIOMXPath("//ns:Price");
        xPath.addNamespace("ns","http://services.samples/xsd");
        OMElement price = (OMElement) xPath.selectSingleNode(result);        
        if (price != null) {
            return price.getText();
        } else {
            throw new Exception("Unexpected response : " + result);
        }
    }

    /**
     * Return a random String of letters
     * @param count number of letters
     * @return the random string
     */
    public static String randomString(int count) {
        int end = 'Z' + 1;
        int start = 'A';

        StringBuffer buffer = new StringBuffer();
        int gap = end - start;

        while (count-- != 0) {
            char ch;
            ch = (char) (RANDOM.nextInt(gap) + start);
            if (Character.isLetter(ch)) {
                buffer.append(ch);
            } else {
                count++;
            }
        }
        return buffer.toString();
    }

}
