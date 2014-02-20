package com.bfb.pos.util;

public enum TransferMethodEnum {

	TRANSFERRECORD("089000", "queryTransList"), REGISTE("089001", "registMerchant"), 
	GETPWD("089002", "getPassword"), MODIFIFYPWD("089003", "modifyPassword"),
	GETNOTICE("089004", "getNotice"), UPLOADSALESSLIP("089005", "uploadSalesSlip"),
	SMS("089006", "sms"), GETBANKS("089007", "getBanks"),
	ADDBANKS("089008", "addBanks"), MODIFYBANKS("089009", "modifyBanks"),
	COMPLETEMERCHANT("089010", "completeMerchant"), IDENTIFYMERCHANT("089020", "identifyMerchant");

	private final String transferCode;
	private final String methodName;

	private TransferMethodEnum(String transferCode, String methodName) {
		this.transferCode = transferCode;
		this.methodName = methodName;
	}

	public String getTransferCode() {
		return this.transferCode;
	}

	public String getMethodName() {
		return this.methodName;
	}

}
