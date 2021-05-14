package com.feeye.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;



public class FingerPrintUtil {
	private static Logger logger = Logger.getLogger(FingerPrintUtil.class);
	public static AtomicLong fingerid = new AtomicLong(0);
	public static AtomicLong zerocount = new AtomicLong(0);
	static{
		if (fingerid.get() == 0) {
			String finger = PropertiesUtils.getPropertiesValue("config", "fingerid");
			if (finger != null) {
				fingerid.set(Long.parseLong(finger));
			}
		}
	}
	/**
	 * 自增fingerid
	 */
	public static void addFingerid(){
		fingerid.incrementAndGet();
		logger.info("当前fingerid值为:"+fingerid);
	}

	/**
	 * 自增fingerid
	 */
	public static synchronized Long addFinger_id(){
		return fingerid.incrementAndGet();
	}
	/**
	 * 当结果为0的请求累计超过20时，
	 * 自增一次fingerid
	 */
	public static void addZeroCount(){
		zerocount.incrementAndGet();
		if(zerocount.get()==5){
			addFingerid();
			zerocount = new AtomicLong(0);
		}
	}
	public static void setZeroCount(){
		zerocount = new AtomicLong(0);
	}
	public static String getDesc(int i){
		String desc = "";
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");
			//String path = "E:\\代码\\eclipse_workspaces\\proxy\\src\\main\\resources\\jrservice.js";
			String path = FingerPrintUtil.class.getResource("/").getPath()+"jrservice.js";
			FileInputStream fileInputStream = new FileInputStream(new File(path));
			Reader reader = new InputStreamReader(fileInputStream, "utf-8");

//			String jsFileName = "jrservice.js";   			  // 读取js文件
//			FileReader reader = new FileReader(jsFileName);   // 执行指定脚本
			engine.eval(reader);

			if(engine instanceof Invocable) {
				Invocable invoke = (Invocable)engine;    		// 调用merge方法，并传入两个参数
				desc = (String) invoke.invokeFunction("encryptByDESModeCBC", new Object[]{fingerid});
				if(StringUtils.isEmpty(desc)){			 		// 可再次获取一次
					desc = (String) invoke.invokeFunction("encryptByDESModeCBC", new Object[]{fingerid});
				}
			}
			reader.close();
			fileInputStream.close();
			return desc;
//			return urlDeco(desc);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (ScriptException e) {
			logger.error(e.getMessage(),e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}
	public static String getDesc(Object ...equparams){
		String desc = "";
		try {
			//String content = "{\"xy\":[\"{\"\"x\"\":614,\"\"y\"\":230,\"\"t\"\":"+new Date().getTime()+"}\"],\"fingerprint\":"+finger_id+"}";
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");
			String path = FingerPrintUtil.class.getResource("/").getPath()+"ex_mouse.js";
			FileInputStream fileInputStream = new FileInputStream(new File(path));
			Reader reader = new InputStreamReader(fileInputStream, "utf-8");
			engine.eval(reader);

			if(engine instanceof Invocable) {

				Invocable invoke = (Invocable)engine;
				desc = (String) invoke.invokeFunction("eq_u", equparams);
			}
			reader.close();
			fileInputStream.close();
			return desc;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (ScriptException e) {
			logger.error(e.getMessage(),e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}
	public static String getDesc(Long finger_id){
		String desc = "";
//		try {
//			String content = "{\"xy\":[\"{\"\"x\"\":614,\"\"y\"\":230,\"\"t\"\":"+new Date().getTime()+"}\"],\"fingerprint\":"+finger_id+"}";
//			ScriptEngineManager manager = new ScriptEngineManager();
//			ScriptEngine engine = manager.getEngineByName("javascript");
//			String path = FingerPrintUtil.class.getResource("/").getPath()+"ex_mouse.js";
//			FileInputStream fileInputStream = new FileInputStream(new File(path));
//			Reader reader = new InputStreamReader(fileInputStream, "utf-8");
//			engine.eval(reader);
//
//			if(engine instanceof Invocable) {
//
//				Invocable invoke = (Invocable)engine;
//				desc = (String) invoke.invokeFunction("eq_u", new Object[]{content,"52D2841A3485DFFBCF2EA6A0515077CD"});
//			}
//			reader.close();
//			fileInputStream.close();
//			return desc;
//		} catch (FileNotFoundException e) {
//			logger.error(e.getMessage(),e);
//		} catch (ScriptException e) {
//			logger.error(e.getMessage(),e);
//		} catch (NoSuchMethodException e) {
//			logger.error(e.getMessage(),e);
//		} catch (IOException e) {
//			logger.error(e.getMessage(),e);
//		}
//		return "";
		try {
			String path = FingerPrintUtil.class.getResource("/").getPath()+"ex_mouse.js";
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine jsExectour = manager.getEngineByName("javascript");
			jsExectour.put("fingerprint", finger_id);
			//logger.info("finger_id"+finger_id);
			jsExectour.eval(new FileReader(new File(path)));
			desc = (String) jsExectour.get("ciphertext");
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (ScriptException e) {
			logger.error(e.getMessage(),e);
		}
		return desc;

	}

	public static String getDesc(){
		String desc = "";
		try {
			
			Random r = new Random();
			int x = r.nextInt(1000);
			int y = r.nextInt(1000);
//			String content = "{\"xy\":[\"{\"\"x\"\":614,\"\"y\"\":230,\"\"t\"\":"+new Date().getTime()+"}\"],\"fingerprint\":"+fingerid+"}";
			String content = "{\"xy\":[\"{\\\"x\\\":"+x+",\\\"y\\\":"+y+",\\\"t\\\":"+new Date().getTime()+"}\"],\"fingerprint\":"+fingerid+"}"; //新版
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");
			String path = FingerPrintUtil.class.getResource("/").getPath()+"ex_mouse.js";
			path = URLDecoder.decode(path, "utf-8");
			FileInputStream fileInputStream = new FileInputStream(new File(path));
			Reader reader = new InputStreamReader(fileInputStream, "utf-8");
			engine.eval(reader);

			if(engine instanceof Invocable) {

				Invocable invoke = (Invocable)engine;
				desc = (String) invoke.invokeFunction("eq_u", new Object[]{content,"52D2841A3485DFFBCF2EA6A0515077CD"});
			}
			//fingerid.incrementAndGet();
			reader.close();
			fileInputStream.close();
			return desc;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (ScriptException e) {
			logger.error(e.getMessage(),e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}

	
	
	public static String getCZpsw(String key, String psw){
		String desc = "";
		try {
			String expotent = "10001";
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");
			String path = FingerPrintUtil.class.getResource("/").getPath()+"rsa.js";
			path = URLDecoder.decode(path, "utf-8");
			FileInputStream fileInputStream = new FileInputStream(new File(path));
			Reader reader = new InputStreamReader(fileInputStream, "utf-8");
			engine.eval(reader);

			if(engine instanceof Invocable) {

				Invocable invoke = (Invocable)engine;
				desc = (String) invoke.invokeFunction("RSASetPublic", key,expotent,psw);
			
				//String psw = (String) invoke.invokeFunction("RSAEncrypt", desc);
				//String result = (String) invoke.invokeFunction("RSASetPublic", new Object[]{key,expotent});
			
			}
			//fingerid.incrementAndGet();
			reader.close();
			fileInputStream.close();
			return desc;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (ScriptException e) {
			logger.error(e.getMessage(),e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}
	
	public static String getPsw(String key,String pass){
		String desc = "";			
		try {
//			String content = "{\"xy\":[\"{\"\"x\"\":614,\"\"y\"\":230,\"\"t\"\":"+new Date().getTime()+"}\"],\"fingerprint\":"+fingerid+"}";
			//String content = "{\"xy\":[\"{\\\"x\\\":957,\\\"y\\\":671,\\\"t\\\":"+new Date().getTime()+"}\"],\"fingerprint\":"+fingerid+"}"; //新版
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");
			String path = FingerPrintUtil.class.getResource("/").getPath()+"rsa.js";
			path = URLDecoder.decode(path, "utf-8");
			FileInputStream fileInputStream = new FileInputStream(new File(path));
			Reader reader = new InputStreamReader(fileInputStream, "utf-8");
			engine.eval(reader);
			Invocable invoke = null ;
			if(engine instanceof Invocable) {
				invoke = (Invocable)engine;				
				desc = (String) invoke.invokeFunction("RSASetPublic", key,"AQAB",pass);
			}
			logger.info("psw:"+desc);
			reader.close();
			fileInputStream.close();
			return desc;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (ScriptException e) {
			logger.error(e.getMessage(),e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}
	
	
	
	
	 public static String getJDDesc(String fingerID){
			String desc = "";
			try {				
				Map<String,ScriptEngine> map = new HashMap<String,ScriptEngine>();
				String content = "{\"xy\":[],\"fingerprint\":"+fingerID+",\"prevent_cid\":\"c41cff33\"}";				
				ScriptEngine engine = map.get("getJDDesc");
				if(engine == null){					
					ScriptEngineManager manager = new ScriptEngineManager();
					engine = manager.getEngineByName("javascript");
					String path = FingerPrintUtil.class.getResource("/").getPath()+"JDDESC.js";
					path = URLDecoder.decode(path, "utf-8");
					//String path = "E:\\代码\\eclipse_workspaces\\proxy\\src\\main\\resources\\ex_mouse.js";
					FileInputStream fileInputStream = new FileInputStream(new File(path));  
					Reader reader = new InputStreamReader(fileInputStream, "utf-8");  
					engine.eval(reader);
					map.put("getJDDesc", engine);
				}
				
				if(engine instanceof Invocable) {
					
					Invocable invoke = (Invocable)engine;    		
					desc = (String) invoke.invokeFunction("eq_u", new Object[]{content,"52D2841A3485DFFBCF2EA6A0515077CD"});
				}
				fingerid.incrementAndGet();
				/*reader.close();
				fileInputStream.close();*/
				return desc;
			}catch (ScriptException e) {
				logger.error(e.getMessage(),e);
			} catch (NoSuchMethodException e) {
				logger.error(e.getMessage(),e);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			return "";
		}
	
	public static String getJson(String json){
		String desc = "";
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");
			String path = "E:\\代码\\eclipse_workspaces\\proxy\\target\\classes\\parser.js";
			FileInputStream fileInputStream = new FileInputStream(new File(path));
			Reader reader = new InputStreamReader(fileInputStream, "utf-8");

//			String jsFileName = "jrservice.js";   			  // 读取js文件
//			FileReader reader = new FileReader(jsFileName);   // 执行指定脚本
			engine.eval(reader);

			if(engine instanceof Invocable) {
				Invocable invoke = (Invocable)engine;    		// 调用merge方法，并传入两个参数
				desc = (String) invoke.invokeFunction("stringify", new Object[]{json});
				if(StringUtils.isEmpty(desc)){			 		// 可再次获取一次
					desc = (String) invoke.invokeFunction("stringify", new Object[]{json});
				}
			}
			reader.close();
			fileInputStream.close();
			return desc;
//			return urlDeco(desc);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (ScriptException e) {
			logger.error(e.getMessage(),e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}
	public static String urlDeco(String desc){
		try {
			desc = URLDecoder.decode(desc,"utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return desc;
	}

	//NS,getRid()
	public static String getRid(String param){
		String res = "";
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");
			String path = "C://ns/ns.js";
			FileInputStream fileInputStream = new FileInputStream(new File(path));
			Reader reader = new InputStreamReader(fileInputStream, "utf-8");

			engine.eval(reader);

			if(engine instanceof Invocable) {
				Invocable invoke = (Invocable)engine;    		// 调用merge方法，并传入两个参数
				res = (String) invoke.invokeFunction("getRid", new Object[]{param});
				if(StringUtils.isEmpty(res)){			 		// 可再次获取一次
					res = (String) invoke.invokeFunction("getRid", new Object[]{param});
				}
			}
			reader.close();
			fileInputStream.close();
			return res;
		} catch(Exception e) {
			logger.error(e);
		}
		return "";
	}

	public static String getStamp(){
		String res = "";
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");
			String path = "C://ns/ns.js";
			FileInputStream fileInputStream = new FileInputStream(new File(path));
			Reader reader = new InputStreamReader(fileInputStream, "utf-8");

			engine.eval(reader);

			if(engine instanceof Invocable) {
				Invocable invoke = (Invocable)engine;    		// 调用merge方法，并传入两个参数
				res = (String) invoke.invokeFunction("getStamp", new Object[]{});
				if(StringUtils.isEmpty(res)){			 		// 可再次获取一次
					res = (String) invoke.invokeFunction("getStamp", new Object[]{});
				}
			}
			reader.close();
			fileInputStream.close();
			return res;
		} catch(Exception e) {
			logger.error(e);
		}
		return "";
	}

	//写入av.js文件
	public static boolean inputAVJS(String content,int id){
		boolean falg = false;
		OutputStream out = null;
		try {
			if(!StringUtils.isEmpty(content)){
				content = content.replace("window.gen_aid=function(n){return eval(n).call();};})();", "");
				content = content.replace("(function(){", "");
			}
//			String path = FingerPrintUtil.class.getResource("/").getPath()+"av"+id+".js";
			String path = "C://ns/av"+id+".js";
//			FileInputStream fileInputStream = new FileInputStream(new File(path));
			File file = new File(path);
			if(!file.exists()){
				file.createNewFile();
			}
			out = new FileOutputStream(file);

			out.write(content.getBytes());
			falg = true;
		} catch(Exception e) {
			logger.error(e);
		}finally{
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return falg;
	}

	//获取V2Rid
	public static String getv2Rid(String rid,String stamp,int id){
		String res = "";
		String gg = "";
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");
			String path = "C://ns/ns.js";
			FileInputStream fileInputStream = new FileInputStream(new File(path));
			Reader reader = new InputStreamReader(fileInputStream, "utf-8");

//			String path1 = FingerPrintUtil.class.getResource("/").getPath()+"av"+id+".js";
			String path1 = "C://ns/av"+id+".js";
			FileInputStream fileInputStream1 = new FileInputStream(new File(path1));
			Reader reader1 = new InputStreamReader(fileInputStream1, "utf-8");

			engine.eval(reader);
			engine.eval(reader1);

			if(engine instanceof Invocable) {
				Invocable invoke = (Invocable)engine;    		// 调用merge方法，并传入两个参数
				res = (String) invoke.invokeFunction("getv2Rid", new Object[]{stamp});
				gg = (String) invoke.invokeFunction(res, new Object[]{});
				if(StringUtils.isEmpty(res)){			 		// 可再次获取一次
					res = (String) invoke.invokeFunction("getv2Rid", new Object[]{rid,stamp});
					gg = (String) invoke.invokeFunction(res, new Object[]{});
				}
			}
			reader.close();
			reader1.close();
			fileInputStream.close();
			fileInputStream1.close();
			return gg;
		} catch(Exception e) {
			logger.error(e);
		}
		return "";
	}

	/**
	 * 获取JR加密参数
	 * @return
	 */
	public static String getCiphertext(){
		String desc = "";
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");
			String path = FingerPrintUtil.class.getResource("/").getPath()+"jr.js";
			FileInputStream fileInputStream = new FileInputStream(new File(path));
			Reader reader = new InputStreamReader(fileInputStream, "utf-8");
			engine.eval(reader);

			if(engine instanceof Invocable) {
				Invocable invoke = (Invocable)engine;    		// 调用merge方法，并传入两个参数
				desc = (String) invoke.invokeFunction("getCiphertext1", new Object[]{});
				if(StringUtils.isEmpty(desc)){			 		// 可再次获取一次
					desc = (String) invoke.invokeFunction(""
							+ "", new Object[]{});
				}
			}
			reader.close();
			fileInputStream.close();
			return desc;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (ScriptException e) {
			logger.error(e.getMessage(),e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}


	/**
	 * 获取JR加密参数
	 * @return
	 */
	public static String getCiphertext2(){
		String desc = "";
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");
			String path = FingerPrintUtil.class.getResource("/").getPath()+"8y_ex_mouse.js";
			FileInputStream fileInputStream = new FileInputStream(new File(path));
			Reader reader = new InputStreamReader(fileInputStream, "utf-8");

			engine.eval(reader);
			engine.eval("var fpObj = new Fingerprint();");

			if(engine instanceof Invocable) {
				Invocable invoke = (Invocable)engine;    		// 调用merge方法，并传入两个参数
				desc = (String) invoke.invokeFunction("getCiphertext", new Object[]{});
				Object object=engine.get("fpObj");
				desc = (String) invoke.invokeMethod(object, "getCiphertext");
				if(StringUtils.isEmpty(desc)){			 		// 可再次获取一次
					desc = (String) invoke.invokeFunction("getCiphertext", new Object[]{});
				}
			}
			reader.close();
			fileInputStream.close();
			return desc;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (ScriptException e) {
			logger.error(e.getMessage(),e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}


	public static String getParam(String jsonParam){
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByExtension("js");
		FileReader reader = null;
		try {
			reader = new FileReader("compile.js");
			se.eval(reader);
			String param = null;
			if(se instanceof Invocable){
				Invocable invoke = (Invocable) se;
				param = (String) invoke.invokeFunction("compile",jsonParam);
			}
			return param;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void main(String[] args) {
//		System.out.println(getDesc(0));
//		System.out.println(getStamp());
		long timeStamp = 1532051175884l;//System.currentTimeMillis();
		System.out.println("timeStamp="+timeStamp);
		String json = "{\"abuild\":\"10107\",\"adultCount\":1,\"akey\":\"9E4BBDDEC6C8416EA380E418161A7CD3\",\"aname\":\"com.hnair.spa.web.standard\",\"atarget\":\"standard\",\"aver\":\"6.12.2\",\"childCount\":0,\"depDate\":\"2018-07-20\",\"did\":\"defualt_web_did\",\"dstCode\":\"SZX\",\"gtcid\":\"defualt_web_gtcid\",\"orgCode\":\"PEK\",\"schannel\":\"HTML5\",\"slang\":\"zh-CN\",\"slat\":\"slat\",\"slng\":\"slng\",\"sname\":\"Win32\",\"stime\":1532051175884,\"sver\":\"5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Mobile Safari/537.36\",\"szone\":-480,\"tripType\":1,\"cabins\":[\"Y\"]}";
		System.out.println(execHnjs(json,"","",""));
	}

	public static String execHnjs(String json,String type,String token,String secret){
		String code = "";
		try {
			Map<String,ScriptEngine> map = new HashMap<String,ScriptEngine>();
			ScriptEngine engine = map.get("hnCode");
			if(engine == null){
				
				ScriptEngineManager manager = new ScriptEngineManager();
				engine = manager.getEngineByName("javascript");
				String path = FingerPrintUtil.class.getResource("/").getPath()+"hnCode.js";
				path = URLDecoder.decode(path, "utf-8");
				//String path = "E:\\代码\\eclipse_workspaces\\proxy\\src\\main\\resources\\hnCode.js";
				FileInputStream fileInputStream = new FileInputStream(new File(path));  
				Reader reader = new InputStreamReader(fileInputStream, "utf-8");  
				engine.eval(reader);
				map.put("hnCode", engine);
			}
			
//			engine.eval("var fpObj = new Fingerprint();");
//			String object = "{\"contentType\":\"application/json\",\"currPath\":\"/book/query/start\",\"errorSelf\":true,\"params\":{\"adultCount\":1,\"childCount\":0,\"depDate\":\"2018-07-19\",\"dstCode\":\"PEK\",\"orgCode\":\"SZX\",\"tripType\":1},\"url\":\"https://app.hnair.com/mapp/webservice/v4/common/flight/list\",\"_data\":{\"common\":{\"sname\":\"Win32\",\"sver\":\"5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Mobile Safari/537.36\",\"schannel\":\"HTML5\",\"slang\":\"zh-CN\",\"did\":\"defualt_web_did\",\"stime\":1531814390741,\"szone\":-480,\"aname\":\"com.hnair.spa.web.standard\",\"aver\":\"6.12.2\",\"akey\":\"9E4BBDDEC6C8416EA380E418161A7CD3\",\"abuild\":\"10107\",\"atarget\":\"standard\",\"slat\":\"slat\",\"slng\":\"slng\",\"gtcid\":\"defualt_web_gtcid\"},\"data\":{\"tripType\":1,\"orgCode\":\"PEK\",\"dstCode\":\"SZX\",\"depDate\":\"2018-07-19\",\"cabins\":[\"Y\"],\"adultCount\":1,\"childCount\":0}}}";
//			String obj = "{\"tripType\":1,\"sname\":\"Win32\",\"sver\":\"5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Mobile Safari/537.36\",\"schannel\":\"HTML5\",\"slang\":\"zh-CN\",\"did\":\"defualt_web_did\",\"stime\":1531814390741,\"szone\":-480,\"aname\":\"com.hnair.spa.web.standard\",\"aver\":\"6.12.2\",\"akey\":\"9E4BBDDEC6C8416EA380E418161A7CD3\",\"abuild\":\"10107\",\"atarget\":\"standard\",\"slat\":\"slat\",\"slng\":\"slng\",\"gtcid\":\"defualt_web_gtcid\",\"adultCount\":1,\"childCount\":0,\"depDate\":\"2018-07-19\",\"dstCode\":\"PEK\",\"orgCode\":\"SZX\",\"cabins\":[\"Y\"]}";
			if(engine instanceof Invocable) {
				Invocable invoke = (Invocable)engine;    		// 调用merge方法，并传入两个参数
				code = (String) invoke.invokeFunction("encryt", new Object[]{json,type,token,secret});
//				Object object=engine.get("fpObj");
//				desc = (String) invoke.invokeMethod(object, "getCiphertext");
//				if(StringUtils.isEmpty(desc)){			 		// 可再次获取一次
//					desc = (String) invoke.invokeFunction("getCiphertext", new Object[]{});
//				}
			}
//			logger.info("执行函数返回的加密数据为:"+code);
//			reader.close();
//			fileInputStream.close();
//			return desc;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (ScriptException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return code;
	}
	

	public static String getCoreSessionId(){
		String param = "";
	
        try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");
			String path = FingerPrintUtil.class.getResource("/").getPath()+"zh.js";
			//String path = "E:\\代码\\eclipse_workspaces\\proxy\\8LNew.js";
			FileInputStream fileInputStream = new FileInputStream(new File(path));  
			Reader reader = new InputStreamReader(fileInputStream, "utf-8");  
			engine.eval(reader);   
	
			Random r = new Random();
			int aa = r.nextInt(1000);
			int bb = r.nextInt(1440);
			if(engine instanceof Invocable) {
				Invocable invoke = (Invocable)engine;    		// 调用merge方法，并传入两个参数
				param = (String) invoke.invokeFunction("getCoreSessionId", new Object[]{aa+"",bb+""});
			}
//			reader.close();
//			fileInputStream.close();
			return param;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
        return "";
	}

	public static String getDidStr(String content) {
		String desc = "";
		try {
			Map<String,ScriptEngine> map = new HashMap<String,ScriptEngine>();
			ScriptEngine engine = map.get("getDidStr");
			if(engine == null){
				
				ScriptEngineManager manager = new ScriptEngineManager();
				engine = manager.getEngineByName("javascript");
				String path = FingerPrintUtil.class.getResource("/").getPath()+"HUM.js";
				path = URLDecoder.decode(path, "utf-8");
				//String path = "E:\\代码\\eclipse_workspaces\\proxy\\src\\main\\resources\\HUM.js";
				FileInputStream fileInputStream = new FileInputStream(new File(path));  
				Reader reader = new InputStreamReader(fileInputStream, "utf-8");  
				engine.eval(reader);
				map.put("getDidStr", engine);
			}
			
			if(engine instanceof Invocable) {
				
				Invocable invoke = (Invocable)engine;    		
				desc = (String) invoke.invokeFunction("getDid", new Object[]{content});
				desc = desc.substring(0,16);
			}
//			fingerid.incrementAndGet();
			/*reader.close();
			fileInputStream.close();*/
			return desc;
		}catch (ScriptException e) {
			logger.error(e.getMessage(),e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}
	
	
	public static String getCZPwd(String pwd) {
		String desc = "";
		try {
			Map<String,ScriptEngine> map = new HashMap<String,ScriptEngine>();
			ScriptEngine engine = map.get("getPWD");
			if(engine == null){
				
				ScriptEngineManager manager = new ScriptEngineManager();
				engine = manager.getEngineByName("javascript");
				String path = FingerPrintUtil.class.getResource("/").getPath()+"cz.js";
				path = URLDecoder.decode(path, "utf-8");
				//String path = "E:\\代码\\eclipse_workspaces\\proxy\\src\\main\\resources\\HUM.js";
				FileInputStream fileInputStream = new FileInputStream(new File(path));  
				Reader reader = new InputStreamReader(fileInputStream, "utf-8");  
				engine.eval(reader);
				map.put("getPWD", engine);
			}
			
			if(engine instanceof Invocable) {
				Invocable invoke = (Invocable)engine;    		
				desc = (String) invoke.invokeFunction("getPWD", new Object[]{pwd});
				
			}
			return desc;
		}catch (ScriptException e) {
			logger.error(e.getMessage(),e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}

	public static String getHUMDesc() {
		String desc = "";
		try {
			//"{"xy":["{\"x\":null,\"y\":null,\"t\":1602680294598}","{\"x\":null,\"y\":null,\"t\":1602680296119}","{\"x\":null,\"y\":null,\"t\":1602680975830}"],"fingerprint":3885420687}"
			String content = "{\"xy\":[\"{\\\"x\\\":null,\\\"y\\\":null,\\\"t\\\":"+new Date().getTime()+"}\"],\"fingerprint\":"+fingerid+"}";
			Map<String,ScriptEngine> map = new HashMap<String,ScriptEngine>();
			ScriptEngine engine = map.get("getDesc");
			if(engine == null){
				
				ScriptEngineManager manager = new ScriptEngineManager();
				engine = manager.getEngineByName("javascript");
				String path = FingerPrintUtil.class.getResource("/").getPath()+"HUDESC.js";
				path = URLDecoder.decode(path, "utf-8");
				//String path = "E:\\代码\\eclipse_workspaces\\proxy\\src\\main\\resources\\ex_mouse.js";
				FileInputStream fileInputStream = new FileInputStream(new File(path));  
				Reader reader = new InputStreamReader(fileInputStream, "utf-8");  
				engine.eval(reader);
				map.put("getDesc", engine);
			}
			
			if(engine instanceof Invocable) {
				
				Invocable invoke = (Invocable)engine;    		
				desc = (String) invoke.invokeFunction("eq_u", new Object[]{content,"52D2841A3485DFFBCF2EA6A0515077CD"});
			}
			fingerid.incrementAndGet();
			/*reader.close();
			fileInputStream.close();*/
			return desc;
		}catch (ScriptException e) {
			logger.error(e.getMessage(),e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}
	
	public static String getHUFingerPrintToken(String data,String appid,String userAgent){
		String fingerPrintToken = "";
        try {
        	Map<String,ScriptEngine> map = new HashMap<String,ScriptEngine>();
        	ScriptEngine engine = map.get("token");
        	if(engine == null){
        		ScriptEngineManager manager = new ScriptEngineManager();
        		engine = manager.getEngineByName("javascript");
        		String path = FingerPrintUtil.class.getResource("/").getPath()+"HU.js";
        		path = URLDecoder.decode(path, "utf-8");
        		//String path = "E:\\代码\\eclipse_workspaces\\proxy\\src\\main\\resources\\HU.js";
        		FileInputStream fileInputStream = new FileInputStream(new File(path));  
        		Reader reader = new InputStreamReader(fileInputStream, "utf-8");  
        		engine.eval(reader);
        		reader.close();
    			fileInputStream.close();
        		map.put("token", engine);
        	}
			
			if(engine instanceof Invocable) {
				
				Invocable invoke = (Invocable)engine;    		
				//invoke.invokeFunction("setData", new Object[]{data});
				fingerPrintToken = (String) invoke.invokeFunction("getPvalue", new Object[]{data,appid,userAgent});
			}
			
		}catch (ScriptException e) {
			logger.error(e.getMessage(),e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
        return fingerPrintToken;
	}

	public static String getQunarHeader(String params){
		String param = "";
		try {
			Map<String,ScriptEngine> map = new HashMap<String,ScriptEngine>();
			ScriptEngine engine = map.get("getQunarHeader");
			if(engine == null){
				ScriptEngineManager manager = new ScriptEngineManager();
				engine = manager.getEngineByName("javascript");
				String path = FingerPrintUtil.class.getResource("/").getPath()+"Qunar.js";
				path = URLDecoder.decode(path, "utf-8");
				//String path = "E:\\代码\\eclipse_workspaces\\proxy\\src\\main\\resources\\JD.js";
				FileInputStream fileInputStream = new FileInputStream(new File(path));
				Reader reader = new InputStreamReader(fileInputStream, "utf-8");
				engine.eval(reader);
				map.put("getQunarHeader", engine);
			}
			if(engine instanceof Invocable) {
				Invocable invoke = (Invocable)engine;    		// 调用merge方法，并传入两个参数
				param = (String) invoke.invokeFunction("getHeders", new Object[]{params});
			}
			return param;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}

	@Test
	public void a() {
		System.out.println(FingerPrintUtil.class.getResource("/").getPath());
	}
	public static String getQunarM(String token,String qtime){
		String param = "";
		try {
			Map<String,ScriptEngine> map = new HashMap<String,ScriptEngine>();
			ScriptEngine engine = map.get("getQunarHeader");
			if(engine == null){
				ScriptEngineManager manager = new ScriptEngineManager();
				engine = manager.getEngineByName("javascript");
				String path = FingerPrintUtil.class.getResource("/").getPath()+"Qunar.js";
				path = URLDecoder.decode(path, "utf-8");
				//String path = "E:\\代码\\eclipse_workspaces\\proxy\\src\\main\\resources\\JD.js";
				FileInputStream fileInputStream = new FileInputStream(new File(path));
				Reader reader = new InputStreamReader(fileInputStream, "utf-8");
				engine.eval(reader);
				map.put("getQunarHeader", engine);
			}
			if(engine instanceof Invocable) {
				Invocable invoke = (Invocable)engine;    		// 调用merge方法，并传入两个参数
				param = (String) invoke.invokeFunction("getM", new Object[]{token,qtime});
			}
			return param;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}


	public static String getQunarToken(String token,String qtime){
		String param = "";
		try {
			Map<String,ScriptEngine> map = new HashMap<String,ScriptEngine>();
			ScriptEngine engine = map.get("getQunarHeader");
			if(engine == null){
				ScriptEngineManager manager = new ScriptEngineManager();
				engine = manager.getEngineByName("javascript");
				String path = FingerPrintUtil.class.getResource("/").getPath()+"Qunar.js";
				path = URLDecoder.decode(path, "utf-8");
				//String path = "E:\\代码\\eclipse_workspaces\\proxy\\src\\main\\resources\\JD.js";
				FileInputStream fileInputStream = new FileInputStream(new File(path));
				Reader reader = new InputStreamReader(fileInputStream, "utf-8");
				engine.eval(reader);
				map.put("getQunarHeader", engine);
			}
			if(engine instanceof Invocable) {
				Invocable invoke = (Invocable)engine;    		// 调用merge方法，并传入两个参数
				param = (String) invoke.invokeFunction("getTokenValue", new Object[]{token,qtime});
			}
			return param;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}
}
