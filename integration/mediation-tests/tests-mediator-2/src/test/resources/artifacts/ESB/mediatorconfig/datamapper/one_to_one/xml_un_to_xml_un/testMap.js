map_S_test_S_test = function(){
var outputtest={};

var count_i_sf_Id = 0;
outputtest =  {};
outputtest.axis2ns11_records_un_Separat0r_xsi_type_sf_sObject =  {};
outputtest.axis2ns11_records_un_Separat0r_xsi_type_sf_sObject.attr_xsi_type = inputtest.axis2ns11_records_un_Separat0r_xsi_type_sf_sObject.attr_xsi_type;
outputtest.axis2ns11_records_un_Separat0r_xsi_type_sf_sObject.sf_type = inputtest.axis2ns11_records_un_Separat0r_xsi_type_sf_sObject.sf_type;
outputtest.axis2ns11_records_un_Separat0r_xsi_type_sf_sObject.sf_Id =  [];
outputtest.axis2ns11_records_un_Separat0r_xsi_type_sf_sObject.sf_CreatedDate = inputtest.axis2ns11_records_un_Separat0r_xsi_type_sf_sObject.sf_CreatedDate;
outputtest.axis2ns11_records_un_Separat0r_xsi_type_sf_sObject.sf_Name = inputtest.axis2ns11_records_un_Separat0r_xsi_type_sf_sObject.sf_Name;

for(i_sf_Id in inputtest.axis2ns11_records_un_Separat0r_xsi_type_sf_sObject.sf_Id){
outputtest.axis2ns11_records_un_Separat0r_xsi_type_sf_sObject.sf_Id[count_i_sf_Id] = inputtest.axis2ns11_records_un_Separat0r_xsi_type_sf_sObject.sf_Id[i_sf_Id];

count_i_sf_Id++;
}
return outputtest;
};
