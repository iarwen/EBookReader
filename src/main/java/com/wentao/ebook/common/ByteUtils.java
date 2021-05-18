package com.wentao.ebook.common;

import java.util.Date;

public class ByteUtils {
    public static long bytes2long(byte[] bs) throws Exception {
        switch (bs.length) {
            case 0:
                return 0;
            case 1:
                return (bs[0] & 0xffL);
            case 2:
                return (bs[0] & 0xffL) << 8 | (bs[1] & 0xffL);
            case 3:
                return (bs[0] & 0xffL) << 16 | (bs[1] & 0xffL) << 8| (bs[2] & 0xffL);
            case 4:
                return (bs[0] & 0xffL) << 24 | (bs[1] & 0xffL) << 16 | (bs[2] & 0xffL) << 8 | (bs[3] & 0xffL);
            case 8:
                return (bs[0] & 0xffL) << 56 | (bs[1] & 0xffL) << 48 | (bs[2] & 0xffL) << 40 | (bs[3] & 0xffL) << 32 |
                        (bs[4] & 0xffL) << 24 | (bs[5] & 0xffL) << 16 | (bs[6] & 0xffL) << 8 | (bs[7] & 0xffL);
            default:
                throw new Exception("not support");
        }
    }

    public static Date bytes2date(byte[] input) throws Exception {
        long diffSeconds = bytes2long(input);
        if (diffSeconds == 0) {
            return null;
        }
        boolean topBitSet = input[0] < 0;
        Date baseDate = topBitSet ? TimeUtils.BASE_DATE_1904 : TimeUtils.BASE_DATE_1970;
        return new Date(baseDate.getTime() + diffSeconds * 1000);
    }
     
}
