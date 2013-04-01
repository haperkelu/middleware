/** 
* @Title: LoggerUtils.java
* @Package com.pieli.middleware.utils
* @Description: TODO
* @author Pie.Li
* @date 2013-3-23 下午2:56:45
* @version V1.0 
*/
package com.pieli.middleware.utils;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @ClassName: LoggerUtils
 * @Description: TODO
 * @date 2013-3-23 下午2:56:45
 * 
 */
public class LoggerUtils {
	public static  Logger logger;
	static{
		LogManager manager = LogManager.getLogManager();
		try {
			manager.readConfiguration(LoggerUtils.class.getResourceAsStream("/logging.properties"));
			logger =Logger.getLogger("Common");
			manager.addLogger(logger);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} 
	}
	
	/**
	 * 
	* @Title: logExceptionDetail
	* @Description: TODO
	* @param @param msg
	* @param @param e
	* @return void
	* @throws
	 */
	public static void logExceptionDetail(String msg, Exception e){
		if(e == null){return;}
		if(msg == null){msg = e.getMessage();}
		StackTraceElement [] messages = e.getStackTrace();
		StringBuilder result = new StringBuilder();
		result.append(msg + "\r\n");
		if(messages != null){
			for(StackTraceElement item: messages){
				result.append("ClassName:"+ item.getClassName());
				result.append("getFileName:"+item.getFileName());
				result.append("getLineNumber:"+item.getLineNumber());
				result.append("getMethodName:"+ item.getMethodName());
				result.append("toString:"+ item.toString());
			}
		}
		logger.info(result.toString());
	}
}
