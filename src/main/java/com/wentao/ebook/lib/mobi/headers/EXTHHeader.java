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

public class EXTHHeader {
    /**
     * the characters  E X T H
     */
    @Offset(4)
    private String identifier;
    /**
     * the length of the EXTH header, including the previous 4 bytes
     */
    @Offset(4)
    private long headerLength;
    /**
     * 	The number of records in the EXTH header. 
     * 	the rest of the EXTH header consists of repeated EXTH records to the end of the EXTH length.
     */
    @Offset(4)
    private long recordCount;
    
    /**
     * 	EXTH records
     */
    @Repeat
    private List<EXTHRecordInfo> exthRecords;
    private int readPos = 0;
    public void read(byte[] record0, int start) throws Exception {
        readPos = start;
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Offset offsetAnnotation = field.getAnnotation(Offset.class);
            if (offsetAnnotation != null) {
                int offset = offsetAnnotation.value();
                byte[] bytes = Arrays.copyOfRange(record0, readPos, readPos+offset);
                readPos+=offset;
                if (field.getType() == String.class) {
                    field.set(this, new String(bytes).trim());
                } else if (field.getType() == long.class) {
                    field.set(this, ByteUtils.bytes2long(bytes));
                }
            }
            Repeat repeatAnnotation = field.getAnnotation(Repeat.class);
            if(repeatAnnotation!=null){
                Type genericType = field.getGenericType();
                if(genericType instanceof ParameterizedType){
                    ParameterizedType pt = (ParameterizedType) genericType;
                    Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0];
                    if(genericClazz == EXTHRecordInfo.class){
                        List<EXTHRecordInfo> records = new ArrayList<>();
                         
                        for(int i = 0 ;i<this.recordCount;i++){
                            EXTHRecordInfo info = new EXTHRecordInfo();
                            info.read(record0, readPos);
                            readPos+=info.getRecordLength();
                            records.add(info);
                        }
                        field.set(this, records);
                    }
                }
            }
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public long getHeaderLength() {
        return headerLength;
    }

    public long getRecordCount() {
        return recordCount;
    }

    public List<EXTHRecordInfo> getExthRecords() {
        return exthRecords;
    }

    public int getReadPos() {
        return readPos;
    }
}
