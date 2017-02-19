package org.wso2.ei.businessprocess.samples.bpmn.datatypes.xml;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.w3c.dom.NodeList;
import org.wso2.carbon.bpmn.core.types.datatypes.xml.api.XMLDocument;

public class XMLDemo implements JavaDelegate{

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.out.println("Executing Java Task START---------------------------------------------------------------------------");
		
		//Retrieve variables
		XMLDocument xmlDoc = (XMLDocument) execution.getVariable("xmlDoc");
		
		
		System.out.println("********************Book List START********************");
		NodeList numOfBooks = xmlDoc.getElementsByTagName("book");
		for (int i = 0; i < numOfBooks.getLength(); i++) {
			System.out.println("===================Book " + i + "===================");
			NodeList book = numOfBooks.item(i).getChildNodes();
			System.out.println("Book " +book.item(0).getNodeName() +" : " +book.item(0).getFirstChild().getNodeValue());
			System.out.println("Book " +book.item(1).getNodeName() +" : " +book.item(1).getFirstChild().getNodeValue());
			System.out.println("Book " +book.item(2).getNodeName() +" : " +book.item(2).getFirstChild().getNodeValue());
		}
		System.out.println("********************Book List END********************");
		
		System.out.println("Executing Java Task END---------------------------------------------------------------------------");
	}

}
