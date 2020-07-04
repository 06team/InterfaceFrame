package com.cases;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.utils.HttpUtils;
import com.utils.JsonUtil;
import com.utils.ResponesData;
import com.utils.loadExcelFile;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class demo {
    final static Logger Log = Logger.getLogger(demo.class);

    //excel表格信息
    @DataProvider(name = "excelData")
    public Object[] exceldata() throws Exception {
        String filepath = "\\testdata\\inter_test_data.xlsx";
        loadExcelFile lefc = new loadExcelFile();
        List<Map<String, String>> xlsxData = new ArrayList<Map<String, String>>();
        xlsxData = loadExcelFile.getExcuteListMap(filepath, "注册接口用例");
        System.out.println(xlsxData);
        return xlsxData.toArray();
    }


    @Test(dataProvider = "excelData")
    public void Test(Map<String, String> map) throws Exception {
        Log.info("开始执行测试用例");
        System.out.println("map是" + map);
        String url = map.get("requesturl");
        String reqdata = map.get("RequestData");
        System.out.println("请求参数" + reqdata);
        //JSONObject requestdata = JSON.parseObject(reqdata);
        System.out.println(reqdata.getClass().getName());
        //  System.out.println(url + requestdata);
        String responsecode = map.get("ResponseCode");
        System.out.println(responsecode);
        HttpUtils hu = new HttpUtils();
        ResponesData responesData=null;
        //Assert.assertEquals(map.get("RequestMethod"),"post");
        //System.out.println(map.get("RequestMethod"));
        if (map.get("RequestMethod").equals("post")) {
            System.out.println("post执行");
            responesData = hu.doPost(url, reqdata);
        }else if (map.get("RequestMethod").equals("get")){
            System.out.println("get执行");
            responesData = hu.doGet(url, JSONObject.parseObject(reqdata));
        }
        System.out.println(responesData);
        try {
            Assert.assertEquals(responesData.getStatus(), 200);
        } catch (Exception e) {
            Log.error(e);
            System.out.println(e);
        } finally {
            System.out.println(responesData.getBody());
            String checkpoint = map.get("CheckPoint");
            System.out.println("checkpoint" + checkpoint);
            System.out.println(JSON.parseObject(checkpoint).get("code"));
            JSONObject json = JSON.parseObject(checkpoint);
            System.out.println(json.keySet());
            for (String s : json.keySet()) {
                Object s1 = json.get(s);
                String value = JsonUtil.getValueByJPath(JSON.parseObject(responesData.getBody()), s);
                System.out.println(s + ":" + s1 + "  " + value);
                Assert.assertEquals(s1.toString(), value);

            }

        }


        // hu.doPost(url,requestdata);
    }


    public static void main(String[] args) {

    }


}
