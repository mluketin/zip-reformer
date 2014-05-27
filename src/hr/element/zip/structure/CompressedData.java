package hr.element.zip.structure;
import hr.element.zip.tools.ByteArrayTool;




public class CompressedData  {

  public final byte[] Data;
  public final int Method;
  public final int CRC32;

  public CompressedData(final byte[] body, final int method, final int CRC32) {
    this.Data = body;
    this.Method = method;
    this.CRC32 = CRC32;
  }

  public CompressedData(final byte[] body, final int method) {
    this.Data = body;
    this.Method = method;
    this.CRC32 = ByteArrayTool.computeCRC32(body, 0, body.length);
  }


  public int getLength() {
    return Data.length;
  }


  public byte[] getBytes() {
    return Data;
  }
}
