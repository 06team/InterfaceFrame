package com.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class JsonUtil {

    /**
     * 网上找的
     * @param responseJson,这个变量是拿到响应字符串通过json转换成json对象
     * @param jpath,这个jpath指的是用户想要查询json对象的值的路径写法
     * jpath写法举例：1) per_page  2)data[1]/first_name ，data是一个json数组，[1]表示索引
     * /first_name 表示data数组下某一个元素下的json对象的名称为first_name
     * @return，返回first_name这个json对象名称对应的值
     */


    //1 json解析方法
    public static String getValueByJPath(JSONObject responseJson, String jpath) {
        Object obj = responseJson;
        //JSONArray js2= JSON.parseArray(responseJson.getString("data").toString());
        for (String s : jpath.split("/")) {
            //System.out.println("split:"+s);
            if (!s.isEmpty()) {
                if (!(s.contains("[") || s.contains("]"))) {
                    obj = ((JSONObject) obj).get(s);
                    //System.out.println("zhi"+obj);
                } else if (s.contains("[") || s.contains("]")) {
                    //s=s.split("\\[")[0];
                    //System.out.println("第一步s："+s.split("\\[")[0]);
                    obj=((JSONObject)obj).get(s.split("\\[")[0]);
                    //System.out.println("f:"+obj);
                    //System.out.println(s.split("\\["));
                    //s=s.split("\\[")[1];
                   // System.out.println("第二次："+s);
                    s=s.split("\\[")[1].replaceAll("]", "");
                    //System.out.println("s:"+s);
                    obj = ((JSONArray)obj).get(Integer.parseInt(s));
                    //obj =((JSONArray)((JSONObject)obj).get(s.split("\\[")[0])).get(Integer.parseInt(s.split("\\[")[1].replaceAll("]", "")));

                }
            }
        }
        //System.out.println(obj.toString());

        return obj.toString();
        }
}

