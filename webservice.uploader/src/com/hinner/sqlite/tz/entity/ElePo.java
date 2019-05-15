package com.hinner.sqlite.tz.entity;

public class ElePo {
	// 接口字段
	private String CompID;
	private String aMLineID;
	private String ElePoID;
	private int Serial;
	private double LineLen;
	// 数据库扩展字段
	private double totalLen;// 累计档距

	public String getaMLineID() {
		return aMLineID;
	}

	public void setaMLineID(String aMLineID) {
		this.aMLineID = aMLineID;
	}

	public String getElePoID() {
		return ElePoID;
	}

	public void setElePoID(String elePoID) {
		ElePoID = elePoID;
	}

	public int getSerial() {
		return Serial;
	}

	public void setSerial(int serial) {
		Serial = serial;
	}

	public double getLineLen() {
		return LineLen;
	}

	public void setLineLen(double lineLen) {
		LineLen = lineLen;
	}

	public String getCompID() {
		return CompID;
	}

	public void setCompID(String compID) {
		CompID = compID;
	}

	public double getTotalLen() {
		return totalLen;
	}

	public void setTotalLen(double totalLen) {
		this.totalLen = totalLen;
	}

	@Override
	public String toString() {
		return "ElePo [CompID=" + CompID + ", aMLineID=" + aMLineID + ", ElePoID=" + ElePoID + ", Serial=" + Serial
				+ ", LineLen=" + LineLen + ", totalLen=" + totalLen + "]";
	}

}
