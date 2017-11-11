# s3-presigned-url
Creating a PreSigned URL for the Amazon S3 to download the data via plain HTTP.

## Why s3-presigned-url
* To get a file from  the Amazon S3 bucket, we need to login to Amazon S3 via cli or a programatically by implementing the Amazon Libraries.

* s3-presigned-url allows the developer to write only two or three lines of code to get the data out of S3 bucket.

* The existing project code for aquiring the files via ftp, http, wget or curl needs not to be changed. The Pre-Signed URL from the s3-presigned-url.jar provides the ability of directly using this to download the file.

* The URL generated can also be used in browser to download the file from browser.

* Not only to download the file, but also to get the xml file which has the details about the list of files hosted in the bucket. All that need to be done is provide the path of the folder.


## Parameters
* Following parameters are required.
  * Endpoint URL
  * Credential Map
  * HTTP Method
  * Bucket Region
  * TTL
  * Marker (under constn.)
* *Endpoint URL* is the URL of your bucket along with the path of file / folder.
  * Eg:- http://s3-us-west-1.amazonaws.com/<BUCKET_NAME>/<FOLDER>/<FILENAME.FORMAT>
  * Every region has its own format of ENd Point URL. The End point URL should be constructed perfectly to access the data.
  * Example End Point URL Construction:
      
      Region | End Point URL 
      -------|--------------
      us-west-2 | http://s3-us-west-2.amazonaws.com/<BUCKET_NAME>/\<FOLDER\>/\<FILENAME.FORMAT>
      ap-northeast-2 | http://s3.ap-northeast-2.amazonaws.com/<FOLDER\>/\<FILENAME.FORMAT>
  * For region specific URLs, please refer to the Amazon site [Amazon Region Specific End Point URLs and Protocols](http://docs.aws.amazon.com/general/latest/gr/rande.html#s3_region).
      
* *Credential Map :* s3-presigned-url takes the credentials in HashMap<String, String>. 
  * Map S3Constants.AWS_ACCESS_ID as the key and the Access ID as the value string.
  * Map S3Constants.AWS_SECRET_KEY as the key and secret key / password as the value string.
  * Passing the encrypted password is coming soon.
  
* *HTTP Method* is used to specify the method of interaction over the HTTP protocol. Please pass "GET" tothis parameter.

* *Bucket Region* is where the S3 bucket is physically hosted. Pass the region data found under this [table](http://docs.aws.amazon.com/general/latest/gr/rande.html#s3_region) for the bucket the Pre-Signed Url needs to be generated.

* *TTL* is the *Time **To **L*ive for the generated Pre-Signed URL. It should be provided in seconds. If provided as 0, it is set to expire in 1Hr.

* *Marker* is used to get the list of files if it is more than 1000 in the response XML. It is a future under construction. As of now it can take *null* and do no harm.

## Usage
*s3-presigned-url* is available as a dependency jar in Maven central repository. 

### Maven/Gradle

xml
<dependency>
  <groupId>com.github.gkarthiks</groupId>
  <artifactId>s3-presigned-url</artifactId>
  <version>0.0.1</version>
 </dependency>
 
## Implementation
* Create a Map for the credentials.
* Construct the End Point URL based on this [Amazon Reference table](http://docs.aws.amazon.com/general/latest/gr/rande.html#s3_region).
* Pass "GET" as httpMethod.
* Pass the bucket region where it is hosted.
* Provide the desired expiry time in seconds for TTL.
* As of now pass null for marker.

Java
Map<String, String> s3Creds = new HashMap<>();
s3Creds.put(S3Constants.AWS_ACCESS_ID, <ACCESS_ID>);
s3Creds.put(S3Constants.AWS_SECRET_KEY, <SECRET_KEY>);

String preSignedURL = S3PresignedURL.getS3PresignedURL(<EndPoint_URL>, s3Creds, "GET", <Bucket_Region>, 3600, null);
