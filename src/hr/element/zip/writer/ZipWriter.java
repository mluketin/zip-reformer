package hr.element.zip.writer;
import hr.element.zip.reader.ZipReader;
import hr.element.zip.structure.CentralDirectoryRecord;
import hr.element.zip.structure.EndOfCentralDirectory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class ZipWriter {
	
	private ZipReader zip1;
	private ZipReader zip2;
	private EndOfCentralDirectory endOfCdRecord;
	private List<CentralDirectoryRecord> listOfCdEntries;

	
	public ZipWriter(ZipReader zp1, ZipReader zp2) throws IOException {
		this.zip1 = zp1;
		this.zip2 = zp2;
				
		//list1 and list2 are used to make code more readable
		List<CentralDirectoryRecord> list1 = zip1.getListOfCdEntries();
		List<CentralDirectoryRecord> list2 = zip2.getListOfCdEntries();

		System.out.println();
		this.listOfCdEntries = getJointList(list1, list2);

		updateEndOfCdRecord();		
		
	}



	private void updateEndOfCdRecord() {
		
		this.endOfCdRecord = zip1.getEndOfCdRecord(); //or zip2 it is practicly the same (if joint zip is written on some other disk than endOfCdRecord will have to change

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
	}



	private List<CentralDirectoryRecord> getJointList(List<CentralDirectoryRecord> list1, List<CentralDirectoryRecord> list2) {
		List<CentralDirectoryRecord> listOfCdEntries = new ArrayList<>();
		
		CentralDirectoryRecord cdr1;
		CentralDirectoryRecord cdr2;
				
		for (int i = 0; i < list1.size(); i++) {
			cdr1 = list1.get(i);
		
//			System.out.println(String.format("%02X ", cdr1.getCRC32()));
//			System.out.println(cdr1.getCompressionMethod());
		
			for (int j = 0; j < list2.size(); j++ ) {
				cdr2 = list2.get(j);
				if(cdr1.getCRC32() == cdr2.getCRC32()){	//sada se gledaju velicine ako im je crc isti
					if( (cdr1.getLength()+cdr1.getLocalFileRecord().getLength()) > (cdr2.getLength()+cdr2.getLocalFileRecord().getLength())  ){
						listOfCdEntries.add(cdr2);
					} else {
						listOfCdEntries.add(cdr1);
					}
					list1.remove(cdr1);	//faster, and removes duplicating files (if there are 2 same files in both archives)
					list2.remove(cdr2);
					i--;
					j--;
					break;
				} else if(j == list2.size() - 1){ //ako ne postoji record u drugom zipu
					listOfCdEntries.add(cdr1);
					list1.remove(cdr1);
					i--;
				}	
			}
			
		}
		
		for (CentralDirectoryRecord cdr : list2) {		//those files in second zip file that do not exist in first one are added to joint archive
				listOfCdEntries.add(cdr);
		}
		
		
		return listOfCdEntries;
	}



	public void writeArchive() throws IOException {
		File file = new File("C:\\ZipReformerTestArchives\\archive_joint.zip");
		
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
