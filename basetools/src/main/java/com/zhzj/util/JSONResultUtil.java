package com.zhzj.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bjut_zjh on 2016/12/3.
 */
public class JSONResultUtil {
    public static int SUCCESS_NUM = 0;
    public static String SUCCESS_MSG = "成功";

    public static int ERR_NUM = 1;
    public static String ERR_MSG = "接口错误";

    public static JSONObject getResultJSON(int num, String msg){
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("result", num);
        map.put("msg", msg);
        try {
            jsonObject.put("result", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }



    public static String getResultJSONP(JSONObject obj){

        return  "Operation.ajax_handresponse("+obj.toString()+")";
    }
}
