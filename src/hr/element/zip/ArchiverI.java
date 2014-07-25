package hr.element.zip;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import hr.element.zip.structure.CentralDirectoryRecord;

public interface ArchiverI {
	
	//join s drugom zip arhivom, promijenjeni (ako su u drugoj zip arhivi manje bajtova) (+dodani ako ne postoje) elementi drugog zip arhivea 
	public void join(ArchiverI arch);
	
	//koristi se nad  praznim zipom 
	public void join(ArchiverI arch1, ArchiverI arch2);
	
	//dodaje zipFile u zipArhivu, nije bitno je li vec postoji, record se ipak dodaje
	public void addRecord(CentralDirectoryRecord rec) throws NoSuchAlgorithmException, IOException;
	public void addRecord(ArchiverI arch, String pathname) throws NoSuchAlgorithmException, IOException;

	
	//brise zadani zipFile ako se nalazi u arhivi
	public void deleteRecord(CentralDirectoryRecord rec);
	public void deleteRecord(String FileName);
	
	//returns number of entries in archive
	public int numberOfEntries();
	
}
