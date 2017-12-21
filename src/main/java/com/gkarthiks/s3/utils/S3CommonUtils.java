package com.gkarthiks.s3.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author gkarthiks
 *
 */
public class S3CommonUtils {

	private S3CommonUtils() {
	}
	
	/**
	 * Converts the Stream to String
	 * @param is
	 * @return
	 */
	public static String getStringFromInputStream(InputStream is) 
	{
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try 
		{
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) 
			{
				sb.append(line);
			}
		} 
		catch (Exception e)
		{
			System.out.println("Exception while converting the Input Stream to String in getStringFromInputStream()."+e.getMessage());
			e.printStackTrace();
		}
		finally 
		{
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Returns false if the given string is null or if the string doesn't have any data,
	 * else will return true.
	 */
	public static boolean isEmptyOrNull(String inStr) {
		if(null != inStr && inStr != "" && inStr.length() > 0)
			return false;
		else return true;
	}

	/**
	 * Validate and adds the missing mandatory attribute in ErrorList.
	 * @param errorList
	 * @param s3Props
	 * @return
	 */
	public static List<String> parseAndValidate(List<String> errorList, Properties s3Props) {
		if(S3CommonUtils.isEmptyOrNull(s3Props.getProperty("s3.access.id"))) errorList.add("Access ID");
		if(S3CommonUtils.isEmptyOrNull(s3Props.getProperty("s3.secret.key"))) errorList.add("Secret Key");
		if(S3CommonUtils.isEmptyOrNull(s3Props.getProperty("s3.endpoint.url"))) errorList.add("End Point URL");
		if(S3CommonUtils.isEmptyOrNull(s3Props.getProperty("s3.http.method"))) errorList.add("HTTP Method");
		if(S3CommonUtils.isEmptyOrNull(s3Props.getProperty("s3.bucket.region"))) errorList.add("Bucket Region");
		if(S3CommonUtils.isEmptyOrNull(s3Props.getProperty("s3.ttl"))) errorList.add("TTL");
		return errorList;
	}
}
