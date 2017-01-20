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

package samples.mediators;

import org.apache.synapse.MessageContext;
import org.apache.synapse.Mediator;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;

public class DiscountQuoteMediator extends AbstractMediator {

    private static final Log log = LogFactory.getLog(DiscountQuoteMediator.class);

    private String discountFactor="10";

    private String bonusFor="10";

    private int bonusCount=0;

    public DiscountQuoteMediator(){}

    public boolean mediate(MessageContext mc) {

        String price= mc.getEnvelope().getBody().getFirstElement().getFirstElement().
                getFirstChildWithName(new QName("http://services.samples/xsd","last")).getText();

        //converting String properties into integers
        int discount=Integer.parseInt(discountFactor);
        int bonusNo=Integer.parseInt(bonusFor);
        double currentPrice=Double.parseDouble(price);

        //discounting factor is deducted from current price form every response
        Double lastPrice = new Double(currentPrice - currentPrice * discount / 100);

        //Special discount of 5% offers for the first responses as set in the bonusFor property
        if (bonusCount <= bonusNo) {
            lastPrice = new Double(lastPrice.doubleValue() - lastPrice.doubleValue() * 0.05);
            bonusCount++;
        }

        String discountedPrice = lastPrice.toString();

        mc.getEnvelope().getBody().getFirstElement().getFirstElement().getFirstChildWithName
                (new QName("http://services.samples/xsd","last")).setText(discountedPrice);

        System.out.println("Quote value discounted.");
        System.out.println("Original price: " + price);
        System.out.println("Discounted price: " + discountedPrice);

        return true;
    }

    public String getType() {
        return null;
    }

    public void setTraceState(int traceState) {
        this.traceState = traceState;
    }

    public int getTraceState() {
        return 0;
    }

    public void setDiscountFactor(String discount) {
        discountFactor=discount;
    }

    public String getDiscountFactor() {
        return discountFactor;
    }

    public void setBonusFor(String bonus){
        bonusFor=bonus;
    }

    public String getBonusFor(){
        return bonusFor;
    }
}

