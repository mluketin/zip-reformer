
public class Record extends ZipFile {

	private CentralDirectoryRecord cdr;
	private LocalFileRecord lfr;	
	
	
	public Record(byte[] body, int i) {
		super(body,i);
		cdr = new CentralDirectoryRecord(body, i);
		lfr = null;
	}


	public CentralDirectoryRecord getCentralDirRecord() {
		return cdr;
	}


	public LocalFileRecord getLocalFileRecord() {
		return lfr;
	}
	
//	public boolean isCommpresionDeflate(){
//		if(ByteArrayTool.readShortLittleEndian(body, offset + index);)
//	}
//	
	
}
	
	
	
