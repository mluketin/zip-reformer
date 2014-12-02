package hr.element.zip.structure;
import hr.element.zip.ByteBlock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class CentralDirectoryRecord extends ByteBlock {
	
	private LocalFileRecord lfr;
	private ZipEntry ze; //sluzi za SHA2 hash 
	private ZipFile zf;	//sluzi za SHA2 hash 
	
	private Sha256Hash sha256Hash;
	
	private byte[] hash;
	
	// OFFSETS
	
	public static final int COMPRESSION_STORED = 0;
	public static final int COMPRESSION_DEFLATED = 8;
	
	private static final int OFF_CompressionMethod  = 10;
	private static final int OFF_CRC32              = 16;
	private static final int OFF_CompressedSize     = 20; 

	private static final int OFF_FileName_Length    = 28;
	private static final int OFF_ExtraField_Length  = 30;
	private static final int OFF_FileComment_Length = 32;
	private static final int OFF_LocalHeader        = 42;
	private static final int OFF_FileName           = 46;
	
	private final int OFF_ExtraField()  { return OFF_FileName + getFileNameLength(); }
	private final int OFF_FileComment() { return OFF_ExtraField() + getExtraFieldLength(); }
	  // END OFFSETS

	//****************************************************************//
	public CentralDirectoryRecord(final byte[] centralBody, final byte[] localBody, final Sha256Hash sha256Hash){
		super(centralBody);
		
		this.lfr = new LocalFileRecord(localBody);
		this.sha256Hash = sha256Hash;
		this.hash = this.sha256Hash.getHashBytes();
	}
	
	public CentralDirectoryRecord(final byte[] centralBody, final Sha256Hash sha256Hash){
		super(centralBody);
		
		this.lfr = null;
		this.sha256Hash = sha256Hash;
		this.hash = this.sha256Hash.getHashBytes();
	}
	
	public CentralDirectoryRecord(byte[] body, int offset, ZipFile zf) throws NoSuchAlgorithmException, IOException {
		super(body, offset);
		
		lfr = new LocalFileRecord(body, this.getLocalFileHeaderOffset());
	    this.zf = zf;
	    this.ze = this.zf.getEntry(this.getFileName());	
	    this.sha256Hash = new Sha256Hash(this, this.zf, ze);
	    this.hash = sha256Hash.getHashBytes();
	}
	
	//****************************************************************//
	
	
	public int getCompressionMethod() {	//10
	    return getShort(OFF_CompressionMethod);
	}
	
	public int getCRC32() {	//16
		 return getInt(OFF_CRC32);
	}
	
	private int getFileNameLength() {	//28
	    return getShort(OFF_FileName_Length);
	}

	private int getExtraFieldLength() { //30
	    return getShort(OFF_ExtraField_Length);
	}
	  
	private int getFileCommentLength() { //32
	    return getInt(OFF_FileComment_Length);
	}

	public int getLocalFileHeaderOffset() {	//42
	    return getInt(OFF_LocalHeader);
	}

	//****************************************************************//
	public void setLocalFileHeaderOffset(final int offset) {
	    setInt(OFF_LocalHeader, offset);
	}

	public void setCompressionMethod(final int method) {
	    setShort(OFF_CompressionMethod, method);
	}

	public void setCRC32(final int crc32) {
	    setInt(OFF_CRC32, crc32);
	}

	public void setCompressedSize(final int size) {
	    setInt(OFF_CompressedSize, size);
	}

	//****************************************************************//
	public String getFileName() {
	    return getString(OFF_FileName, getFileNameLength());
	}

	public String getFileComment() {
	    return getString(OFF_FileComment(), getFileCommentLength());
	}	
	//****************************************************************//

	
	  /**
	   * Returns length of central directory  (not the file it represents)
	   * 46 + getFileNameLength() + getExtraFieldLength() + getFileCommentLength()
	   */
	public int getLength() {
	    return 46 + getFileNameLength() + getExtraFieldLength() + getFileCommentLength();
	}
	    
	  
	  /**
	   * returns length of central and local file
	   * cdr.getLength + localRecord.getLength()
	   * @return
	   */
	  public int getRecordLength(){
		  return getLength() + this.getLocalFileRecord().getLength();
	  }
	  
	//****************************************************************//

	  public LocalFileRecord getLocalFileRecord() {
		  return this.lfr;
	  }
	  
	  public void setLocalFileRecord(LocalFileRecord lfr) {
		this.lfr = lfr;  
	  }

	  @Override
	  public String toString() {
	    return getFileName();
	  }
	  
	  public byte[] toByteArray() {
		  	
		  	 int len = this.getLength();
		  
			 byte[] b = new byte[len];
			 for(int i = 0; i < len; i++){
				 b[i] = body[i+offset];
			 }
				 
			return b;
	  }
	
	  public void updateLocRecOff(int off) {
		setLocalFileHeaderOffset(off);
	}
	  
	  
		//****************************************************************//

	  public Sha256Hash getHashObject(){
		  return sha256Hash;
	  }
	  
	  public byte[] getHash(){
		  return sha256Hash.getHashBytes();
	  }
	  
	  public boolean compareSha(CentralDirectoryRecord cdr) throws NoSuchAlgorithmException, IOException{
		  return sha256Hash.compareSha(cdr);
	  }
	  
		//****************************************************************//

//	public void setHash(ZipFile zf) throws NoSuchAlgorithmException, IOException {
//		setZipEntry(zf);
//		this.sha256Hash = getSha256();
//			
//	}
	  
//	private void setZipEntry(ZipFile zf) {
//		this.zf = zf;
//		this.ze = zf.getEntry(this.getFileName());
//	}
	
//	private byte[] getSha256() throws NoSuchAlgorithmException, IOException{
//		
//		byte[] buffer = null;
//		
//		if(getCompressionMethod() == COMPRESSION_STORED ){
//			buffer = getLocalFileRecord().getCompressedDataObject().getBytes();
//			
//		} else if(getCompressionMethod() == COMPRESSION_DEFLATED ) {
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			InputStream in = zf.getInputStream(ze);
//			for (int c = in.read(); c != -1; c = in.read()) {
//		        baos.write(c);
//		    }
//		      
//			buffer = baos.toByteArray();	//ovaj buffer sadrzi byte array uncompressed filea
//			baos.close();
//		}
//				
//		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
//		  
//		byte[] hash = sha256.digest(buffer);
//		return hash;
//		
//	}
	
	//ovo vrati input stream za originalni dekomprimirani file koji ce onda proc sha2 hash metodu
//	private InputStream getInputStream() throws Exception{
////		if(this.getFileName() == null){
////			throw new NullPointerException("name");
////		}
//		
//		switch(this.getCompressionMethod()){
//			case COMPRESSION_STORED:
//				return null;
//			
//			
//			
//			
//			case COMPRESSION_DEFLATED:
//				return null;
//		
//				
//				
//			default:
//				throw new Exception("invalid compression method");
//		}	
//	}
	
//	public boolean compareSha(CentralDirectoryRecord cdr) throws NoSuchAlgorithmException, IOException{
////		byte[] hash1 = this.getSha256();
////		byte[] hash2 = cdr.getSha256();
////		
////		for (int i = 0; i < hash1.length; i++) {
////			if(hash1[i] != hash2[i]){
////				return false;
////			}
////		}
//		
//		for (int i = 0; i < this.sha256Hash.length; i++) {
//			if(this.sha256Hash[i] != cdr.sha256Hash[i]){
//				return false;
//			}
//		}
//		
//		return true;
//	}

	
	
	  
}
