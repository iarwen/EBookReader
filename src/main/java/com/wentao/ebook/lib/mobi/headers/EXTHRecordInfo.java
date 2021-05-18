package com.wentao.ebook.lib.mobi.headers;

import com.wentao.ebook.annotation.Offset;
import com.wentao.ebook.annotation.Repeat;
import com.wentao.ebook.common.ByteUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EXTHRecordInfo {
    /**
     * 	Exth Record type. Just a number identifying what's stored in the record
     */
    @Offset(4)
    private long recordType;
    /**
     * length of EXTH record = L , including the 8 bytes in the type and length fields
     */
    @Offset(4)
    private long recordLength;
    /**
     * 	The number of records in the EXTH header. 
     * 	the rest of the EXTH header consists of repeated EXTH records to the end of the EXTH length.
     */
    @Offset(value = -1, variableValue = true)
    private String recordData;


    public void read(byte[] record0, int startPos) throws Exception {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Offset offsetAnnotation = field.getAnnotation(Offset.class);
            if (offsetAnnotation != null) {
                int offset = offsetAnnotation.variableValue()? (int) (recordLength - 8) : offsetAnnotation.value();
                byte[] bytes = Arrays.copyOfRange(record0, startPos, startPos+offset);
                startPos += offset;
                if (field.getType() == String.class) {
                    field.set(this, new String(bytes).trim());
                } else if (field.getType() == long.class) {
                    field.set(this, ByteUtils.bytes2long(bytes));
                }
            }
        }
    }

    public long getRecordType() {
        return recordType;
    }

    public long getRecordLength() {
        return recordLength;
    }

    public String getRecordData() {
        return recordData;
    }
}
