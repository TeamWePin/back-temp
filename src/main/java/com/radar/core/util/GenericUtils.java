package com.radar.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class GenericUtils {

	private static final Logger logger = LoggerFactory.getLogger(GenericUtils.class);

	public static Map<String, Object> converObjectToMap(Object obj) {
		try {
			// Field[] fields = obj.getClass().getFields(); //private field�� ������
			// ����.
			Field[] fields = obj.getClass().getDeclaredFields();
			Map<String, Object> resultMap = new HashMap<>();
			for (int i = 0; i <= fields.length - 1; i++) {
				fields[i].setAccessible(true);
				resultMap.put(fields[i].getName(), fields[i].get(obj));
			}
			return resultMap;
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		return null;

	}
	public static Map<String, Object> converObjectToMapSuper(Object obj) {
		try {
			// Field[] fields = obj.getClass().getFields(); //private field�� ������
			// ����.
			Field[] fields = obj.getClass().getSuperclass().getDeclaredFields();
			Map<String, Object> resultMap = new HashMap<>();
			for (int i = 0; i <= fields.length - 1; i++) {
				fields[i].setAccessible(true);
				resultMap.put(fields[i].getName(), fields[i].get(obj));
			}
			return resultMap;
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		return null;

	}

	public static Object convertMapToObject(Map<String, Object> map, Object objClass) {
		String keyAttribute = null;
		String setMethodString = "set";
		String methodString = null;
		for (String s : map.keySet()) {
			keyAttribute = s;
			methodString = setMethodString + keyAttribute.substring(0, 1).toUpperCase() + keyAttribute.substring(1);
			try {
				Method[] methods = objClass.getClass().getDeclaredMethods();
				for (int i = 0; i <= methods.length - 1; i++) {
					if (methodString.equals(methods[i].getName())) {
						logger.info("invoke : " + methodString);
						methods[i].invoke(objClass, map.get(keyAttribute));
					}
				}
			} catch (Exception e) {
				logger.error("ERROR", e);
			}
		}
		return objClass;
	}

}
