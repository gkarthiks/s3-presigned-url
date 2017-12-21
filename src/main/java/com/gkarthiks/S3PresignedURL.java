package com.gkarthiks;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.gkarthiks.s3.utils.S3CommonUtils;
import com.gkarthiks.s3.utils.S3File;

/**
 * Produces the HTTPPresigned URL for the Amazon S3 Virtual hosted buckets.
 * @author gkarthiks
 */
public class S3PresignedURL {

	public static final String AWS_ACCESS_ID = "AWS_ACCESS_ID";
	public static final String AWS_SECRET_KEY = "AWS_SECRET_KEY";
	
	/**
	 * Generates the Pre-signed HTTP URL for the S3 service
	 * @param endPointURL
	 * @param s3Credentials
	 * @param httpMethod
	 * @param bucketRegion
	 * @param ttl
	 * @return
	 * @throws Exception
	 */
	public static String getS3PresignedURL(String endPointURL, 
						Map<String, String> s3Credentials, 
						String httpMethod, 
						String bucketRegion, 
						int ttl) throws Exception {
		S3PresignedHttpUrlHelper obj = new S3PresignedHttpUrlHelper();
		return obj.getPreSignedHttpUrl(endPointURL, s3Credentials, httpMethod, bucketRegion, ttl, null);
	}
	
	/**
	 * Returns the List of files hosted in the given S3 bucket.
	 * @param endPointURL
	 * @param s3Credentials
	 * @param httpMethod
	 * @param bucketRegion
	 * @param ttl
	 * @param proxyPort
	 * @param proxyHost
	 * @return {@link List} of {@link S3File}
	 * @throws Exception
	 */
	public static List<S3File> getFilesList(String endPointURL, 
			Map<String, String> s3Credentials, 
			String httpMethod, 
			String bucketRegion, 
			int ttl,
			int proxyPort,
			String proxyHost) throws Exception{
		S3PresignedHttpUrlHelper obj = new S3PresignedHttpUrlHelper();
		return obj.getListFiles(endPointURL, s3Credentials, httpMethod, bucketRegion, ttl, proxyPort, proxyHost);
	}
	
	/**
	 * Generates the Pre-Signed URL by reading the values from Properties file.
	 * @param propertiesFileName
	 * @return
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public static String getS3PresignedURL(String propertiesFileName) throws NumberFormatException, Exception {
		if( !S3CommonUtils.isEmptyOrNull(propertiesFileName) ) {
			
			String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			String s3PropPath = rootPath + propertiesFileName;
			Properties s3Props = new Properties();
			s3Props.load(new FileInputStream(s3PropPath));
			
			String accessId = s3Props.getProperty("s3.access.id"),
					secretKey = s3Props.getProperty("s3.secret.key"),
					endPointURL = s3Props.getProperty("s3.endpoint.url"),
					httpMethod = s3Props.getProperty("s3.http.method"),
					bucketRegion = s3Props.getProperty("s3.bucket.region"),
					ttl = s3Props.getProperty("s3.ttl");
			
			//Validate and throws exception if the mandatory value are not present.
			List<String> errorList = new ArrayList<>();
			if(S3CommonUtils.isEmptyOrNull(accessId)) errorList.add("Access ID");
			if(S3CommonUtils.isEmptyOrNull(secretKey)) errorList.add("Secret Key");
			if(S3CommonUtils.isEmptyOrNull(endPointURL)) errorList.add("End Point URL");
			if(S3CommonUtils.isEmptyOrNull(httpMethod)) errorList.add("HTTP Method");
			if(S3CommonUtils.isEmptyOrNull(bucketRegion)) errorList.add("Bucket Region");
			if(S3CommonUtils.isEmptyOrNull(ttl)) errorList.add("TTL");

			if( errorList.isEmpty() ) {
				Map<String, String> s3Credentials = new HashMap<>();
				s3Credentials.put(S3PresignedURL.AWS_ACCESS_ID, accessId);
				s3Credentials.put(S3PresignedURL.AWS_SECRET_KEY, secretKey);
				
				return getS3PresignedURL(endPointURL, s3Credentials, httpMethod, bucketRegion, Integer.parseInt(ttl));
			} else {
				throw new Error("No value found for "+String.join(", ", errorList));
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the List of files hosted in the given S3 bucket.
	 * @param propertiesFileName
	 * @return
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public static List<S3File> getFilesList(String propertiesFileName) throws NumberFormatException, Exception {
		if( !S3CommonUtils.isEmptyOrNull(propertiesFileName) ) {

			String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			String s3PropPath = rootPath + propertiesFileName;
			Properties s3Props = new Properties();
			s3Props.load(new FileInputStream(s3PropPath));
			
			List<String> errorList = new ArrayList<>();
			S3CommonUtils.parseAndValidate(errorList, s3Props);

			if( errorList.isEmpty() ) {
				Map<String, String> s3Credentials = new HashMap<>();
				s3Credentials.put(S3PresignedURL.AWS_ACCESS_ID, s3Props.getProperty("s3.access.id"));
				s3Credentials.put(S3PresignedURL.AWS_SECRET_KEY, s3Props.getProperty("s3.secret.key"));
				int port = S3CommonUtils.isEmptyOrNull(s3Props.getProperty("s3.proxy.port")) ? 0 : Integer.parseInt(s3Props.getProperty("s3.proxy.port"));
				String host = S3CommonUtils.isEmptyOrNull(s3Props.getProperty("s3.proxy.host")) ? null : s3Props.getProperty("s3.proxy.host");
				return getFilesList(s3Props.getProperty("s3.endpoint.url"), 
										s3Credentials, 
										s3Props.getProperty("s3.http.method"), 
										s3Props.getProperty("s3.bucket.region"), 
										Integer.parseInt(s3Props.getProperty("s3.ttl")), 
										port, 
										host);
			} else {
				throw new Error("No value found for "+String.join(", ", errorList));
			}
		} else {
			return null;
		}
	}
	
	
}
