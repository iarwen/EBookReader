package com.wentao.ebook.lib.mobi.headers;

import com.wentao.ebook.annotation.Offset;
import com.wentao.ebook.common.ByteUtils;
import com.wentao.ebook.common.LZ77;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

public class PalmDatabaseFormatRecordInfo {
    private byte[] data;
    /**
     * the offset of record n from the start of the PDB of this record
     */
    @Offset(4)
    private long recordDataOffset;

    /**
     * bit field. The least significant four bits are used to represent the category values. These are the categories used to split the databases for viewing on the screen. A few of the 16 categories are pre-defined but the user can add their own. There is an undefined category for use if the user or programmer hasn't set this.
     * 0x10 (16 decimal) Secret record bit.
     *
     * 0x20 (32 decimal) Record in use (busy bit).
     *
     * 0x40 (64 decimal) Dirty record bit.
     *
     * 0x80 (128, unsigned decimal) Delete record on next HotSync.
     */
    @Offset(1)
    private long recordAttributes;

    /**
     * The unique ID for this record. Often just a sequential count from 0
     */
    @Offset(3)
    private long uniqueID;

    public void read(InputStream in) throws Exception {
        Field[]  fields = this.getClass().getDeclaredFields();
        for(Field field:fields ){
            field.setAccessible(true);
            Offset offsetAnnotation = field.getAnnotation(Offset.class);
            if(offsetAnnotation!=null){
                int offset = offsetAnnotation.value();
                byte[] bytes = new byte[offset];
                assert in.read(bytes) == offset;
                if(field.getType() == String.class){
                    field.set(this, new String(bytes).trim());
                } else if(field.getType() == long.class){
                    field.set(this, ByteUtils.bytes2long(bytes));
                } else if(field.getType() == Date.class){
                    field.set(this, ByteUtils.bytes2date(bytes));
                } else if (field.getType() == List.class){
                    field.set(this, ByteUtils.bytes2date(bytes));
                }
            }
        }
    }

    public long getRecordDataOffset() {
        return recordDataOffset;
    }

    public long getRecordAttributes() {
        return recordAttributes;
    }

    public long getUniqueID() {
        return uniqueID;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    public String getContent(int recordSize) throws Exception {
        return new String(LZ77.decompress(data, recordSize));
    }
}
