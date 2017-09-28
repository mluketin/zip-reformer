package hr.element.zipreformer.zip.writer;
import hr.element.zipreformer.zip.ByteBlock;
import hr.element.zipreformer.zip.reader.ZipReader;
import hr.element.zipreformer.zip.structure.CentralDirectoryRecord;
import hr.element.zipreformer.zip.structure.EndOfCentralDirectory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class ZipJoiner extends ByteBlock{
	
	private EndOfCentralDirectory endOfCdRecord;
	private List<CentralDirectoryRecord> listOfCdEntries;

	public ZipJoiner(ZipReader zp1, ZipReader zp2) throws IOException, NoSuchAlgorithmException {
		List<CentralDirectoryRecord> list1 = new ArrayList<>();
		list1.addAll(zp1.getListOfCdEntries());

		List<CentralDirectoryRecord> list2 = new ArrayList<>(); 
		list2.addAll(zp2.getListOfCdEntries());
		
		this.listOfCdEntries = getJointList(list1, list2);
		this.endOfCdRecord = new EndOfCentralDirectory();
		updateEndOfCdRecord();		
	}

	public EndOfCentralDirectory getEndOfCdRecord() {
		return endOfCdRecord;
	}

	public List<CentralDirectoryRecord> getListOfCdEntries() {
		return listOfCdEntries;
	}

	private List<CentralDirectoryRecord> getJointList(List<CentralDirectoryRecord> list1, List<CentralDirectoryRecord> list2) throws NoSuchAlgorithmException, IOException {
	
		List<CentralDirectoryRecord> listOfCdEntries = new ArrayList<>();
		
		CentralDirectoryRecord cdr1;
		CentralDirectoryRecord cdr2;
				
		for (int i = 0; i < list1.size(); i++) {
			cdr1 = list1.get(i);
		
			for (int j = 0; j < list2.size(); j++ ) {
				cdr2 = list2.get(j);
				if(cdr1.getCRC32() == cdr2.getCRC32()) {
					if( cdr1.compareSha(cdr2)){	//sada se gledaju velicine ako im je crc i sha isti
//						if( (cdr1.getLength()+cdr1.getLocalFileRecord().getLength()) > (cdr2.getLength()+cdr2.getLocalFileRecord().getLength())  ){
						if( (cdr1.getCompressedSize()) > (cdr2.getCompressedSize())  ){

							listOfCdEntries.add(cdr2);
						} else {
							listOfCdEntries.add(cdr1);
						}
						list1.remove(cdr1);	//faster, and removes duplicating files (if there are 2 same files in both archives)
						list2.remove(cdr2);
						i--;
						j--;
						break;
					}
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
	
	
	private void updateEndOfCdRecord() {
		int localCounter = 0;
		int brCentralDirektorija = 0;
		
		for (CentralDirectoryRecord cdr : listOfCdEntries) {
			localCounter += cdr.getLocalFileRecord().getLength();
			brCentralDirektorija += cdr.getLength();
		}
		
		endOfCdRecord.set(listOfCdEntries.size(), brCentralDirektorija, localCounter);
	}
	
}
















