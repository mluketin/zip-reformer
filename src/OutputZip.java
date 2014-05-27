import java.io.File;


public class OutputZip {
	
	private byte[] body;
	
	public OutputZip() {
		File file = new File("E:\\ZipReformerTestArchives\\joint_archive.zip");
	}

	public OutputZip(int size) {
		body = new byte[size];
	}
	
	public void sendToOutput(){
		
	}
	
	

}
