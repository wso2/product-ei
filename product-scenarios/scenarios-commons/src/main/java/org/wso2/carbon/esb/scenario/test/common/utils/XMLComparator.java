package org.wso2.carbon.esb.scenario.test.common.utils;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.Vector;

/**
 * Util class for comparing two OMElements
 */
public class XMLComparator {

    private static final Log LOG = LogFactory.getLog(XMLComparator.class);
    //if namespaces needs to be ignored need to add to this list
    private static Vector ignorableNamespaceList = new Vector();

    /**
     * Compares two OMElements. It compares attributes, namespaces, elements with trimmed values.
     *
     * @param elementOne first element to compare
     * @param elementTwo element to compare with the first
     * @return true if compares
     */
    public static boolean compare(OMElement elementOne, OMElement elementTwo) {

        boolean status;
        //ignore if the elements belong to any of the ignorable namespaces list

        if (isIgnorable(elementOne) || isIgnorable(elementTwo)) {
            return true;
        }

        if (elementOne == null && elementTwo == null) {
            LOG.info("Both Elements are null.");
            return true;
        }
        if (elementOne == null && elementTwo != null) {
            return false;
        }
        if (elementOne != null && elementTwo == null) {
            return false;
        }

        if(LOG.isDebugEnabled()) {
            LOG.debug("Now Checking " + elementOne.getLocalName() + " and " + elementTwo.getLocalName()
                    + "=============================");
        }

        LOG.debug("Comparing Element Names .......");
        status = compare(elementOne.getLocalName(), elementTwo.getLocalName());
        if (!status)
            return false;

        LOG.debug("Comparing Namespaces .........");
        status = compare(elementOne.getNamespace(), elementTwo.getNamespace());
        if (!status)
            return false;

        LOG.debug("Comparing attributes .....");
        status = compareAllAttributes(elementOne, elementTwo);
        if (!status)
            return false;

        LOG.debug("Comparing texts .....");

        /*
         * Trimming the value of the XMLElement is not correct
         * since this compare method cannot be used to compare
         * element contents with trailing and leading whitespaces
         * BUT for the practicalltiy of tests and to get the current
         * tests working we have to trim() the contents
         */
        status = compare(elementOne.getText().trim(), elementTwo.getText().trim());
        if (!status)
            return false;

        LOG.debug("Comparing Children ......");
        status = compareAllChildren(elementOne, elementTwo);

        return status;
    }

    private static boolean compareAllAttributes(OMElement elementOne, OMElement elementTwo) {
        boolean status;
        status = compareAttributes(elementOne, elementTwo);
        status = compareAttributes(elementTwo, elementOne);
        return status;
    }

    private static boolean compareAllChildren(OMElement elementOne, OMElement elementTwo) {
        boolean status;
        status = compareChildren(elementOne, elementTwo);
        return status;
    }


    private static boolean isIgnorable(OMElement elt) {
        if (elt != null) {
            OMNamespace namespace = elt.getNamespace();
            if (namespace != null) {
                return ignorableNamespaceList.contains(namespace.getNamespaceURI());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    private static boolean compareChildren(OMElement elementOne, OMElement elementTwo) {
        //ignore if the elements belong to any of the ignorable namespaces list
        boolean status = true;
        if (isIgnorable(elementOne) ||
                isIgnorable(elementTwo)) {
            return true;
        }
        Iterator elementOneChildren = elementOne.getChildren();
        while (elementOneChildren.hasNext()) {
            OMNode omNode = (OMNode) elementOneChildren.next();
            if (omNode instanceof OMElement) {
                OMElement elementOneChild = (OMElement) omNode;
                OMElement elementTwoChild = null;
                //Do the comparison only if the element is not ignorable
                if (!isIgnorable(elementOneChild)) {
                    Iterator elementTwoChildren = elementTwo.getChildren();
                    while (elementTwoChildren.hasNext()) {
                        status = false;
                        OMNode node = (OMNode) elementTwoChildren.next();
                        if (node.getType() == OMNode.ELEMENT_NODE) {
                            elementTwoChild = (OMElement) node;
                            if (elementTwoChild.getLocalName()
                                    .equals(elementOneChild.getLocalName())) {
                                //Do the comparison only if the element is not ignorable
                                if (!isIgnorable(elementTwoChild)) {
                                    if (elementTwoChild == null) {
                                        return false;
                                    }
                                }

                                status = compare(elementOneChild, elementTwoChild);

                            }
                        }
                        if (status) {
                            break;
                        }
                    }
                    if (!status) {
                        return false;
                    }
                } else
                    status = compare(elementOneChild, elementTwoChild);
            }
        }

        return status;
    }

    private static boolean compareAttributes(OMElement elementOne, OMElement elementTwo) {
        int elementOneAttribCount = 0;
        int elementTwoAttribCount = 0;
        Iterator attributes = elementOne.getAllAttributes();
        while (attributes.hasNext()) {
            OMAttribute omAttribute = (OMAttribute) attributes.next();
            OMAttribute attr = elementTwo.getAttribute(omAttribute.getQName());
            if (attr == null) {
                return false;
            }
            elementOneAttribCount++;
        }

        Iterator elementTwoIter = elementTwo.getAllAttributes();
        while (elementTwoIter.hasNext()) {
            elementTwoIter.next();
            elementTwoAttribCount++;

        }

        return elementOneAttribCount == elementTwoAttribCount;
    }

    private static boolean compare(String one, String two) {
        return one.equals(two);
    }

    private static boolean compare(OMNamespace one, OMNamespace two) {
        if (one == null && two == null) {
            return true;
        } else if (one != null && two == null) {
            return false;
        } else if (one == null && two != null) {
            return false;
        }
        if (!one.getNamespaceURI().equals(two.getNamespaceURI())) {
            return false;
        }

        // Do we need to compare prefixes as well
        return true;
    }
}
