package com.hinner.api;

import org.apache.log4j.Logger;

import com.hinner.common.util.DeployUtil;
import com.hinner.ws.api.Service;

/**
 * webservice客户端调用者。所有调用生成的webservice的jar，都从这里出去.同时提供测试方式，当配置为测试的时候，在此打印
 * 
 * @author robin
 *
 */
public class WSClient {
	public static Logger logger = Logger.getLogger(WSClient.class);

	// 测试模式
	static boolean isTestMode = false;// 是否是测试模式，模式为不是
	public static final String TEST_MLineID = "M0001";
	public static final String TEST_DTUID1 = "C0171234567812341";
	public static final String TEST_DTUID2 = "C0171234567812342";
	public static final String TEST_tElePoID1 = "1号杆塔";
	public static final String TEST_tElePoID2 = "2号杆塔";
	public static final String TEST_tElePoID3 = "3号杆塔";
	//

	static String aUserID;
	static String aPWD;
	static String aCompID;

	public static void init() {
		// 调用方式加载
		String wsTestLocal = DeployUtil.getProperties("ws.test.local", "false");
		if ("true".equals(wsTestLocal)) {
			isTestMode = true;
			logger.info("websercvice启动【测试模式】，所有的接口不会真正的打印，返回的数据也有可能是程序写死的，仅仅用于测试.");
		} else {
			isTestMode = false;
			logger.info("websercvice启动【接口模式】，会真正调用webservice接口.");
		}
		// 加载webservice是否使用代理
		String useProxy = DeployUtil.getProperties("ws.http.proxy.boot", "false");
		logger.info("是否使用http代理[" + useProxy + "]");
		if ("true".equalsIgnoreCase(useProxy)) {// 使用
			String proxyHost = DeployUtil.getProperties("ws.http.proxyHost", "lll");
			String proxyPort = DeployUtil.getProperties("ws.http.proxyPort", "111");
			logger.info("设置代理proxyHost=[" + proxyHost + "],proxyPort=[" + proxyPort + "]");
			// 设置
			System.getProperties().setProperty("http.proxyHost", proxyHost);
			System.getProperties().setProperty("http.proxyPort", proxyPort);
		}

		// 获取厂商配置
		aUserID = DeployUtil.getProperties("ws.aUserID", "ABC");
		aPWD = DeployUtil.getProperties("ws.aPWD", "123");
		aCompID = DeployUtil.getProperties("ws.aCompID", "CBA");
		logger.info("获取厂商配置.aUserID=[" + aUserID + "],aPWD=[" + aPWD + "],aCompID=[" + aCompID + "]");
	}

	/**
	 * 测试接口
	 * 
	 * @param service
	 * @return
	 */
	public static String getEcho(Service service) {
		logger.info("调用接口GetEcho.aUserID=[" + aUserID + "],aPWD=[" + aPWD + "]");
		if (isTestMode) {
			return "测试模式:true";
		}
		String aMsg = "";
		String ret = service.getWCFBindingServiceInterface().getEcho(aUserID, aPWD, aMsg);
		logger.info("调用GetEcho,返回结果[" + ret + "],aMsg[" + aMsg + "]");
		return ret;
	}

	/**
	 * 获取表的台账更新时间
	 * 
	 * @param aTableID
	 * @return
	 */
	public static String getTableUpdateTime(Service service, int aTableID) {
		logger.info("调用接口GetTableUpdateTime.aUserID=[" + aUserID + "], aPWD=[" + aPWD + "], aCompID=[" + aCompID
				+ "], aTableID=[" + aTableID + "]");
		if (isTestMode) {
			return "测试模式:true";
		}
		String ret = service.getWCFBindingServiceInterface().getTableUpdateTime(aUserID, aPWD, aCompID, aTableID);
		logger.info("调用GetTableUpdateTime,返回结果[" + ret + "]");
		return ret;
	}

	/**
	 * 台账，获取线路信息
	 * 
	 * @param service
	 * @param linename
	 * @return
	 */
	public static String getMLineList(Service service, String aMLName) {
		logger.info("调用接口GetMLineList.aUserID=[" + aUserID + "], aPWD=[" + aPWD + "], aCompID=[" + aCompID
				+ "],aMLName=[" + aMLName + "]");
		if (isTestMode) {
			logger.info("使用测试模式.程序将模拟出台账信息");
			// 模拟台账
			String omniret = "";
			// 标题栏
			omniret += "MLineID\tMLName\tVolevel\tStation1\tStation2\n";
			omniret += TEST_MLineID + "\t模拟测试线路一\t110kV\t龙门架起始站\t龙门架终点站\n";
			return omniret;
		}

		String ret = service.getWCFBindingServiceInterface().getMLineList(aUserID, aPWD, aMLName);
		logger.info("调用GetMLineList返回.ret=[" + ret + "]");
		return ret;
	}

	/**
	 * 台账，获取线路ID下的杆塔信息列表
	 * 
	 * @param service
	 * @param mLineID
	 * @return
	 */
	public static String getElePoList(Service service, String aMLineID) {
		logger.info("调用接口GetElePoList.aUserID=[" + aUserID + "], aPWD=[" + aPWD + "], aMLineID=[" + aMLineID + "]");
		if (isTestMode) {
			logger.info("使用测试模式，程序将模拟杆塔信息列表");
			String moni = "aMLineID\tElePoID\tSerial\tLineLen\n";
			moni += TEST_MLineID + "\t" + TEST_tElePoID3 + "\t3\t30\n";
			moni += TEST_MLineID + "\t" + TEST_tElePoID1 + "\t1\t10\n";
			moni += TEST_MLineID + "\t" + TEST_tElePoID2 + "\t2\t20\n";
			return moni;
		}
		String ret = service.getWCFBindingServiceInterface().getElePoList(aUserID, aPWD, aMLineID);
		logger.info("调用GetElePoList返回.ret=[" + ret + "]");
		return ret;
	}

	/**
	 * 获取装置的安装台账，安装在哪个杆塔，线路的什么相别上
	 * 
	 * @param service
	 * @param mLineID
	 * @return
	 */
	public static String getDTUList(Service service, String aMLineID) {
		logger.info("调用接口GetDTUList.aUserID=[" + aUserID + "], aPWD=[" + aPWD + "], aCompID=[" + aCompID
				+ "], aMLineID=[" + aMLineID + "]");
		if (isTestMode) {
			logger.info("测试模式.程序将模拟设备台账信息");
			String moni = "DTUID\tMLineID\tElePoID\tSLineID\n";
			moni += TEST_DTUID1 + "\t" + TEST_MLineID + "\t" + TEST_tElePoID1 + "\t1\n";
			moni += TEST_DTUID2 + "\t" + TEST_MLineID + "\t" + TEST_tElePoID3 + "\t1\n";
			return moni;
		}
		String ret = service.getWCFBindingServiceInterface().getDTUList(aUserID, aPWD, aCompID, aMLineID);
		logger.info("调用GetDTUList.ret=[" + ret + "]");
		return ret;
	}

	/**
	 * 上传心跳数据
	 * 
	 * @param service
	 * @param fDTUID
	 * @param fWorkTime
	 * @param fResultStr
	 * @return
	 */
	public static boolean addHeartBeat(Service service, String fDTUID, String fWorkTime, String fResultStr) {
		Integer fFrameType = 0x05;
		Integer fMessType = 0x01;
		logger.info("调用接口AddHeartBeat.aUserID=[" + aUserID + "], aPWD=[" + aPWD + "], aCompID=[" + aCompID
				+ "], fDTUID=[" + fDTUID + "], fFrameType=[" + fFrameType + "],fMessType=[" + fMessType
				+ "], fWorkTime=[" + fWorkTime + "], fResultStr=[" + fResultStr + "]");
		if (isTestMode) {
			logger.info("测试模式");
			return true;
		}
		boolean ret = service.getWCFBindingServiceInterface().addHeartBeat(aUserID, aPWD, aCompID, fDTUID, fFrameType,
				fMessType, fWorkTime, fResultStr);
		logger.info("调用接口AddHeartBeat.ret=[" + ret + "],fResultStr=[" + fResultStr + "]");
		return ret;
	}

	/**
	 * 上传工况数据
	 * 
	 * @param service
	 * @param fDTUID
	 * @param fWorkTime
	 * @param fBatteryState
	 * @param fBatteryV
	 * @param fDevTemperature
	 * @param fCurrentValue
	 * @param fResultStr
	 * @return
	 */
	public static boolean addWorkLog(Service service, String fDTUID, String fWorkTime, int fBatteryState,
			double fBatteryV, double fDevTemperature, double fCurrentValue, String fResultStr) {
		Integer fFrameType = 0x05;
		Integer fMessType = 0x05;
		logger.info("调用接口AddWorkLog.aUserID=[" + aUserID + "], aPWD=[" + aPWD + "], aCompID=[" + aCompID + "], fDTUID=["
				+ fDTUID + "], fFrameType=[" + fFrameType + "],fMessType=[" + fMessType + "], fWorkTime=[" + fWorkTime
				+ "], fBatteryState=[" + fBatteryState + "], fBatteryV=[" + fBatteryV + "], fDevTemperature=["
				+ fDevTemperature + "], fCurrentValue=[" + fCurrentValue + "], fResultStr=[" + fResultStr + "]");
		if (isTestMode) {
			logger.info("调用测试模式");
			return true;
		}
		boolean ret = service.getWCFBindingServiceInterface().addWorkLog(aUserID, aPWD, aCompID, fDTUID, fFrameType,
				fMessType, fWorkTime, fBatteryState, fBatteryV, fDevTemperature, fCurrentValue, fResultStr);
		logger.info("调用AddWorkLog返回.ret=[" + ret + "],fResultStr=[" + fResultStr + "]");
		return ret;
	}

	/**
	 * 提交基本信息报文
	 * 
	 * @param service
	 * @param fDTUID
	 * @param fDevName
	 *            装置名称
	 * @param fDevModel
	 *            装置型号
	 * @param fDevVersion
	 *            装置基本信息版本号
	 * @param fDevCompanyName
	 *            生产厂家
	 * @param fProcudeDate
	 *            生产日期
	 * @param fProcudeSerial
	 *            出场编号
	 * @param fResultStr
	 * @return
	 */
	public static boolean submitDTUCore(Service service, String fDTUID, String fDevName, String fDevModel,
			String fDevVersion, String fDevCompanyName, String fProcudeDate, String fProcudeSerial, String fResultStr) {
		logger.info("调用接口SubmitDTUCore.aUserID=[" + aUserID + "], aPWD=[" + aPWD + "], aCompID=[" + aCompID
				+ "], fDTUID=[" + fDTUID + "], fDevName=[" + fDevName + "],fDevModel=[" + fDevModel + "], fDevVersion=["
				+ fDevVersion + "], fDevCompanyName=[" + fDevCompanyName + "], fProcudeDate=[" + fProcudeDate
				+ "], fProcudeSerial=[" + fProcudeSerial + "], fResultStr=[" + fResultStr + "]");
		if (isTestMode) {
			logger.info("调用测试模式");
			return true;
		}
		boolean ret = service.getWCFBindingServiceInterface().submitDTUCore(aUserID, aPWD, aCompID, fDTUID, fDevName,
				fDevModel, fDevVersion, fDevCompanyName, fProcudeDate, fProcudeSerial, fResultStr);
		logger.info("调用SubmitDTUCore返回.ret=[" + ret + "],fResultStr=[" + fResultStr + "]");
		return ret;
	}

	/**
	 * 
	 * @param service
	 * @param fDTUID
	 * @param fWaveBatchID
	 * @param fAheadTime
	 * @param fWaveData
	 * @param fDataLen
	 * @param fSampRate
	 * @param fResultStr
	 * @return
	 */
	public static boolean commitDTUWave_XB(Service service, String fDTUID, String fWaveBatchID, String fAheadTime,
			String fWaveData, int fDataLen, int fSampRate, String fResultStr) {
		int fFrameType = 0x01;//
		int fMessType = 0x01;//
		logger.info("调用接口提交行波波形CommitDTUWave.aUserID=[" + aUserID + "], aPWD=[" + aPWD + "], aCompID=[" + aCompID
				+ "], fDTUID=[" + fDTUID + "],fWaveBatchID=[" + fWaveBatchID + "], fAheadTime=[" + fAheadTime
				+ "], fWaveData=[...], fDataLen=[" + fDataLen + "], fSampRate=[" + fSampRate + "], fFrameType=["
				+ fFrameType + "], fMessType=[" + fMessType + "], fResultStr=[" + fResultStr + "]");
		if (isTestMode) {
			logger.info("使用测试模式");
			return true;
		}
		boolean ret = service.getWCFBindingServiceInterface().commitDTUWave(aUserID, aPWD, aCompID, fDTUID,
				fWaveBatchID, fAheadTime, fWaveData, fDataLen, fSampRate, fFrameType, fMessType, fResultStr);
		logger.info("调用接口提交行波波形CommitDTUWave.ret=[" + ret + "],fResultStr=[" + fResultStr + "]");
		return ret;
	}

	/**
	 * 
	 * @param service
	 * @param fDTUID
	 * @param fWaveBatchID
	 * @param fAheadTime
	 * @param fWaveData
	 * @param fDataLen
	 * @param fSampRate
	 * @param fResultStr
	 * @return
	 */
	public static boolean commitDTUWave_GP(Service service, String fDTUID, String fWaveBatchID, String fAheadTime,
			String fWaveData, int fDataLen, int fSampRate, String fResultStr) {
		int fFrameType = 0x01;//
		int fMessType = 0x03;//
		logger.info("调用接口提交工频波形CommitDTUWave.aUserID=[" + aUserID + "], aPWD=[" + aPWD + "], aCompID=[" + aCompID
				+ "], fDTUID=[" + fDTUID + "],fWaveBatchID=[" + fWaveBatchID + "], fAheadTime=[" + fAheadTime
				+ "], fWaveData=[...], fDataLen=[" + fDataLen + "], fSampRate=[" + fSampRate + "], fFrameType=["
				+ fFrameType + "], fMessType=[" + fMessType + "], fResultStr=[" + fResultStr + "]");
		if (isTestMode) {
			logger.info("使用测试模式");
			return true;
		}
		boolean ret = service.getWCFBindingServiceInterface().commitDTUWave(aUserID, aPWD, aCompID, fDTUID,
				fWaveBatchID, fAheadTime, fWaveData, fDataLen, fSampRate, fFrameType, fMessType, fResultStr);
		logger.info("调用接口提交工频波形CommitDTUWave.ret=[" + ret + "],fResultStr=[" + fResultStr + "]");
		return ret;
	}

	/**
	 * 上传行波的波形性质
	 * 
	 * @param fWaveBatchID
	 * @param fRelaFlag
	 *            0 未处理 1雷击 2非雷击 3绕击 4反击 -1不能区分
	 * @param fResultStr
	 * @return
	 */
	public static boolean commitWaveRelaFlag(Service service, String fWaveBatchID, int fRelaFlag, String fResultStr) {
		logger.info("调用接口commitWaveRelaFlag.aUserID=[" + aUserID + "], aPWD=[" + aPWD + "], aCompID=[" + aCompID
				+ "], fWaveBatchID=[" + fWaveBatchID + "],fRelaFlag=[" + fRelaFlag + "], fResultStr=[" + fResultStr
				+ "]");
		if (isTestMode) {
			logger.info("使用测试模式");
			return true;
		}
		boolean ret = service.getWCFBindingServiceInterface().commitWaveRelaFlag(aUserID, aPWD, aCompID, fWaveBatchID,
				fRelaFlag, fResultStr);
		logger.info("调用commitWaveRelaFlag返回.ret=[" + ret + "],fResultStr=[" + fResultStr + "]");
		return ret;
	}

	/**
	 * 提交故障自动诊断结果
	 * 
	 * @param fAcciID
	 * @param fAcciTime
	 * @param fMLineID
	 * @param fElePoID
	 * @param fAheadElePoID
	 * @param fEndElePoID
	 * @param fSLType
	 * @param fLight
	 * @param fLightPro
	 * @param fGate
	 * @param fNaofFault
	 * @param fResultStr
	 * @return
	 */
	public static boolean autoDiaRes(Service service, String fAcciID, String fAcciTime, String fMLineID,
			String fElePoID, String fAheadElePoID, String fEndElePoID, int fSLType, int fLight, int fLightPro,
			int fGate, String fNaofFault, String fResultStr) {
		logger.info("调用接口AutoDiaRes(自动诊断)。aUserID=[" + aUserID + "], aPWD=[" + aPWD + "], aCompID=[" + aCompID
				+ "], fAcciID=[" + fAcciID + "], fAcciTime=[" + fAcciTime + "],fMLineID=[" + fMLineID + "], fElePoID=["
				+ fElePoID + "], fAheadElePoID=[" + fAheadElePoID + "], fEndElePoID=[" + fEndElePoID + "], fSLType=["
				+ fSLType + "], fLight=[" + fLight + "], fLightPro=[" + fLightPro + "], fGate=[" + fGate
				+ "], fNaofFault=[" + fNaofFault + "],fResultStr=[" + fResultStr + "]");
		if (isTestMode) {
			logger.info("使用测试接口");
			return true;
		}
		boolean ret = service.getWCFBindingServiceInterface().autoDiaRes(aUserID, aPWD, aCompID, fAcciID, fAcciTime,
				fMLineID, fElePoID, fAheadElePoID, fEndElePoID, fSLType, fLight, fLightPro, fGate, fNaofFault,
				fResultStr);
		logger.info("调用AutoDiaRes。ret=[" + ret + "],fResultStr=[" + fResultStr + "]");
		return ret;
	}

	/**
	 * 提交人工复合结果
	 * 
	 * @param fAcciID
	 * @param fAcciTime
	 * @param fMLineID
	 * @param fElePoID
	 * @param fAheadElePoID
	 * @param fEndElePoID
	 * @param fSLType
	 * @param fLight
	 * @param fLightPro
	 * @param fGate
	 * @param fNaofFault
	 * @param fErrDistance
	 * @param fBaseElePo
	 * @param fResultStr
	 * @return
	 */
	public static boolean manualDiaResWGS(Service service, String fAcciID, String fAcciTime, String fMLineID,
			String fElePoID, String fAheadElePoID, String fEndElePoID, int fSLType, int fLight, int fLightPro,
			int fGate, String fNaofFault, String fErrDistance, String fBaseElePo, String fResultStr) {
		logger.info("调用ManualDiaResWGS。aUserID=[" + aUserID + "], aPWD=[" + aPWD + "], aCompID=[" + aCompID
				+ "], fAcciID=[" + fAcciID + "],fAcciTime=[" + fAcciTime + "], fMLineID=[" + fMLineID + "], fElePoID=["
				+ fElePoID + "], fAheadElePoID=[" + fAheadElePoID + "], fEndElePoID=[" + fEndElePoID + "], fSLType=["
				+ fSLType + "], fLight=[" + fLight + "], fLightPro=[" + fLightPro + "], fGate=[" + fGate
				+ "],fNaofFault=[" + fNaofFault + "], fErrDistance=[" + fErrDistance + "], fBaseElePo=[" + fBaseElePo
				+ "], fResultStr=[" + fResultStr + "]");
		if (isTestMode) {
			logger.info("测试模式");
			return true;
		}
		boolean ret = service.getWCFBindingServiceInterface().manualDiaResWGS(aUserID, aPWD, aCompID, fAcciID,
				fAcciTime, fMLineID, fElePoID, fAheadElePoID, fEndElePoID, fSLType, fLight, fLightPro, fGate,
				fNaofFault, fErrDistance, fBaseElePo, fResultStr);
		logger.info("调用ManualDiaResWGS。ret=[" + ret + "],fResultStr=[" + fResultStr + "]");
		return ret;
	}

	/**
	 * 获取下发指令
	 * 
	 * @param service
	 * @return
	 */
	public static String getSendCom(Service service) {
		int fRowNum = 100;
		int fState = 0;
		logger.info("调用接口GetSendCom.aUserID=[" + aUserID + "], aPWD=[" + aPWD + "], aCompID=[" + aCompID
				+ "], fRowNum=[" + fRowNum + "], fState=[" + fState + "]");
		if (isTestMode) {
			logger.info("测试模式");
			return "";
		}
		String resp = service.getWCFBindingServiceInterface().getSendCom(aUserID, aPWD, aCompID, fRowNum, fState);
		logger.info("调用GetSendCom.返回resp=[" + resp + "]");
		return resp;
	}

	/**
	 * 回传cmd指令处理结果
	 * 
	 * @param service
	 * @param serial
	 * @param fState
	 *            -1 命令发送失败 1命令发送成功 2命令发送中间状态
	 * @param fResultStr
	 * @return
	 */
	public static boolean setSendCom(Service service, int fSerial, int fState, String fResultStr) {
		logger.info("调用接口SetSendCom.aUserID=[" + aUserID + "], aPWD=[" + aPWD + "], aCompID=[" + aCompID
				+ "], fSerial=[" + fSerial + "], fState=[" + fState + "],fResultStr=[" + fResultStr + "]");
		if (isTestMode) {
			logger.info("测试模式");
			return true;
		}
		boolean ret = service.getWCFBindingServiceInterface().setSendCom(aUserID, aPWD, aCompID, fSerial, fState,
				fResultStr);
		logger.info("调用SetSendCom返回.ret=[" + ret + "],fResultStr=[" + fResultStr + "]");
		return ret;
	}

	public static Service newService() {
		if (isTestMode)
			return null;
		return new Service();
	}

}
