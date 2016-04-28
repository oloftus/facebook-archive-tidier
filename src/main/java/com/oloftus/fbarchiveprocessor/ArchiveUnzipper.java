package com.oloftus.fbarchiveprocessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ArchiveUnzipper {

    private static final String ARCHIVE_TMP = "archive_tmp_";

    private File inputArchive;
    private File outputDirectory;

    public ArchiveUnzipper(File inputArchive, File outputDirectory) {

        this.inputArchive = inputArchive;
        this.outputDirectory = outputDirectory;
    }

    public Path expandArchive() throws FileNotFoundException, IOException {

        Path expandIntoDir = Files.createTempDirectory(outputDirectory.toPath(), ARCHIVE_TMP);
        unzipZipFile(inputArchive, expandIntoDir.toString());

        return expandIntoDir;
    }

    private void unzipZipFile(File zipFile, String expandIntoDir) throws FileNotFoundException,
            IOException {

        FileInputStream fis = new FileInputStream(zipFile);
        ZipInputStream zis = new ZipInputStream(fis);
        byte[] buffer = new byte[1024];

        ZipEntry entry = zis.getNextEntry();
        while (entry != null) {
            String fileName = entry.getName();
            File entryFile = new File(expandIntoDir + File.separator + fileName);
            
            if (entry.isDirectory()) {
                entryFile.mkdirs();
            }
            else {
                new File(entryFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(entryFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
            }

            entry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
    }
}
