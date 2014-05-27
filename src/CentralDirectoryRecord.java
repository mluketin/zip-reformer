
public class CentralDirectoryRecord extends ZipFile {
	
	private LocalFileRecord lfr;
	
	// OFFSETS
	private static final int OFF_CompressionMethod  = 10;
	private static final int OFF_CRC32              = 16;
	private static final int OFF_CompressedSize     = 16; //ovo mi nije jasno sta tocno radi

	private static final int OFF_FileName_Length    = 28;
	private static final int OFF_ExtraField_Length  = 30;
	private static final int OFF_FileComment_Length = 32;
	private static final int OFF_LocalHeader        = 42;
	private static final int OFF_FileName           = 46;
	
	private final int OFF_ExtraField()  { return OFF_FileName + getFileNameLength(); }
	private final int OFF_FileComment() { return OFF_ExtraField() + getExtraFieldLength(); }
	  // END OFFSETS


	
	public CentralDirectoryRecord(final byte[] body, final int offset) {
	    super(body, offset);
	    
	    lfr = new LocalFileRecord(body, this.getLocalFileHeaderOffset());
	  }

	public int getCompressionMethod() {
	    return getShort(OFF_CompressionMethod);
	  }
	
	  private int getFileNameLength() {
	    return getShort(OFF_FileName_Length);
	  }

	  private int getExtraFieldLength() {
	    return getShort(OFF_ExtraField_Length);
	  }
	  
	 public int getCRC32() {
		 return getInt(OFF_CRC32);
	 }

	  private int getFileCommentLength() {
	    return getInt(OFF_FileComment_Length);
	  }

	  public int getLocalFileHeaderOffset() {
	    return getInt(OFF_LocalHeader);
	  }

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

	  public String getFileName() {
	    return getString(OFF_FileName, getFileNameLength());
	  }

	  public String getFileComment() {
	    return getString(OFF_FileComment(), getFileCommentLength());
	  }

	  public int getLength() {
	    return 46 + getFileNameLength() + getExtraFieldLength() + getFileCommentLength();
	  }
	  
	  public boolean isCompressionDeflate(){
		  if(getShort(OFF_CompressionMethod) == 8){
			  return true;
		  } 
			  
		  return false;
	  }
	  
	  public LocalFileRecord getLocalFileRecord() {
		  return this.lfr;
	  }

	  @Override
	  public String toString() {
	    return getFileName();
	  }
	  
	  public byte[] toByteArray() {
			 byte[] b = new byte[this.getLength()];
			 for(int i = 0; i < getLength(); i++){
				 b[i] = body[i+offset];
			 }
				 
			return b;
		  }
}
