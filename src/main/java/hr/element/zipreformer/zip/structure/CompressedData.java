package hr.element.zipreformer.zip.structure;




public class CompressedData {

  public final byte[] Data;
  public final int Method;
  public final int CRC32;

  public CompressedData(final byte[] body, final int method, final int CRC32) {
    this.Data = body;
    this.Method = method;
    this.CRC32 = CRC32;
  }


  public int getLength() {
    return Data.length;
  }


  public byte[] getBytes() {
    return Data;
  }
}
