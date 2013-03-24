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
import java.util.Properties;

import junit.framework.Assert;

/**
 * @ClassName: CommonUtils
 * @Description: TODO
 * @date 2013-3-23 上午10:40:29
 * 
 */
public class CommonUtils {

	private static final SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
	private static final Properties _sysProps=System.getProperties();
	
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
	
	/**
	 * 
	* @Title: isCurrentSystemWindows
	* @Description: TODO
	* @param @return
	* @return boolean
	* @throws
	 */
	public static boolean isCurrentSystemWindows(){
		
		String osName = null;
		if((osName = _sysProps.getProperty("os.name")).toLowerCase().indexOf("windows") != -1){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	* @Title: convertBytesToHexStr
	* @Description: TODO
	* @param @param input
	* @param @return
	* @return String
	* @throws
	 */
	public static String convertBytesToHexStr(byte[] input){
		if(input == null){
			throw new IllegalArgumentException("input could not be null!");
		}
		StringBuilder result = new StringBuilder();
		for(byte item: input){
			String c = Integer.toHexString(item & 0xff);
			result.append(c.length() == 1? "0" + c: c);
		}
		return result.toString();
	}
	
	/**
	 * 
	* @Title: convertHexStrToBytes
	* @Description: TODO
	* @param @param input
	* @param @return
	* @return byte[]
	* @throws
	 */
	public static byte[] convertHexStrToBytes(String input){
		if(input == null){
			throw new IllegalArgumentException("input could not be null!");
		}
		Assert.assertTrue(input.length() % 2 == 0);
		
		input = input.toLowerCase();
		byte[] result = new byte[input.length() / 2];
		for(int i = 0; i < result.length; i ++){
			
			byte high = (byte) (Character.digit(input.charAt(2 * i), 16) & 0xff);  
	        byte low = (byte) (Character.digit(input.charAt(2 * i + 1), 16) & 0xff);  
			
			result[i] = (byte) (high << 4 | low);
		}
		return result;
	}
	
}
