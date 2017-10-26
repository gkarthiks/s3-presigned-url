package com.gkarthiks;

import java.util.Map;

/**
 * Produces the HTTPPresigned URL for the Amazon S3 Virtual hosted buckets.
 * @author gkarthiks
 */
public class S3PresignedURL {

	public static String getS3PresignedURL(String endPointURL, 
						Map<String, String> s3Credentials, 
						String httpMethod, 
						String bucketRegion, 
						int ttl, 
						String marker) throws Exception {
		S3PresignedHttpUrlHelper obj = new S3PresignedHttpUrlHelper();
		return obj.getPreSignedHttpUrl(endPointURL, s3Credentials, httpMethod, bucketRegion, ttl, marker);
	}
}
