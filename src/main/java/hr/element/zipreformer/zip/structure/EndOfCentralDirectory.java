package hr.element.zipreformer.zip.structure;

import hr.element.zipreformer.zip.ByteBlock;


public class EndOfCentralDirectory extends ByteBlock {

    private static final int OFF_NumberOfDiskEntrys = 8;
    private static final int OFF_NumberOfCentralDirectoryEntrys = 10;
    private static final int OFF_CentralDirectory_Size = 12;
    private static final int OFF_CentralDirectory_Start = 16;    //offset od pocetka archivea
    private static final int OFF_Comment_Length = 20;
    private static final int OFF_Comment = 22;

    /**
     * creates empty end of central directory, first 4bytes are marker, everything else is set to 0
     */
    public EndOfCentralDirectory() {
        byte[] newBody = new byte[22];
        newBody[0] = 0x50;
        newBody[1] = 0x4b;
        newBody[2] = 0x05;
        newBody[3] = 0x06;

        for (int i = 4; i < newBody.length; i++) {
            newBody[i] = 0;
        }

        setBody(newBody);
    }

    public EndOfCentralDirectory(final byte[] body) {
        super(body);
    }

    public EndOfCentralDirectory(final byte[] body, final int offset) {
        super(body, offset);
    }

    public int getDiskNumberOfEntrys() {
        return getShort(OFF_NumberOfDiskEntrys);
    }

    public int getCentralDirectoryNumberOfEntrys() {
        return getShort(OFF_NumberOfCentralDirectoryEntrys);
    }

    //size of CD does not include EndOfCD

    /**
     * Returns length of central directory without End Of Central Directory
     *
     * @return
     */
    public int getCentralDirectoryLength() {
        return getInt(OFF_CentralDirectory_Size);
    }

    public int getCentralDirectoryStartOffset() {
        return getInt(OFF_CentralDirectory_Start);
    }

    /**
     * Length of ENDofCentralDirectory (22+ commentLength)
     *
     * @return
     */
    public int getLength() {
        return 22 + getCommentLength();    //velicina bez komentara je 22
    }

    private int getCommentLength() {
        return getShort(OFF_Comment_Length);
    }

    public String getComment() {
        return getString(OFF_Comment, getCommentLength());
    }

    public void setDiskNumberOfCd(int number) {
        setShort(OFF_NumberOfDiskEntrys, number);
    }

    public void setNumberOfCd(int number) {
        setShort(OFF_NumberOfCentralDirectoryEntrys, number);
    }

    public void setCentralDirectoryStartOffset(int number) {
        setInt(OFF_CentralDirectory_Start, number);
    }

    public void setCentralDirectorySize(int number) {
        setInt(OFF_CentralDirectory_Size, number);
    }

    public byte[] toByteArray() {

        int len = this.getLength();

        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[i] = body[i + offset];
        }

        return b;
    }

    //i, adds i to number of entries on disk and number of entries in central directory
    //cdLength is length of central directory record
    //locLength is length of local record
    public void update(int i, int cdLength, int locLength) {

        if (i != 0) {
            setDiskNumberOfCd(getDiskNumberOfEntrys() + i);
            setNumberOfCd(getCentralDirectoryNumberOfEntrys() + i);
        }

        if (cdLength != 0) {
            setCentralDirectorySize(getCentralDirectoryLength() + cdLength);
        }

        if (locLength != 0) {
            setCentralDirectoryStartOffset(getCentralDirectoryStartOffset() + locLength);
        }
    }

    /**
     * @param i         number of records
     * @param cdLength  size of central directory
     * @param locLength size of local directory (so offset to CD can be set =>  locLength+1)
     */
    public void set(int i, int cdLength, int locLength) {
        setDiskNumberOfCd(i);
        setNumberOfCd(i);
        setCentralDirectorySize(cdLength);
        setCentralDirectoryStartOffset(locLength);

    }

}
