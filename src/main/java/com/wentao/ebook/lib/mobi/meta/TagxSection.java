package com.wentao.ebook.lib.mobi.meta;

import com.wentao.ebook.annotation.Offset;
import com.wentao.ebook.common.ByteUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

public class TagxSection {
    /**
     * the characters T A G X
     */
    @Offset(4)
    private String identifier;
    /**
     * 	the length of the TAGX header, including the previous 4 bytes
     */
    @Offset(4)
    private long headerLength;

    /**
     * 	the number of control bytes
     */
    @Offset(4)
    private long controlByteCount;

    @Offset(-1)
    private byte[] tagTable;
 

    private int readPos = 0;
    public void read(byte[] recordIndex) throws Exception {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Offset offsetAnnotation = field.getAnnotation(Offset.class);
            if (offsetAnnotation != null) {
                int offset = offsetAnnotation.value();
                if (offset < 0) {
                    offset = (int) headerLength - 12;
                }
                byte[] bytes = Arrays.copyOfRange(recordIndex, readPos, readPos + offset);
                readPos += offset;
                if (field.getType() == String.class) {
                    field.set(this, new String(bytes).trim());
                } else if (field.getType() == long.class) {
                    field.set(this, ByteUtils.bytes2long(bytes));
                } else if (field.getType() == byte[].class) {
                    field.set(this, bytes);
                }
            }
        }
    }
}
