package cn.wwdab.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class StrUtils {
	
//	获取随机字符串--LoginJwc
	public static String getRandomStr() {
		String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder stringBuilder = new StringBuilder();
		Random random = new Random();
//		获取时间串
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateFormat = simpleDateFormat.format(date);
//		获取随机字符串
		 for(int i=0;i<6;i++){
		       int number=random.nextInt(62);
		       stringBuilder.append(str.charAt(number));
		     }
		 String randStr = dateFormat.concat(stringBuilder.toString());
		 
		return randStr;
	}
	
//	格式化学期学年  传入36、37返回2016、2017|传入1、2返回春、秋
	public static String[] getFormatYearAndTerm(String year,String term) {
		String[] strs=new String [2];
		switch (year) {
		
			case "36":
				strs[0]="2016";
				break;
			case "37":
				strs[0]="2017";
				break;
			case "38":
				strs[0]="2018";
				break;
			case "39":
				strs[0]="2019";
				break;
			case "40":
				strs[0]="2020";
				break;
			case "41":
				strs[0]="2021";
				break;
			case "42":
				strs[0]="2022";
				break;
			case "43":
				strs[0]="2023";
				break;
			case "44":
				strs[0]="2024";
				break;
			default:
				strs[0]="";
				break;
			}
		
		switch (term) {
			
			case "1":
				strs[1]="春";
				break;
			case "2":
				strs[1]="秋";
				break;
			default:
				strs[1]="";
				break;
			}
		
		return strs;
		
	}

}
