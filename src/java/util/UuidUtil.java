package util;

import java.util.UUID;

/**
 * UUID工具
 * @author 张京辉
 * 创建时间:2015/9/9
 * 更新时间:
 */

public class UuidUtil {

	public static String get32UUID() {
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}
	public static void main(String[] args) {
		System.out.println(get32UUID());
	}
}

