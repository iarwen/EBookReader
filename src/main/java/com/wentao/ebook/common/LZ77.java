package com.wentao.ebook.common;

public class LZ77 {

	public static byte[] decompress(byte[] compressBytes, int recordSize) throws Exception {
		byte[] decompressBytes = new byte[recordSize];
		int index = 0;
		for(int i=0;i<compressBytes.length;i++){
			if(index >=recordSize) continue;
			byte b = compressBytes[i];
			long v = ByteUtils.bytes2long(new byte[]{b});
			if(v>=0x09 && v<=0x7f){  //done
				decompressBytes[index] = b;
				index++;
			} else if(v>=0x01 && v<=0x08){ // done
				for(int ii = 0;ii<v;ii++){
					decompressBytes[index] = compressBytes[i+ii+1];
					index++;
				}
				i=i+(int)v;
			} else if(v>=0x80 && v<=0xbf){ // done
				i++;
				if(compressBytes.length == i){
					decompressBytes[index] = b;
					index++;
					continue;
				}
				long vv = ByteUtils.bytes2long(new byte[]{compressBytes[i]});
				StringBuilder vvString = new StringBuilder(Integer.toBinaryString(((int) vv)));
				while (vvString.length()<8){
					vvString.insert(0, "0");
				}
				String byteStr = Integer.toBinaryString((int)v) + vvString;
				int distance = Integer.parseInt(byteStr.substring(2, 13), 2);
				int repeat = 3 + Integer.parseInt(byteStr.substring(13), 2);
				int startCopy = index - distance;
				if(startCopy < 0){
					continue;
				}
				for(int ii = 0; ii< repeat; ii++){
					if(startCopy+ii >= index + 1) continue;
					decompressBytes[index] = decompressBytes[startCopy+ii];
					index++;
				}
			} else if(v>=0xc0 && v<=0xff){ // done
				decompressBytes[index] = (byte)0x20;
				index++;
				decompressBytes[index] = (byte)(v^0x80);
				index++;
			} else {  //0x00
				decompressBytes[index] = b;
				index++;
			}
		}
		return decompressBytes;
	}
}