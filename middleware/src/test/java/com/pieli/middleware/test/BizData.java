package com.pieli.middleware.test;

import java.io.Serializable;

public class BizData implements Serializable {

	/**
	* @Fields serialVersionUID : TODO
	*/
	private static final long serialVersionUID = 7426022815556780215L;
	
	private int f1;
	private String f2;
	/**
	 * @return the f1
	 */
	public int getF1() {
		return f1;
	}
	/**
	 * @param f1 the f1 to set
	 */
	public void setF1(int f1) {
		this.f1 = f1;
	}
	/**
	 * @return the f2
	 */
	public String getF2() {
		return f2;
	}
	/**
	 * @param f2 the f2 to set
	 */
	public void setF2(String f2) {
		this.f2 = f2;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = "{f1:" + this.f1 + ",f2:'" + this.f2 + "'}";
		System.out.println(str);
		byte[] strArr = str.getBytes();
		StringBuilder result = new StringBuilder();
		for(byte item: strArr){
			String c = Integer.toHexString(item & 0xff);
			result.append(c.length() == 1? "0" + c: c);
		}
		System.out.println(result.toString());
		return result.toString();
	}
	
	
	
}
