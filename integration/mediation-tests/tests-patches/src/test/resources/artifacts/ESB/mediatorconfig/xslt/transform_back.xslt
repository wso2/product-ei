<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
  ~  Copyright (c) 2005-2010, WSO2 Inc. (http://wso2.com) All Rights Reserved.
  ~
  ~  WSO2 Inc. licenses this file to you under the Apache License,
  ~  Version 2.0 (the "License"); you may not use this file except
  ~  in compliance with the License.
  ~  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~  Unless required by applicable law or agreed to in writing,
  ~  software distributed under the License is distributed on an
  ~  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~  KIND, either express or implied.  See the License for the
  ~  specific language governing permissions and limitations
  ~  under the License.
  ~
  -->
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
                xmlns:m0="http://services.samples"
                xmlns:ax21="http://services.samples/xsd"
                exclude-result-prefixes="m0 ax21 fn">
    <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>

    <xsl:template match="/">
        <xsl:apply-templates select="//m0:return"/>
    </xsl:template>

    <xsl:template match="m0:return">

        <m:CheckPriceResponse xmlns:m="http://services.samples/xsd">
            <m:Code>
                <xsl:value-of select="ax21:symbol"/>
            </m:Code>
            <m:Price>
                <xsl:value-of select="ax21:last"/>
            </m:Price>
        </m:CheckPriceResponse>

    </xsl:template>
</xsl:stylesheet>