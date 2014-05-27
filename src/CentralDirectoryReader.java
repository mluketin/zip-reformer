
public class CentralDirectoryReader extends ZipFile {

	public final CentralDirectoryRecord[] records;
	public final EndOfCentralDirectory endRecord;
	
	public CentralDirectoryReader(EndOfCentralDirectory cde) {
		super(cde.body, cde.getCentralDirectoryStartOffset());
		this.endRecord = cde;
		
		// lets get records
	    int recordsCount = endRecord.getCentralDirectoryNumberOfEntrys();
	    this.records = new CentralDirectoryRecord[recordsCount];

	    records[0] = new CentralDirectoryRecord(body, endRecord.getCentralDirectoryStartOffset());
	    int offsetSum = 0;
	    for (int i = 1; i < recordsCount; i++) {
	      offsetSum += records[i-1].getLength();
	      records[i] = new CentralDirectoryRecord(body, offset + offsetSum);
	    }
	}

	
	public int getLength() {
	    return endRecord.getCentralDirectoryLength();
	  }
	
}
