package com.gkarthiks;

import java.util.List;
import java.util.Map;

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
	
	
}
