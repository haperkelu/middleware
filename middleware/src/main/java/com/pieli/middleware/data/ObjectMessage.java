/** 
* @Title: ObjectMessage.java
* @Package com.pieli.middleware.data
* @Description: TODO
* @author Pie.Li
* @date 2013-3-23 上午10:30:15
* @version V1.0 
*/
package com.pieli.middleware.data;

/**
 * @ClassName: ObjectMessage
 * @Description: TODO
 * @date 2013-3-23 上午10:30:15
 * 
 */
public class ObjectMessage implements Message {

	/**
	* @Fields serialVersionUID : TODO
	*/
	private static final long serialVersionUID = -6521604994973227658L;
	
	private String topic;
	private int segmentId;
	
	private int messageId;
	private Object bizData;
	private String sentTime;
	
	/**
	 * @return the messageId
	 */
	public int getMessageId() {
		return messageId;
	}
	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(int messageId) {
		this.messageId = messageId;
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
	
	/**
	 * @return the sentTime
	 */
	public String getSentTime() {
		return sentTime;
	}
	/**
	 * @param sentTime the sentTime to set
	 */
	public void setSentTime(String sentTime) {
		this.sentTime = sentTime;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Header: Class/ObjectMessage,sentTime/" + sentTime + ";");
		builder.append("Body: bizData/" + bizData + ";");
		return builder.toString();
	}
	
	/**
	 * 
	 * @see com.pieli.middleware.data.Message#getTopic()
	 */
	public String getTopic() {
		return this.topic;
	}
	
	/**
	 * 
	 * @see com.pieli.middleware.data.Message#getSegmentId()
	 */
	public int getSegmentId() {
		return this.segmentId;
	}
	/**
	 * @param topic the topic to set
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}
	/**
	 * @param segmentId the segmentId to set
	 */
	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}	
	
}
