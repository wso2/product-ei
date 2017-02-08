<%@ page import="org.apache.axiom.om.OMElement" %>
<%@ page import="javax.xml.namespace.QName" %>
<p>
        <%
        String customerFirstName = "";
        String customerLastName = "";

        OMElement requestElement = (OMElement) request.getAttribute("taskInput");
        String ns = "http://www.example.com/claims/schema";

        if (requestElement != null) {

            OMElement firstName = requestElement.getFirstChildWithName(new QName(ns, "firstname"));

            if(firstName !=null){
                customerFirstName = firstName.getText();
            }

            OMElement lastName = requestElement.getFirstChildWithName(new QName(ns, "lastname"));

            if(lastName !=null){
                customerLastName = lastName.getText();
            }
        }
    %>

<table border="0">
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

</table>

</p>
