package hr.element.zipreformer;

import hr.element.zipreformer.zip.reader.ZipReader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple ZipReader.
 */
public class ZipReaderTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ZipReaderTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ZipReaderTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {

//        ZipReader zip1 = new ZipReader("C:\\Users\\student\\Desktop\\zipReformerTest\\archive1.zip");
//        ZipReader zip2 = new ZipReader("C:\\Users\\student\\Desktop\\zipReformerTest\\archive2.zip");
//
//        zip1.join(zip2);
//        zip1.writeArchive("C:\\Users\\student\\Desktop\\zipReformerTest\\joint_archive.zip");

//			ZipReader zip3 = new ZipReader();
//			zip3.join(zip1, zip2);
//
//			zip3.deleteRecord("file.txt");
//			CentralDirectoryRecord cdr = zip3.getListOfCdEntries().get(0);
//			zip3.deleteRecord(cdr);
//			zip3.addRecord(zip1, "a.txt");
//
//			ZipReader zip4 = new ZipReader("E:\\ZipReformerTestArchives\\archive4.zip");
//
//			zip3.join(zip4);
//			zip3.writeArchive("E:\\ZipReformerTestArchives\\joint_archive1234.zip");

        assertTrue(true);
    }
}
