<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
  ~
  ~ WSO2 Inc. licenses this file to you under the Apache License,
  ~ Version 2.0 (the "License"); you may not use this file except
  ~ in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied. See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cmc="cmCustomerQuote"
                exclude-result-prefixes="cmc" version="2.0">
    <xsl:output indent="yes" method="xml" omit-xml-declaration="yes" />
    <xsl:template match="/">
        <mktgmessage>
            <xsl:text>&#10;</xsl:text>
            <xsl:text disable-output-escaping="yes">&amp;#10;</xsl:text>
            <xsl:text>&#10;</xsl:text>
            <xsl:text disable-output-escaping="yes">&amp;#10;</xsl:text>
            <xsl:text>&#10;</xsl:text>
            <xsl:text disable-output-escaping="yes">&amp;#10;</xsl:text>
            <xsl:text>&#10;</xsl:text>
            <xsl:text disable-output-escaping="yes">&amp;#10;</xsl:text>
            <xsl:text>&#10;</xsl:text>
            <xsl:text disable-output-escaping="yes">&amp;#10;</xsl:text>
            <xsl:text>&#10;</xsl:text>
            <xsl:text disable-output-escaping="yes">&amp;#10;</xsl:text>
        </mktgmessage>
    </xsl:template>
</xsl:stylesheet>