import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;


public class Test {

	public static void main(String[] args) throws IOException {

		
		File file = new File("C:\\ZipReformerTestArchives\\file1.txt");
		InputStream in = new FileInputStream(file);
		byte[] body = new byte[(int)file.length()];
		in.read(body);
		in.close();
		
		CRC32 crc = new CRC32();
		crc.reset();
		crc.update(body);
		System.out.println(crc.getValue());
		System.out.print(String.format("%02X ", crc.getValue()));
	
	
	}

}
