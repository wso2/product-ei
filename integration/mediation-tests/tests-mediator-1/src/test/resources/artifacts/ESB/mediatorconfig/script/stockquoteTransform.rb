<!--
 ~  Licensed to the Apache Software Foundation (ASF) under one
 ~  or more contributor license agreements.  See the NOTICE file
 ~  distributed with this work for additional information
 ~  regarding copyright ownership.  The ASF licenses this file
 ~  to you under the Apache License, Version 2.0 (the
 ~  "License"); you may not use this file except in compliance
 ~  with the License.  You may obtain a copy of the License at
 ~
 ~   http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~  Unless required by applicable law or agreed to in writing,
 ~  software distributed under the License is distributed on an
 ~   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~  KIND, either express or implied.  See the License for the
 ~  specific language governing permissions and limitations
 ~  under the License.
 -->
<x><![CDATA[
require 'rexml/document'
include REXML

def transformRequest(mc)
  newRequest= Document.new '<m:getQuote xmlns:m="http://services.samples">'<<
            '<m:request><m:symbol></m:symbol></m:request></m:getQuote>'
  newRequest.root.elements[1].elements[1].text = mc.getPayloadXML().root.elements[1].get_text
  mc.setPayloadXML(newRequest)
end

def transformResponse(mc)
  newResponse = Document.new '<m:CheckPriceResponse xmlns:m="http://services.samples/xsd"><m:Code>' <<
    '</m:Code><m:Price></m:Price></m:CheckPriceResponse>'
  lastPrice=mc.getPayloadXML().root.elements[1].get_elements('*:last')
  code=mc.getPayloadXML().root.elements[1].get_elements('*:symbol') 

  newResponse.root.elements[1].text =code[0].get_text
  newResponse.root.elements[2].text =lastPrice[0].get_text 
  mc.setPayloadXML(newResponse)
end
]]></x>
