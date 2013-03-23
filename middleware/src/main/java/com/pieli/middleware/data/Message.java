package com.pieli.middleware.data;

import java.io.Serializable;

public  interface Message extends Serializable {
	
	public void setTopic(String topic);

	public void setSegmentId(int segmentId) ;
	
	/**
	 * 
	* @Title: getTopic
	* @Description: TODO
	* @param @return
	* @return String
	* @throws
	 */
	public String getTopic();
	
	/**
	 * 
	* @Title: getSegmentId
	* @Description: TODO
	* @param @return
	* @return int
	* @throws
	 */
	public int getSegmentId();
	
	/**
	 * 
	* @Title: setSentTime
	* @Description: TODO
	* @param @param sentTime
	* @return void
	* @throws
	 */
	public void setSentTime(String sentTime);
	/**
	 * 
	* @Title: setMessageId
	* @Description: TODO
	* @param @param messageId
	* @return void
	* @throws
	 */
	public void setMessageId(int messageId);
}
