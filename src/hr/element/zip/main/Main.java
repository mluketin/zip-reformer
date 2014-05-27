package hr.element.zip.main;
import hr.element.zip.reader.ZipReader;
import hr.element.zip.wirter.ZipWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Main {

	public static void main(String[] args) throws IOException {
		
		try {
			
			//first zip archive
			File file1 = new File("C:\\ZipReformerTestArchives\\pipi.zip");
			
			//second zip archive
			File file2 = new File("C:\\ZipReformerTestArchives\\pipi2.zip");

			
			//reading zip-file (archive1)
			InputStream in = new FileInputStream(file1);
			byte[] body1 = new byte[(int)file1.length()];
			in.read(body1);
			in.close();
			
			//reading zip-file (archive2)
			in = new FileInputStream(file2);
			byte[] body2 = new byte[(int)file2.length()];
			in.read(body2);
			in.close();
			
			
			
			//writing content of zip file in hex
//			for (int j = 0; j < body1.length; j++) 
//			{
//				 System.out.print(String.format("%02X ", body1[j]));
//			
//				 if( (j+1) % 4 == 0)
//					 System.out.println(); 
//	    	}
//			System.out.println();
//			System.out.println("size: " + file1.length());
		
			
			ZipReader zip1 = new ZipReader(body1);
			ZipReader zip2 = new ZipReader(body2);
			
			ZipWriter joint_zip = new ZipWriter(zip1, zip2);	//creates joint archive
			joint_zip.writeArchive();							//writes joint archive
		
		} catch (FileNotFoundException e) {
		      e.printStackTrace();
		} catch (IOException e) {
		      e.printStackTrace();
		}
	}
	
	
}
	