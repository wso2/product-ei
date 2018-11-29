map_S_company_S_company = function(){
var outputcompany={};
var CONCAT_10_0 = '';
var CONCAT_11_0 = '';
var CONCAT_12_0 = '';

CONCAT_10_0 = inputcompany.asiaoffice.address.no.toString().concat('',inputcompany.asiaoffice.address.city);
CONCAT_11_0 = inputcompany.asiaoffice.address.no.toString().concat('',inputcompany.asiaoffice.address.city);
CONCAT_12_0 = inputcompany.usoffice.address.no.toString().concat('',inputcompany.usoffice.address.city);
outputcompany =  {};
outputcompany.offices =  {};
outputcompany.offices.asiaoffice =  {};
outputcompany.offices.asiaoffice.fax = inputcompany.usoffice.fax;
outputcompany.offices.asiaoffice.phone = inputcompany.usoffice.phone;
outputcompany.offices.asiaoffice.address = inputcompany.name.toString().concat('',CONCAT_10_0);
outputcompany.offices.europeoffice =  {};
outputcompany.offices.europeoffice.fax = inputcompany.asiaoffice.fax;
outputcompany.offices.europeoffice.phone = inputcompany.asiaoffice.phone;
outputcompany.offices.europeoffice.address = inputcompany.name.toString().concat('',CONCAT_11_0);
outputcompany.offices.usoffice =  {};
outputcompany.offices.usoffice.phone = inputcompany.europeoffice.phone;
outputcompany.offices.usoffice.address = customFunction(inputcompany.name,CONCAT_12_0);
return outputcompany;
};

customFunction = function(in1,in2){ return (in1 + in2);};
