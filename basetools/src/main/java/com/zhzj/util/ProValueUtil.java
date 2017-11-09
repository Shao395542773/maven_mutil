package com.zhzj.util;

import java.io.*;
import java.util.Properties;

/**
 * Created by bjut_zjh on 2016/9/22.
 */
public class ProValueUtil {
    private static Properties p = new Properties();

    public static Properties loadProperties(String fileName) {
        InputStream inputStream = ProValueUtil.class.getClassLoader()
                .getResourceAsStream(fileName);
        try {
            p.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            // 读取配置文件出错
            e.printStackTrace();
        }
        return p;
    }


    public static void WriteProperties (String filePath, String pKey, String pValue) throws IOException {
                 File f= new File(filePath);
                 if (!f.exists())
                     f.createNewFile();
                 Properties pps = new Properties();

                 InputStream in = new FileInputStream(filePath);
                 //从输入流中读取属性列表（键和元素对）
                 pps.load(in);
                 //调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
                 //强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
                 OutputStream out = new FileOutputStream(filePath);
                 pps.setProperty(pKey, pValue);
                 //以适合使用 load 方法加载到 Properties 表中的格式，
                 //将此 Properties 表中的属性列表（键和元素对）写入输出流
                 pps.store(out, "Update " + pKey + " name");
    }
}
