<%@ page import="org.apache.axiom.om.OMElement" %>
<%@ page import="javax.xml.namespace.QName" %>
        <%
        String approved = "No Value Assigned";
		String savedDataApproved = "";
		String savedDataDisapproved = "";
        OMElement responseElement = (OMElement) request.getAttribute("taskOutput");

        if (responseElement != null) {
			if(responseElement.getFirstElement() !=null) {
		       approved = responseElement.getFirstElement().getText();
				if(approved.equals("true")){
					savedDataApproved = "checked=\"true\"";
				}else if(approved.equals("false")){
					savedDataDisapproved = "checked=\"true\"";
				}
			} 
        }
    	%>

<script type="text/javascript">
createTaskOutput = function() {checked="true"
    var outputVal = getCheckedRadio();
    if(outputVal == 'approve') {
		return '<sch:ClaimApprovalResponse xmlns:sch="http://www.example.com/claims/schema"><sch:approved>true</sch:approved></sch:ClaimApprovalResponse>';
	} else if (outputVal == 'disapprove') {
		return '<sch:ClaimApprovalResponse xmlns:sch="http://www.example.com/claims/schema"><sch:approved>false</sch:approved></sch:ClaimApprovalResponse>';
	}
};

getCheckedRadio = function () {
      var radioButtons = document.getElementsByName("responseRadio");
      for (var x = 0; x < radioButtons.length; x ++) {
        if (radioButtons[x].checked) {
          return radioButtons[x].value;
        }
      }
    };
</script>

<p>
<form>
<table border="0">
    <tr>
	<td>
		<input type="radio" name="responseRadio" id="responseRadio1" value="approve" <%=savedDataApproved%>/> Approve
		<input type="radio" name="responseRadio" id="responseRadio2" value="disapprove" <%=savedDataDisapproved%>/> Disapprove 
    </td>
    </tr>

</table>
</form>
</p>
