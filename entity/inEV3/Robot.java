package entity.inEV3;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.utility.Delay;

/**
 * 配達・収集担当ロボットの親クラスとなるクラスです。
 * ロボットに取り付けられたセンサ・モータ等の制御,機体の移動と方向転換に関する処理をします。
 * @author 大竹 知幸
 * @version 1.0(2019/1/13)
 */
public class Robot {

	protected final float P_GAIN = 0.84f;

	protected final float I_GAIN = 0.13f;

	protected final float D_GAIN = 0.65f;

	protected boolean isRightSide;

	protected final float BLACK_THRESHOLD = 0.17f;

	protected final float WHITE_THRESHOLD = 0.44f;

	protected final float GRAY_THRESHOLD = (BLACK_THRESHOLD + WHITE_THRESHOLD) * 0.5f;

	protected final int SECOND_GEAR_SPEED = 175;

	protected final int THIRD_GEAR_SPEED = 315;

	protected final int ZERO_ANGLE = 0;

	protected final int RIGHT_ANGLE = 90;

	protected final int STRAIGHT_ANGLE = 180;

	protected final int ROUND_ANGLE = 360;

	private final float DELTA_TIME = 0.01f;

	private final int DELAY_TIME = 100;

	private final int LED_OFF = 0;

	private final int LED_GREEN = 1;

	private final int LED_RED = 2;

	private float parkingDistance;

	private EV3ColorSensor colorSensor;

	private SensorMode colorMode;

	private float[] colorSamples;

	private EV3GyroSensor gyroSensor;

	private SensorMode gyroMode;

	private float[] gyroSamples;

	private EV3IRSensor ev3IRSensor;

	private SensorMode irMode;

	private float[] irSamples;


	/**
	 * ロボットに取り付けられたセンサ（光センサ・ジャイロセンサ・IRセンサ）を稼働させるために呼び出します。
	 * 光センサのポート			:1番
	 * ジャイロセンサのポート	:2番
	 * IRセンサのポート			:3番
	 */
	protected void openSensor() {
		try {
			colorSensor  = new EV3ColorSensor(SensorPort.S1);
			colorMode    = colorSensor.getRedMode();
			colorSamples = new float[colorMode.sampleSize()];

			gyroSensor  = new EV3GyroSensor(SensorPort.S2);
			gyroMode    = gyroSensor.getMode(1);
			gyroSamples = new float[gyroMode.sampleSize()];

			ev3IRSensor = new EV3IRSensor(SensorPort.S3);
			irMode      = ev3IRSensor.getDistanceMode();
			irSamples   = new float[irMode.sampleSize()];
		} catch(Exception e) {
			Button.LEDPattern(LED_GREEN);
			Delay.msDelay(DELAY_TIME);
			openSensor();
			Button.LEDPattern(LED_OFF);
		}
	}

	/**
	 * ロボットに取り付けられたセンサ（光センサ・ジャイロセンサ・IRセンサ）の接続を切断する処理をします。
	 */
	protected void closeSensor(){
		colorSensor.close();
		gyroSensor.close();
		ev3IRSensor.close();
	}

	/**
	 *
	 * 指定された速度で指定された距離をライントレースで移動します。
	 * フィールド上のisRightSideによりで搬送路の右側または左側をトレースします。
	 *
	 * @param distance ロボットを移動させたい距離
	 * @param speed ロボットを走らせるときの速度
	 */
	protected void lineTrace(float distance, int speed) {
		try{
			EV3LargeRegulatedMotor motorA;
			EV3LargeRegulatedMotor motorB;
			if(isRightSide) {
				motorA = new EV3LargeRegulatedMotor(MotorPort.A);
				motorB = new EV3LargeRegulatedMotor(MotorPort.D);
			} else {
				motorA = new EV3LargeRegulatedMotor(MotorPort.D);
				motorB = new EV3LargeRegulatedMotor(MotorPort.A);
			}

			final float DIAMETER_TIRE = 5.6f;
			final float DIFFERENCE_BLACK_WHITE = 1/(BLACK_THRESHOLD-WHITE_THRESHOLD) * 100;
			float p = 0.0f;
			float i = 0.0f;
			float d = 0.0f;
			float mileage = 0.0f;
			float turn = 0.0f;
			float eval = 0.0f;

			while(mileage <= distance) {
				colorMode.fetchSample(colorSamples, 0);
	            if(turn==0) Sound.beep();

				// TODO ここ処理時間削減できるよ
				p = P_GAIN * (float)(colorSamples[0] - GRAY_THRESHOLD) * DIFFERENCE_BLACK_WHITE;
				d = D_GAIN * (colorSamples[0] - eval) * DIFFERENCE_BLACK_WHITE;
				i += I_GAIN * (colorSamples[0] - GRAY_THRESHOLD) * DIFFERENCE_BLACK_WHITE * DELTA_TIME;
				turn = p + d + i;

				motorA.setSpeed(speed - turn);
				motorB.setSpeed(speed + turn);
				motorA.forward();
				motorB.forward();
				eval = colorSamples[0];
				mileage = (float) (motorA.getPosition() / 360 * DIAMETER_TIRE * Math.PI);
			}
			stopMotor(motorA, motorB);
		} catch(Exception e) {
			Button.LEDPattern(LED_RED);
			Delay.msDelay(DELAY_TIME);
			lineTrace(distance, speed);
			Button.LEDPattern(LED_OFF);
		}
	}

	/**
	 * 引数で与えられた角度だけ回転します。
	 * 例）90  : 反時計回りへ90度回転
	 * 例）-180: 時計回りへ180度回転
	 *
	 * @param angle 機体を回転させたい回転角
	 */
	protected void rotate(int angle) {

		final int SPEED  = 100;
		EV3LargeRegulatedMotor motorA = null;
		EV3LargeRegulatedMotor motorB = null;

		if(angle > 0) {
	        motorA = new EV3LargeRegulatedMotor(MotorPort.A);
	    	motorB = new EV3LargeRegulatedMotor(MotorPort.D);
    	}else {
    		motorA = new EV3LargeRegulatedMotor(MotorPort.D);
	    	motorB = new EV3LargeRegulatedMotor(MotorPort.A);
    	}

		motorA.setSpeed(SPEED);
		motorB.setSpeed(SPEED);

		try {
			gyroMode.fetchSample(gyroSamples, 0);
			float currentAngle = gyroSamples[0];
			while(Math.abs(angle) >= Math.abs(currentAngle - gyroSamples[0])) {
				motorA.forward();
				motorB.backward();
				gyroMode.fetchSample(gyroSamples, 0);
			}
			stopMotor(motorA, motorB);
		} catch(Exception e) {
			Button.LEDPattern(LED_RED);
			Delay.msDelay(DELAY_TIME);
			rotate(angle);
			Button.LEDPattern(LED_OFF);
		}
	}
	/**
	 * 機体の方向を0°,90°,-90°,180°に合わせます。
	 */
    protected void initRotate(){
    	Button.LEDPattern(LED_GREEN);
    	gyroMode.fetchSample(gyroSamples,0);

    	/*
    	 * 現在の角度から0°,90°,-90°,180°のどの角へ回転するかを計算します
    	 * ansAngleに現在の角に上記のうち一番近い角を代入します。
    	 */
	    int modAngle = ((((int)(gyroSamples[0]+0.5) % 360) + 360)% 360 + 45)/90 % 4;
    	int ansAngle = (int)(90* Math.pow(modAngle, (Math.max(modAngle,2)+1)%2) * (int)Math.pow(-1, modAngle/3));
	    rotate(ansAngle-(int)gyroSamples[0] % 360);
	    Button.LEDPattern(LED_OFF);
    }

    /**
     * 搬送路のトレースする側面をそのままに方向転換をするためのメソッドです。
     */
	protected void turn() {
		try {
			EV3LargeRegulatedMotor motorA;
			EV3LargeRegulatedMotor motorB;

		    motorA = new EV3LargeRegulatedMotor(MotorPort.A);
		    motorB = new EV3LargeRegulatedMotor(MotorPort.D);

		    final int TURN_SPEED = 100;
		    final int TURN_SPEED_HALF = 50;
		    final int TURN_ANGLE = 180;

			motorA.setSpeed(TURN_SPEED);
			motorB.setSpeed(TURN_SPEED_HALF);
			gyroMode.fetchSample(gyroSamples, 0);
			float currentAngle = gyroSamples[0];
			while(true){
				gyroMode.fetchSample(gyroSamples, 0);
				if(Math.abs(TURN_ANGLE) <= Math.abs(gyroSamples[0]-currentAngle)) break;
				motorA.forward();
				motorB.backward();
			}

			stopMotor(motorA,motorB);
			}catch(Exception e){
				Button.LEDPattern(LED_RED);
				Delay.msDelay(DELAY_TIME);
				turn();
				Button.LEDPattern(LED_OFF);
			}
	}

	/**
	 *ロボットが始動した場所にて帰還した際に位置調整をするための距離を記録するための処理です。
	 *ロボットの起動時にENTERボタンで距離を決定します。
	 */
	protected void setParkingDistance() {
		parkingDistance = 0;
		final int DISPLAY_X = 0;
		final int DISPLAY_Y = 2;

		while(Button.ENTER.isUp()) {
			irMode.fetchSample(irSamples,0);
			parkingDistance = irSamples[0];
			LCD.clear();
			LCD.drawString("parking dist:"+irSamples[0], DISPLAY_X, DISPLAY_Y);
			Delay.msDelay(DELAY_TIME);
			LCD.clear();
		}
	}

	/**
	 * ロボットが始動した場所へ帰還後、ロボット発進時に置いた位置へ調整して停止します。
	 */
	protected void parking() {
		try {
			Button.LEDPattern(LED_GREEN);
			EV3LargeRegulatedMotor motorA = new EV3LargeRegulatedMotor(MotorPort.A);
			EV3LargeRegulatedMotor motorB = new EV3LargeRegulatedMotor(MotorPort.D);
			final int PARK_SPEED = 50;

			motorA.setSpeed(PARK_SPEED);
			motorB.setSpeed(PARK_SPEED);
			irMode.fetchSample(irSamples, 0);
			while(irSamples[0] != parkingDistance) {
				irMode.fetchSample(irSamples, 0);
				Delay.msDelay(DELAY_TIME);
				if(irSamples[0]>parkingDistance) {
					motorA.backward();
					motorB.backward();
				}
				else if(irSamples[0]<parkingDistance) {
					motorA.forward();
					motorB.forward();
				}
			}
			stopMotor(motorA,motorB);
			Button.LEDPattern(LED_OFF);
		}catch(Exception e) {
			Button.LEDPattern(LED_RED);
			Delay.msDelay(DELAY_TIME);
			LCD.clear();
			parking();
			Button.LEDPattern(LED_OFF);
		}
	}

	/**
	 * モータ動作後、モータを停止するための処理を行うメソッドです。
	 *
	 * @param motorA 停止させたいモータ
	 * @param motorB 停止させたいモータ
	 */
	private void stopMotor(EV3LargeRegulatedMotor motorA,EV3LargeRegulatedMotor motorB) {
		motorA.setSpeed(0);
		motorB.setSpeed(0);
		motorA.stop();
		motorB.stop();
		motorA.close();
		motorB.close();
	}
}
