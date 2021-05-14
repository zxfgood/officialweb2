package com.feeye.test;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class TestDllFile {
	public interface MyDLL extends Library {
		MyDLL instance = (MyDLL) Native.loadLibrary("Md", MyDLL.class);

		String getMD5();
		String getDES();
		String getDLProxyInfo();
		String getABProxyInfo();

	}

	/**
	 * 
	 * @param args
	 * 
	 */

	public static void main(String[] args) {
		System.out.println(new String(MyDLL.instance.getMD5()));
		System.out.println(new String(MyDLL.instance.getDES()));
		System.out.println(new String(MyDLL.instance.getDLProxyInfo()));
		System.out.println(new String(MyDLL.instance.getABProxyInfo()));

	}
}
