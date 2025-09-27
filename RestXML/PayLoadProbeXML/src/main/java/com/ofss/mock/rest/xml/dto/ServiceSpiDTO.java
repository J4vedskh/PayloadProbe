package com.ofss.mock.rest.xml.dto;

import java.util.ArrayList;
import java.util.List;

public class ServiceSpiDTO {
	List<String> serviceSpiList;
	int serviceSpiCount;
	
	public int getServiceSpiCount() {
		return serviceSpiCount;
	}

	public void setServiceSpiCount(int serviceSpiCount) {
		this.serviceSpiCount = serviceSpiCount;
	}

	public List<String> getServiceSpiList() {
		return serviceSpiList;
	}

	public void setServiceSpiList(List<String> serviceSpiList) {
		this.serviceSpiList = serviceSpiList;
	}

	public ServiceSpiDTO(){
		serviceSpiList = new ArrayList<>();
	}
	
	public void addToList(String spiName) {
		serviceSpiList.add(spiName);
	}

}
