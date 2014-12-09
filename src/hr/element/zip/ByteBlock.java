package hr.element.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

 
public class ByteBlock {
	
	protected byte[] body;
//	private int index;
	protected int offset;
//	private int length;
	
	protected ByteBlock() {
		body = null;
//		index = 0;
		offset = 0;
//		length = 0;
	}
	
	protected ByteBlock(byte[] body){
		this.body = body;
//		index = 0;
		offset = 0;
//		length = body.length;
	}
	
	protected ByteBlock(byte[] body, int offset){
		this.body = body;
//		index = 0;
		this.offset = offset;
//		length = body.length;
	}
	
	protected ByteBlock(byte[] body, int index, int offset, int length) {
		this.body = body;
//		this.index = index;
		this.offset = offset;
//		this.length = length;
	}
	
	public void setBody(byte[] body) {
		this.body = body;
	}
	
	protected byte[] getBody() {
		return body;
	}

	
	
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	public short readShortLittleEndian(int off) {	
		return (short) ((body[off] & 0xff) | (body[off + 1] & 0xff) << 8);
	}
	
	
	public int readIntLittleEndian(int off){	
	      return (int)( ((body[off] & 0xff) | (body[off+1] & 0xff) << 8) | ((body[off+2] & 0xff) | (body[off+3] & 0xff) << 8) << 16);
	}
	
	public long readLongLittleEndian(int off){
	    long temp = 0;
	    temp |= body[off+7]&0xff;
	    temp <<=8;
	    temp |= body[off+6]&0xff;
	    temp <<=8;
	    temp |= body[off+5]&0xff;
	    temp <<=8;
	    temp |= body[off+4]&0xff;
	    temp <<=8;
	    temp |= body[off+3]&0xff;
	    temp <<=8;
	    temp |= body[off+2]&0xff;
	    temp <<=8;
	    temp |= body[off+1]&0xff;
	    temp <<=8;
	    temp |= body[off]&0xff;
	    return temp;
	}
	
	public void writeShortLittleEndian(int pos, short value) {		
		body[pos +1] = (byte) (value >>> 8);
	    body[pos ] = (byte) (value & 0xFF);
	}
	
	public void writeIntLittleEndian(int pos, int value) {
		body[pos+3] = (byte) (value >>>24);
	    body[pos+2] = (byte) (value >>>16);
	    body[pos+1] = (byte) (value >>>8);
	    body[pos] = (byte) (value &0xFF);	
		
	}
	
	public void writeLongLittleEndian(int pos, long value){
	    body[pos+7] = (byte) (value >>>56);
	    body[pos+6] = (byte) (value >>>48);
	    body[pos+5] = (byte) (value >>>40);
	    body[pos+4] = (byte) (value >>>32);
	    body[pos+3] = (byte) (value >>>24);
	    body[pos+2] = (byte) (value >>>16);
	    body[pos+1] = (byte) (value >>>8);
	    body[pos] = (byte) (value &0xFF);
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
	
//	public byte[] getHash() throws NoSuchAlgorithmException{
//		return getHash(0, this.body.length);
//	}
//	
//	//prototip, triba vjerojatno promijeniti
//	public byte[] getHash(int off, int len) throws NoSuchAlgorithmException {
//		
//		if(body.length < off+len){
//			System.err.println("hash error");
//			return null;
//		}
//		
//		  MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
//		  
//		  byte[] message = new byte[len];
//		  System.arraycopy(body, off, message, 0, len);
//		  
//		  byte[] hash = sha256.digest(message);
//		  return hash;
//	  }
	
	protected int getShort(final int index) throws IllegalArgumentException {		
	    if (index >= body.length - 1) throw new IllegalArgumentException();			//len = 8, index= 6, 6>=7 OK
	    return readShortLittleEndian(offset + index);
	}
	
	protected void setShort(final int index, final int value) throws IllegalArgumentException {
	    if (index >= body.length - 1) throw new IllegalArgumentException();
	    writeShortLittleEndian(offset + index, (short)value);
	}
	
	protected int getInt(final int index) throws IllegalArgumentException {
	    if (index >= body.length - 3) throw new IllegalArgumentException();
	    return (readIntLittleEndian(offset + index));
	}
	
	protected void setInt(final int index, final int value) throws IllegalArgumentException {
	    if (index >= body.length - 3) throw new IllegalArgumentException();
	    writeIntLittleEndian(offset + index, value);
	}
	
	
	protected String getString(final int index, final int length) throws IllegalArgumentException {
	    if (index >= body.length - length) throw new IllegalArgumentException();
//	    System.out.println("BODY SIZE = " + body.length);
//	    System.out.println("OFFSET    = " + length);
//	    System.out.println("LENGTH    = " + getLength());
	//
//	    System.out.println("INDEX     = " + index);
//	    System.out.println("LENGTH    = " + length);
	//
//	    System.out.println("OFF INDEX = " + (offset + index));
//	    System.out.println("OFF LEN   = " + (offset + index + length));

	    return new String(body, offset + index, length);
	}
	
	
	@SuppressWarnings("resource")
	public void writeToDisk(String pathname) throws IOException {
		File file = new File(pathname);
		OutputStream out = new FileOutputStream(file);
		out.write(body);
	}
	
}
