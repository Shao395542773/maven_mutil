package util;

import org.springframework.stereotype.Component;
import utils.Base64Utils;
import utils.RSAUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成license
 * @author happyqing
 * 2014.6.15
 */
@Component
public class LicenseGenerator {


    /****
     *
     Product.name=产品名称
     Product.version=版本
     License.type=license类型
     License.expiry=license有效期
     Server.macaddress=mac地址
     signature=302d0215008b4ef2390e11d28f5ea7b86f71ba5168bfeaa6b402146c77706b985be68cc515c6079b31ad50e3f17552
     */

    /**
     * serial：由客户提供
     * timeEnd：过期时间
     */
    private static String licensestatic ="licenseID:00001;produnctName:ss;startTime:20171101;endTime:20171214;macaddress:40:A8:F0:33:BC:B4";

    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYEfnqPwdf+G7f/X8shdLtSBQ0N9vBzCI2rezq\n" +
            "ktFiIGQmIHeUp74yI8Ipej+8EBRsFVo5nBG4IhfrmJ4Jke2Xc8P4Qa8u59VGrA/hEdbiYKBLbL3q\n" +
            "OriBlXkd/3/AB9lk97Z96JEJuOyva0QcbbGOI5JP91C9nUQ+eEphEI/rxQIDAQAB";

    /**
     * RSA算法
     * 公钥和私钥是一对，此处只用私钥加密
     */
    public static final String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJgR+eo/B1/4bt/9fyyF0u1IFDQ3\n" +
            "28HMIjat7OqS0WIgZCYgd5SnvjIjwil6P7wQFGwVWjmcEbgiF+uYngmR7Zdzw/hBry7n1UasD+ER\n" +
            "1uJgoEtsveo6uIGVeR3/f8AH2WT3tn3okQm47K9rRBxtsY4jkk/3UL2dRD54SmEQj+vFAgMBAAEC\n" +
            "gYBwfwvkkFACXSSYq01DQJSzWFkp8BCwIzVEQhI7zfT7pH6x/d64qAb5zJ48VOWFWFfjRUCD0cMB\n" +
            "9qa/cR2ocr3R4oliwAuBz2hOPWmitq+NdCpJ6cLNkICnkcsrFvZZyQIU3ILXBbmF9uJQZb4M6oN0\n" +
            "jsPgs+4CHin4tRlvM1S8wQJBAMwCICzpZ94O029M15a6gxVd81ElTTZwNuDD6ieGeCoLGJHPkxwB\n" +
            "8GBZwgWu4x0GI9N1p3/WkxZYG0evuzhAE+kCQQC+01CqUfW+M+mvz1bo4GMeUIQfy6VU4/R53ubY\n" +
            "cP3yCmu1CMmdVu2/5cd5K6VY56hwnYAdl45bDZl2kUS3A7t9AkEAipKXEGqqpucjL2Levf+KsqTS\n" +
            "dbgEromifSIWlyp7zPMkiUaaAlg+0vOlGv8kPb7B7wzn7anu1yqZvSPc4+SCoQJAIU3kMDWpvA53\n" +
            "ZJW9R1uSMQJAbQQv0McQLgz3ISNnTsz+y1PDTFCVof5KeB1j+EZGIZF7gK6oy79t30GvSnm+SQJA\n" +
            "KuOOilmuwIpkz85LnceG3AdUxXCvAR7W6rHvnQc46JalVqk5YpBKRvdTnAQD/B5FPI5KpHkMNkRS\n" +
            "hc4APEVR/Q==";

    public static void generator() throws Exception {
        System.err.println("私钥加密");
        System.out.println("原文字：\r\n" + licensestatic);
        byte[] data = licensestatic.getBytes();
        byte[] encodedData = RSAUtils.encryptByPrivateKey(data, privateKey);
        String encode = Base64Utils.encode(encodedData);
        System.out.println("加密后：\r\n" + encode); //加密后乱码是正常的



        String sign=RSAUtils.sign(encode.getBytes(),privateKey);
        System.out.println("数字证书："+sign);
        ProValueUtil.WriteProperties(FileUtil.getBasePath()+File.separator+"publicKey.properties","publicKey",publicKey);
        ProValueUtil.WriteProperties(FileUtil.getBasePath()+File.separator+"publicKey.properties","sign",sign);

        String licenseText="data:"+encode+";publicKey:"+publicKey+";signature:"+sign;

        Base64Utils.byteArrayToFile(licenseText.getBytes(), FileUtil.getBasePath()+File.separator+"license.dat");


    }


    /**
     * license 文件校验
     * @throws Exception
     */
    public static void chkGenerator() throws Exception {
        System.err.println("公钥解密");

        byte[] licenseBase64 = Base64Utils.fileToByte(FileUtil.getBasePath() + File.separator + "license.dat");
        Map<String, String> stringStringMap = licenseToMap(new String(licenseBase64));


        byte[] licenseArray = Base64Utils.decode(stringStringMap.get("data").toString());

        String pk = stringStringMap.get("publicKey").toString();
        String sign = stringStringMap.get("signature").toString();
        System.out.println(sign);
        String data = stringStringMap.get("data").toString();
        System.out.println(data);
        //验证数字证书
        boolean verify = RSAUtils.verify(stringStringMap.get("data").toString().getBytes(), pk, sign);
        System.out.println(verify);

        //解密
        byte[] bytes = RSAUtils.decryptByPublicKey(licenseArray, publicKey);
        System.out.println(new String(bytes));

    }




    public static void main(String[] args) throws Exception {
        generator();
        chkGenerator();


        //System.out.print(DateUtil.getSdfDay(new Date(1404057600000l)));
    }


    /***
     *  将解密后的数据转成MAP结构    eg:  name=xxxxx;version=xxxxx
     * @param target
     * @return
     */
    public static Map<String,String> licenseToMap(String target){
        String[] split = target.split(";");
        Map<String,String> license= new HashMap<>();
        for(String s: split){
            String[] split1 = s.split(":");
            license.put(split1[0],split1[1]);
        }
        for(Map.Entry entry:license.entrySet()){
            System.out.println(entry.getKey()+" "+entry.getValue());
        }
        return license;
    }
}