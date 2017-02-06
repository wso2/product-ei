<%@ page import="org.apache.axiom.om.OMElement" %>
<%@ page import="javax.xml.namespace.QName" %>
<p>
        <%
        String customerId = "";
        String customerFirstName = "";
        String customerLastName = "";
        String amount = "";
        String region = "";

        OMElement requestElement = (OMElement) request.getAttribute("taskInput");
        String ns = "http://www.example.com/claims/schema";

        if (requestElement != null) {
            OMElement customerElement = requestElement.getFirstChildWithName(new QName(ns, "cust"));

            if (customerElement != null) {
                OMElement id = customerElement.getFirstChildWithName(new QName(ns, "id"));
                if (id != null) {
                    customerId = id.getText();
                }

                OMElement fName = customerElement.getFirstChildWithName(new QName(ns, "firstname"));
                if(fName !=null){
                    customerFirstName = fName.getText();
                }

                OMElement lName = customerElement.getFirstChildWithName(new QName(ns, "lastname"));
                if(lName !=null){
                    customerLastName = lName.getText();
                }
            }

            OMElement regionElement = requestElement.getFirstChildWithName(new QName(ns, "region"));

            if(regionElement !=null){
                region = regionElement.getText();
            }

            OMElement amountElement = requestElement.getFirstChildWithName(new QName(ns, "amount"));

            if(amountElement !=null){
                amount = amountElement.getText();
            }
        }
    %>

<table border="0">
    <tr>
        <td>Customer Id</td>
        <td><%=customerId%>
        </td>
    </tr>
    <tr>
        <td>First Name</td>
        <td><%=customerFirstName%>
        </td>
    </tr>
    <tr>
        <td>Last Name</td>
        <td><%=customerLastName%>
        </td>
    </tr>
    <tr>
        <td>Amount</td>
        <td><%=amount%>
        </td>
    </tr>
    <tr>
        <td>Region</td>
        <td><%=region%>
        </td>
    </tr>

</table>

</p>
