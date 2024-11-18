package org.djutils.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * File writer for multiple files in to a zip file. Typical use is:
 * 
 * <pre>
 * try (CompressedFileWriter compressedFileWriter = new CompressedFileWriter("CsvData.zip"))
 * {
 *     BufferedWriter bufferedWriter = compressedFileWriter.next("data_2023.csv");
 *     
 *     // write data for data_2023
 *     bufferedWriter.write(...);
 *     
 *     compressedFileWriter.next("data_2024.csv");
 *     
 *     // write data for data_2024
 *     bufferedWriter.write(...);
 * }
 * </pre>
 * 
 * If the {@code BufferedWriter} is closed, so too is the {@code CompressedFileWriter}. Any consumers of the
 * {@code BufferedWriter} should thus not close it.
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://tudelft.nl/staff/p.knoppers-1">Peter Knoppers</a>
 * @author <a href="https://dittlab.tudelft.nl">Wouter Schakel</a>
 */
public final class CompressedFileWriter implements AutoCloseable
{

    /** Zip output stream to create new zip entries. */
    private final ZipOutputStream zipOutputStream;

    /** Buffered writer to write in to. */
    private BufferedWriter bufferedWriter;

    /**
     * Constructor.
     * @param file file, if this does not end with .zip (case insensitive), ".zip" will be appended to it
     * @throws FileNotFoundException if the zip file can not be written
     */
    public CompressedFileWriter(final String file) throws FileNotFoundException
    {
        this.zipOutputStream =
                new ZipOutputStream(new FileOutputStream(file.toLowerCase().endsWith(".zip") ? file : file + ".zip"));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.zipOutputStream));
    }

    /**
     * Closes the previous file in the zip file, and opens up the next file. The {@code BufferedWriter} returned is the same for
     * each call on a {@code CompressedFileWriter}.
     * @param name name of the next file in the zip file
     * @return writer to write the next file in to.
     * @throws IOException if no next entry could be created in the zip file
     */
    public BufferedWriter next(final String name) throws IOException
    {
        this.bufferedWriter.flush();
        this.zipOutputStream.putNextEntry(new ZipEntry(name));
        return this.bufferedWriter;
    }

    @Override
    public void close() throws IOException
    {
        this.bufferedWriter.flush();
        this.zipOutputStream.close();
    }

    /**
     * Creates a writer to write data to a file, which can be a zipped file or a regular file. In particular if
     * {@code zipped = true}, then with {@code file = "myFile.csv"}, a file {@code myFile.csv.zip} will be created in which a
     * file {@code myFile.csv} is located. Writing occurs on this file.
     * @param filePath path of the file to write; in case of a zipped file, the filename of the zip-file will end with
     *            .zip, and the filename in the zip file will be the the filename without .zip.
     * @param zipped whether to contain the file in a zip file
     * @return BufferedWriter writer tot write in to
     * @throws IOException on error with filenames, file writing, closing, etc.
     */
    public static BufferedWriter create(final String filePath, final boolean zipped) throws IOException
    {
        if (zipped)
        {
            ZipOutputStream zipOutputStream = new ZipOutputStream(
                    new FileOutputStream(filePath.toLowerCase().endsWith(".zip") ? filePath : filePath + ".zip"));
            String fileName = new File(filePath).getName();
            fileName = fileName.toLowerCase().endsWith(".zip") ? fileName.substring(0, fileName.length() - 4) : fileName;
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            return new BufferedWriter(new OutputStreamWriter(zipOutputStream));
        }
        return new BufferedWriter(new FileWriter(filePath));
    }

}
