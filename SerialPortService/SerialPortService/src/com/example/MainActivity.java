package com.example;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.winplus.serial.utils.ISerialPortService;
import org.winplus.serial.utils.ProtocolUtils;
import org.winplus.serial.utils.SerialPort;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String path = "/dev/ttyS3";
	private int baudrate = 115200;
	private ReadThread mReadThread;
	private TextView mTextView;
	private Button mButton1;
	private EditText mETData;
	private Button mBTNSend;
	private SeekBar mSeekBarZZ,mSeekBarYZ,mSeekBarTime;
	private TextView mProgressZZValue,mProgressYZValue,mProgressTimeValue;
	private int mProgressZZ,mProgressYZ,mProgressTime;
	
	private boolean isRun = true;
	private static final String ACTION_SERVICE = "org.winplus.serial.utils.SerialPortService";
	private ISerialPortService mISerialPortService;
	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			try {
				mISerialPortService.close();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mISerialPortService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mISerialPortService = ISerialPortService.Stub.asInterface(service);
			try {
				mISerialPortService.open();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		bindService(new Intent(ACTION_SERVICE), connection,
				Context.BIND_AUTO_CREATE);

		mTextView = (TextView) findViewById(R.id.TV_receiveData);
		mETData = (EditText) findViewById(R.id.ET_data);
		mBTNSend = (Button) findViewById(R.id.BTN_send);
		mSeekBarZZ = (SeekBar) findViewById(R.id.progress_zz);
		mProgressZZValue= (TextView) findViewById(R.id.progress_zz_value);

		mSeekBarYZ = (SeekBar) findViewById(R.id.progress_yz);
		mProgressYZValue= (TextView) findViewById(R.id.progress_yz_value);

		mSeekBarTime = (SeekBar) findViewById(R.id.progress_time);
		mProgressTimeValue= (TextView) findViewById(R.id.progress_time_value);
		
		mButton1 = (Button)findViewById(R.id.button1);
		
//		mButton1.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
				mReadThread = new ReadThread();
				mReadThread.start();
				Log.v("yzh","thread start");
//				try {
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
				
//			}
//		});
				
		mBTNSend.setOnClickListener(new View.OnClickListener() {
			String str;
			@Override
			public void onClick(View v) {
				Log.v("tt","onclick");
				str = mETData.getText().toString();
				byte[] tests = {(byte) (byte)0xF0, (byte)0x20, (byte)0xBC, (byte)0x14, (byte)0x0F};
				tests = ProtocolUtils.packageForwardCommand((byte)mProgressZZ,(byte)mProgressYZ, (byte)mProgressTime);
				Log.d("yzh","send data is " + str);
				try {
					//mISerialPortService.write(str.getBytes(), 0, str.getBytes().length);
					mISerialPortService.write(tests, 0, tests.length);
				} catch (RemoteException e) {
					e.printStackTrace();
					Log.e("yzh","write error " + e.toString());
				}
			}
		});
		

		mSeekBarZZ.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				int tempProgress = progress;
				int maxProgress=mSeekBarZZ.getMax();
				String action="";
				if(progress>maxProgress/2){
					tempProgress=progress-(maxProgress/2);
					action ="正转: ";
				}else if(progress<(maxProgress/2)){
					tempProgress=-((maxProgress/2)-progress);
					action ="反转: ";
				}else{
					tempProgress=0;
				}
				mProgressZZ=tempProgress;
				mProgressZZValue.setText(action+Math.abs(tempProgress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
		});
		

		mSeekBarYZ.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub

				int tempProgress = progress;
				int maxProgress=mSeekBarZZ.getMax();
				String action="";
				if(progress>maxProgress/2){
					tempProgress=progress-(maxProgress/2);
					action ="正转: ";
				}else if(progress<(maxProgress/2)){
					tempProgress=-((maxProgress/2)-progress);
					action ="反转: ";
				}else{
					tempProgress=0;
				}
				mProgressYZ = tempProgress;
				mProgressYZValue.setText(action+Math.abs(tempProgress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		mSeekBarTime.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				String action="";
				if(progress==13){
					action="一直运行";
				}else{
					action="运行时间为： "+progress+" 秒钟";
				}
				mProgressTime = progress;
				mProgressTimeValue.setText(action);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		isRun = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
	}
	
	/**将字节转化为16进制算法*/
	public static final String toHex(byte b) {
		  return ("" + "0123456789ABCDEF".charAt(0xf & b >> 4) + "0123456789ABCDEF".charAt(b & 0xf));
	}
	/**将字节转化为16进制算法*/
	public static final String toHex(byte[] bytes) {
		StringBuffer strBuff = new StringBuffer();
		String str = "";
		for(int i=0; i< bytes.length; i++) {
			str = toHex(bytes[i]);
			strBuff.append(str);
		}
		return strBuff.toString();
	}
	
	//命令的长度
	private final int CMD_LENGTH = 9;
	private class ReadThread extends Thread {
		Message msg = new Message();
		byte[] buffer  = new byte[CMD_LENGTH];
		String str;
		int size = 0;
		int position = 0;
		@Override
		public void run() {
			while (isRun) {
				try {
					if (mISerialPortService == null) {
						Thread.sleep(2000);
					}
					Log.d("yzh","read data begin ");
					size += mISerialPortService.read(buffer, position, CMD_LENGTH - position);
					if(size == CMD_LENGTH) {
						//Log.d("yzh","---read full size = " + size + "<--->read data = " + (int)buffer[0] + (int)buffer[1] + (int)buffer[2] + (int)buffer[3] 
                        //        + (int)buffer[4] + (int)buffer[5] + (int)buffer[6] + (int)buffer[7]
                        //        + (int)buffer[8]);
						
						Log.d("yzh","<Hex> read full size = " + size + "<--->read data = " + toHex(buffer));
						if (size > 0) {
							mHandler.sendMessage(mHandler.obtainMessage(1111, "接收到的数据 : " + toHex(buffer)));
						}
						size = 0;
						position = 0;
						Thread.sleep(200);
					}else if(size < CMD_LENGTH) {
						Log.d("yzh","not full size = " + size);
						position = size;
						continue;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			Log.v("yzh", "Thread stop");
		}
	}

	private Handler mHandler = new Handler() {
		private String str;
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1111:
				str = (String)msg.obj;
				mTextView.setText(str);
				break;
			default:
				break;
			}
		};
	};
}
