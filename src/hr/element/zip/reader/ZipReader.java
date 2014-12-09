package hr.element.zip.reader;
import hr.element.zip.ArchiverI;
import hr.element.zip.ByteBlock;
import hr.element.zip.structure.CentralDirectoryRecord;
import hr.element.zip.structure.EndOfCentralDirectory;
import hr.element.zip.structure.Sha256Hash;
import hr.element.zip.writer.ZipJoiner;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipReader extends ByteBlock implements ArchiverI{

	private EndOfCentralDirectory endOfCdRecord;		
	private List<CentralDirectoryRecord> listOfCdEntries = new ArrayList<>();
	private ZipInputStream zis;
	
	/**
	 * Creates empty archive with only EndOfCentralDirectory ((22bytes))
	 */
	public ZipReader() {
		endOfCdRecord = new EndOfCentralDirectory();
		listOfCdEntries = null;
	};
	
	
	
	//OVAJ CE SE KORISTITI 
	/**
	 * Creates zipReader from array of bytes
	 * @param body
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws DataFormatException 
	 */
	public ZipReader(byte[] body) throws NoSuchAlgorithmException, IOException, DataFormatException{
		super(body);

		process();
	}
	

	
	//ZA TESTIRANJE (jer moram ucitat fajlove sa diska za dobre testove
	/**
	 * Creates ZipReader from zip file on disk using pathname in argument
	 * @param pathname
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 * @throws DataFormatException 
	 */
	@SuppressWarnings("resource")
	public ZipReader(final String pathname) throws IOException, NoSuchAlgorithmException, DataFormatException{
		super();
		File file = new File(pathname);
		
		InputStream in = new FileInputStream(file);
		in = new FileInputStream(file);
		byte[] body = new byte[(int)file.length()];
		in.read(body);
		in.close();
		super.setBody(body);
		process();	
	}
	
	/**
	 * Metoda pronade end of central directory i kreira listu centralnih direktorija
	 * @param body body od zipa koji je na disku
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws DataFormatException 
	 */
	private void process() throws NoSuchAlgorithmException, IOException, DataFormatException {
		int offsetEndOfCd = findEndOfCd();
		
		if(offsetEndOfCd >= 0)
		{
			zis = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(body)));

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
	 * @throws DataFormatException 
	 */
	private List<CentralDirectoryRecord> getCentralDirectoryEntries() throws NoSuchAlgorithmException, IOException, DataFormatException {
		List<CentralDirectoryRecord> listOfCdEntries = new ArrayList<>();
	
		for(int i = this.endOfCdRecord.getCentralDirectoryStartOffset(); i < this.endOfCdRecord.getOffset();) {
			if(IsCentralDirectory(this.body, i)){
				
//				ZipEntry  ze = zis.getNextEntry();
//				CentralDirectoryRecord cdr = new CentralDirectoryRecord(body, i, zis);
				
				
				CentralDirectoryRecord cdr = new CentralDirectoryRecord(this.body, i);

				Sha256Hash sha256Hash = null;
				
				//testni blok
				ZipEntry  ze = zis.getNextEntry();
				if(ze != null){
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
			        byte[] buffer = new byte[1024];
			        int count;
			        while ((count = zis.read(buffer)) != -1) {
			             baos.write(buffer, 0, count);
			        }
			        byte[] decompressedBytes = baos.toByteArray();

					sha256Hash = new Sha256Hash(decompressedBytes);

			        
//					if(cdr.getFileName().compareTo(ze.getName()) == 0 &&
//							ze.getCompressedSize() == cdr.getCompressedSize() &&
//							ze.getCrc() == cdr.getCRC32() &&
//							ze.getMethod() == cdr.getCompressionMethod() &&
//							ze.getSize() == cdr.getUnCompressedSize()
//							){
//						
//						System.out.println(ze.getCrc() + " " + cdr.getCRC32());
//						
//						sha256Hash = new Sha256Hash(decompressedBytes);
//						
//					}
					
					
					
				} else {
					System.err.println("greska ze == null");
				}
				
				if(sha256Hash != null){
					cdr.setSha256Hash(sha256Hash);
					
				} else {
					
					System.err.println("sha256Hash == null");
					System.out.println(ze.getName() + " " + cdr.getFileName());
					System.out.println(ze.getCompressedSize() + " " + cdr.getCompressedSize());
					System.out.println(ze.getCrc() + " " + cdr.getCRC32());

					System.out.println(ze.getMethod() + " " + cdr.getCompressionMethod());

					System.out.println(ze.getSize() + " " + cdr.getUnCompressedSize());

				}
				
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
	
	/**
	 * Joins your current archive with zipArch
	 */
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
	
	
	/**
	 * @throws DataFormatException 
	 * 
	 */
	@Override
	public void addRecord(ArchiverI arch, String pathname) throws NoSuchAlgorithmException, IOException, DataFormatException {
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
	 * Adds rec to archive following way: compares every record in zip with rec
	 * -if they have same CRC32 and SHA256 compares size of record;
	 * -if rec has less bytes it gets added to archive and record that is compared to rec is deleted
	 * 
	 * 
	 * trenutni problem: ako se dodaje CDR koji ima isto ime ka neki file u arhivi
	 */
	@Override
	public void addRecord(CentralDirectoryRecord rec) throws NoSuchAlgorithmException, IOException {
		
		List<CentralDirectoryRecord> listCdr = new ArrayList<>();
		
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
					
//					if( (record.getLength()+record.getLocalFileRecord().getLength()) > (cdr.getLength()+cdr.getLocalFileRecord().getLength())  ){

					if( (record.getCompressedSize()) > (cdr.getCompressedSize())  ){

						listCdr.add(cdr);
						
						this.endOfCdRecord.update(0, 
												  cdr.getLength() - record.getLength(),
												  cdr.getLocalFileRecord().getLength() - record.getLocalFileRecord().getLength()
												  );					
					} else {
						listCdr.add(record); //record manje zauzima prostora od cdr s kojim usporedujem
					}
				} else {
					listCdr.add(record);	//SHA256 razlicit
				}
			} else{
				listCdr.add(record); //CRC32 razlicit
			}
		}
		
		listOfCdEntries = listCdr;
		
		if(flag) { //ako nije SHA isti s niti jednim recordom
			listOfCdEntries.add(cdr);
			this.endOfCdRecord.update(1, cdr.getLength(), cdr.getLocalFileRecord().getLength());
		}
	
	}
	
	
	
	//ako se radi s recordima nekog zip readera pa se iz liste central direcotry entria preda neki rekord koji se zeli izbrisati
	/**
	 * Deletes rec from zipReader
	 */
	@Override
	public void deleteRecord(CentralDirectoryRecord rec) {
		int cdLen = rec.getLength();
		int localLen = rec.getLocalFileRecord().getLength();
	
		this.endOfCdRecord.update(-1, -cdLen, -localLen);
		listOfCdEntries.remove(rec);
		

	}

	/**
	 * Deletes ALL records with recordName
	 */
	@Override
	public void deleteRecord(String recordName) {
		for (CentralDirectoryRecord cdr : listOfCdEntries) {
			if(recordName.compareToIgnoreCase(cdr.getFileName()) == 0) {
				deleteRecord(cdr);
				return;
			}
		}
		System.err.println("No such file.");
	}


	@Override
	public int numberOfEntries() {
		return endOfCdRecord.getCentralDirectoryNumberOfEntrys();
	}
	
	
	/**
	 * Sluzi za testiranje da se vidi je li dobro napise file na disku
	 * @param pathname
	 * @throws IOException
	 */
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

	

}
