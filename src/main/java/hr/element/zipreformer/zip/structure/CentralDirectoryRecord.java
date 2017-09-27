package hr.element.zipreformer.zip.structure;
import hr.element.zipreformer.zip.ByteBlock;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DataFormatException;


public class CentralDirectoryRecord extends ByteBlock {
	
	private LocalFileRecord lfr;
	private Sha256Hash sha256Hash;
		
	// OFFSETS
	
	public static final int COMPRESSION_STORED = 0;
	public static final int COMPRESSION_DEFLATED = 8;
	
	private static final int OFF_CompressionMethod  = 10;
	private static final int OFF_CRC32              = 16;
	private static final int OFF_CompressedSize     = 20; 
	private static final int OFF_UnCompressedSize   = 24; 


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
		
		this.lfr = new LocalFileRecord(localBody, 0, this.getCompressedSize());
		this.sha256Hash = sha256Hash;
	}
	
	public CentralDirectoryRecord(final byte[] centralBody, final Sha256Hash sha256Hash){
		super(centralBody);
		
		this.lfr = null;
		this.sha256Hash = sha256Hash;
	}
	
	//ovaj koristin pri konstrukciji zipReadera
	public CentralDirectoryRecord(byte[] body, int offset) throws NoSuchAlgorithmException, IOException, DataFormatException{
		super(body, offset);
		
		//predajen body, offset i len za compressed size za slucaj da local record ima data descriptor
		lfr = new LocalFileRecord(body, this.getLocalFileHeaderOffset(), this.getCompressedSize());
		
		
	}
	
	
//	public CentralDirectoryRecord(byte[] body, int offset, ZipInputStream zis) throws NoSuchAlgorithmException, IOException {
//		super(body, offset);
//		
//		this.sha256Hash = new Sha256Hash(getUnCompressedData(zis));
//		
//		lfr = new LocalFileRecord(body, this.getLocalFileHeaderOffset(), this.getCompressedSize());
//
//	}
//	
//	//****************************************************************//
//	
//	
//	
//	private byte[] getUnCompressedData(ZipInputStream zis) throws IOException {
//
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	        byte[] buffer = new byte[1024];
//	        int count;
//	        while ((count = zis.read(buffer)) != -1) {
//	             baos.write(buffer, 0, count);
//	        }
//	        byte[] decompressedBytes = baos.toByteArray();
//
//
//		return decompressedBytes;
//	}
	
	
	/*********************************************************************/
	
	public int getCompressionMethod() {	//10
	    return getShort(OFF_CompressionMethod);
	}
	
	public int getCRC32() {	//16
		 return getInt(OFF_CRC32);
	}
	
	/**
	 * returns size of compressed data
	 */
	public int getCompressedSize(){ //20
		return getInt(OFF_CompressedSize);
	}
	
	public int getUnCompressedSize(){ //24
		return getInt(OFF_UnCompressedSize);
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

	  public void setSha256Hash(Sha256Hash sha256Hash2) {
			this.sha256Hash = sha256Hash2;
	  }
	  
	  public Sha256Hash getHashObject(){
		  return sha256Hash;
	  }
	  
	  public byte[] getHash(){
		  return sha256Hash.getHashBytes();
	  }
	  
	  public boolean compareSha(CentralDirectoryRecord cdr) throws NoSuchAlgorithmException, IOException{
		  return sha256Hash.compareSha(cdr);
	  }
	  
}
