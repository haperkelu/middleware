/** 
* @Title: TransferStandardData.java
* @Package com.pieli.middleware.data
* @Description: TODO
* @author Pie.Li
* @date 2013-3-31 下午6:04:14
* @version V1.0 
*/
package com.pieli.middleware.transfer;

import java.io.Serializable;

/**
 * @ClassName: TransferStandardData
 * @Description: TODO
 * @date 2013-3-31 下午6:04:14
 * 
 */
public class TransferStandardData implements Serializable {

	/**
	* @Fields serialVersionUID : TODO
	*/
	private static final long serialVersionUID = 363746610085594616L;
	
	private String header;
	
	private int type;
	
	private Object bizData;

	/**
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the bizData
	 */
	public Object getBizData() {
		return bizData;
	}

	/**
	 * @param bizData the bizData to set
	 */
	public void setBizData(Object bizData) {
		this.bizData = bizData;
	}
}
