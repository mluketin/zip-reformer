package hr.element.zip.structure;
import hr.element.zip.ByteBlock;


public class LocalFileRecord extends ByteBlock {
	  public static final byte[] HeaderSignature = { 0x50, 0x4b, 0x03, 0x04 };
	
	  // OFFSETS
	  private static final int OFF_CompressionMethod        =  8;
	  private static final int OFF_CRC32                    = 14;
	  private static final int OFF_CompressedData_Length    = 18;
	  private static final int OFF_UnCompressedData_Length  = 22;
	  private static final int OFF_FileName_Length          = 26;
	  private static final int OFF_ExtraField_Length        = 28;
	  private static final int OFF_FileName                 = 30;
	
	  private final int OFF_ExtraField()      { return OFF_FileName + getFileNameLength(); }
	  private final int OFF_CompressedData()  { return  OFF_ExtraField() + getExtraFieldLength(); }
	  // END OFFSETS
	
	 
	
	  
	  public LocalFileRecord(final byte[] body){
		  super(body);
	  }
	  
	  public LocalFileRecord(final byte[] body, final int offset) {
	    super(body, offset);
	  }
	  
	//****************************************************************//
	  
	  
	  private int getCompressedMethod() {			//8
		    return getShort(OFF_CompressionMethod);
	  }
	  
	  private int getCRC32() {						//14
		    return getInt(OFF_CRC32);
	  }
	  
	  private int getCompressedDataLength() {		//18
		    return getInt(OFF_CompressedData_Length);
	  }
	  
	  private int getUnCompressedDataLength() {		//22
		    return getInt(OFF_UnCompressedData_Length);
	  }
	  
	  private int getFileNameLength() {				//26
		    return getShort(OFF_FileName_Length);
	  }
	  
	  private int getExtraFieldLength() {			//28
		    return getShort(OFF_ExtraField_Length);
	  }
	  
	  /**
	   * 
	   * @return length of header of local record
	   */
	  public int getOnlyHeaderLength() {
	    return 30 + getFileNameLength() + getExtraFieldLength();
	  }
	
	  /**
	   * 
	   * @return length of whole local record (length of header + len of compressedData)
	   */
	  public int getLength() {
	    return getOnlyHeaderLength() + getCompressedDataLength();
	  }
	  	
		//****************************************************************//

	
	  private void setCRC32(final int CRC32) {
	    setInt(OFF_CRC32, CRC32);
	  }
	
	  private void setCompressedDataLength(final int length) {
	    setInt(OFF_CompressedData_Length, length);
	  }
	
	  public void setUnCompressedDataLength(final int length) {
	    setInt(OFF_UnCompressedData_Length, length);
	  }
	  	
	  private void setCompressedMethod(final int method) {
	    setShort(OFF_CompressionMethod, method);
	  }
	
	  private byte[] getOnlyCompressedData() {
	    return getByteRange(OFF_CompressedData(), getCompressedDataLength());
	  }
	
	  private void setCompressedData(final byte[] data) throws IllegalArgumentException {
	    if (data.length > getCompressedDataLength()) throw new IllegalArgumentException();
	    int offset = OFF_CompressedData();
	    for (int i = 0; i < data.length; i++) {
	      body[offset + i] = data[i];
	    }
	
	  }
	
	  public CompressedData getCompressedDataObject() {
	    return new CompressedData(getOnlyCompressedData(), getCompressedMethod(), getCRC32());
	  }
	
	  public void setCompressedDataObject(final CompressedData data) {
	    if (data != null) {
	      setCRC32(data.CRC32);
	      setCompressedMethod(data.Method);
	      setCompressedDataLength(data.Data.length);
	      setCompressedData(data.Data);
	    }
	  }
	
	  private String getFileName() {
	      return getString(OFF_FileName, getFileNameLength());
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
