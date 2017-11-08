package utils;

import util.DateUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Scanner;

public class RSATester {

    static String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJRd8up4xG/QEtv70V4pxlFXJ0fAqoHareeol7" +
            "UlW9/cIGFxWql6hMmRPjM4rsKH83vi7GaMpkEaV37EVpje84R4Y2waBtqt72qAPpYgIKokMj8gVw" +
            "BrqVBK/rseD0PlAmNpq3ZwJALngYkB9tvda4S2+ALvPVXkLzzGSxlHPR0wIDAQAB" +
            "BrqVBK/rseD0PlAmNpq3ZwJALngYkB9tvda4S2+ALvPVXkLzzGSxlHPR0wIDAQAB";
    static String privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIlF3y6njEb9AS2/vRXinGUVcnR8" +
            "Cqgdqt56iXtSVb39wgYXFaqXqEyZE+Mziuwofze+LsZoymQRpXfsRWmN7zhHhjbBoG2q3vaoA+li" +
            "AgqiQyPyBXAGupUEr+ux4PQ+UCY2mrdnAkAueBiQH2291rhLb4Au89VeQvPMZLGUc9HTAgMBAAEC" +
            "gYBYi+uUyprfx5+VBRNRJKXBRn5XyROOseRgMiawl8GbFi6rgwSEz+LN1ghQDBVWlCDLsi+16C51" +
            "MjTMEGoN1nIxoRknH25ZjfXjRwYZPBG87/I9XOTGzqWtU6wkPri8xU48u5UzAXl5meSa9JCRIEvf" +
            "MCDojyzmHN7woUZkHdoqAQJBAOv+NKU7ud7/XmRYqLl1LJ8gWcdeo1d5SlKyEXVz1V+TwBCLkV13" +
            "iy29PWbIpu7bLXu28N90K6VqxTZfdlFgHoECQQCU6SD0jMLWQZ4SgyZjOv/wOQfRt1uUQXMK4YAD" +
            "NKwQs6RnFgY6flKWw/VyySi8kS63QVSU4F1+b8Wmp8h67u5TAkEAyekNEfIv7WOAV0qj0Uk3jE4Z" +
            "gkKubkEQHgDMfzlD7ipzwbQoA1gsdE3dIvu6F9Le72JuZED4tubLJOI4URd0AQJAC05oIilN4da5" +
            "mc47a+1e4W5zGYlCes89XOYiQhwNtHbvVKRndKmCu8/FIyIC5c5gLanYMjWW47K43x5+KldZ4QJB" +
            "AL5AuX+W2X/bMSjlhRbFUYwRlHVDJMoU5Xu+eApNgwiXTwwH285HJIC4dHBul0P0071NM7itD+ga" +
            "2i6MibjP7nk=";

//    static {
//        try {
//            Map<String, Object> keyMap = RSAUtils.genKeyPair();
//            publicKey = RSAUtils.getPublicKey(keyMap);
//            privateKey = RSAUtils.getPrivateKey(keyMap);
//            System.err.println("公钥: \n\r" + publicKey);
//            System.err.println("私钥： \n\r" + privateKey);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) throws Exception {
        //test();
        //testSign();
        try {
            long start = System.currentTimeMillis();
            Process process = Runtime.getRuntime().exec(
                    new String[] { "ifconfig","|"," grep","en0"});
            process.getOutputStream().close();

            Scanner sc = new Scanner(process.getInputStream());

            while (sc.hasNext()){
                String property = sc.next();
                String serial = sc.next();
                System.out.println(property + ": " + serial);
            }

            //URL url= new URL("http://api.avatardata.cn/BeijingTime/LookUp?key=82cf1cbc948a482bb75540dae57e3f5f");
            URL url= new URL("http://www.baidu.com");
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            long date  = urlConnection.getDate();


            String sdfDay = DateUtil.getSdfDay(new Date(date));
            System.out.println(sdfDay);
//            InputStream inputStream = urlConnection.getInputStream();
//            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
//            byte [] len=new byte[1024];
//            int size=-1;
//            while ((size=inputStream.read(len))!=-1){
//                outputStream.write(len,0,size);
//            }
//            inputStream.close();
//            outputStream.close();
            //System.out.println(new String(outputStream.toByteArray()));


//            System.out.println("time:" + (System.currentTimeMillis() - start));
//            String os = System.getProperty("os.name").toLowerCase();
//            System.out.println(os);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
      //  String cpuSerial = getMotherboardSN();
       // System.out.println(cpuSerial);
    }

    static void test() throws Exception {
        System.err.println("公钥加密——私钥解密");
        String source = "ssssssss";
        System.out.println("\r加密前文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = RSAUtils.encryptByPublicKey(data, publicKey);
        System.out.println("加密后文字：\r\n" + new String(encodedData));
        byte[] decodedData = RSAUtils.decryptByPrivateKey(new String(encodedData).getBytes(), privateKey);
        String target = new String(decodedData);
        System.out.println("解密后文字: \r\n" + target);
    }

    static void testSign() throws Exception {
//        System.err.println("私钥加密——公钥解密");
//        String source = DateUtil.getDays();
//        System.out.println("原文字：\r\n" + source);
//        byte[] data = source.getBytes();
//        byte[] encodedData = RSAUtils.encryptByPrivateKey(data, privateKey);
//        System.out.println("加密后：\r\n" + new String(encodedData));
//        byte[] decodedData = RSAUtils.decryptByPublicKey(encodedData, publicKey);
//        String target = new String(decodedData);
//        System.out.println("解密后: \r\n" + target);
//        System.err.println("私钥签名——公钥验证签名");
//        String sign = RSAUtils.sign(encodedData, privateKey);
//        System.err.println("签名:\r" + sign);
        //boolean status = RSAUtils.verify(encodedData, publicKey, sign);
        String sign = RSAUtils.sign(Base64Utils.decode("erO4kAzEG6ZaHntXANsgw0IcccqOdumeiTJUD1jnhU7z1Wht4Ik5bLKx7SB3ScelcN6E0ByJOCh3kto0NJPZCpzJRYRV2JRZaptLIA+dW1UqgsgiDhBG3yQi+mVTgZEVSCwpZB/6xemP+0IvNDv21ZjQvzHGFJJRf4wddY6FEHM="), "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJaM8kW9IwYEB6q1YWF/LNLqbjsq\n" +
                "ZIIeIoIHO2DusnzPqcbfBxaVB3/l1Ip139OdlnIPPNdLELgn0U+YTU0YOHcjJfJISEtupSfCxZp9\n" +
                "wyswlg9SNAXuWh3WVJMAA9tklbcP6k1wiJhlObNJRhIURzoo7Ay8D9J3ee5lmcFDwbqjAgMBAAEC\n" +
                "gYBYk77rlzdnD7HvF16fPXMC+Vk3yF5wTAijlluyUV6Enq3WS1xhRfeuoWumt2mkmSBuJVb3mLvS\n" +
                "jeLRwNsiU/DMCHodkTFAkRmRiWtjgvgLBIlGajI96WKWHWsf9R3o3rFsiWQVu0hqJ9+8cOlocnsq\n" +
                "/vBflv+7YQhmnGmJRudDqQJBAM6hSkwiLv5ZRaFDFH4PGmpGkmdSbnIUZL+dx3ef3/jbPjzlWpEq\n" +
                "myhhT1/Qh2XDMs9CRaL5ccXXmbYtCylskSUCQQC6hYF2YJcucQgsZ73P4LG0VhghtbZz+goqk62O\n" +
                "J3xsMqVrvik35/6u37ReKeEZE7sDJUhmboKg1qSrLLLOkcYnAkAeEaifNaZDCzivw/Q6d/YvGv/5\n" +
                "GyzJWyBiJhK93L0aGMWQ1Qt97oZPtQF4/v6FF4fQU2OQ2N3unXZX5WwnZg4lAkBzOOf53b9mxBKd\n" +
                "g9Sg5xg/4/NyV5OHX44KMUJEj6XwHPVNcqJ508nj83Ozu8E/QY4EanKoPFpby1zcFXe73hd/AkB3\n" +
                "RsrPKKdqJkSjj/TIMUxKA8fnBOBIH3fc1K1AuwtulMFLl/lgDABfcdqrryp+tO9cYflWWg70gQKN\n" +
                "VranD3Wk");
        System.out.print(sign);
        boolean status = RSAUtils.verify(Base64Utils.decode("erO4kAzEG6ZaHntXANsgw0IcccqOdumeiTJUD1jnhU7z1Wht4Ik5bLKx7SB3ScelcN6E0ByJOCh3kto0NJPZCpzJRYRV2JRZaptLIA+dW1UqgsgiDhBG3yQi+mVTgZEVSCwpZB/6xemP+0IvNDv21ZjQvzHGFJJRf4wddY6FEHM="),
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWjPJFvSMGBAeqtWFhfyzS6m47KmSCHiKCBztg7rJ8z6nG3wcWlQd/5dSKdd/TnZZyDzzXSxC4J9FPmE1NGDh3IyXySEhLbqUnwsWafcMrMJYPUjQF7lod1lSTAAPbZJW3D+pNcIiYZTmzSUYSFEc6KOwMvA/Sd3nuZZnBQ8G6owIDAQAB",
                "bbV1CcW50W43u6q3XD5PSHhL4Dr9ANyHJ7wDZAtyhPrGXSwcpqzCVI0E2tNhFm3QvVKoKVQ+v+K/\n" +
                        "F0McfLvCwyCp97b2/fXb6ofz7lk8SdYfxORWROygAz7JTNham24B0BQVu6Em+O3Vjdm1P0tDSnY4\n" +
                        "/9lZkOGSV0tkPcwEwQM=");
        byte[] decodedData = RSAUtils.decryptByPublicKey(Base64Utils.decode("erO4kAzEG6ZaHntXANsgw0IcccqOdumeiTJUD1jnhU7z1Wht4Ik5bLKx7SB3ScelcN6E0ByJOCh3kto0NJPZCpzJRYRV2JRZaptLIA+dW1UqgsgiDhBG3yQi+mVTgZEVSCwpZB/6xemP+0IvNDv21ZjQvzHGFJJRf4wddY6FEHM="), "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWjPJFvSMGBAeqtWFhfyzS6m47KmSCHiKCBztg7rJ8z6nG3wcWlQd/5dSKdd/TnZZyDzzXSxC4J9FPmE1NGDh3IyXySEhLbqUnwsWafcMrMJYPUjQF7lod1lSTAAPbZJW3D+pNcIiYZTmzSUYSFEc6KOwMvA/Sd3nuZZnBQ8G6owIDAQAB");
        String target = new String(decodedData);
        System.out.println("解密后: \r\n" + target);
        System.err.println("验证结果:\r" + status);
    }

    public static String getCPUSerial() {
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n"
                    + "   (\"Select * from Win32_Processor\") \n"
                    + "For Each objItem in colItems \n"
                    + "    Wscript.Echo objItem.ProcessorId \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";

            // + "    exit for  \r\n" + "Next";
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec(
                    "cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
            file.delete();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        if (result.trim().length() < 1 || result == null) {
            result = "无CPU_ID被读取";
        }
        return result.trim();
    }

    public static String getMotherboardSN() {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new FileWriter(file);

            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n"
                    + "   (\"Select * from Win32_BaseBoard\") \n"
                    + "For Each objItem in colItems \n"
                    + "    Wscript.Echo objItem.SerialNumber \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec(
                    "cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.trim();
    }

} 