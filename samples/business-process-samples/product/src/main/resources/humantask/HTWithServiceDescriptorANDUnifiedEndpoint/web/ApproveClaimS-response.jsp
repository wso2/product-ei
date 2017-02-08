<%@ page import="org.apache.axiom.om.OMElement" %>
<%@ page import="javax.xml.namespace.QName" %>
<p>
        <%
        String approved = "No Value Assigned";

        OMElement responseElement = (OMElement) request.getAttribute("taskOutput");

        if (responseElement != null) {
           approved = responseElement.getFirstElement().getText();
        }
    	%>

<table border="0">
    <tr>
        <td>Approved :</td>
        <td><%=approved%>
        </td>
    </tr>
</table>
</p>
