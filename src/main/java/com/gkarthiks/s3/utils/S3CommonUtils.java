package com.gkarthiks.s3.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 * @author gkarthiks
 *
 */
public class S3CommonUtils {

	public S3CommonUtils() {
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

}
