/*
 * $Id: AddressCoordinateImportHandler.java,v 1.3 2005/07/12 14:22:30 palli Exp $
 * Created on 3.2.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.business;

import java.rmi.RemoteException;
import java.util.List;
import com.idega.block.importer.business.ImportFileHandler;
import com.idega.block.importer.data.ImportFile;
import com.idega.business.IBOService;
import com.idega.user.data.Group;


/**
 * 
 *  Last modified: $Date: 2005/07/12 14:22:30 $ by $Author: palli $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.3 $
 */
public interface AddressCoordinateImportHandler extends IBOService, ImportFileHandler {

	/**
	 * @see com.idega.core.location.business.AddressCoordinateImportHandlerBean#handleRecords
	 */
	public boolean handleRecords() throws RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressCoordinateImportHandlerBean#setImportFile
	 */
	public void setImportFile(ImportFile file) throws RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressCoordinateImportHandlerBean#setRootGroup
	 */
	public void setRootGroup(Group rootGroup) throws RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressCoordinateImportHandlerBean#getFailedRecords
	 */
	public List getFailedRecords() throws RemoteException;
}