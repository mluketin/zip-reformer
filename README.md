This project is still work in progress. (it is on hold for long time)

Purpose of project is to create simple library that takes two zip archives, archive1 and archive2, and from those archives creates a new archive which size is lower than archive1 and archive2.

One example of usage as seen in ZipReaderTest:

    ZipReader zip1 = new ZipReader("C:\\Users\\student\\Desktop\\ArchiveTest\\_Archive1.zip");
    ZipReader zip2 = new ZipReader("C:\\Users\\student\\Desktop\\ArchiveTest\\_Archive4.zip");
    zip1.join(zip2);
    zip1.writeArchive("C:\\Users\\student\\Desktop\\ArchiveTest\\joint_archive.zip");


This image shows zip1 and zip2:

![alt text](https://github.com/mluketin/zip-reformer/master/assets/Zip1_Zip2.png)

And this image shows zip that is created from them:

![alt text](https://github.com/mluketin/zip-reformer/master/assets/Zip3.png)

