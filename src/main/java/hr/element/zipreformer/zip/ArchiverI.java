package hr.element.zipreformer.zip;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DataFormatException;

import hr.element.zipreformer.zip.structure.CentralDirectoryRecord;

public interface ArchiverI {
	
	void join(ArchiverI arch);
	
	void addRecord(CentralDirectoryRecord rec) throws NoSuchAlgorithmException, IOException;
	void addRecord(ArchiverI arch, String pathname) throws NoSuchAlgorithmException, IOException, DataFormatException;

	void deleteRecord(CentralDirectoryRecord rec);
	void deleteRecord(String FileName);

	int numberOfEntries();
	
}
