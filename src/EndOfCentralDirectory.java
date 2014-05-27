
public class EndOfCentralDirectory extends ZipFile{
	
	private static final int OFF_NumberOfCentralDirectoryEntrys          = 10;
	private static final int OFF_CentralDirectory_Size					 = 12;
	private static final int OFF_CentralDirectory_Start                  = 16;	//offset od pocetka archivea
	private static final int OFF_Comment_Length                          = 20;
	private static final int OFF_Comment                                 = 22;
	
	
	public EndOfCentralDirectory(final byte[] body, final int offset) {
		super(body, offset);			
	}
	
	public int getCentralDirectoryNumberOfEntrys() {
	    return getShort(OFF_NumberOfCentralDirectoryEntrys);	
	}
	
	public int getCentralDirectoryLength() {
		    return getInt(OFF_CentralDirectory_Size);
	}


	public int getCentralDirectoryStartOffset() {
		    return getInt(OFF_CentralDirectory_Start);
	}


	public int getLength() {
		    return 22 + getCommentLength();	//velicina bez komentara je 22 
	}
	 
	
	private int getCommentLength() {
		    return getShort(OFF_Comment_Length);
	}
	
	public void setNumberOfCd(int number){
		setInt(OFF_NumberOfCentralDirectoryEntrys, number);
	}
	
	public void setCentralDirectoryStartOffset(int number){
		setInt(OFF_CentralDirectory_Start, number);
	}
	
	public void setLengthAllCD(int number){
		setInt(OFF_CentralDirectory_Size, number);
	}
	
	public byte[] toByteArray() {
		 byte[] b = new byte[this.getLength()];
		 for(int i = 0; i < getLength(); i++){
			 b[i] = body[i+offset];
		 }
			 
		return b;
	  }

}
