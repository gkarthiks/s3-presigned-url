package com.gkarthiks.s3.utils;

/**
 * @author gkarthiks
 */
public class S3Constants {

	public static final String AWS_ACCESS_ID = "AWS_ACCESS_ID";
	public static final String AWS_SECRET_KEY = "AWS_SECRET_KEY";
	public static final String UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD";
    public static final String SERVICE_NAME = "s3";
    public static final String FILES_LIST = "FILES_LIST";
    public static final String MARKER_KEY = "MARKER_KEY";
    public static final String SCHEME = "AWS4";
    public static final String MAX_KEYS = "10000"; //This is the default size returned from AWS.
    public static final String TERMINATOR = "aws4_request";
    public static final String ALGORITHM = "HMAC-SHA256";
    public static final String HTTP_METHOD = "GET";
	
	public S3Constants() {
	}

}
