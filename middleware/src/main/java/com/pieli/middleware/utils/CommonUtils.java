/** 
* @Title: CommonUtils.java
* @Package com.pieli.middleware.utils
* @Description: TODO
* @author Pie.Li
* @date 2013-3-23 上午10:40:29
* @version V1.0 
*/
package com.pieli.middleware.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

/**
 * @ClassName: CommonUtils
 * @Description: TODO
 * @date 2013-3-23 上午10:40:29
 * 
 */
public class CommonUtils {

	private static final SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");

	
	/**
	 * 
	* @Title: before
	* @Description: TODO
	* @param @param input
	* @param @param delta
	* @param @return
	* @return Date
	* @throws
	 */
	public static boolean beforeNowTime(Date input, long delta){
		if(input == null){
			throw new IllegalArgumentException("Date Could Not Be Null");
		}
		Assert.assertTrue(delta != 0);
		long milliseconds  = input.getTime();
		return milliseconds + delta < Calendar.getInstance().getTimeInMillis();
	}
	
	/**
	 * 
	* @Title: formatDateTime
	* @Description: TODO
	* @param @param date
	* @param @return
	* @return String
	* @throws
	 */
	public static String formatDateTime(Date date){
		if(date == null){
			return null;
		}
		return _sdf.format(date);
	}
	
	/**
	 * 
	* @Title: formatRightNow
	* @Description: TODO
	* @param @return
	* @return String
	* @throws
	 */
	public static String formatRightNow(){
		return formatDateTime(Calendar.getInstance().getTime());
	}
	
	/**
	 * 
	* @Title: makeUpByteArrayWithZero
	* @Description: TODO
	* @param @param input
	* @return void
	* @throws
	 */
	public static byte[] makeUpByteArrayWithZero(String input){
		if(input == null || input.length() == 0){
			return null;
		}
		//Assert.assertTrue(input.length % 512 == 0);
		int size = input.length();
		int count = size / 512;
		int mod = size % 512;
		if(count == 0){
			count += 1;
		} else if(mod != 0){
			count += 1;
		}
		return Arrays.copyOf(input.getBytes(), count * 512);
	}
	
}
