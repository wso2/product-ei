<x><![CDATA[
  declare namespace m0="http://services.samples";
  declare variable $payload as document-node() external;
  <m:getQuote xmlns:m="http://services.samples">
    <m:request>
      <m:symbol>{$payload//m0:CheckPriceRequest/m0:Code/child::text()}</m:symbol>
    </m:request>
  </m:getQuote>
]]></x>