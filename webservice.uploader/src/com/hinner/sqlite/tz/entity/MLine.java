package com.hinner.sqlite.tz.entity;

public class MLine {
	// 接口字段
	private String MLineID;
	private String MLName;
	private String Volevel;
	private String Station1;
	private String Station2;
	// 数据库扩展字段
	private double totalLen;// 线路全长

	public String getMLineID() {
		return MLineID;
	}

	public void setMLineID(String mLineID) {
		MLineID = mLineID;
	}

	public String getMLName() {
		return MLName;
	}

	public void setMLName(String mLName) {
		MLName = mLName;
	}

	public String getVolevel() {
		return Volevel;
	}

	public void setVolevel(String volevel) {
		Volevel = volevel;
	}

	public String getStation1() {
		return Station1;
	}

	public void setStation1(String station1) {
		Station1 = station1;
	}

	public String getStation2() {
		return Station2;
	}

	public void setStation2(String station2) {
		Station2 = station2;
	}

	public double getTotalLen() {
		return totalLen;
	}

	public void setTotalLen(double totalLen) {
		this.totalLen = totalLen;
	}

	@Override
	public String toString() {
		return "MLine [MLineID=" + MLineID + ", MLName=" + MLName + ", Volevel=" + Volevel + ", Station1=" + Station1
				+ ", Station2=" + Station2 + ", totalLen=" + totalLen + "]";
	}

}
