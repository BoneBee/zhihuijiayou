package com.ncse.zhhygis.utils.baseUtils;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 字符串操作工具类
 */
public class StringUtils
{

    /**
     * 首字母变小写
     * @param str
     * @return
     */
    public static String firstCharToLowerCase(String str)
    {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z')
        {
            char[] arr = str.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    /**
     * 首字母变大写
     * @param str
     * @return
     */
    public static String firstCharToUpperCase(String str)
    {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z')
        {
            char[] arr = str.toCharArray();
            arr[0] -= ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    /**
     * 判断是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(final String str)
    {
        return (str == null) || (str.length() == 0 || "".equals(str));
    }

    /**
     * 判断是否不为空
     * @param str
     * @return
     */
    public static boolean isNotEmpty(final String str)
    {
        return !isEmpty(str);
    }

    /**
     * 判断是否不为空并且不为null
     * xgn
     * @param str
     * @return
     */
    public static boolean isNotNullAndEmpty(final String str) {
        String s = "";
        if (str!=null&&!"".equals(str)) {
             s = FilterStr(str);
        }
        return s != null && !isEmpty(s.trim());
    }

    /**
     * 判断是否不为空并且不为null并且长度小于数据库长度
     * xgn
     * @param str
     * @return
     */
    public static boolean isNotNullAndLength(final String str,int length) {
        boolean b = str != null && !isEmpty(str.trim()) &&  str.length()<=length;
        return b;
    }
    public static boolean isNotLength(final String str,int length) {
        boolean b = str.length()<=length;
        return b;
    }

    /**
     * 判断是否不为空并且不为null，是空返回""，不为空返回值
     * xgn
     * @param str
     * @return
     */
    public static String isNotNullAndEmptyBalck(final Object str) {
        if (str != null&&!"NAN".equals(str)){
            if (!isEmpty(str.toString().trim())){
                return str.toString();
            }
            return "";
        }else {
            return "";
        }
    }

    /**
     * 判断是否空白
     * @param str
     * @return
     */
    public static boolean isBlank(final String str)
    {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0))
            return true;
        for (int i = 0; i < strLen; i++)
        {
            if (!Character.isWhitespace(str.charAt(i)))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否不是空白
     * @param str
     * @return
     */
    public static boolean isNotBlank(final String str)
    {
        return !isBlank(str);
    }

    /**
     * 判断多个字符串全部是否为空
     * @param strings
     * @return
     */
    public static boolean isAllEmpty(String... strings)
    {
        if (strings == null)
        {
            return true;
        }
        for (String str : strings)
        {
            if (isNotEmpty(str))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断多个字符串其中任意一个是否为空
     * @param strings
     * @return
     */
    public static boolean isHasEmpty(String... strings)
    {
        if (strings == null)
        {
            return true;
        }
        for (String str : strings)
        {
            if (isEmpty(str))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * checkValue为 null 或者为 "" 时返回 defaultValue
     * @param checkValue
     * @param defaultValue
     * @return
     */
    public static String isEmpty(String checkValue, String defaultValue)
    {
        return isEmpty(checkValue) ? defaultValue : checkValue;
    }

    /**
     * 字符串不为 null 而且不为 "" 并且等于other
     * @param str
     * @param other
     * @return
     */
    public static boolean isNotEmptyAndEquelsOther(String str, String other)
    {
        if (isEmpty(str))
        {
            return false;
        }
        return str.equals(other);
    }

    /**
     * 字符串不为 null 而且不为 "" 并且不等于other
     * @param str
     * @param other
     * @return
     */
    public static boolean isNotEmptyAndNotEquelsOther(String str, String... other)
    {
        if (isEmpty(str))
        {
            return false;
        }
        for (int i = 0; i < other.length; i++)
        {
            if (str.equals(other[i]))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 字符串不等于other
     * @param str
     * @param other
     * @return
     */
    public static boolean isNotEquelsOther(String str, String... other)
    {
        for (int i = 0; i < other.length; i++)
        {
            if (other[i].equals(str))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串不为空
     * @param strings
     * @return
     */
    public static boolean isNotEmpty(String... strings)
    {
        if (strings == null)
        {
            return false;
        }
        for (String str : strings)
        {
            if (str == null || "".equals(str.trim()))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较字符相等
     * @param value
     * @param equals
     * @return
     */
    public static boolean equals(String value, String equals)
    {
        if (isAllEmpty(value, equals))
        {
            return true;
        }
        return value.equals(equals);
    }

    /**
     * 比较字符串不相等
     * @param value
     * @param equals
     * @return
     */
    public static boolean isNotEquals(String value, String equals)
    {
        return !equals(value, equals);
    }

    public static String[] split(String content, String separatorChars)
    {
        return splitWorker(content, separatorChars, -1, false);
    }

    public static String[] split(String str, String separatorChars, int max)
    {
        return splitWorker(str, separatorChars, max, false);
    }

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens)
    {
        if (str == null)
        {
            return null;
        }
        int len = str.length();
        if (len == 0)
        {
            return EMPTY_STRING_ARRAY;
        }
        List<String> list = new ArrayList<String>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null)
        {
            while (i < len)
            {
                if (Character.isWhitespace(str.charAt(i)))
                {
                    if (match || preserveAllTokens)
                    {
                        lastMatch = true;
                        if (sizePlus1++ == max)
                        {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        else if (separatorChars.length() == 1)
        {
            char sep = separatorChars.charAt(0);
            while (i < len)
            {
                if (str.charAt(i) == sep)
                {
                    if (match || preserveAllTokens)
                    {
                        lastMatch = true;
                        if (sizePlus1++ == max)
                        {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        else
        {
            while (i < len)
            {
                if (separatorChars.indexOf(str.charAt(i)) >= 0)
                {
                    if (match || preserveAllTokens)
                    {
                        lastMatch = true;
                        if (sizePlus1++ == max)
                        {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || (preserveAllTokens && lastMatch))
        {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(EMPTY_STRING_ARRAY);
    }

    /**
     * 消除转义字符
     * @param str
     * @return
     */
    public static String escapeXML(String str)
    {
        if (str == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); ++i)
        {
            char c = str.charAt(i);
            switch (c)
            {
                case '\u00FF':
                case '\u0024':
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '\"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
                    if (c >= '\u0000' && c <= '\u001F')
                        break;
                    if (c >= '\uE000' && c <= '\uF8FF')
                        break;
                    if (c >= '\uFFF0' && c <= '\uFFFF')
                        break;
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * 将字符串中特定模式的字符转换成map中对应的值
     *
     * @param s
     *            需要转换的字符串
     * @param map
     *            转换所需的键值对集合
     * @return 转换后的字符串
     */
    public static String replace(String s, Map<String, Object> map)
    {
        StringBuilder ret = new StringBuilder((int) (s.length() * 1.5));
        int cursor = 0;
        for (int start, end; (start = s.indexOf("${", cursor)) != -1 && (end = s.indexOf("}", start)) != -1;)
        {
            ret.append(s.substring(cursor, start)).append(map.get(s.substring(start + 2, end)));
            cursor = end + 1;
        }
        ret.append(s.substring(cursor, s.length()));
        return ret.toString();
    }

    public static String replace(String s, Object... objs)
    {
        if (objs == null || objs.length == 0)
            return s;
        if (s.indexOf("{}") == -1)
            return s;
        StringBuilder ret = new StringBuilder((int) (s.length() * 1.5));
        int cursor = 0;
        int index = 0;
        for (int start; (start = s.indexOf("{}", cursor)) != -1;)
        {
            ret.append(s.substring(cursor, start));
            if (index < objs.length)
                ret.append(objs[index]);
            else
                ret.append("{}");
            cursor = start + 2;
            index++;
        }
        ret.append(s.substring(cursor, s.length()));
        return ret.toString();
    }

    /**
     * 字符串格式化工具,参数必须以{0}之类的样式标示出来.大括号中的数字从0开始。
     *
     * @param source
     *            源字符串
     * @param params
     *            需要替换的参数列表,写入时会调用每个参数的toString().
     * @return 替换完成的字符串。如果原始字符串为空或者参数为空那么将直接返回原始字符串。
     */
    public static String replaceArgs(String source, Object... params)
    {
        if (params == null || params.length == 0 || source == null || isEmpty(source))
        {
            return source;
        }
        StringBuilder buff = new StringBuilder(source);
        StringBuilder temp = new StringBuilder();
        int startIndex = 0;
        int endIndex = 0;
        String param = null;
        for (int count = 0; count < params.length; count++)
        {
            if (params[count] == null)
            {
                param = null;
            }
            else
            {
                param = params[count].toString();
            }

            temp.delete(0, temp.length());
            temp.append("{");
            temp.append(count);
            temp.append("}");
            while (true)
            {
                startIndex = buff.indexOf(temp.toString(), endIndex);
                if (startIndex == -1)
                {
                    break;
                }
                endIndex = startIndex + temp.length();

                buff.replace(startIndex, endIndex, param == null ? "" : param);
            }
            startIndex = 0;
            endIndex = 0;
        }
        return buff.toString();
    }

    public static String substringBefore(final String s, final String separator)
    {
        if (isEmpty(s) || separator == null)
        {
            return s;
        }
        if (isEmpty(separator))
        {
            return "";
        }
        final int pos = s.indexOf(separator);
        if (pos < 0)
        {
            return s;
        }
        return s.substring(0, pos);
    }

    public static String substringBetween(final String str, final String open, final String close)
    {
        if (str == null || open == null || close == null)
        {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != -1)
        {
            final int end = str.indexOf(close, start + open.length());
            if (end != -1)
            {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    public static String substringAfter(final String str, final String separator)
    {
        if (isEmpty(str))
        {
            return str;
        }
        if (separator == null)
        {
            return "";
        }
        final int pos = str.indexOf(separator);
        if (pos == -1)
        {
            return "";
        }
        return str.substring(pos + separator.length());
    }

    /**
     * 转换为字节数组
     * @param bytes
     * @return
     */
    public static String toString(byte[] bytes)
    {
        try
        {
            return new String(bytes, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return null;
        }
    }

    /**
     * 转换为字节数组
     * @param str
     * @return
     */
    public static byte[] getBytes(String str)
    {
        if (str != null)
        {
            try
            {
                return str.getBytes("utf-8");
            }
            catch (UnsupportedEncodingException e)
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * 判断特殊字符
     * @Title : FilterStr
     * @Type : FilterString
     * @date : 2014年2月28日 下午11:01:21
     * @Description : 判断特殊字符
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String FilterStr(String str) throws PatternSyntaxException
    {
        /**
         * 特殊字符
         */
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？_]";

        /**
         * Pattern p = Pattern.compile("a*b");
         * Matcher m = p.matcher("aaaaab");
         * boolean b = m.matches();
         */
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(str);

        /**
         * 返回替换结果
         */
        return mat.replaceAll("").trim();
    }
    public static boolean isInteger(String str) {  
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
        return pattern.matcher(str).matches();  
    }

    /**
     * 去除list重复数据
     * @param strList
     * @return
     */
    public static List<String> removeDuplicate(List<String> strList)
    {
        LinkedHashSet<String> set = new LinkedHashSet<String>(strList.size());
        set.addAll(strList);
        strList.clear();
        strList.addAll(set);
        return strList;
    }

    public static void main(String[] a)
    {
        /*String totalStr = "1.0.0";
        String filterStr = FilterStr(totalStr);
        */
    	String aa="1";
    	String bb="2";
    	System.out.println(aa.compareTo(bb));
    	System.out.println(bb.compareTo(aa));
    	String aaa="2018-12-04 15:41:20";
    	String bbb="2018-12-04 18:45:30";
    	System.out.println(DateUtil.getDistanceTimes(aaa, bbb,"min"));
    }
    
    public static boolean  compareDate(String date1, String date2) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d1 = dateFormat.parse(date1);
			Date d2 = dateFormat.parse(date2);
			if (d1.before(d2)) {

				return true;

			} else if (d1.after(d2)) {

				return false;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return false;
	}
}
