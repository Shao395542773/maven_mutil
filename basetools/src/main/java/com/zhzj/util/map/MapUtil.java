package com.zhzj.util.map;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 地图经纬度工具类
 * Created by bjut_zjh on 2016/12/2.
 */
public class MapUtil {


    public static double DegreeToHuDu(double degree) {
        return degree * Math.PI / 180.0;
    }

    static double m_EarthR = 6378137.0;//地球半径近似

    // 计算两个经纬度坐标点的距离
    // 返回值的单位为米
    public static double DistanceOfTwoLonLatPoints(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = DegreeToHuDu(lat1);
        double radLat2 = DegreeToHuDu(lat2);
        double a = radLat1 - radLat2;
        double b = DegreeToHuDu(lng1) - DegreeToHuDu(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * m_EarthR;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**
     * 判断视野级别确定珊格等级
     *
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return
     */
    public static int getLevel(double lng1, double lat1, double lng2, double lat2) {
        double distance = DistanceOfTwoLonLatPoints(lng1, lat1, lng2, lat2) / 1000;
//        if (distance <= 14.4) {
//            return 1;  //250
//        } else if (distance > 14.4 && distance < 28.8) {
//            return 2;  //500
//        } else {
//            return 3;  //2000
//        }

        if (distance <= 28.8) {
            return 2;  //500
        } else {
            return 3;  //2000
        }
    }

    public static double ConvertMetterToDegreeLatitudeEX(double meters) {
        return meters / (2 * Math.PI * m_EarthR) * 360;
    }

    public static double ConvertMetterToDegreeLongitudeEX(double meters, double avgLatitude) {
        avgLatitude = DegreeToHuDu(avgLatitude);
        return meters / (2 * Math.PI * m_EarthR * Math.cos(avgLatitude)) * 360;
    }

    public static void main(String[] args) {
        System.out.println(DistanceOfTwoLonLatPoints(115.278, 42.198, 117.792, 38.009) / 1000);
    }

    public static Map<String, Double> addBuffer(String[] split) throws Exception {
        Map<String, Double> bufferMap = new HashMap<>();
        Double lon1 = new JSONObject(split[0]).getDouble("lon");
        Double lat1 = new JSONObject(split[0]).getDouble("lat");
        Double lon2 = new JSONObject(split[1]).getDouble("lon");
        Double lat2 = new JSONObject(split[1]).getDouble("lat");

        if (lat1 > lat2) {
            bufferMap.put("lat1", lat1 + Math.abs(MapUtil.ConvertMetterToDegreeLatitudeEX(lat1)));
            bufferMap.put("lat2", lat2 - Math.abs(MapUtil.ConvertMetterToDegreeLatitudeEX(lat2)));
        } else {
            bufferMap.put("lat1", lat1 - Math.abs(MapUtil.ConvertMetterToDegreeLatitudeEX(lat1)));
            bufferMap.put("lat2", lat2 + Math.abs(MapUtil.ConvertMetterToDegreeLatitudeEX(lat2)));
        }

        double mid = (lon2 + lon1) / 2;
        if (lon1 > lon2) {
            bufferMap.put("lon1", lon1 + Math.abs(MapUtil.ConvertMetterToDegreeLongitudeEX(lon1, mid)));
            bufferMap.put("lon2", lon2 - Math.abs(MapUtil.ConvertMetterToDegreeLongitudeEX(lon2, mid)));
        } else {
            bufferMap.put("lon1", lon1 - Math.abs(MapUtil.ConvertMetterToDegreeLongitudeEX(lon1, mid)));
            bufferMap.put("lon2", lon2 + Math.abs(MapUtil.ConvertMetterToDegreeLongitudeEX(lon2, mid)));
        }
        return bufferMap;
    }

    public static int getThreeOdLevel(JSONArray area) throws Exception {
        Double lon1 = Double.MAX_VALUE;
        Double lat1 = Double.MAX_VALUE;
        Double lon2 = Double.MIN_VALUE;
        Double lat2 = Double.MIN_VALUE;
        for (int i = 0; i < area.length(); i++) {
            Double lon = area.getJSONObject(i).getDouble("lon");
            Double lat = area.getJSONObject(i).getDouble("lat");
            if (lon < lon1) {
                lon1 = lon;
            }

            if (lon > lon2) {
                lon2 = lon;
            }

            if (lat < lat1) {
                lat1 = lat;
            }

            if (lat > lat2) {
                lat2 = lat;
            }
        }
        return MapUtil.getLevel(lon1, lat1, lon2, lat2);
    }

    public static String getThreeOdGeometry(JSONArray area) throws Exception {
        JSONObject jsonObject = new JSONObject();
        int len = area.length();
        Double[][][] dousOne = new Double[1][len + 1][2];
        jsonObject.put("spatialReference", new JSONObject().put("wkid", 4326));
        for (int i = 0; i < area.length(); i++) {
            dousOne[0][i][0] = area.getJSONObject(i).getDouble("lon");
            dousOne[0][i][1] = area.getJSONObject(i).getDouble("lat");
        }
        dousOne[0][len][0] = area.getJSONObject(0).getDouble("lon");
        dousOne[0][len][1] = area.getJSONObject(0).getDouble("lat");
        jsonObject.put("rings", dousOne);
        return jsonObject.toString();
    }
}
