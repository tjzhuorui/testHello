package com.hinner.sqlite.tz.entity;

public class Dtu {
	// 接口字段
	private String dtuid;
	private String mLineID;// 线路编号
	private String elePoID;// 杆塔编号
	private int sLineID;// 相位，分别用1，2，3代表ABC三相
	// 扩展字段
	private ElePo elePo;// 安装杆塔

	public String getDtuid() {
		return dtuid;
	}

	public void setDtuid(String dtuid) {
		this.dtuid = dtuid;
	}

	public String getmLineID() {
		return mLineID;
	}

	public void setmLineID(String mLineID) {
		this.mLineID = mLineID;
	}

	public String getElePoID() {
		return elePoID;
	}

	public void setElePoID(String elePoID) {
		this.elePoID = elePoID;
	}

	public int getsLineID() {
		return sLineID;
	}

	public void setsLineID(int sLineID) {
		this.sLineID = sLineID;
	}

	public double getAbsulateDis() {
		if (elePo == null)
			return 0;
		return elePo.getTotalLen();
	}

	public ElePo getElePo() {
		return elePo;
	}

	public void setElePo(ElePo elePo) {
		this.elePo = elePo;
	}

	@Override
	public String toString() {
		return "Dtu [dtuid=" + dtuid + ", mLineID=" + mLineID + ", elePoID=" + elePoID + ", 相位sLineID=" + sLineID
				+ ", 安装杆塔信息elePo=" + elePo + "]";
	}

}
