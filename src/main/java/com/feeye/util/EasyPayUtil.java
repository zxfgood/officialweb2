package com.feeye.util;

import java.util.HashMap;
import java.util.Map;

public class EasyPayUtil {
	private final static String[] keys00 = { "respcode", "resmsg", "amount",
			"payment_complete_datetime", "buyer_account_balance",
			"out_trade_no", "trade_id", "return_url" };
	private final static String[] keys01 = { "respcode", "resmsg", "respmsg",
			"out_trade_no" };

	public static Map<String, String> convertToMap(String src) {
		if (null == src) {
			return null;
		}
		Map<String, String> result = new HashMap<String, String>();
		String respcode = src.substring(10,12);
		String[] keys = ("00".equals(respcode) ? keys00 : keys01);
		for (int i = 0; i < keys.length; i++) {
			if (i == keys.length - 1) {
				result.put(
						keys[i],
						src.substring(src.indexOf(keys[i] + ":")
								+ (keys[i] + ":").length(), src.length() - 1));
			} else {
				result.put(
						keys[i],
						src.substring(src.indexOf(keys[i] + ":")
								+ (keys[i] + ":").length(),
								src.indexOf("," + keys[i + 1] + ":")));
			}
		}
		return result;
	}
}
