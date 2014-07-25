package hr.element.zip;

import hr.element.zip.reader.ZipReader;
import hr.element.zip.structure.CentralDirectoryRecord;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class TestingClass {

	public static void main(String[] args) {
		try {
			
			ZipReader zip1 = new ZipReader("E:\\ZipReformerTestArchives\\archive1.zip");
			ZipReader zip2 = new ZipReader("E:\\ZipReformerTestArchives\\archive2.zip");
			
			zip1.join(zip2);
			zip1.writeArchive("E:\\ZipReformerTestArchives\\joint_archive.zip");
			
			ZipReader zip3 = new ZipReader();
			zip3.join(zip1, zip2);
			
			zip3.deleteRecord("file.txt");
			CentralDirectoryRecord cdr = zip3.getListOfCdEntries().get(0);
			zip3.deleteRecord(cdr);
			zip3.addRecord(zip1, "a.txt");
				
			ZipReader zip4 = new ZipReader("E:\\ZipReformerTestArchives\\archive4.zip");

			zip3.join(zip4);
			zip3.writeArchive("E:\\ZipReformerTestArchives\\joint_archive1234.zip");

		
		} catch (FileNotFoundException e) {
		      e.printStackTrace();
		} catch (IOException e) {
		      e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

}
