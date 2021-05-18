package com.wentao.ebook.lib.mobi.headers;

import com.wentao.ebook.annotation.Offset;
import com.wentao.ebook.annotation.Repeat;
import com.wentao.ebook.common.ByteUtils;
import com.wentao.ebook.common.LZ77;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PalmDatabaseFormatHeader {
    @Offset(32)
    private String name;
    @Offset(2)
    private int attributes;
    @Offset(2)
    private int version;
    @Offset(4)
    private Date creationDate;
    @Offset(4)
    private Date modificationDate;
    @Offset(4)
    private Date lastBackupDate;
    @Offset(4)
    private long modificationNumber;
    @Offset(4)
    private String appInfoID;
    @Offset(4)
    private String sortInfoID;
    @Offset(4)
    private String type;
    @Offset(4)
    private String creator;
    @Offset(4)
    private long uniqueIDSeed;
    @Offset(4)
    private String nextRecordListID;
    @Offset(2)
    private long numberOfRecords;
    @Repeat
    private List<PalmDatabaseFormatRecordInfo> recordInfo;

    public String getName() {
        return name;
    }

    public int getAttributes() {
        return attributes;
    }

    public int getVersion() {
        return version;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public Date getLastBackupDate() {
        return lastBackupDate;
    }

    public long getModificationNumber() {
        return modificationNumber;
    }

    public String getAppInfoID() {
        return appInfoID;
    }

    public String getSortInfoID() {
        return sortInfoID;
    }

    public String getType() {
        return type;
    }

    public String getCreator() {
        return creator;
    }

    public long getUniqueIDSeed() {
        return uniqueIDSeed;
    }

    public String getNextRecordListID() {
        return nextRecordListID;
    }

    public long getNumberOfRecords() {
        return numberOfRecords;
    }

    public List<PalmDatabaseFormatRecordInfo> getRecordInfo() {
        return recordInfo;
    }

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
                }
            }
            Repeat repeatAnnotation = field.getAnnotation(Repeat.class);
            if(repeatAnnotation!=null){
                Type genericType = field.getGenericType();
                if(genericType instanceof ParameterizedType){
                    ParameterizedType pt = (ParameterizedType) genericType;
                    Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0];
                    if(genericClazz == PalmDatabaseFormatRecordInfo.class){
                        List<PalmDatabaseFormatRecordInfo> records = new ArrayList<>();
                        for(int i = 0 ;i<this.numberOfRecords;i++){
                            PalmDatabaseFormatRecordInfo info = (PalmDatabaseFormatRecordInfo)genericClazz.newInstance();
                            info.read(in);
                            records.add(info);
                        }
                        field.set(this, records);
                    }
                }   
            }
        }
    }
    
    public byte[] getRecord(int index){
        return this.getRecordInfo().get(index).getData();
    }
    public byte[] getDecompressRecord(int index, int recordSize) throws Exception {
        return LZ77.decompress(getRecord(index), recordSize);
    }
     
}
