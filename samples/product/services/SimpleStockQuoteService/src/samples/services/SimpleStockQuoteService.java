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
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleStockQuoteService {
    private final AtomicInteger orderCount = new AtomicInteger();

    // in-out
    public GetQuoteResponse getQuote(GetQuote request) throws Exception {

        if ("ERR".equals(request.getSymbol())) {
            throw new Exception("Invalid stock symbol : ERR");
        }
        System.out.println(new Date() + " " + this.getClass().getName() +
            " :: Generating quote for : " + request.getSymbol());
        return new GetQuoteResponse(request.getSymbol());
    }

    // for REST style invocation
    public GetQuoteResponse getSimpleQuote(String symbol) {
        System.out.println(new Date() + " " + this.getClass().getName() +
            " :: Generating quote for : " + symbol);
        return new GetQuoteResponse(symbol);
    }

    // in-out large response
    public GetFullQuoteResponse getFullQuote(GetFullQuote request) {
        System.out.println(new Date() + " " + this.getClass().getName() +
            " :: Full quote for : " + request.getSymbol());
        return new GetFullQuoteResponse(request.getSymbol());
    }

    // in-out large request and response
    public GetMarketActivityResponse getMarketActivity(GetMarketActivity request) {
        StringBuffer sb = new StringBuffer();
        String[] symbols = request.getSymbols();
        sb.append("[");
        for (int i=0; i<symbols.length; i++) {
            sb.append(symbols[i]);
            if (i < symbols.length-1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        System.out.println(new Date() + " " + this.getClass().getName() +
            " :: Generating Market activity report for : "  + sb.toString());
        return new GetMarketActivityResponse(request.getSymbols());
    }

    // in only
    public void placeOrder(PlaceOrder order) {
        System.out.println(new Date() + " " + this.getClass().getName() +
            "  :: Accepted order #" + orderCount.incrementAndGet() + " for : " +
            order.getQuantity() + " stocks of " + order.getSymbol() + " at $ " +
            order.getPrice());
    }
}
