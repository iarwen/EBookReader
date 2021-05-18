package com.wentao.ebook.lib.mobi.meta;

import com.wentao.ebook.annotation.Offset;
import com.wentao.ebook.common.ByteUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

public class IndexMetaRecord {
    /**
     * the characters I N D X
     */
    @Offset(4)
    private String identifier;
    /**
     * the length of the INDX header, including the previous 4 bytes
     */
    @Offset(4)
    private long headerLength;

    /**
     * the type of the index. Known values: 0 - normal index, 2 - inflections
     */
    @Offset(4)
    private long indexType;

    @Offset(4)
    private long unknown1;
    @Offset(4)
    private long unknown2;

    /**
     * the offset to the IDXT section
     */
    @Offset(4)
    private long idxtStart;

    /**
     * the number of index records
     */
    @Offset(4)
    private long indexCount;

    /**
     * 1252 = CP1252 (WinLatin1); 65001 = UTF-8
     */
    @Offset(4)
    private long indexEncoding;

    /**
     * the language code of the index
     * 09= English, next byte is dialect, 08 = British, 04 = US.
     * Thus US English is 1033, UK English is 2057.
     */
    @Offset(4)
    private long indexLanguage;

    /**
     * the number of index entries
     */
    @Offset(4)
    private long totalIndexCount;

    /**
     * the offset to the ORDT section
     */
    @Offset(4)
    private long ordtStart;

    /**
     * the offset to the ligt section
     */
    @Offset(4)
    private long ligtStart;

    @Offset(4)
    private long unknown3;
    @Offset(4)
    private long unknown4;

    private int readPos = 0;

    public void read(byte[] recordIndex) throws Exception {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Offset offsetAnnotation = field.getAnnotation(Offset.class);
            if (offsetAnnotation != null) {
                int offset = offsetAnnotation.value();
                byte[] bytes = Arrays.copyOfRange(recordIndex, readPos, readPos + offset);
                readPos += offset;
                if (field.getType() == String.class) {
                    field.set(this, new String(bytes).trim());
                } else if (field.getType() == long.class) {
                    field.set(this, ByteUtils.bytes2long(bytes));
                }
            }
        }
    }

    public long getHeaderLength() {
        return headerLength;
    }
}
