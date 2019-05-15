package com.hinner.ws.entity;

import java.util.ArrayList;
import java.util.List;

import com.hinner.sqlite.tz.entity.ElePo;
import com.hinner.sqlite.tz.entity.MLine;

import middle.db.operator.dao.entity.Wave;

/**
 * 上传的故障实体类
 * 
 * @author robin
 *
 */
public class Acci {
	// 上传字段
	private String fAcciID;// 故障编号
	private String fAcciTime;// 故障时间
	private String fMLineID;// 所属线路编号
	private String fElePoID;// 故障所属杆塔编号，距离故障发生最近的杆塔
	private String fAheadElePoID;// 故障所属区间的起始杆塔编号
	private String fEndElePoID;// 故障所属区间的种植杆塔编号
	private int fSLType;// 故障相位,分别123代表ABC三相
	private int fLight;// 是否雷击
	private int fLightPro;// 雷击性质
	private int fGate;// 是否跳闸1是，2否
	private String fNaofFault;// 故障性质
	private String fErrDistance;// 故障距离（基准）
	private String fBaseElePo;// 基准杆塔
	private double absuluteDis;// 绝对距离，距离开始点的距离
	// 上传字段 结束
	//
	private MLine line;// 故障发生线路信息，主要可能要使用线路全长
	private List<ElePo> elePoList;// 杆塔列表，用于最后查找故障杆塔，这里的杆塔是排过序的
	private List<Wave> waveList = new ArrayList<Wave>();// 故障波形

	public String getfAcciID() {
		return fAcciID;
	}

	public void setfAcciID(String fAcciID) {
		this.fAcciID = fAcciID;
	}

	public String getfAcciTime() {
		return fAcciTime;
	}

	public void setfAcciTime(String fAcciTime) {
		this.fAcciTime = fAcciTime;
	}

	public String getfMLineID() {
		return fMLineID;
	}

	public void setfMLineID(String fMLineID) {
		this.fMLineID = fMLineID;
	}

	public String getfElePoID() {
		return fElePoID;
	}

	public void setfElePoID(String fElePoID) {
		this.fElePoID = fElePoID;
	}

	public String getfAheadElePoID() {
		return fAheadElePoID;
	}

	public void setfAheadElePoID(String fAheadElePoID) {
		this.fAheadElePoID = fAheadElePoID;
	}

	public String getfEndElePoID() {
		return fEndElePoID;
	}

	public void setfEndElePoID(String fEndElePoID) {
		this.fEndElePoID = fEndElePoID;
	}

	public int getfSLType() {
		return fSLType;
	}

	public void setfSLType(int fSLType) {
		this.fSLType = fSLType;
	}

	public int getfLightPro() {
		return fLightPro;
	}

	public void setfLightPro(int fLightPro) {
		this.fLightPro = fLightPro;
	}

	public String getfNaofFault() {
		return fNaofFault;
	}

	public void setfNaofFault(String fNaofFault) {
		this.fNaofFault = fNaofFault;
	}

	public MLine getLine() {
		return line;
	}

	public void setLine(MLine line) {
		this.line = line;
	}

	public List<ElePo> getElePoList() {
		return elePoList;
	}

	public void setElePoList(List<ElePo> elePoList) {
		this.elePoList = elePoList;
	}

	public List<Wave> getWaveList() {
		return waveList;
	}

	public void addWave(Wave wave) {
		this.waveList.add(wave);
	}

	public int getfLight() {
		return fLight;
	}

	public void setfLight(int fLight) {
		this.fLight = fLight;
	}

	public int getfGate() {
		return fGate;
	}

	public void setfGate(int fGate) {
		this.fGate = fGate;
	}

	public String getfErrDistance() {
		return fErrDistance;
	}

	public void setfErrDistance(String fErrDistance) {
		this.fErrDistance = fErrDistance;
	}

	public String getfBaseElePo() {
		return fBaseElePo;
	}

	public void setfBaseElePo(String fBaseElePo) {
		this.fBaseElePo = fBaseElePo;
	}

	public void setWaveList(List<Wave> waveList) {
		this.waveList = waveList;
	}

	public double getAbsuluteDis() {
		return absuluteDis;
	}

	public void setAbsuluteDis(double absuluteDis) {
		this.absuluteDis = absuluteDis;
	}

}
