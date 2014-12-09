package hr.element.zip.structure;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Hash {
	
	private byte[] hash;

	
	public Sha256Hash(byte[] input) throws NoSuchAlgorithmException{
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		  
		hash = sha256.digest(input);
	}	
	
	public byte[] getHashBytes() {
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
		
		byte[] cdrHash = cdr.getHash();
		int hashLength = hash.length;
		for (int i = 0; i < hashLength; i++) {
			if(hash[i] != cdrHash[i]){
				return false;
			}
		}
		return true;
	}


}
