package hr.element.zipreformer;

import hr.element.zipreformer.zip.reader.ZipReader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DataFormatException;

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
    
    public void testApp() throws NoSuchAlgorithmException, IOException, DataFormatException {

        ZipReader zip1 = new ZipReader("C:\\Users\\student\\Desktop\\ArchiveTest\\_Archive1.zip");
        ZipReader zip2 = new ZipReader("C:\\Users\\student\\Desktop\\ArchiveTest\\_Archive4.zip");

        zip1.join(zip2);
        zip1.writeArchive("C:\\Users\\student\\Desktop\\ArchiveTest\\joint_archive.zip");

        assertTrue(true);
    }
}
