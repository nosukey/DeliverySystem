package entity.inEV3;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.utility.Delay;

public class Robot {

	protected final float P_GAIN = 0.84f;

	protected final float I_GAIN = 0.65f;

	protected final float D_GAIN = 0.13f;

	protected boolean isRightSide;

	protected final float BLACK_THRESHOLD = 0.17f;

	protected final float WHITE_THRESHOLD = 0.44f;

	protected final float GRAY_THRESHOLD = (BLACK_THRESHOLD + WHITE_THRESHOLD) * 0.5f;

	private final float DELTA_TIME = 0.01f;

	private float park;

	private EV3ColorSensor colorSensor;

	private SensorMode colorMode;

	private float[] colorSamples;

	private EV3GyroSensor gyroSensor;

	private SensorMode gyroMode;

	private float[] gyroSamples;

	private EV3IRSensor ev3IRSensor;

	private SensorMode irMode;

	private float[] irSamples;

	Robot() {
		// sensorOpen();
	}

	protected void sensorOpen() {
		try {
			colorSensor  = new EV3ColorSensor(SensorPort.S4);
			colorMode    = colorSensor.getRedMode();
			colorSamples = new float[colorMode.sampleSize()];

			gyroSensor  = new EV3GyroSensor(SensorPort.S1);
			gyroMode    = gyroSensor.getMode(1);
			gyroSamples = new float[gyroMode.sampleSize()];

			ev3IRSensor = new EV3IRSensor(SensorPort.S2);
			irMode      = ev3IRSensor.getDistanceMode();
			irSamples   = new float[irMode.sampleSize()];
		} catch(Exception e) {
			Delay.msDelay(100);			// TODO マジックナンバー
			sensorOpen();
		}
	}

	/**
	 * ライントレースしながら指定された速度で指定された距離を移動する
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

			float p = 0.0f;
			float i = 0.0f;
			float d = 0.0f;
			float mileage = 0.0f;
			float turn = 0.0f;
			float eval = 0.0f;

			eval = colorSamples[0];
			while(mileage <= distance) {
				colorMode.fetchSample(colorSamples, 0);
				// TODO ここ処理時間削減できるよ
				p = P_GAIN * (float)(colorSamples[0] - GRAY_THRESHOLD) * 100 / (float)(BLACK_THRESHOLD - WHITE_THRESHOLD);
				d = D_GAIN * (colorSamples[0] - eval) * 100 / (float)(BLACK_THRESHOLD - WHITE_THRESHOLD);
				//i += I_GAIN * ((colorSamples[0] - GRAY_THRESHOLD) * 100 / (BLACK_THRESHOLD - WHITE_THRESHOLD)) * DELTA_TIME;
				turn = p + d + i;

				motorA.setSpeed(speed - turn);
				motorB.setSpeed(speed + turn);
				motorA.forward();
				motorB.forward();
				eval = colorSamples[0];
				mileage = motorA.getPosition() / 360 * 5.6f * 3.141592f;			// TODO マジックナンバー
			}

			stopMotor(motorA, motorB);
		} catch(Exception e) {
			Delay.msDelay(100);			// TODO マジックナンバー
			lineTrace(distance, speed);
		}
	}

	/**
	 * 機体の軸を固定したまま, 指定された角度分回転する
	 */
	protected void rotate(int angle) {
		try {
			EV3LargeRegulatedMotor motorA;
			EV3LargeRegulatedMotor motorB;
			if(angle > 0) {
		        motorA = new EV3LargeRegulatedMotor(MotorPort.A);
		    	motorB = new EV3LargeRegulatedMotor(MotorPort.D);
	    	}else {
	    		motorA = new EV3LargeRegulatedMotor(MotorPort.D);
		    	motorB = new EV3LargeRegulatedMotor(MotorPort.A);
	    	}

			motorA.setSpeed(100);		// TODO マジックナンバー
			motorB.setSpeed(100);		// TODO マジックナンバー

			gyroMode.fetchSample(gyroSamples, 0);
			float currentAngle = gyroSamples[0];
			while(Math.abs(angle) >= Math.abs(gyroSamples[0] - currentAngle)) {
				motorA.forward();
				motorB.backward();
				gyroMode.fetchSample(gyroSamples, 0);
			}

			stopMotor(motorA, motorB);
		} catch(Exception e) {
			Delay.msDelay(100);			// TODO マジックナンバー
			rotate(angle);
		}
	}


	/**
	 * 現段階では宅配受付所で次の出発に向けて方向転換するための処理を記述する予定である
	 */
	protected void turn() {
		try {
			EV3LargeRegulatedMotor motorA;
			EV3LargeRegulatedMotor motorB;

		    motorA = new EV3LargeRegulatedMotor(MotorPort.A);
		    motorB = new EV3LargeRegulatedMotor(MotorPort.D);


			motorA.setSpeed(100);		// TODO マジックナンバー
			motorB.setSpeed(50);		// TODO マジックナンバー

			while(true){
				gyroMode.fetchSample(gyroSamples, 0);
				if(Math.abs(gyroSamples[0])>=Math.abs(180)) break;
				motorA.setSpeed(0);
				motorB.setSpeed(0);
			}

			stopMotor(motorA,motorB);
			}catch(Exception e){
				Delay.msDelay(100);
				turn();
			}

		//TODO 消す
		System.out.println("Did turn");

		//TODO EV3確かめ用　消すこと
		//writer.write("Did turn");

	}

	private void stopMotor(EV3LargeRegulatedMotor motorA,EV3LargeRegulatedMotor motorB) {
		motorA.setSpeed(0);
		motorB.setSpeed(0);
		motorA.stop();
		motorB.stop();
		motorA.close();
		motorB.close();
	}
}
