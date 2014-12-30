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
package org.wso2.carbon.connector;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.*;
import org.apache.synapse.MessageContext;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.util.FTPSiteUtils;
import org.wso2.carbon.connector.util.ResultPayloadCreater;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class FileRename extends AbstractConnector implements Connector {

	private static Log log = LogFactory.getLog(FileRename.class);

	public void connect(MessageContext messageContext) throws ConnectException {

		String fileLocation =
		                      getParameter(messageContext, "filelocation") == null ? "" : getParameter(
		                                                                                               messageContext,
		                                                                                               "filelocation").toString();
		String filename =
		                  getParameter(messageContext, "file") == null ? "" : getParameter(
		                                                                                   messageContext,
		                                                                                   "file").toString();
		String content =
		                 getParameter(messageContext, "content") == null ? "" : getParameter(
		                                                                                     messageContext,
		                                                                                     "content").toString();
		String newFileName =
		                     getParameter(messageContext, "newfilename") == null ? "" : getParameter(
		                                                                                             messageContext,
		                                                                                             "newfilename").toString();

		String filebeforepprocess =
		                            getParameter(messageContext, "filebeforeprocess") == null ? "" : getParameter(
		                                                                                                          messageContext,
		                                                                                                          "filebeforeprocess").toString();
		if (log.isDebugEnabled()) {
			log.debug("File creation started..." + filename);
			log.debug("File Location..." + fileLocation);
			log.debug("File content..." + content);
		}

		boolean resultStatus = false;
		try {
			resultStatus = renameFile(fileLocation, filename, newFileName, filebeforepprocess);
		} catch (FileSystemException e) {
			handleException(e.getMessage(), messageContext);
		}

		generateResult(messageContext, resultStatus);

	}

	/**
	 * Generate the output
	 * 
	 * @param messageContext Message Context
	 * @param resultStatus result status
	 */
	private void generateResult(MessageContext messageContext, boolean resultStatus) {
		ResultPayloadCreater resultPayload = new ResultPayloadCreater();

		String responce = "<result><success>" + resultStatus + "</success></result>";

		try {
			OMElement element = resultPayload.performSearchMessages(responce);
			resultPayload.preparePayload(messageContext, element);
		} catch (XMLStreamException e) {
			log.error("XML stream exception when generating results", e);
			handleException(e.getMessage(), messageContext);
		} catch (IOException e) {
			log.error("IO exception when generating results", e);
			handleException(e.getMessage(), messageContext);
		} catch (JSONException e) {
			log.error("JSON exception when generating result", e);
			handleException(e.getMessage(), messageContext);
		}

	}

	/**
	 * Rename the files
	 * 
	 * @param fileLocation File location
	 * @param filename File Name
	 * @param newFileName New File Name
	 * @param filebeforepprocess File Before Process
	 * @return Boolean
	 */
	private boolean renameFile(String fileLocation, String filename, String newFileName,
	                           String filebeforepprocess) throws FileSystemException {
		boolean resultStatus = false;
		FileSystemManager manager = VFS.getManager();
		if (manager != null) {
			// Create remote object
			FileObject remoteFile =
			                        manager.resolveFile(fileLocation +
			                                            filename,
			                                            FTPSiteUtils.createDefaultOptions());

			FileObject reNameFile =
			                        manager.resolveFile(fileLocation +
			                                            newFileName,
			                                            FTPSiteUtils.createDefaultOptions());
			if (remoteFile.exists()) {
				if (!filebeforepprocess.equals("")) {
					FileObject fBeforeProcess = manager.resolveFile(filebeforepprocess + filename);
					fBeforeProcess.copyFrom(remoteFile, Selectors.SELECT_SELF_AND_CHILDREN);
				}

				remoteFile.moveTo(reNameFile);
				resultStatus = true;
				if (log.isDebugEnabled()) {
					log.debug("Rename remote file success");
				}
			}
		}

		return resultStatus;
	}

}
