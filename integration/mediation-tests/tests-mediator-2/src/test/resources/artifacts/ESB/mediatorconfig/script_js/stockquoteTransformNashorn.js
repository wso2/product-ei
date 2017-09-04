  /*
   *  Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
   *
   *  WSO2 Inc. licenses this file to you under the Apache License,
   *  Version 2.0 (the "License"); you may not use this file except
   *  in compliance with the License.
   *  You may obtain a copy of the License at
   *
   *    http://www.apache.org/licenses/LICENSE-2.0
   *
   *  Unless required by applicable law or agreed to in writing,
   *  software distributed under the License is distributed on an
   *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   *  KIND, either express or implied. See the License for the
   *  specific language governing permissions and limitations
   *  under the License.
   *
   */

  function transformRequest(mc) {
      var payload = mc.getPayloadXML();
      var expression = "//*[local-name()='Code']";
      var xpath = mc.getXpathResult(expression);
      var nodeList = xpath.selectNodes(payload);
      var symbol = nodeList.get(0).getText();
      mc.setPayloadXML(
          "<m:getQuote xmlns:m=\"http://services.samples\">"
            + "<m:request>"
                + "<m:symbol>"+ symbol +"</m:symbol>"
            + "</m:request>"
        + "</m:getQuote>");
  }

  function transformResponse(mc) {

      var payload = mc.getPayloadXML();

      var expression = "//*[local-name()='symbol']";
      var xpath = mc.getXpathResult(expression);
      var nodeList = xpath.selectNodes(payload);
      var symbol = nodeList.get(0).getText();

      expression = "//*[local-name()='last']";
      xpath = mc.getXpathResult(expression);
      nodeList = xpath.selectNodes(payload);
      var price = nodeList.get(0).getText();

      mc.setPayloadXML(
          "<m:CheckPriceResponse xmlns:m=\"http://services.samples/xsd\">"
        + "<m:Code>"+ symbol +"</m:Code>"
        + "<m:Price>"+ price +"</m:Price>"
        + "</m:CheckPriceResponse>");
  }

  