package com.ablesky.asdeploy.service;

import com.ablesky.asdeploy.pojo.DeployLock;
import com.ablesky.asdeploy.pojo.DeployRecord;

public interface IDeployService {

	DeployLock checkCurrentLock();

	void saveOrUpdateDeployLock(DeployLock lock);

	void saveOrUpdateDeployRecord(DeployRecord record);

	void unlockDeploy();

}
