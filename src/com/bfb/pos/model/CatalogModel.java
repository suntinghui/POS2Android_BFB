package com.bfb.pos.model;

public class CatalogModel {
	private int catalogId;
	private String title;
	private int parentId;
	private String actionType;
	private String actionId;
	private int iconId;
	private String description;
	private boolean needReverse;
	private String transferCode; // 交易码
	private boolean isActive;
	
	public CatalogModel(){
		
	}

	public int getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(int catalogId) {
		this.catalogId = catalogId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
	public String getActionType(){
		return actionType;
	}

	public String getActionId() {
		return actionId;
	}

	public void setActionType(String actionType){
		this.actionType = actionType;
	}
	
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	
	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTransferCode() {
		if (null == transferCode || "".equals(transferCode))
			return null;
		else 
			return transferCode.replace(" ", "");
	}

	public void setTransferCode(String transferCode) {
		this.transferCode = transferCode;
	}
	
	public boolean isNeedReverse() {
		return needReverse;
	}

	public void setNeedReverse(boolean needReverse) {
		this.needReverse = needReverse;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
}
