<x><![CDATA[
  declare namespace m0="http://services.samples";
  declare variable $payload as document-node() external;
  declare variable $commission as document-node() external;
  <m0:return xmlns:m0="http://services.samples" xmlns:m1="http://services.samples/xsd">
  	<m1:symbol>{$payload//m0:return/m1:symbol/child::text()}</m1:symbol>
  	<m1:last>{$payload//m0:return/m1:last/child::text()+ $commission//commission/vendor[@symbol=$payload//m0:return/m1:symbol/child::text()]}</m1:last>
  </m0:return>  
]]></x>