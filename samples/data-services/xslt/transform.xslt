<xsl:stylesheet version="2.0" xmlns="http://ws.wso2.org/dataservice/samples/excel_sample_service" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>
   <xsl:template match="m0:Products" xmlns:m0="http://ws.wso2.org/dataservice/samples/excel_sample_service">
       <Products>
          <xsl:for-each select="//m0:Products/m0:Product">
               <product>
		<xsl:for-each select="./*">                
                <xsl:variable name="element-name" select="local-name(.)"/>
                <xsl:element name="Product-{$element-name}">
                    <xsl:value-of select="."/>
                </xsl:element>                
                </xsl:for-each>               
               </product>               
          </xsl:for-each>
       </Products>
   </xsl:template>
</xsl:stylesheet>


