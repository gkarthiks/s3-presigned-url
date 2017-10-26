package com.gkarthiks.s3.utils;


/**
 * To get hold of the File details in the Amazon S3 bucket
 * @author gkarthiks
 *
 */
public class S3File 
{
	/**
	 * File name with extension
	 */
	private String fileName;
	
	/**
	 * The date when, according to Amazon S3, this object was last modified.
	 */
	private String lastModifiedTime;
	
	/**
	 * The hex encoded 128-bit MD5 hash of this object's contents as computed by Amazon S3.
	 * The ETag reflects changes only to the contents of an object, not its metadata.
	 */
	private String entityTag;
	
	/**
	 * The size of this object in bytes.
	 */
	private String size;
	
	/**
	 * The storage class used by Amazon S3 for this object.
	 */
	private String storageClass;

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the lastModifiedTime
	 */
	public String getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * @param lastModifiedTime the lastModifiedTime to set
	 */
	public void setLastModifiedTime(String lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * @return the entityTag
	 */
	public String getEntityTag() {
		return entityTag;
	}

	/**
	 * @param entityTag the entityTag to set
	 */
	public void setEntityTag(String entityTag) {
		this.entityTag = entityTag;
	}

	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * @return the storageClass
	 */
	public String getStorageClass() {
		return storageClass;
	}

	/**
	 * @param storageClass the storageClass to set
	 */
	public void setStorageClass(String storageClass) {
		this.storageClass = storageClass;
	}

	@Override
	public String toString() 
	{
		return "S3File [fileName=" + fileName+ ", entityTag=" + entityTag
				+ ", lastModifiedTime=" + lastModifiedTime + ", size="
				+ size + ", storageClass=" + storageClass
				+ "]";
	}
	
}
