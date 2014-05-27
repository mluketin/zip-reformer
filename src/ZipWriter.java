import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class ZipWriter {
	
	private ZipReader zip1;
	private ZipReader zip2;
	private EndOfCentralDirectory endOfCdRecord;
	private List<CentralDirectoryRecord> listOfCdEntries = new ArrayList<>();
	private byte[] body1; 
	private byte[] body2;
	



	public ZipWriter(ZipReader zp1, ZipReader zp2) throws IOException {
		this.zip1 = zp1;
		this.zip2 = zp2;
		
		OutputZip oz = new OutputZip(zip1.body.length + zip2.body.length);
		
		this.body1 = zip1.body;
		this.body2 = zip2.body;
		
		int numOfRecords1 = zip1.getListOfCdEntries().size();
		int numOfRecords2 = zip2.getListOfCdEntries().size();
	
		CentralDirectoryRecord cdr1;
		CentralDirectoryRecord cdr2;
		List<CentralDirectoryRecord> list1 = zip1.getListOfCdEntries();
		List<CentralDirectoryRecord> list2 = zip2.getListOfCdEntries();

		System.out.println();
		
		for (int i = 0; i < list1.size(); i++) {
			cdr1 = list1.get(i);
		
//			System.out.println(crc1);
			System.out.println(String.format("%02X ", cdr1.getCRC32()));
			System.out.println(cdr1.getCompressionMethod());
			
			if(cdr1.isCompressionDeflate()) {
				for (int j = 0; j < list2.size(); j++ ) {
					cdr2 = list2.get(j);
					if(cdr2.isCompressionDeflate()) {
						if(cdr1.getCRC32() == cdr2.getCRC32()){	//sada se gledaju velicine 
							if( (cdr1.getLength()+cdr1.getLocalFileRecord().getLength()) > (cdr2.getLength()+cdr2.getLocalFileRecord().getLength())  ){
								listOfCdEntries.add(cdr2);
		//						list1.remove(cdr1); //izbacimo njega jer je veci pa u slucaju ponavljanja ce se dodati onaj manji
							} else {
								listOfCdEntries.add(cdr1);
		//						list2.remove(cdr2);
							}
							list1.remove(cdr1);
							list2.remove(cdr2);
							i--;
							j--;
							break;
						} else if(j == numOfRecords2 - 1){ //ako ne postoji record u drugom zipu
							listOfCdEntries.add(cdr1);
							list1.remove(cdr1);
							i--;
						}
						
						
					} else {
						System.out.println("jedan record nije deflate");
						if(j == numOfRecords2 - 1){ //ako ne postoji record u drugom zipu
							listOfCdEntries.add(cdr1);
							list1.remove(cdr1);
							i--;
						}
					}
				}
			} else {
				System.out.println("jedan record nije deflate");
			}
		}
		
		for (CentralDirectoryRecord cdr : list2) {
			if(cdr.isCompressionDeflate()){
				listOfCdEntries.add(cdr);
			}
		}
		
		this.endOfCdRecord = zip1.getEndOfCdRecord();
		endOfCdRecord.setNumberOfCd(listOfCdEntries.size());					//postavi se broj cd recorda
		System.out.println("broj fileova " + listOfCdEntries.size());
		System.out.println();
		int brojac = 0;
		int brCentralDirektorija = 0;
		
		for (CentralDirectoryRecord cdr : listOfCdEntries) {
			System.out.print("CRC: ");
			System.out.println(String.format("%02X ", cdr.getCRC32()));
			brojac += cdr.getLocalFileRecord().getLength();
			brCentralDirektorija += cdr.getLength();
		}
		
		System.out.println("BROJAC WRITERA = " + brojac);
		endOfCdRecord.setCentralDirectoryStartOffset(brojac);		//postavi se offset na pocetak cd recorda (odmah nakon zadnjeg local file headera)
		
		//sad ostaje velicina central directory (velicina filea - lokalni fileovi)
		brCentralDirektorija += endOfCdRecord.getLength();
		endOfCdRecord.setLengthAllCD(brCentralDirektorija);
		System.out.println("velicina: " + brCentralDirektorija);
		this.writeArchive();
		
		
	}



	private void writeArchive() throws IOException {
		File file = new File("E:\\ZipReformerTestArchives\\archive_joint.zip");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		OutputStream out = new FileOutputStream(file);
		
		for (CentralDirectoryRecord cdr : listOfCdEntries) {
			out.write(cdr.getLocalFileRecord().toByteArray());
		}
		
		for (CentralDirectoryRecord cdr : listOfCdEntries) {
			out.write(cdr.toByteArray());
		}
		
		out.write(endOfCdRecord.toByteArray());
		out.close();
	}

	
}
