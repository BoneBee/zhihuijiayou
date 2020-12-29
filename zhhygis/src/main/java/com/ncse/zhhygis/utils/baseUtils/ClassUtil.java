package com.ncse.zhhygis.utils.baseUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zh.bean.login.MyStaff;

public class ClassUtil {
	public static void getObjectValue(Object object) throws Exception {

		if (object != null) {// if (object!=null ) ----begin
			// 拿到该类
			Class<?> clz = object.getClass();
			// 获取实体类的所有属性，返回Field数组
			Field[] fields = clz.getDeclaredFields();

			for (Field field : fields) {// --for() begin
				System.out.println(field.getGenericType());// 打印该类的所有属性类型

				System.out.println(field.getName());
				// 如果类型是String
				if (field.getGenericType().toString().equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class
																							// "，后面跟类名
					// 拿到该属性的gettet方法

					Method m = (Method) object.getClass().getMethod("get" + getMethodName(field.getName()));

					String val = (String) m.invoke(object);// 调用getter方法获取属性值
					if (val != null) {
						System.out.println("String type:" + val);
					}

				}

				// 如果类型是Integer
				if (field.getGenericType().toString().equals("class java.lang.Integer")) {
					Method m = (Method) object.getClass().getMethod("get" + getMethodName(field.getName()));
					Integer val = (Integer) m.invoke(object);
					if (val != null) {
						System.out.println("Integer type:" + val);
					}

				}

				// 如果类型是Double
				if (field.getGenericType().toString().equals("class java.lang.Double")) {
					Method m = (Method) object.getClass().getMethod("get" + getMethodName(field.getName()));
					Double val = (Double) m.invoke(object);
					if (val != null) {
						System.out.println("Double type:" + val);
					}

				}

				// 如果类型是Boolean 是封装类
				if (field.getGenericType().toString().equals("class java.lang.Boolean")) {
					Method m = (Method) object.getClass().getMethod(field.getName());
					Boolean val = (Boolean) m.invoke(object);
					if (val != null) {
						System.out.println("Boolean type:" + val);
					}

				}

				// 如果类型是boolean 基本数据类型不一样 这里有点说名如果定义名是 isXXX的 那就全都是isXXX的
				// 反射找不到getter的具体名
				if (field.getGenericType().toString().equals("boolean")) {
					Method m = (Method) object.getClass().getMethod(field.getName());
					Boolean val = (Boolean) m.invoke(object);
					if (val != null) {
						System.out.println("boolean type:" + val);
					}

				}
				// 如果类型是Date
				if (field.getGenericType().toString().equals("class java.util.Date")) {
					Method m = (Method) object.getClass().getMethod("get" + getMethodName(field.getName()));
					Date val = (Date) m.invoke(object);
					if (val != null) {
						System.out.println("Date type:" + val);
					}

				}
				// 如果类型是Short
				if (field.getGenericType().toString().equals("class java.lang.Short")) {
					Method m = (Method) object.getClass().getMethod("get" + getMethodName(field.getName()));
					Short val = (Short) m.invoke(object);
					if (val != null) {
						System.out.println("Short type:" + val);
					}

				}
				// 如果还需要其他的类型请自己做扩展

			} // for() --end

		} // if (object!=null ) ----end
	}

	// 把一个字符串的第一个字母大写、效率是最高的、
	private static String getMethodName(String fildeName) throws Exception {
		byte[] items = fildeName.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}

	public static void main(String[] args) throws Exception {
		/*MyStaff sta = new MyStaff();
		getObjectValue(sta);*/
		
		String aa=" ";
		System.out.println(StringUtils.isEmpty(aa));
		System.out.println(!StringUtils.isEmpty(aa));
		System.out.println("bbbb"+org.springframework.util.StringUtils.isEmpty(aa));
	}

	public static List<String> getFiledName(Object object) throws Exception {
		List<String> reList=new ArrayList();
		if (object != null) {// if (object!=null ) ----begin
			// 拿到该类
			Class<?> clz = object.getClass();
			// 获取实体类的所有属性，返回Field数组
			Field[] fields = clz.getDeclaredFields();

			for (Field field : fields) {// --for() begin
				reList.add(field.getName());
			}
		}
		return reList;
	}
	
	public static Map MygetFiledName(Map map) throws Exception {
		List<String> list=getFiledName(new MyStaff());
		for(String s:list) {
			map.remove(s);
		}
		map.remove("loginTime");
		map.remove("expireTime");
		map.remove("token");
		return map;
	}
}
