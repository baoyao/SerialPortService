package org.winplus.serial.utils;

import java.util.Arrays;

import android.util.Log;

/**
 * @author houen.bao
 * @date Jul 1, 2016 2:21:34 PM
 */
public class CommandUtils {

	private static final String TAG="tt";
	
	private static final byte COMMAND_HEAD_FORWARD = (byte) 0xf0;
	private static final byte COMMAND_HEAD_LED = (byte) 0xf1;

	private static byte[] packageForwardCommand(byte[] datas) {
		Log.v(TAG,"\n\nTemp Forward Commands: "+Arrays.toString(datas));
		int size = datas.length + 2;
		byte[] commands = new byte[size];
		commands[0] = COMMAND_HEAD_FORWARD;
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

		Log.v(TAG,"Forward Commands: "+Arrays.toString(commands));
		return commands;
	}

	public static byte[] packageForwardCommand(byte progressZZ,
			byte progressYZ, byte progressTime) {
		return packageForwardCommand(new byte[] { progressZZ, progressYZ, progressTime });
	}
	
	
	public static byte[] packageLedCommand(){
		byte[] left = new byte[] { (byte) 0x81, 0x42, 0x42, 0x18, 0x18, 0x24,
				0x42, (byte) 0x81 };
		byte[] right = new byte[] { (byte) 0xff, (byte) 0x83, (byte) 0x85,
				(byte) 0x89, (byte) 0x91, (byte) 0xa1, (byte) 0xc1, (byte) 0xff };
		byte[] mouth = new byte[] { (byte) 0xff, 0x01, (byte) 0xff,
				(byte) 0x80, (byte) 0xff, 0x01, (byte) 0xff, (byte) 0xc0 };

		int count = left.length + right.length + mouth.length;
		byte[] tempCommands = new byte[count];
		for (int i = 0; i < tempCommands.length; i++) {
			if (i < left.length) {
				tempCommands[i] = left[i];
			} else if (i < (left.length + right.length)) {
				tempCommands[i] = right[i - (left.length)];
			} else if (i < (left.length + right.length + mouth.length)) {
				tempCommands[i] = mouth[i - (left.length + right.length)];
			}
		}

		byte lastData1 = COMMAND_HEAD_LED;
		byte lastData2 = 0;
		byte[] commands = new byte[count+1];
		for (int i = 0; i < commands.length; i++) {
			if(i<tempCommands.length){
				lastData2 = tempCommands[i];
			}
			commands[i] = lastData1;
			lastData1 = lastData2;
		}
		Log.v(TAG,"\n\nLed Commands: "+Arrays.toString(commands));
		return commands;
	}
	

}
