package hr.element.zipreformer.zip.structure;
import hr.element.zipreformer.zip.ByteBlock;


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
	
	  //zbog descriptora moram imati ove varijable
	  private int CRC32;
	  private int compressedLen;
	  private int unCompressedLen;
	  
	  private boolean descriptorExists = false;
	 
	
	  
//	  public LocalFileRecord(final byte[] body){
//		  super(body);
//	  }
	  
//	  public LocalFileRecord(final byte[] body, final int offset) {
//	    super(body, offset);
//	  }
	  
	  public LocalFileRecord(final byte[] body,final int offset, final int compressedSize) {
		    super(body, offset);
		    
		    
		    if(getInt(OFF_CRC32)== 0 && getInt(OFF_UnCompressedData_Length) == 0 && getInt(OFF_CompressedData_Length) == 0){
		    	//postoji data descriptor

		    	descriptorExists = true;
		    	for (int i = offset + OFF_CompressedData() + compressedSize; i < body.length; i++) {
					if(i+15 < body.length && isDescriptor(i)){
						this.CRC32 = getInt(i+4);
						this.compressedLen = getInt(i+8);
						this.unCompressedLen = getInt(i+12);
						break;
					}
				}
		    	
		    } else {
		    	this.CRC32 = getInt(OFF_CRC32);
		    	this.compressedLen = getInt(OFF_CompressedData_Length);
		    	this.unCompressedLen = getInt(OFF_UnCompressedData_Length); 
		    }
	  }
	  
	  
	  
	  private boolean isDescriptor(int pos) {
			return body[pos + 0] == 0x50 && body[pos + 1] == 0x4b && body[pos + 2] == 0x7 && body[pos + 3] == 0x8 ;

	  }
		//****************************************************************//

	  
	  private int getCompressedMethod() {			//8
		    return getShort(OFF_CompressionMethod);
	  }
	  
	  private int getCRC32() {						//14
//		    return getInt(OFF_CRC32);
		  return this.CRC32;
	  }
	  
	  private int getCompressedDataLength() {		//18
//		    return getInt(OFF_CompressedData_Length);
		  return this.compressedLen;
	  }
	  
	  private int getUnCompressedDataLength() {		//22
//		    return getInt(OFF_UnCompressedData_Length);
		  return this.unCompressedLen;
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
		  if(descriptorExists){
			    return 30 + getFileNameLength() + getExtraFieldLength() + 16;
		  }
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
