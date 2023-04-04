package com.radar.core.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.radar.core.util.ObjectUtils.isEmpty;


public class StringUtil {
    private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);

    private static final String NOT_SPECIAL_CHARACTER_REGEX = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]*$";

    public static String makeStackTrace(Throwable t){
        if(t == null) return "";
        try{
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            t.printStackTrace(new PrintStream(bout));
            bout.flush();
            String error = new String(bout.toByteArray());

            return error;
        }catch(Exception ex){
            return "";
        }
    }

    // &#39;
    public static String textValidity(String text) {
        if(!StringUtils.isBlank(text)) {
            //TODO 필터링 다시 수정해야한다.
            String validity = text.trim().replaceAll("&lt;","<")
                    //.replaceAll("\\\\","")
                    .replaceAll("&gt",">")
                    .replaceAll("\"","&quot;")
                    /*.replaceAll("'","\''")*/
                    .replaceAll("'","&#39;")
                    .replaceAll("\r\n","<br>")
                    .replaceAll("\n","<br>");
            return validity;
        }
        return text;
    }
    public static String textValidityToDB(String text) {
        if(!StringUtils.isBlank(text)) {
            String validity = text.trim()
                    .replaceAll("&lt;","<")
                    .replaceAll("&gt",">")
                    .replaceAll("\"","\"")
                    .replaceAll("&quot;","\"")
                    .replaceAll("'","''")
                    .replaceAll("&#39;","''");
            return validity;
        }
        return text;
    }

    public static boolean isStringNumber(String s) {
        try {
            if(!StringUtils.isBlank(s)) {
                Double.parseDouble(s);
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * max글자수가 되도록 왼쪽부터 "0"으로 채우기
     */
    public static String lpadCount(String text, int max) {
        String lpadCount = "";
        if(max < 1) {
            return text;
        }
        for(int i=0; i < max-text.trim().length(); i++) {
            lpadCount = lpadCount + "0";
        }
        return lpadCount + text.trim();
    }


    public static boolean lengthCheck(String text, int length) {
        if(!StringUtils.isBlank(text)) {
            if(text.length() >= length) {
                return true;
            }
        }
        return false;
    }


    public static int checkStringLength(String str){  //순수 문자열 10글자 미만
        int cnt = 0;
        for(int i = 0 ; i < str.length() ; i++)
        {
            if(str.charAt(i) == ' '){
                cnt++;
            }
        }
        return cnt;
    }

    public static String speratePhoneNumber(String phone) {
        String sep = phone.replaceAll("-","");
        sep = sep.substring(0,3) +"-"+
                sep.substring(3,7) +"-"+
                sep.substring(7,11);
        return sep;
    }

    public static boolean checkSpecialCharacter(String content) {
        return !Pattern.matches(NOT_SPECIAL_CHARACTER_REGEX, content);
    }

    // 따옴표 형태 변환
    public static String changeQuotaionForSql(String content) {
        return content.replaceAll("\"", "\\\"").replaceAll("\'","\\\'");
    }

    public static String changeZeroToNull(Object value) {
        logger.info("changeZeroToNull value : {}", value);
        if(value instanceof Integer ) {
            return value.equals(0) ? "NULL" : String.valueOf(value);
        } else if(value instanceof String) {
            return StringUtils.isBlank((String)value) ? "NULL" : String.valueOf(value);
        }
        return "NULL";
    }

    public static String changeNullToZero(Object value) {
        if(value == null) {
            return "0";
        }
        return String.valueOf(value);
    }

    public static String argToFormBodyString(Map<String, Object> uriVariables) {
        if(isEmpty(uriVariables))
            return "";
        String formBodyString = uriVariables.toString();
        formBodyString = formBodyString.replaceAll("\\{", "")
                .replaceAll("\\}", "")
                .replaceAll(", ", "&");
        return formBodyString;
    }


    public static boolean isPathVariable(String url, String paramName) {
        boolean isPathVariable = false;
        Pattern p = Pattern.compile("\\{(.*?)\\}");
        Matcher m;
        m = p.matcher(url);
        while(m.find()) {
            isPathVariable = paramName.equals(m.group(1));
            if(isPathVariable)
                break;
        }
        return isPathVariable;
    }

    public static String makeQueryString(String url, Map<String, Object> queryVariables) {
        StringJoiner query = new StringJoiner("&");
        queryVariables.forEach((k, v) -> {
            query.add(k + "=" + v.toString());
        });
        if(!StringUtils.isBlank(query.toString()))
            return url + "?" + query;
        return url;
    }

    public static String fillPathValue(String url, Map<String, Object> pathParameters) {
        for (String key : pathParameters.keySet()) {
            if (pathParameters.get(key) != null) {
                url = url.replaceAll("\\{" + key + "}", pathParameters.get(key).toString());
            }
        }
        return url;
    }

    public static boolean checkSpaceExistFromText(String text) {
        Matcher matcher = Pattern.compile("\\s").matcher(text);
        while(matcher.find()) {
            return true;
        }
        return false;
    }

    public static String removeSpaceFromText(String text) {
        text = text.trim();
        text = Pattern.compile("\\s").matcher(text).replaceAll("");
        text = text.replaceAll("\\p{Z}", "");
        return text;
    }

    public static String changeUrlTextToATag(String urlText) {
        Matcher m = Pattern.compile("^(http:\\\\)?(www.)?([^a-zA-Z0-9.])*+(\\S/)*+(\\S?)+(\\S&)*+(\\s)?+(\n)?$").matcher(urlText);
        while(m.find()) {
            String linkText = "<a title=\"" + urlText + "\" href=\"" + urlText + "\" target=\"_blank\" class=\"out-link\">" + urlText + "</a>";
            String val = m.group();

            logger.info("매칭된 하위 문자열: \"" + val + "\"");

            logger.info("중복된 단어: " + m.group(1) + "\n");

            return linkText;
        }
        return urlText;
    }

    public static String subStringEnd(String text, int sliceLength) {
        if(text.length() <= sliceLength)
            return text;
        else
            return text.substring(0, text.length()-sliceLength);
    }

}
