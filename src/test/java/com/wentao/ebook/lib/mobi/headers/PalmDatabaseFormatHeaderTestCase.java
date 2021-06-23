package com.wentao.ebook.lib.mobi.headers;

import com.wentao.ebook.lib.mobi.meta.IndexMetaRecord;
import com.wentao.ebook.lib.mobi.meta.TagxSection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.stream.FileImageOutputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PalmDatabaseFormatHeaderTestCase {
    private BufferedInputStream inputStream;
    private byte[] wholeFileData;

    private String[] files = new String[]{"", "test001-xuanzangfashizhuan.mobi", "test002-maoxuan1-4.mobi", "test003-shuyi.mobi"};
    String fileName = files[3];

    @Before
    public void before() throws IOException {
        inputStream = (BufferedInputStream) this.getClass().getClassLoader().getResourceAsStream(fileName);
        wholeFileData = new byte[inputStream.available()];
        inputStream.read(wholeFileData);
        inputStream.close();
        inputStream = (BufferedInputStream) this.getClass().getClassLoader().getResourceAsStream(fileName);
    }

    @After
    public void after() throws IOException {
        inputStream.close();
    }

    @Test
    public void testPalmDatabaseFormatHeader() throws Exception {
        PalmDatabaseFormatHeader header = new PalmDatabaseFormatHeader();
        header.read(inputStream);
        inputStream.skip(2);

        for (int index = 0; index < header.getNumberOfRecords(); index++) {
            PalmDatabaseFormatRecordInfo recordInfo = header.getRecordInfo().get(index);
            int offsetFromFile = (int) recordInfo.getRecordDataOffset();

            if (index < header.getNumberOfRecords() - 1) {
                recordInfo.setData(Arrays.copyOfRange(wholeFileData, offsetFromFile, (int) header.getRecordInfo().get(index + 1).getRecordDataOffset()));
                continue;
            }
            //last one
            recordInfo.setData(Arrays.copyOfRange(wholeFileData, offsetFromFile, wholeFileData.length));
        }

        PalmDOCHeader palmDOCHeader = new PalmDOCHeader();
        palmDOCHeader.read(inputStream);

        MOBIHeader mobiHeader = new MOBIHeader();
        mobiHeader.read(inputStream);

        //if(mobiHeader.)
        EXTHHeader exthHeader = new EXTHHeader();
        // 16 is PalmDOCHeader length
        exthHeader.read(header.getRecord(0), (int) mobiHeader.getHeaderLength() + 16);

        for (int a = (int) mobiHeader.getFirstImageIndex(); a < mobiHeader.getLastContentRecordNumber(); a++) {
            File imageFile = new File("a-" + a + ".jpg");
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
            FileImageOutputStream fileImageOutputStream = new FileImageOutputStream(imageFile);
            fileImageOutputStream.write(header.getRecord(a));
            fileImageOutputStream.close();
        }

        byte[] firstRecord = header.getRecord((int) mobiHeader.getIndxRecordOffset());
        IndexMetaRecord indexMetaRecord = new IndexMetaRecord();
        indexMetaRecord.read(firstRecord);
        TagxSection tagxSection = new TagxSection();
        tagxSection.read(Arrays.copyOfRange(firstRecord, (int) indexMetaRecord.getHeaderLength(), firstRecord.length));
        byte[] decompressData = new byte[(int) mobiHeader.getFirstNonBookIndex() * (int) palmDOCHeader.getRecordSize()];
        for (int a = 1; a <= palmDOCHeader.getRecordCount(); a++) {
            System.arraycopy(header.getDecompressRecord(a, (int) palmDOCHeader.getRecordSize()), 0, decompressData, (a - 1) * (int) palmDOCHeader.getRecordSize(), (int) palmDOCHeader.getRecordSize());
        }
        System.out.println(new String(decompressData));
    }

    private AtomicInteger a = new AtomicInteger(0);
    private final Object lock = new Object();
    @Test
    public void testVolatile() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        
        for (int i = 0;i<1000000;i++){
            executorService.execute(new Increase());
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println(a);

    }
    class Increase implements Runnable{
        @Override
        public void run() {
//            synchronized (lock){
                a.getAndIncrement();   
//            }
        }
    }
}
