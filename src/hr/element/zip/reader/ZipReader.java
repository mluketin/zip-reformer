package hr.element.zip.reader;
import hr.element.zip.structure.CentralDirectoryRecord;
import hr.element.zip.structure.EndOfCentralDirectory;
import hr.element.zip.tools.ZipFile;


import java.util.ArrayList;
import java.util.List;

public class ZipReader extends ZipFile{

	private EndOfCentralDirectory endOfCdRecord;		
	private List<CentralDirectoryRecord> listOfCdEntries = new ArrayList<>();
	
	
	public ZipReader(final byte[] body) {
		super(body);
		
		int offsetEndOfCd = findEndOfCd();
		
		if(offsetEndOfCd >= 0)
		{
			System.out.println("offset: " + offsetEndOfCd);
			endOfCdRecord = new EndOfCentralDirectory(body, offsetEndOfCd);	
			
			listOfCdEntries = getCentralDirectoryEntries();	//list containing all central directory records 
				
			if(endOfCdRecord.offset == ( listOfCdEntries.get(listOfCdEntries.size()-1).offset + listOfCdEntries.get(listOfCdEntries.size()-1).getLength() ) ){
				System.out.println("Nema niceg izmedu zadnjeg CD i EndCD");
				//mozda exit jer nevalja zip
			}	
				
			ispis();
		
		} else {
			System.err.println("ZIP not found. EndOfCentralDirectoryMissing. Cannot find offset");
			System.exit(1);
		}
		
	}


	private void ispis() {

//		int brojac = 0;
		
//		System.out.println("Broj central directory entrya: " + listOfCdEntries.size());
//		System.out.println("Imena fileova u central directory");
//		for (CentralDirectoryRecord cdr : listOfCdEntries) {
//			brojac += cdr.getLocalFileRecord().getLength();
//			System.out.print(cdr.getFileName() + "        CRC: ");
//			System.out.println(String.format("%02X ", cdr.getCRC32()));
//		}
		
//		for (CentralDirectoryRecord cdr : listOfCdEntries) {
//			brojac += cdr.getLength();
//		}
//		brojac += endOfCdRecord.getLength();
//		System.out.println("offset na CD " + endOfCdRecord.getCentralDirectoryStartOffset());
//		System.out.println("size na CD " + endOfCdRecord.getCentralDirectoryLength());
//		System.out.println("BROJAC = " + brojac);
		
//		System.out.println();
//		centralDirectoryRecord = new CentralDirectoryReader(endOfCdRecord);
//		System.out.println(centralDirectoryRecord.records[0].body.length);
//		System.out.println(centralDirectoryRecord.records[0].offset);
				
	}


	//ovdje se central directory spreme u listu koju metoda vrati (lista sadrzava central direktorije)
	private List<CentralDirectoryRecord> getCentralDirectoryEntries() {
		List<CentralDirectoryRecord> listOfCdEntries = new ArrayList<>();
	
		for(int i = this.endOfCdRecord.getCentralDirectoryStartOffset(); i < this.endOfCdRecord.offset;) {
			if(IsCentralDirectory(this.body, i)){
				CentralDirectoryRecord cdr = new CentralDirectoryRecord(this.body, i);
				listOfCdEntries.add(cdr);
				i = i + cdr.getLength();
				
			} else {
				System.err.println("Zip archive is invalid. Blank space between central directory entries");
			}
		}
		
		return listOfCdEntries;
	}

	private int findEndOfCd()
	{
		for (int i =  super.body.length-22; i > 0; i--) 
		{
			if(IsEndOfCentralDirectory(body,i))
				return i;
		}
		
		return -1;
	}
	
	private boolean IsEndOfCentralDirectory(byte[] body, int pos) {
		return body[pos + 0] == 0x50 && body[pos + 1] == 0x4b && body[pos + 2] == 0x5 && body[pos + 3] == 0x6 ;
	}

	private boolean IsCentralDirectory(byte[] body, int pos)
	{	
		return body[pos + 0] == 0x50 && body[pos + 1] == 0x4b && body[pos + 2] == 0x1 && body[pos + 3] == 0x2 ;
		
	}
	
	
	public EndOfCentralDirectory getEndOfCdRecord() {
		return endOfCdRecord;
	}


	public List<CentralDirectoryRecord> getListOfCdEntries() {
		return listOfCdEntries;
	}

	
}
