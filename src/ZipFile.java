import java.util.Arrays;


public class ZipFile {

	public final byte[] body;
	public int offset;
	
	public ZipFile(final byte[] body, final int offset)
	{
		this.body = body;
		this.offset = offset;
	}
	
	public ZipFile(final byte[] body)
	{
		this.body = body;
		this.offset = 0;
	}
	
	
	
	
	
	protected int getShort(final int index) throws IllegalArgumentException {
	    if (index >= body.length - 2) throw new IllegalArgumentException();
	    return ByteArrayTool.readShortLittleEndian(body, offset + index);
	  }
	
	 protected void setShort(final int index, final int value) throws IllegalArgumentException {
		    if (index >= body.length - 2) throw new IllegalArgumentException();
		    ByteArrayTool.writeShortLittleEndian(body, offset + index, (short)value);
		  }
	 
	 
	
	protected int getInt(final int index) throws IllegalArgumentException {
	    if (index >= body.length - 4) throw new IllegalArgumentException();
	    return ByteArrayTool.readIntLittleEndian(body, offset + index);
	  }
	
	protected void setInt(final int index, final int value) throws IllegalArgumentException {
	    if (index >= body.length - 4) throw new IllegalArgumentException();
	    ByteArrayTool.writeIntLittleEndian(body, offset + index, value);
	  }
	
	
	protected byte[] getByteRange(final int index, final int length) {
	    return Arrays.copyOfRange(body, offset + index, offset + index + length);
	  }

	
	public boolean startsWith(final byte[] pattern) throws IllegalArgumentException {
	    if (pattern.length > body.length) throw new IllegalArgumentException();
	    if (body[offset + 0] == pattern[0]) {
	      for (int i=1; i < pattern.length; i++) {
	        if (body[offset + i] != pattern[i]) return false;
	      }
	    }
	    return true;
	  }
	
	 protected String getString(final int index, final int length) throws IllegalArgumentException {
		    if (index >= body.length - length) throw new IllegalArgumentException();
//		    System.out.println("BODY SIZE = " + body.length);
//		    System.out.println("OFFSET    = " + length);
//		    System.out.println("LENGTH    = " + getLength());
		//
//		    System.out.println("INDEX     = " + index);
//		    System.out.println("LENGTH    = " + length);
		//
//		    System.out.println("OFF INDEX = " + (offset + index));
//		    System.out.println("OFF LEN   = " + (offset + index + length));

		    return new String(body, offset + index, length);
		  }
	
	
	
}
