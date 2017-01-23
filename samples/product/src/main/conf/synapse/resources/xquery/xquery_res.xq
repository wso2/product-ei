<x><![CDATA[
  declare namespace m0="http://services.samples";
  declare variable $code as xs:string external;
  declare variable $price as xs:double external;
  <m:CheckPriceResponse xmlns:m="http://services.samples" xmlns:m1="http://services.samples/xsd">
  	<m1:Code>{$code}</m1:Code>
  	<m1:Price>{$price}</m1:Price>
  </m:CheckPriceResponse>
]]></x>