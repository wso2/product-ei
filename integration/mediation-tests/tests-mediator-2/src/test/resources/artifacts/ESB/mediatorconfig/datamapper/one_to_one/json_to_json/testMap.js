function map_S_company_S_company( ){
var outputcompany={};
outputcompany.offices ={};
outputcompany.offices.usoffice ={};
outputcompany.offices.usoffice.address = inputcompany.name.concat(inputcompany.usoffice.address.no,inputcompany.usoffice.address.city);
outputcompany.offices.usoffice.phone = inputcompany.usoffice.phone;
outputcompany.offices.usoffice.fax = inputcompany.usoffice.fax;

outputcompany.offices.europeoffice ={};
outputcompany.offices.europeoffice.address = inputcompany.name.concat(inputcompany.europeoffice.address.no,inputcompany.europeoffice.address.city);
outputcompany.offices.europeoffice.phone = inputcompany.europeoffice.phone;
outputcompany.offices.europeoffice.fax = inputcompany.europeoffice.fax;

outputcompany.offices.asiaoffice ={};
outputcompany.offices.asiaoffice.address = inputcompany.name.concat(inputcompany.asiaoffice.address.no,inputcompany.asiaoffice.address.city);
outputcompany.offices.asiaoffice.phone = inputcompany.asiaoffice.phone;
outputcompany.offices.asiaoffice.fax = inputcompany.asiaoffice.fax;
return outputcompany;
}
