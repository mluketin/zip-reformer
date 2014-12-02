package hr.element.zip.reader;
import hr.element.zip.ArchiverI;
import hr.element.zip.ByteBlock;
import hr.element.zip.structure.CentralDirectoryRecord;
import hr.element.zip.structure.EndOfCentralDirectory;
import hr.element.zip.writer.ZipJoiner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

public class ZipReader extends ByteBlock implements ArchiverI{

	private EndOfCentralDirectory endOfCdRecord;		
	private List<CentralDirectoryRecord> listOfCdEntries = new ArrayList<>();
	private ZipFile zf;	//sluzi samo radi provedbe SHA2 hasha nad dekomprimiranim sadrzajem pojedinih recorda
						//jedino sam preko zipFile.getInputStream(zipEntry) usjesno dekomprimirao, pa cim uspijem na neki drugi nacin, promijenit cu kod
						//bitno je samo da svakom centralDirectoryRecordu pridodam njegov zipEntry koji je spojen sa originalnim zipFileom kako bi se moglo procitati
	
	/**
	 * Creates empty archive with only EndOfCentralDirectory ((22bytes))
	 */
	public ZipReader() {
		endOfCdRecord = new EndOfCentralDirectory();
		listOfCdEntries = null;
	};
	

	/**
	 * Creates ZipReader from zip file on disk using pathname in argument
	 * @param pathname
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	@SuppressWarnings("resource")
	public ZipReader(final String pathname) throws IOException, NoSuchAlgorithmException{
		super();
		File file = new File(pathname);
		zf = new ZipFile(pathname);
		
		InputStream in = new FileInputStream(file);
		in = new FileInputStream(file);
		byte[] body = new byte[(int)file.length()];
		in.read(body);
		in.close();
		super.setBody(body);
		process(body);	
	}
	
	/**
	 * Metoda pronade end of central directory i kreira listu centralnih direktorija
	 * @param body body od zipa koji je na disku
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	private void process(final byte[] body) throws NoSuchAlgorithmException, IOException {
		int offsetEndOfCd = findEndOfCd();
		
		if(offsetEndOfCd >= 0)
		{
			endOfCdRecord = new EndOfCentralDirectory(body, offsetEndOfCd);	
			listOfCdEntries = getCentralDirectoryEntries();	//list containing all central directory records 
		} else {
			System.err.println("ZIP not found. EndOfCentralDirectoryMissing. Cannot find offset");
			System.exit(1);
		}
	}


	/**
	 * Metoda vrati listu centralnih direktorija u arhivi
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	private List<CentralDirectoryRecord> getCentralDirectoryEntries() throws NoSuchAlgorithmException, IOException {
		List<CentralDirectoryRecord> listOfCdEntries = new ArrayList<>();
	
		for(int i = this.endOfCdRecord.getCentralDirectoryStartOffset(); i < this.endOfCdRecord.getOffset();) {
			if(IsCentralDirectory(this.body, i)){
//				CentralDirectoryRecord cdr = new CentralDirectoryRecord(this.body, i);
				CentralDirectoryRecord cdr = new CentralDirectoryRecord(this.body, i, zf);

//				cdr.setHash(zf);	//____________________________::::::
				listOfCdEntries.add(cdr);
				i = i + cdr.getLength();
				
			} else {
				System.err.println("Zip archive is invalid. Blank space between central directory entries");
			}
		}
		
		return listOfCdEntries;
	}

	
	
	private int findEndOfCd(){
		for (int i =  super.body.length-22; i >= 0; i--) 
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
	
	public int length(){
		return endOfCdRecord.getCentralDirectoryStartOffset() + endOfCdRecord.getCentralDirectoryLength() + endOfCdRecord.getLength();
	}
	
	
	
	
	//**************************************************************************//
	//**************************************************************************//
	//**************************************************************************//
	//**************************************************************************//
	
	@Override
	public void join(ArchiverI zipArch) {
		
		try {
			ZipJoiner zipW = new ZipJoiner(this, (ZipReader) zipArch);
				
			this.endOfCdRecord = zipW.getEndOfCdRecord();
			this.listOfCdEntries = zipW.getListOfCdEntries();
				
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void join(ArchiverI zipArch1, ArchiverI zipArch2) {
		
		try {
			ZipJoiner zipW = new ZipJoiner((ZipReader) zipArch1,(ZipReader) zipArch2);
			
			this.endOfCdRecord = zipW.getEndOfCdRecord();
			this.listOfCdEntries = zipW.getListOfCdEntries();
		
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * compare and add
	 */
	@Override
	public void addRecord(ArchiverI arch, String pathname) throws NoSuchAlgorithmException, IOException {
		ZipReader zipR = (ZipReader) arch;
		List<CentralDirectoryRecord> listOfCdEntries = zipR.getCentralDirectoryEntries();
		
		for (CentralDirectoryRecord cdr : listOfCdEntries) {
			if(pathname.compareToIgnoreCase(cdr.getFileName()) == 0){
				addRecord(cdr);
				return;
			}
		}
		System.err.println("No such file.");
		
	}
	
	/**
	 * compare and add
	 * trenutni problem: ako se dodaje CDR koji ima isto ime ka neki file u arhivi
	 */
	@Override
	public void addRecord(CentralDirectoryRecord rec) throws NoSuchAlgorithmException, IOException {
		
		//KREIRATI NOVU LISTU CDR I U NJU STAVLJATI CDR-ove KOJI SU PREGLEDANI I USPOREÐENI; PROBLEM KOD IMENA??
		
		//kreiram novi cdr koji cu staviti u ZipReader 
		CentralDirectoryRecord cdr = new CentralDirectoryRecord(rec.toByteArray(), 
																rec.getLocalFileRecord().toByteArray(), 
																rec.getHashObject()
																);		
		CentralDirectoryRecord record;
		
		
		boolean flag = true;
		for (int i = 0; i < listOfCdEntries.size(); i++) {
			record = listOfCdEntries.get(i);
			
			if(record.getCRC32() == cdr.getCRC32()) {	
				if(record.compareSha(cdr)) {
					flag = false;
					
					if( (record.getLength()+record.getLocalFileRecord().getLength()) > (cdr.getLength()+cdr.getLocalFileRecord().getLength())  ){
//						listOfCdEntries.add(cdr);
						
						listOfCdEntries.remove(record);
						listOfCdEntries.add(i, cdr);
						
						this.endOfCdRecord.update(0, 
												  cdr.getLength() - record.getLength(),
												  cdr.getLocalFileRecord().getLength() - record.getLocalFileRecord().getLength()
												  );					
					}
				}
			}
		}
		
		if(flag) {
			listOfCdEntries.add(cdr);
			this.endOfCdRecord.update(1, cdr.getLength(), cdr.getLocalFileRecord().getLength());
		}
		
		
	}
	
	
	
	//ako se radi s recordima nekog zip readera pa se iz liste central direcotry entria preda neki rekord koji se zeli izbrisati
	@Override
	public void deleteRecord(CentralDirectoryRecord rec) {
		int cdLen = rec.getLength();
		int localLen = rec.getLocalFileRecord().getLength();
	
		this.endOfCdRecord.update(-1, -cdLen, -localLen);
		listOfCdEntries.remove(rec);
		

	}

	@Override
	public void deleteRecord(String pathname) {
		for (CentralDirectoryRecord cdr : listOfCdEntries) {
			if(pathname.compareToIgnoreCase(cdr.getFileName()) == 0) {
				deleteRecord(cdr);
				return;
			}
		}
		System.err.println("No such file.");
	}


	public void writeArchive(String pathname) throws IOException {
		File file = new File(pathname);
		
		OutputStream out = new FileOutputStream(file);
		
		byte[] newBody = createNewBody();
	
		
		out.write(newBody);
		out.close();
	}


	private byte[] createNewBody() {
		byte[] newBody = new byte[length()];
		byte[] buffer;
		
		int counter = 0;
		for (CentralDirectoryRecord cdr : listOfCdEntries) {
			buffer = cdr.getLocalFileRecord().toByteArray();
			System.arraycopy(buffer, 0, newBody, counter, buffer.length);			
			counter += buffer.length;
		}
		
		int offsetCounter = 0;
		CentralDirectoryRecord record;
		for (CentralDirectoryRecord cdr : listOfCdEntries) {
			record = new CentralDirectoryRecord(cdr.toByteArray(), cdr.getHashObject());
			record.setLocalFileHeaderOffset(offsetCounter);
			offsetCounter += cdr.getLocalFileRecord().getLength();
			System.arraycopy(record.toByteArray(), 0, newBody, counter, record.getLength());
			counter += record.getLength();
		}
		
		System.arraycopy(endOfCdRecord.toByteArray(), 0, newBody, counter, endOfCdRecord.getLength());
		
		return newBody;
	}

	@Override
	public int numberOfEntries() {
		return endOfCdRecord.getCentralDirectoryNumberOfEntrys();
	}

}
