package org.winplus.serial.utils;

import java.util.Arrays;

import android.util.Log;

/**
 * @author houen.bao
 * @date Jul 1, 2016 2:21:34 PM
 */
public class CommandUtils {

	private static final byte HEAD = (byte) 0xf0;

	private static byte[] packageCommand(byte[] datas) {
		Log.v("tt","\n\ndata0: "+Arrays.toString(datas));
		int size = datas.length + 2;
		byte[] commands = new byte[size];
		commands[0] = HEAD;
		if (datas[0] < 0) {
			datas[0] = (byte) ((-datas[0]) | 0x80);
		}
		commands[1] = datas[0];
		if (datas[1] < 0) {
			datas[1] = (byte) ((-datas[0]) | 0x80);
		}
		commands[2] = datas[1];

		if (datas[2] >= 13) {
			datas[2] = (byte) 0xff;
		} else {
			datas[2] = (byte) ((datas[2] *1000)/50);
		}
		commands[3] = datas[2];

		byte temp=0;
		for (int i = 0; i < datas.length; i++) {
			temp+=commands[i+1];
		}
		commands[4]=(byte) (0xff-temp);

		Log.v("tt","data1: "+Arrays.toString(commands));
		return commands;
	}

	public static byte[] packageCommand(byte progressZZ,
			byte progressYZ, byte progressTime) {
		return packageCommand(new byte[] { progressZZ, progressYZ, progressTime });
	}
	

}
