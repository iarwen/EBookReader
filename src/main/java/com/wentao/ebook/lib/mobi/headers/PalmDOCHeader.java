package com.wentao.ebook.lib.mobi.headers;

import com.wentao.ebook.annotation.Offset;
import com.wentao.ebook.common.ByteUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

public class PalmDOCHeader {
    //public final int COMPRESSION_NO = 1;
    //public final int COMPRESSION_PALM_DOC = 2;
    /**
     * The higher compression mode is using a Huffman coding scheme that has been called the Huff/cdic algorithm.
     */
    //public final int COMPRESSION_HUFF_CDIC = 17480;
    /**
     * 1 == no compression, 2 = PalmDOC compression, 17480 = HUFF/CDIC compression
     */
    @Offset(2)
    private long compression;
    @Offset(2)
    private String unused;
    /**
     * Uncompressed length of the entire text of the book
     */
    @Offset(4)
    private long uncompressedTextLength;

    /**
     * Number of PDB records used for the text of the book.
     */
    @Offset(2)
    private long recordCount;
    /**
     * Maximum size of each record containing text, always 4096
     */
    @Offset(2)
    private long recordSize;

    /**
     * Current reading position, as an offset into the uncompressed text
     */
    @Offset(4)
    private long currentPosition;
    
    @Offset(2)
    private long encryptionType;
    @Offset(2)
    private long unknown;

    public void read(InputStream in) throws Exception {
        Field[]  fields = this.getClass().getDeclaredFields();
        for(Field field:fields ) {
            field.setAccessible(true);
            Offset offsetAnnotation = field.getAnnotation(Offset.class);
            if (offsetAnnotation != null) {
                if("currentPosition".equals(field.getName()) && 17480 == compression){
                    continue;
                }
                if("encryptionType".equals(field.getName()) && 17480 != compression){
                    continue;
                }
                if("unknown".equals(field.getName()) && 17480 != compression){
                    continue;
                }
                int offset = offsetAnnotation.value();
                byte[] bytes = new byte[offset];
                assert in.read(bytes) == offset;
                if (field.getType() == String.class) {
                    field.set(this, new String(bytes).trim());
                } else if (field.getType() == long.class) {
                    field.set(this, ByteUtils.bytes2long(bytes));
                } else if (field.getType() == Date.class) {
                    field.set(this, ByteUtils.bytes2date(bytes));
                } else if (field.getType() == List.class) {
                    field.set(this, ByteUtils.bytes2date(bytes));
                }
            }
        }
    }

    public long getRecordCount() {
        return recordCount;
    }

    public long getRecordSize() {
        return recordSize;
    }
}
