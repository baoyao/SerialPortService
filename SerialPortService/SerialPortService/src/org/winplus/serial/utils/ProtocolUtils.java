package org.winplus.serial.utils;

import java.util.Arrays;

import android.util.Log;

/**
 * @author houen.bao
 * @date Jul 1, 2016 2:21:34 PM
 */
public class ProtocolUtils {

	private static final byte HEAD = (byte) 0xf0;

	private static byte[] packageCommand(byte[] commands) {
		Log.v("tt","\n\ndata0: "+Arrays.toString(commands));
		int size = commands.length + 2;
		byte[] datas = new byte[size];
		datas[0] = HEAD;
		if (commands[0] < 0) {
			commands[0] = (byte) ((-commands[0]) | 0x80);
		}
		datas[1] = commands[0];
		if (commands[1] < 0) {
			commands[1] = (byte) ((-commands[0]) | 0x80);
		}
		datas[2] = commands[1];

		if (commands[2] >= 13) {
			commands[2] = (byte) 0xff;
		} else {
			commands[2] = (byte) ((commands[2] *1000)/50);
		}
		datas[3] = commands[2];

		byte temp=0;
		for (int i = 0; i < commands.length; i++) {
			temp+=datas[i+1];
		}
		datas[4]=(byte) (0xff-temp);

		Log.v("tt","data1: "+Arrays.toString(datas));
		return datas;
	}

	public static byte[] packageForwardCommand(byte progressZZ,
			byte progressYZ, byte progressTime) {
		return packageCommand(new byte[] { progressZZ, progressYZ, progressTime });
	}
	

}
