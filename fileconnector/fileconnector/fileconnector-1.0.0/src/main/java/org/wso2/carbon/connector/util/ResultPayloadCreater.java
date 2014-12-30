/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.connector.util;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPBody;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.codehaus.jettison.json.JSONException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class ResultPayloadCreater {

	/**
	 * Prepare pay load
	 * 
	 * @param messageContext
	 * @param element
	 */
	public void preparePayload(MessageContext messageContext, OMElement element) {

		SOAPBody soapBody = messageContext.getEnvelope().getBody();
		soapBody.addChild(element);

	}

	/**
	 * Create a OMElement
	 * 
	 * @param output
	 * @return
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws JSONException
	 */
	public OMElement performSearchMessages(String output) throws XMLStreamException,

	IOException, JSONException {
		OMElement resultElement;
		if (!output.equals("")) {
			resultElement = AXIOMUtil.stringToOM(output);
		} else {
			resultElement = AXIOMUtil.stringToOM("<result></></result>");
		}

		return resultElement;

	}

	/**
	 * Send error status
	 * 
	 * @param ctxt
	 * @param e
	 */

	public static void sendErrorStatus(MessageContext ctxt, Exception e) {
		ctxt.setProperty(SynapseConstants.ERROR_EXCEPTION, e);
		ctxt.setProperty(SynapseConstants.ERROR_MESSAGE, e.getMessage());
	}
}
