package com.bfb.pos.model;

import java.io.Serializable;

public class TransferDetailModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String account1;
	private String amount;
	private String card_branch_id;
	private String local_log;
	private String localdate;
	private String localtime;
	private String snd_log;
	private String snd_cycle;
	private String systransid;
	private String rspmsg;
	private String flag;

	public TransferDetailModel() {

	}

	public String getAccount1() {
		return account1;
	}

	public void setAccount1(String account1) {
		this.account1 = account1;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCard_branch_id() {
		return card_branch_id;
	}

	public void setCard_branch_id(String card_branch_id) {
		this.card_branch_id = card_branch_id;
	}

	public String getLocal_log() {
		return local_log;
	}

	public void setLocal_log(String local_log) {
		this.local_log = local_log;
	}

	public String getLocaldate() {
		return localdate;
	}

	public void setLocaldate(String localdate) {
		this.localdate = localdate;
	}

	public String getLocaltime() {
		return localtime;
	}

	public void setLocaltime(String localtime) {
		this.localtime = localtime;
	}

	public String getSnd_log() {
		return snd_log;
	}

	public void setSnd_log(String snd_log) {
		this.snd_log = snd_log;
	}

	public String getSnd_cycle() {
		return snd_cycle;
	}

	public void setSnd_cycle(String snd_cycle) {
		this.snd_cycle = snd_cycle;
	}

	public String getSystransid() {
		return systransid;
	}

	public void setSystransid(String systransid) {
		this.systransid = systransid;
	}

	public String getRspmsg() {
		return rspmsg;
	}

	public void setRspmsg(String rspmsg) {
		this.rspmsg = rspmsg;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
