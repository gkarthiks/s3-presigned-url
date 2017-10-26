package com.gkarthiks;


import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import com.gkarthiks.s3.utils.S3Constants;

/**
 * 
 * @author gkarthiks
 * This Helper class is used to prepare the pre-signed URL for the S3 services
 * which can be used for HTTP Downloads. 
 */
public class S3PresignedHttpUrlHelper
{
	
	protected final static SimpleDateFormat dateTimeFormat;
	protected final static SimpleDateFormat dateStampFormat;
	
	public static final String ISO8601BasicFormat = "yyyyMMdd'T'HHmmss'Z'";
    public static final String DateStringFormat = "yyyyMMdd";
	
	static{
		dateTimeFormat = new SimpleDateFormat(ISO8601BasicFormat);
		dateTimeFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
		dateStampFormat = new SimpleDateFormat(DateStringFormat);
		dateStampFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
	}
	
	private final static Logger logger = Logger.getLogger(S3PresignedHttpUrlHelper.class);
	
	/**
	 * Generates the Pre-signed HTTP URL for the S3 service
	 * @param endPointURL
	 * @param s3Credentials
	 * @param httpMethod
	 * @param bucketRegion
	 * @param ttl
	 * @param marker
	 * @return
	 * @throws Exception
	 */
	public String getPreSignedHttpUrl(String endPointURL, Map<String, String> s3Credentials, String httpMethod, String bucketRegion, int ttl, String marker) throws Exception 
	{
	
		if(endPointURL.endsWith("/"))
			endPointURL = endPointURL.substring(0, endPointURL.length()-1);
		
		String awsAccessKey, awsSecretKey, authorizationQueryParameters = null, presignedUrl = null ;
		if(s3Credentials!= null && !s3Credentials.isEmpty())
		{
			URL endpointUrl;
			
			if(s3Credentials.containsKey(S3Constants.AWS_ACCESS_ID) && null != s3Credentials.get(S3Constants.AWS_ACCESS_ID)
					&& s3Credentials.containsKey(S3Constants.AWS_SECRET_KEY) && null != s3Credentials.get(S3Constants.AWS_SECRET_KEY))
			{
				awsAccessKey = s3Credentials.get(S3Constants.AWS_ACCESS_ID);
				awsSecretKey = s3Credentials.get(S3Constants.AWS_SECRET_KEY);
			}
			else
			{
				logger.error("AWS Access ID or Secret Key not found!");
				throw new Exception("Cannot find AWS Access ID or Secret Key!");
			}
			
	        //Forming the End-point URL 
	        try 
	        {
	        	endpointUrl= new URL(endPointURL);
	        }
	        catch (MalformedURLException e) 
	        {
	        	logger.error("Unable to parse service endpoint: "+endPointURL+". Exception : " + e.getMessage());
	            throw new Exception("Unable to parse service endpoint: " + e.getMessage());
	        }
	        
	        
	        Map<String, String> queryParams = new HashMap<String, String>();
	        int expiresIn = (ttl == 0) ? (60 * 60) : ttl;
	        logger.info("TTL for the URL has been set to "+expiresIn);
	        queryParams.put("X-Amz-Expires", "" + expiresIn); //Expiration time to be appended as part of Pre-signed URL
	        Map<String, String> headers = new HashMap<String, String>();	        //We have no headers for this, but the signer will add 'host'
	        
	        authorizationQueryParameters = this.computeSignature(endpointUrl, bucketRegion, headers, queryParams, S3Constants.UNSIGNED_PAYLOAD, awsAccessKey, awsSecretKey, marker);
	        
	        presignedUrl = endpointUrl.toString() + "?" + authorizationQueryParameters;
		}
		
		 return presignedUrl;
	}

	/**
	 * Access the bucket and fetches the list of files in that bucket
	 * @param preSignedBucketURL
	 * @param client 
	 * @param config 
	 * @return
	 */
	/*private List<String> getFileList(String preSignedBucketURL, CloseableHttpClient client, RequestConfig config) 
	{
		logger.info("Trying to get the list of files from the S3 bucket via URL -"+preSignedBucketURL);
		
		CloseableHttpResponse response = null;
		List<String> lstPostedFiles = null;
		HttpEntity entity;
		try 
		{
			HttpGet request = new HttpGet(preSignedBucketURL);
			
			//Adding the proxy host
			if (config != null)
				request.setConfig(config);
			
			response = client.execute(request);
			entity = response.getEntity();
			
			if ((response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) || (entity == null))
			{
				System.out.println("Failed to get the HTTP response" + response.getStatusLine());
			}
			else
			{
				// handle the response
				try 
				{
					//downloadedFile = handleResponseStream(entity.getContent());
					String strXML = S3CommonUtils.getStringFromInputStream(entity.getContent());
					lstPostedFiles = getFileListFromXMLStr(strXML);
				}
				catch (Exception e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) 
		{
			logger.error("Exception occured when trying to execute a HTTP GET on Amazon S3 :"+e.getMessage());
		}
		
		return lstPostedFiles;
	}*/

	/**
	 * Creates the list of File names from the XML String
	 * @param strXML
	 * @return
	 */
	/*private List<String> getFileListFromXMLStr(String strXML) 
	{
		//Instantiating the Document factory for the XML Builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    DocumentBuilder builder;  
	    List<String> lstPostedFiles = new ArrayList<>();
	    try  
	    {  
	        builder = factory.newDocumentBuilder();  
	        Document docXML = builder.parse(new InputSource(new StringReader(strXML)));  
	        //Normalizing, not required but recommended
	        docXML.getDocumentElement().normalize();
	        //To check, if the XML has been parsed without errors
	        System.out.println("Root element :" + docXML.getDocumentElement().getNodeName());
	        //Taking the List of COntsnts tag which holds the Key (a.k.a File name) 
	        NodeList nList = docXML.getElementsByTagName("Contents");
	        
	        for (int temp = 0; temp < nList.getLength(); temp++) 
	        {
	        	Node nNode = nList.item(temp);
		        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
		        {
					Element eElement = (Element) nNode;
					lstPostedFiles.add(eElement.getElementsByTagName("Key").item(0).getFirstChild().getNodeValue());
				}		        
	        }
	    }
	    catch(Exception e)
	    {
	    	logger.error("Exception occured while creating the XML document form the XML String. "+e.getMessage());
	    }
		return lstPostedFiles;
	}*/
	
	
	/**
	 * Takes the input stream, which is an XML data and converts it into Lis of S3Files 
	 * @param content
	 * @param marker 
	 * @return 
	 * @throws Exception 
	 */
	/*private Map<String, Object> readS3ListFrmXMLStream(InputStream content) throws Exception 
	{
		Map<String, Object> filesAndMarkerMap = new HashMap<>();
		String marker = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    DocumentBuilder builder;
	    List<S3File> lstPostedFiles;
	    
		logger.info("Converting the stream into a string. ");
		//Converting the Input Stream to String object
		try
		{
			String strXML = getStringFromInputStream(content);
			logger.info("Converted the IP stream to String format");

			//Instantiating the Document factory for the XML Builder
			builder = factory.newDocumentBuilder();  
	        Document docXML = builder.parse(new InputSource(new StringReader(strXML)));  
	        //Normalizing, not required but recommended
	        docXML.getDocumentElement().normalize();
	        logger.debug("To check, if the XML has been parsed without errors, Root element :" + docXML.getDocumentElement().getNodeName());
	        //Taking the List of COntsnts tag which holds the Key (a.k.a File name) 
	        NodeList nList = docXML.getElementsByTagName("Contents");
	        lstPostedFiles = new ArrayList<>();
	        logger.debug("List of files data added are as follows");
	        for (int temp = 0; temp < nList.getLength(); temp++) 
	        {
	        	S3File s3fileMetaData = new S3File();
	        	Node nNode = nList.item(temp);
		        if (nNode.getNodeType() == Node.ELEMENT_NODE) 
		        {
					Element eElement = (Element) nNode;
					s3fileMetaData.setFileName(eElement.getElementsByTagName("Key").item(0).getFirstChild().getNodeValue());
					s3fileMetaData.setLastModifiedTime(eElement.getElementsByTagName("LastModified").item(0).getFirstChild().getNodeValue());
					s3fileMetaData.setEntityTag(eElement.getElementsByTagName("ETag").item(0).getFirstChild().getNodeValue());
					s3fileMetaData.setSize(eElement.getElementsByTagName("Size").item(0).getFirstChild().getNodeValue());
					s3fileMetaData.setStorageClass(eElement.getElementsByTagName("StorageClass").item(0).getFirstChild().getNodeValue());
			        //Taking the last Key from the current response for the pre-signed URL to query again with new marker to get the next iteration.					
					marker = s3fileMetaData.getFileName();

					logger.debug(s3fileMetaData.toString());
				}	
		        lstPostedFiles.add(s3fileMetaData);
	        }
	        logger.info("Total number of files details added / got from S3 with node size  "+nList.getLength()+" is = "+lstPostedFiles.size());
		}
		catch(ParserConfigurationException e)
		{
			throw new Exception("Exception occured while parsing the String XML into a XML document: "+e.getMessage());
		}
		catch(SAXException e)
		{
			throw new Exception("SAXException occured to a XML document: "+e.getMessage());
		}
		catch(Exception e)
		{
			throw new Exception("Exception occured while generating the list of S3Files from the bucket: "+e.getMessage());
		}
		
		//Populating the map with the list of files and marker of the current list.
		 
		if(lstPostedFiles != null && lstPostedFiles.size() > 0)
		{
			filesAndMarkerMap.put(S3Constants.FILES_LIST, lstPostedFiles);
			filesAndMarkerMap.put(S3Constants.MARKER_KEY, marker);
		}
		else
		{
			filesAndMarkerMap.put(S3Constants.FILES_LIST, "EOL");
			filesAndMarkerMap.put(S3Constants.MARKER_KEY, "EOF");
		}
		
		return filesAndMarkerMap;
	}*/
	
	/**
	 * To get the String from Input Stream 
	 * @param is
	 * @return
	 */
	/*private String getStringFromInputStream(InputStream is) 
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
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			if (br != null) 
			{
				try 
				{
					br.close();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}*/

	/**
	 * Iterates through the list of files and gives the matched files to download 
	 * @param lstPostedFiles
	 * @param ddSourcePattern
	 * @return
	 *//*
	public List<S3File> getListOfMatchedFilesToDownload(List<S3File> lstPostedFiles, String ddSourcePattern) 
	{
		List<S3File> lstToDwnld = new ArrayList<>();
		if(null != lstPostedFiles && lstPostedFiles.size() > 0 )
		{
			Pattern p = Pattern.compile(ddSourcePattern);
			lstPostedFiles.forEach(s3File -> {
				Matcher m = p.matcher(s3File.getFileName());
				if (m.matches()) {
					lstToDwnld.add(s3File);
				}
			});
		}
		return lstToDwnld;
	}
*/
	/**
	 * Computational of the signature is initialized
	 * @param endpointUrl 
	 * @param regionName 
	 * @param headers
	 * @param queryParams
	 * @param unsignedPayload
	 * @param awsAccessKey
	 * @param awsSecretKey
	 * @return
	 * @throws DapException 
	 */
	private String computeSignature(URL endpointUrl, String regionName, Map<String, String> headers, Map<String, String> queryParameters, String bodyHash, String awsAccessKey, String awsSecretKey, String marker) throws Exception 
	{
        // first get the date and time for the subsequent request, and convert
        // to ISO 8601 format for use in signature generation
        Date now = new Date();
        String dateTimeStamp = dateTimeFormat.format(now);

        // make sure "Host" header is added
        String hostHeader = endpointUrl.getHost();
        int port = endpointUrl.getPort();
        if ( port > -1 ) {
            hostHeader.concat(":" + Integer.toString(port));
        }
        headers.put("Host", hostHeader);
        
        // canonicalized headers need to be expressed in the query
        // parameters processed in the signature
        String canonicalizedHeaderNames = getCanonicalizeHeaderNames(headers);
        String canonicalizedHeaders = getCanonicalizedHeaderString(headers);
        
        // Adding the scope as part of the query parameters
        String dateStamp = dateStampFormat.format(now);
        String scope =  dateStamp + "/" + regionName + "/" + S3Constants.SERVICE_NAME + "/" + S3Constants.TERMINATOR;
        
        // add the fixed authorization params required by Signature V4
        
        queryParameters.put("X-Amz-Algorithm", S3Constants.SCHEME + "-" + S3Constants.ALGORITHM);
        queryParameters.put("X-Amz-Credential", awsAccessKey + "/" + scope);
        queryParameters.put("X-Amz-Date", dateTimeStamp);        // x-amz-date is now added as a query parameter, but still need to be in ISO8601 basic form
        queryParameters.put("X-Amz-SignedHeaders", canonicalizedHeaderNames);
        
        /**
         * Additional parameters for iterating the list of available files.
         */
        if(null != marker)
        {
        	queryParameters.put("max-keys", S3Constants.MAX_KEYS);
        	queryParameters.put("marker",marker);
        }
        
        // build the expanded canonical query parameter string that will go into the
        // signature computation
        String canonicalizedQueryParameters = getCanonicalizedQueryString(queryParameters);
        
        // express all the header and query parameter data as a canonical request string
        String canonicalRequest = getCanonicalRequest(endpointUrl, S3Constants.HTTP_METHOD, canonicalizedQueryParameters, canonicalizedHeaderNames, canonicalizedHeaders, bodyHash);
        logger.debug("--------- Canonical request --------");
        logger.debug(canonicalRequest);
        logger.debug("------------------------------------");
        
        // construct the string to be signed
        String stringToSign = getStringToSign(S3Constants.SCHEME, S3Constants.ALGORITHM, dateTimeStamp, scope, canonicalRequest);
        logger.debug("--------- String to sign -----------");
        logger.debug(stringToSign);
        logger.debug("------------------------------------");
        
        // compute the signing key
        byte[] kSecret = (S3Constants.SCHEME + awsSecretKey).getBytes();
        byte[] kDate = sign(dateStamp, kSecret, "HmacSHA256");
        byte[] kRegion = sign(regionName, kDate, "HmacSHA256");
        byte[] kService = sign(S3Constants.SERVICE_NAME, kRegion, "HmacSHA256");
        byte[] kSigning = sign(S3Constants.TERMINATOR, kService, "HmacSHA256");
        byte[] signature = sign(stringToSign, kSigning, "HmacSHA256");
        
        // form up the authorization parameters for the caller to place in the query string
        StringBuilder authString = new StringBuilder();
        
        authString.append("X-Amz-Algorithm=" + queryParameters.get("X-Amz-Algorithm"));
        authString.append("&X-Amz-Credential=" + queryParameters.get("X-Amz-Credential"));
        authString.append("&X-Amz-Date=" + queryParameters.get("X-Amz-Date"));
        authString.append("&X-Amz-Expires=" + queryParameters.get("X-Amz-Expires"));
        authString.append("&X-Amz-SignedHeaders=" + queryParameters.get("X-Amz-SignedHeaders"));
        authString.append("&X-Amz-Signature=" + toHex(signature));
        
        /**
         * Additional parameters for iterating the list of available files.
         */
        if(null != marker)
        {
        	authString.append("&max-keys=" + S3Constants.MAX_KEYS);
        	authString.append("&marker=" +marker);
        }

        return authString.toString();
    }
	
	/**
     * 
     * @param stringData
     * @param key
     * @param algorithm
     * @return
     * @throws DapException 
     */
    private static byte[] sign(String stringData, byte[] key, String algorithm) throws Exception 
    {
        try 
        {
            byte[] data = stringData.getBytes("UTF-8");
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(data);
        }
        catch (Exception e) 
        {
            throw new Exception("Unable to calculate a request signature: " + e.getMessage());
        }
    }
    
    /**
     * Converts byte data to a Hex-encoded string.
     * @param data data to hex encode.
     * @return hex-encoded string.
     */
    private static String toHex(byte[] data) 
    {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (int i = 0; i < data.length; i++) 
        {
            String hex = Integer.toHexString(data[i]);
            if (hex.length() == 1) 
            {
                // Append leading zero.
                sb.append("0");
            }
            else if (hex.length() == 8) 
            {
                // Remove ff prefix from negative numbers.
                hex = hex.substring(6);
            }
            sb.append(hex);
        }
        return sb.toString().toLowerCase(Locale.getDefault());
    }
    
    /**
     * Arranges the data required for signature and prepares the String to Sign
     * @param scheme
     * @param algorithm
     * @param dateTime
     * @param scope
     * @param canonicalRequest
     * @return
     * @throws DapException 
     */
    private static String getStringToSign(String scheme, String algorithm, String dateTime, String scope, String canonicalRequest) throws Exception 
    {
        String stringToSign =
                        scheme + "-" + algorithm + "\n" +
                        dateTime + "\n" +
                        scope + "\n" +
                        toHex(hash(canonicalRequest));
        return stringToSign;
    }
    
    /**
     * Hashes the string contents (assumed to be UTF-8) using the SHA-256
     * algorithm.
     * @throws DapException 
     */
    private static byte[] hash(String text) throws Exception 
    {
        try 
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes("UTF-8"));
            return md.digest();
        }
        catch (Exception e) 
        {
            throw new Exception("Unable to compute hash while signing request: " + e.getMessage());
        }
    }
    

	 /**
    * Returns the canonical collection of header names that will be included in
    * the signature. For AWS4 authentication, all header names must be included 
    * in the process in sorted canonicalized order.
    */
   private String getCanonicalizeHeaderNames(Map<String, String> headers) 
   {
       List<String> sortedHeaders = new ArrayList<String>();
       sortedHeaders.addAll(headers.keySet());
       Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER);

       StringBuilder buffer = new StringBuilder();
       for (String header : sortedHeaders) 
       {
           if (buffer.length() > 0) buffer.append(";");
           buffer.append(header.toLowerCase());
       }
       return buffer.toString();
   }
   
   /**
    * Computes the canonical headers with values for the request.
    */
   private String getCanonicalizedHeaderString(Map<String, String> headers) 
   {
       if ( headers == null || headers.isEmpty() ) 
       {
           return "";
       }
       //sort the headers by case-insensitive order
       List<String> sortedHeaders = new ArrayList<String>();
       sortedHeaders.addAll(headers.keySet());
       Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER);

       // form the canonical header:value entries in sorted order. 
       // Multiple white spaces in the values should be compressed to a single space.
       StringBuilder buffer = new StringBuilder();
       for (String key : sortedHeaders) 
       {
           buffer.append(key.toLowerCase().replaceAll("\\s+", " ") + ":" + headers.get(key).replaceAll("\\s+", " "));
           buffer.append("\n");
       }
       return buffer.toString();
   }
   
   /**
    * Examines the specified query string parameters and returns a canonicalized form.
    * <p>
    * The canonicalized query string is formed by first sorting all the query string parameters,
    * then URI encoding both the key and value and then joining them, in order, separating key value pairs with an '&'.
    *
    * @param parameters - The query string parameters to be canonicalized.
    * @return A canonicalized form for the specified query string parameters.
    * @throws DapException 
    */
   private static String getCanonicalizedQueryString(Map<String, String> parameters) throws Exception 
   {
       if ( parameters == null || parameters.isEmpty() ) 
       {
           return "";
       }
       
       SortedMap<String, String> sorted = new TreeMap<String, String>();

       Iterator<Map.Entry<String, String>> pairs = parameters.entrySet().iterator();
       while (pairs.hasNext()) 
       {
           Map.Entry<String, String> pair = pairs.next();
           String key = pair.getKey();
           String value = pair.getValue();
           sorted.put(urlEncode(key, false), urlEncode(value, false));
       }

       StringBuilder builder = new StringBuilder();
       pairs = sorted.entrySet().iterator();
       while (pairs.hasNext()) 
       {
           Map.Entry<String, String> pair = pairs.next();
           builder.append(pair.getKey());
           builder.append("=");
           builder.append(pair.getValue());
           if (pairs.hasNext()) 
           {
               builder.append("&");
           }
       }
       return builder.toString();
   }
   
   /**
    * Encodes the given URL in UTF-8 format
    * @param url
    * @param keepPathSlash
    * @return
    * @throws DapException 
    */
   private static String urlEncode(String url, boolean keepPathSlash) throws Exception 
   {
       String encoded;
       try 
       {
           encoded = URLEncoder.encode(url, "UTF-8");
       }
       catch (UnsupportedEncodingException e) 
       {
           throw new Exception("UTF-8 encoding is not supported."+ e.getMessage());
       }
       
       if ( keepPathSlash ) 
       {
           encoded = encoded.replace("%2F", "/");
       }
       return encoded;
   }
   
   /**
    * Returns the canonical request string to go into the signer process; this consists of several canonical sub-parts.
    * @return
    * @throws DapException 
    */
   private static String getCanonicalRequest(URL endpoint, String httpMethod, String queryParameters, String canonicalizedHeaderNames,
                                        String canonicalizedHeaders, String bodyHash) throws Exception 
   {
       String canonicalRequest = httpMethod + "\n" +
                       getCanonicalizedResourcePath(endpoint) + "\n" +
                       queryParameters + "\n" +
                       canonicalizedHeaders + "\n" +
                       canonicalizedHeaderNames + "\n" +
                       bodyHash;
       return canonicalRequest;
   }
   
   /**
    * Returns the canonicalized resource path for the service end-point.
    * @throws DapException 
    */
   private static String getCanonicalizedResourcePath(URL endpoint) throws Exception 
   {
       if ( endpoint == null ) 
       {
           return "/";
       }
       
       String path = endpoint.getPath();
       if ( path == null || path.isEmpty() ) 
       {
           return "/";
       }
       
       String encodedPath = urlEncode(path, true);
       if (encodedPath.startsWith("/")) 
       {
           return encodedPath;
       }
       else 
       {
           return "/".concat(encodedPath);
       }
   }
}
