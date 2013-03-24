package com.pieli.middleware.data;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.pieli.middleware.utils.CommonUtils;

import junit.framework.Assert;

/**
 * 
* @ClassName: MessageSerializer
* @Description: TODO
* @date 2013-3-24 上午10:49:50
*
 */
public class MessageSerializer {

	/**
	 * 序列化消息对象
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws IntrospectionException 
	 * 
	* @Title: serialize
	* @Description: TODO
	* @param @param m
	* @param @return
	* @return String
	* @throws
	 */
	@SuppressWarnings("rawtypes")
	public static  String serialize(Message m) throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		
		Assert.assertTrue(m != null);
		
		if(m instanceof ObjectMessage){
			ObjectMessage obj = (ObjectMessage)m;
			StringBuilder builder = new StringBuilder();
			builder.append("Header: Class/ObjectMessage,sentTime/" + obj.getSentTime() + ";");
			builder.append("Body: bizData/");
			
			if(obj.getBizData() == null){
				return builder.toString() + ";";
			} else {
				Class<?> targetClass = obj.getBizData().getClass();
				Field[] fields = targetClass.getDeclaredFields();
				StringBuilder jsonStr = new StringBuilder();
				if(fields != null){					
					jsonStr.append("{");
					for(int i =0; i < fields.length; i ++ ){
						Type type = fields[i].getType();
						String name = fields[i].getName();
						int mod = fields[i].getModifiers();
						
						if(Modifier.isStatic(mod)){continue;}
						
						PropertyDescriptor descriptor = new PropertyDescriptor(name, targetClass);
						Method method = null;
						try {
							method = descriptor.getReadMethod();
						} catch (Exception e) {
							continue;
						}
						
						if(method != null){
							
							if(jsonStr.toString().length() > 1){
								jsonStr.append(",");
							}
							jsonStr.append("\"" + name + "\"" + ":");
							if(type == int.class){
								int temp = (Integer) method.invoke(obj.getBizData());
								jsonStr.append(temp);
							}
							if(type == double.class){
								double temp = (Double)method.invoke(obj.getBizData());
								jsonStr.append(temp);
							}							
							if(type == String.class){
								Object temp = method.invoke(obj.getBizData());
								jsonStr.append(temp == null? "null" : "\"" + temp.toString().replace("\"", "\\\"") + "\""); //转义
							}
							if(type == Enum.class){
								Object temp = method.invoke(obj.getBizData());
								jsonStr.append(temp == null? "null" : "\"" + ((Enum)temp).name().toString().replace("\"", "\\\"") +  "\""); //转义
							}
						}

					}
					jsonStr.append("}");
				}
				builder.append(CommonUtils.convertBytesToHexStr(jsonStr.toString().getBytes()) + ";");
			}
			return builder.toString();		
		}
		
		return null;
	}
	
}
