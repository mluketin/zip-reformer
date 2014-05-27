import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Main {

	public static void main(String[] args) throws IOException {
		
		try {
			
			File file1 = new File("E:\\ZipReformerTestArchives\\pipi.zip");
			
			File file2 = new File("E:\\ZipReformerTestArchives\\pipi2.zip");

//			File file = new File("C:\\ZipReformerTestArchives\\archive.zip");
//			File file = new File("C:\\ZipReformerTestArchives\\projekt07.zip");

			
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
			
			ZipWriter joint_zip = new ZipWriter(zip1, zip2);	//prode sve fileove u zip fileu i ako postoje 2 ista fajla al razlicitog sizea onda preuzme taj manji fajl
																//ako u jednom zipu ne postoji fajl koji je u drugome, onda samo iz drugog uzme taj file
			
		
		} catch (FileNotFoundException e) {
		      e.printStackTrace();
		} catch (IOException e) {
		      e.printStackTrace();
		}
	}
	
	
}
	