package org.apache.etch.util.core.io;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;

public class AoapPacketizer {
	private static final String TAG = "AoapPacketizer";
	
	private static final int AOAP_AUDIO_PACKET 	= 1;
	private static final int AOAP_ETCH_PACKET 	= 2;	

	private static final byte[] AOAP_PREAMBLE = { 'O', 'B', 'G', 'P', 'K', 'T'};
	
	private static final int AOAP_PACKET_PREAMBLE_SIZE = 6;
	private static final int AOAP_PACKET_TYPE_POS = AOAP_PACKET_PREAMBLE_SIZE;
	private static final int AOAP_PACKET_TYPE_SIZE = 1;	
	private static final int AOAP_PACKET_LENGTH_POS = AOAP_PACKET_TYPE_POS + AOAP_PACKET_TYPE_SIZE;
	private static final int AOAP_PACKET_LENGTH_SIZE = 4;
	private static final int AOAP_PACKET_CRC_POS = AOAP_PACKET_LENGTH_POS + AOAP_PACKET_LENGTH_SIZE;
	private static final int AOAP_PACKET_CRC_SIZE = 8;
	private static final int AOAP_PACKET_HEADER_LENGTH = AOAP_PACKET_CRC_POS + AOAP_PACKET_CRC_SIZE;
	private static final int AOAP_PACKET_DATA_POS = AOAP_PACKET_HEADER_LENGTH;
	
	
// ============================================================================
//	//- AOAP PACKET FORMAT 2013.10.28 -//
//	Described AOAP packet format as following. 
//	
//	---------------------------------------------------------------------------
//	PREABLE(6bytes) | PACKET TYPE(1bytes) | PACKET LENGTH(4bytes) | CRC(8bytes)
//	---------------------------------------------------------------------------
//									Data
//	---------------------------------------------------------------------------
//	
//	PREABLE 		: 'OBGPKT' ASCII characters consist of 6bytes
//	PACKET TYPE		: e.g. AOAP_AUDIO_PACKET, AOAP_ETCH_PACKET
//	PACKET LENGTH 	: Data length
//	CRC				: 32 bits CRC
//
// ----------------------------------------------------------------------------
	
	/**
	 * @param type aoap packet type
	 * @param src source address 
	 * @param dst destination address
	 * @return return packet size
	 */
	public int aoapSetPacket(byte type, ByteBuffer src, ByteBuffer dst)
	{
		if(src.array().length > 0) {
			int retLength = 0;
			// Preamble
			dst.put(AOAP_PREAMBLE);
			retLength += AOAP_PREAMBLE.length;
			// Type
			dst.putShort(type);
			retLength += AOAP_PACKET_TYPE_SIZE;
			// length
			dst.putInt(src.array().length);
			retLength += AOAP_PACKET_LENGTH_SIZE;
			// Checksum
			CRC32 checksum = new CRC32();
			checksum.update(src.array());
			long val = checksum.getValue();
			dst.put(src.array());
			retLength += AOAP_PACKET_CRC_SIZE;			
			// Data
			retLength += src.array().length;
			return retLength;
		}
		
		return 0;
	}

	public int aoapUnPacket(byte type, ByteBuffer src, ByteBuffer dst)
	{
		if(checkAoapPacket(src))
		{
			int retLength = checkSize(src);
			System.arraycopy(src.array(), AOAP_PACKET_DATA_POS, dst.array(), AOAP_PACKET_DATA_POS, retLength);
			return retLength;
		}
		
		return 0;
	}
	
	public byte getAoapPacketType(ByteBuffer src)
	{
		ByteBuffer typeBuf = src.get(src.array(), AOAP_PACKET_TYPE_POS, AOAP_PACKET_TYPE_SIZE);
		Log.d(TAG, "Pakcet Type is " + typeBuf.get());
		return typeBuf.get();
	}
	
	public boolean checkAoapPacket(ByteBuffer src)
	{
		if(checkPreamble(src))
			if(checkSize(src) > 0)
				if(checkCRC(src))
					return true;
		
		Log.d(TAG, "Error packet");
		return false;
	}
	
	private boolean checkPreamble(ByteBuffer src)
	{	
		ByteBuffer buf = src.get(src.array(), 0, AOAP_PREAMBLE.length);
		
		return Arrays.equals(AOAP_PREAMBLE, buf.array());
	}
	
	private int checkSize(ByteBuffer src)
	{
		int realLength = src.array().length - AOAP_PACKET_HEADER_LENGTH;
		ByteBuffer lengthBuf = src.get(src.array(), AOAP_PACKET_LENGTH_POS, AOAP_PACKET_LENGTH_SIZE);

		int headerLength = lengthBuf.getInt();
		
		if(realLength == headerLength)
			return headerLength;
		
		return 0;
	}
	
	private boolean checkCRC(ByteBuffer src)
	{
		ByteBuffer crcBuf = src.get(src.array(), AOAP_PACKET_CRC_POS, AOAP_PACKET_CRC_SIZE);
		ByteBuffer dataBuf = src.get(src.array(), AOAP_PACKET_DATA_POS, src.array().length - AOAP_PACKET_DATA_POS);
		CRC32 crc = new CRC32();
		
		crc.update(src.array(), AOAP_PACKET_DATA_POS, src.array().length - AOAP_PACKET_DATA_POS);
		
		long calculatedCRC = crc.getValue();
		
		if(calculatedCRC == crcBuf.getLong())
			return true;
		
		return false;
	}
}
