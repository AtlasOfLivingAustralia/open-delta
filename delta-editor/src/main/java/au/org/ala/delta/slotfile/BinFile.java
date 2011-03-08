/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.slotfile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.SyncFailedException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * //binfile.cpp
 * 
 * //Encapsulate a binary data file similar to TFile //but incorporate exception
 * handling. // //Currently uses low level c function. //Could be rewritten to
 * use ansi compatible FILE functions or c++ streams.
 * 
 */
public class BinFile {

	protected String _filename;
	protected BinFileMode _fileMode;
	protected RandomAccessFile _file;
	protected byte[] _buffer;
	protected int _filePointer;
	
	private BinFileStats _stats = new BinFileStats();

	protected FileChannel _channel;

	public BinFile() {
	}

	public BinFile(String filename, BinFileMode mode) {

		if (filename == null) {
			filename = makeTempFileName();
			mode = BinFileMode.FM_TEMPORARY;
		}
		_filename = filename;
		open(mode);
	}

	public void write(byte[] data) {
		// try {
		// _file.write(data);
		writeBytes(data);
		// } catch (IOException ioex) {
		// throw new RuntimeException(ioex);
		// }
	}

	public byte[] read(int length) {
		return readBytes(length);
	}

	protected String makeTempFileName() {
		try {
			return File.createTempFile("delta_temp_file", ".dlt")
					.getAbsolutePath();
		} catch (IOException ioex) {
			throw new RuntimeException(ioex);
		}
	}

	public void open(BinFileMode mode) {
		_fileMode = mode;
		File f = new File(_filename);
		_filename = f.getAbsolutePath();
		try {
			String ra_mode = "r";
			switch (mode) {
			case FM_APPEND:
			case FM_NEW:
			case FM_EXISTING:
			case FM_TEMPORARY:
				ra_mode = "rw";
				break;
			}
			_file = new RandomAccessFile(f, ra_mode);
			_channel = _file.getChannel();

			/*
			 * if (mode == BinFileMode.FM_READONLY) { CodeTimer t = new
			 * CodeTimer("loading file buffer"); _file.seek(0); _buffer = new
			 * byte[(int) _file.length()]; _file.read(_buffer); t.stop(true);
			 * _file.seek(0); }
			 */

		} catch (IOException ioex) {
			throw new RuntimeException(ioex);
		}
	}

	public void close() {
		/*
		 * if (_file != null) { try { _file.close(); _file = null; } catch
		 * (IOException ioex) { throw new RuntimeException(ioex); } }
		 */
		if (_channel != null) {
			try {
				_channel.close();
				_channel = null;
			} catch (IOException ioex) {
				throw new RuntimeException(ioex);
			}
		}
		if (_buffer != null) {
			_filePointer = 0;
			_buffer = null;
		}
		// Clean up the file after closing if it is a temporary file.
		if (_fileMode == BinFileMode.FM_TEMPORARY) {
			File temp = new File(_filename);
			temp.delete();
		}
	}

	public boolean isOpen() {
		return _channel != null;
	}

	public int seek(int offset) {

		if (_buffer != null) {
			_filePointer = offset;
		} else {
			assert _channel != null;
			try {
				// _file.seek(offset);
				_channel.position(offset);
			} catch (IOException ioex) {
				throw new RuntimeException(ioex);
			}
		}
		return offset;
	}

	public int seekToEnd() {
		assert _channel != null;
		try {
			// return seek((int)_file.length());
			return seek((int) _channel.size());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public int seekToBegin() {
		assert _channel != null;

		return seek(0);
	}

	public ByteBuffer readByteBuffer(int size) {
		ByteBuffer bb = ByteBuffer.allocate(size);
		try {
			int bytesRead = _channel.read(bb);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.position(0);
			assert bytesRead == size;
		} catch (IOException ioex) {
			throw new RuntimeException(ioex);
		}
		return bb;
	}

	public int readBytes(byte[] buffer) {
		if (_buffer != null) {
			for (int i = 0; i < buffer.length; ++i) {
				buffer[i] = _buffer[_filePointer];
				_filePointer++;
			}
			return buffer.length;
		} else {
			try {
				ByteBuffer bb = ByteBuffer.allocate(buffer.length);
				int bytesRead = _channel.read(bb);
				bb.position(0);
				bb.get(buffer, 0, buffer.length);
				return bytesRead;
			} catch (IOException ioex) {
				throw new RuntimeException(ioex);
			}
		}
	}

	public int tell() {
		if (_buffer != null) {
			return _filePointer;
		} else {
			try {
				// return (int) _file.getFilePointer();
				return (int) _channel.position();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	protected byte[] readBytes(int count) {
		ByteBuffer b = readByteBuffer(count);
		_stats.ReadBytes++;
		return b.array();
	}

	public void writeBytes(byte[] buffer) {
		try {
			ByteBuffer bb = ByteBuffer.allocate(buffer.length);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.put(buffer);
			bb.position(0);
			_channel.write(bb);
			// _file.write(buffer);
		} catch (IOException ioex) {
			throw new RuntimeException(ioex);
		}
	}

	private void writeBytes(ByteBuffer buffer) {
		try {
			buffer.position(0);
			_channel.write(buffer);
		} catch (IOException ioex) {
			throw new RuntimeException(ioex);
		}
	}

	public void writeByte(byte b) {
		try {
			// _file.write(b);
			ByteBuffer bb = ByteBuffer.allocate(1);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.put(b);
			bb.position(0);
			_channel.write(bb);
		} catch (IOException ioex) {
			throw new RuntimeException(ioex);
		}
	}

	public void writeShort(short value) {
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putShort(value);
		writeBytes(buffer);
	}

	public void write(int i) {
		writeInt(i);
	}

	public void write(short i) {
		writeShort(i);
	}

	public void write(byte b) {
		writeByte(b);
	}

	public void write(String str, int length) {
		swrite(str, length);
	}

	public void write(long lng) {
		writeLong(lng);
	}

	public void writeInt(int value) {
		ByteBuffer b = ByteBuffer.allocate(4);
		b.order(ByteOrder.LITTLE_ENDIAN);
		b.putInt(value);
		writeBytes(b);
	}

	public void writeLong(long value) {
		ByteBuffer b = ByteBuffer.allocate(8);
		b.order(ByteOrder.LITTLE_ENDIAN);
		b.putLong(value);
		writeBytes(b);
	}

	/*
	 * protected int read() { if (_buffer != null) { if (_filePointer >=
	 * _buffer.length) { return -1; } int result = _buffer[_filePointer];
	 * _filePointer++; return result & 0x000000ff; } else { try { return
	 * _file.read(); } catch (IOException ioex) { throw new
	 * RuntimeException(ioex); } } }
	 */

	public byte readByte() {
		ByteBuffer b = readByteBuffer(1);
		_stats.ReadByte++;		
		return b.get();
	}

	public short readShort() {
		ByteBuffer b = readByteBuffer(2);		
		_stats.ReadShort++;
		return b.getShort();
	}

	public long readLong() {
		ByteBuffer b = readByteBuffer(8);
		_stats.ReadLong++;		
		return b.getLong();
	}

	public int readInt() {
		ByteBuffer b = readByteBuffer(4);		
		_stats.ReadInt++;
		return b.getInt();
	}

	// Not really sure what swrite is all about yet...
	public void swrite(byte[] data) {
		write(data);
	}

	public void swrite(String data, int length) {
		byte[] buffer = new byte[length];
		byte[] stringBytes = SlotFileEncoding.encode(data);
		for (int i = 0; i < length; ++i) {
			if (i < stringBytes.length) {
				buffer[i] = stringBytes[i];
			} else {
				buffer[i] = 0;
			}
		}
		write(buffer);
	}

	public String sread(int size) {
		ByteBuffer bb = readByteBuffer(size);
		return SlotFileEncoding.decode(bb.array());
	}

	/**
	 * Forces the contents of this file to be written to disk.
	 */
	public void commit() {

		try {
			// _file.getFD().sync();
			_channel.force(false);
		} catch (SyncFailedException e) {
			// TODO uhoh, what do we do if the sync fails... (i am not sure why
			// it would)
			e.printStackTrace();
		} catch (IOException e) {
			// TODO mmm file descriptor was null - that is bad.
			e.printStackTrace();
		}
		if (_fileMode == BinFileMode.FM_TEMPORARY) {
			_fileMode = BinFileMode.FM_EXISTING;
		}
	}

	public String getFileName() {
		return _filename;
	}

	public long getFileTime() {
		File f = new File(_filename);
		return f.lastModified();
	}

	public void setFileTime(long time) {
		File f = new File(_filename);
		f.setLastModified(time);
	}

	public BinFileMode getFileMode() {
		return _fileMode;
	}

	public void copyFile(BinFile other, int dataSize) {
		// Make sure other is not this.
		if (other == this) {
			// TODO create a BinFileException
			throw new RuntimeException("FE_BAD_COPY : " + _filename);
		}
		// Copy in blocks.
		int blkSize = 1024 * 8; // 8K
		int numBlk = dataSize / blkSize;
		int rest = dataSize % blkSize;
		byte[] buf = new byte[blkSize];
		for (int i = 0; i < numBlk; i++) {
			other.readBytes(buf);
			swrite(buf);
		}
		buf = new byte[rest];
		other.readBytes(buf);
		swrite(buf);
	}

	public void setLength(int newLength) {

		try {
			// _file.setLength(newLength);
			int currentPos = tell();
			_channel.position(_channel.size());
			long growSize = newLength - _channel.size();
			ByteBuffer dummyBuffer = ByteBuffer.allocate((int)growSize);
			for (int i=0; i < growSize; i++) {
				dummyBuffer.put((byte)0);
			}
			dummyBuffer.position(0);
			_channel.write(dummyBuffer);
			seek(currentPos);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public int getLength() {
		try {
			// return (int)_file.length();
			return (int) _channel.size();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void dumpStats() {
		_stats.dump();
	}

}

class BinFileStats {
	
	public int ReadByte = 0;
	public int ReadInt = 0;
	public int ReadShort = 0;
	public int ReadLong = 0;
	public int ReadBytes = 0;
	
	public void dump() {
		System.out.printf("ReadByte: %d\nReadInt: %d\nReadShort: %d\nReadLong: %d\nReadBytes: %d\n", ReadByte, ReadInt, ReadShort, ReadLong, ReadBytes);
	}
	
}
