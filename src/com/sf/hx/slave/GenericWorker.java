package com.sf.hx.slave;

import com.sf.biocapture.entity.PassportData;
import com.sf.hx.data.GenericService;
import com.sf.hx.queue.LocalBaseEntityBlockingBuffer;

public class GenericWorker extends Worker<Object> {

	PassportData passport = new PassportData();
	private GenericService dbService = new GenericService();

	public GenericWorker(LocalBaseEntityBlockingBuffer<Object> buffer, String hexDirectoryForBackup,
			boolean backUpHexToFileBeforeConverting, boolean storeHexToFileAfterConverting,
			String hexDirectoryForBackupOfNewByte) {
		super(buffer, hexDirectoryForBackup, backUpHexToFileBeforeConverting, storeHexToFileAfterConverting,
				hexDirectoryForBackupOfNewByte);
	}

	@Override
	public Long getIdOfObject(Object obj) {
		return null;
	}

	@Override
	public byte[] getByteArray(Object obj) {
		passport = (PassportData) obj;

		return passport.getPassportData();
	}

	@Override
	public Object setByteArray(byte[] image, Object obj) {
		passport = (PassportData) obj;
		passport.setPassportData(image);
		return passport;

	}

	@Override
	public void updateFixedRecord(Object obj) {
		dbService.update(obj);

	}

}
