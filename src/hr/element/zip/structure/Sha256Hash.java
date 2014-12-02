package hr.element.zip.structure;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Sha256Hash {
	
	private byte[] hash;
	private ZipFile zf;
	private ZipEntry ze;
	private CentralDirectoryRecord cdr;


	public Sha256Hash(CentralDirectoryRecord cdr,ZipFile zf, ZipEntry ze) throws NoSuchAlgorithmException, IOException {
		this.cdr = cdr;
		this.zf = zf;
		this.ze = ze;
		this.hash = getSha256();
	
	}
	
	private byte[] getSha256() throws NoSuchAlgorithmException, IOException{
		
		byte[] buffer = null;
		
		if(cdr.getCompressionMethod() == CentralDirectoryRecord.COMPRESSION_STORED ){
			buffer = cdr.getLocalFileRecord().getCompressedDataObject().getBytes();
			
		} else if(cdr.getCompressionMethod() == CentralDirectoryRecord.COMPRESSION_DEFLATED ) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream in = zf.getInputStream(ze);
			for (int c = in.read(); c != -1; c = in.read()) {
		        baos.write(c);
		    }
		      
			buffer = baos.toByteArray();	//ovaj buffer sadrzi byte array uncompressed filea
			baos.close();
		}
				
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		  
		byte[] hash = sha256.digest(buffer);
		return hash;	
	}
	
	/**
	 * usporedi SHA sa SHA od cdr-a
	 * @param cdr
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public boolean compareSha(CentralDirectoryRecord cdr) throws NoSuchAlgorithmException, IOException{
//		byte[] hash1 = this.getSha256();
//		byte[] hash2 = cdr.getSha256();
//		
//		for (int i = 0; i < hash1.length; i++) {
//			if(hash1[i] != hash2[i]){
//				return false;
//			}
//		}
		
		byte[] cdrHash = cdr.getHash();
		for (int i = 0; i < hash.length; i++) {
			if(hash[i] != cdrHash[i]){
				return false;
			}
		}
		
		return true;
	}

	public byte[] getHashBytes() {
		return hash;
	}

}
