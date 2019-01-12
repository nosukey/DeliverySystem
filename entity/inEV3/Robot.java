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

public class Robot {

	protected final float P_GAIN = 0.84f;

	protected final float I_GAIN = 0.13f;

	protected final float D_GAIN = 0.65f;

	protected boolean isRightSide;

	protected final float BLACK_THRESHOLD = 0.17f;

	protected final float WHITE_THRESHOLD = 0.44f;

	protected final float GRAY_THRESHOLD = (BLACK_THRESHOLD + WHITE_THRESHOLD) * 0.5f;

	private final float DELTA_TIME = 0.01f;
	
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

	


	protected void openSensor() {
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
			openSensor();
		}
	}
	
	protected void closeSensor(){		
		colorSensor.close();
		gyroSensor.close();
		ev3IRSensor.close();
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

			while(mileage <= distance) {
				colorMode.fetchSample(colorSamples, 0);
	            if(turn==0) Sound.beep();

				// TODO ここ処理時間削減できるよ
				p = P_GAIN * (float)(colorSamples[0] - GRAY_THRESHOLD) * 100 / (float)(BLACK_THRESHOLD - WHITE_THRESHOLD);
				d = D_GAIN * (colorSamples[0] - eval) * 100 / (float)(BLACK_THRESHOLD - WHITE_THRESHOLD);
				i += I_GAIN * ((colorSamples[0] - GRAY_THRESHOLD) * 100 / (BLACK_THRESHOLD - WHITE_THRESHOLD)) * DELTA_TIME;
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

		System.out.println("rotate("+angle+")");

		EV3LargeRegulatedMotor motorA = null;
		EV3LargeRegulatedMotor motorB = null;

		if(angle > 0) {
	        motorA = new EV3LargeRegulatedMotor(MotorPort.A);
	    	motorB = new EV3LargeRegulatedMotor(MotorPort.D);
    	}else {
    		motorA = new EV3LargeRegulatedMotor(MotorPort.D);
	    	motorB = new EV3LargeRegulatedMotor(MotorPort.A);
    	}

		motorA.setSpeed(100);		// TODO マジックナンバー
		motorB.setSpeed(100);		// TODO マジックナンバー

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
			Delay.msDelay(10);			// TODO マジックナンバー
			rotate(angle);
		}
	}
    
  //引数なしver
    protected void initRotate(){
    	Button.LEDPattern(1);
    	gyroMode.fetchSample(gyroSamples,0);
    	System.out.println(gyroSamples[0]);
	    int modAngle = ((((int)(gyroSamples[0]+0.5) % 360) + 360)% 360 + 45)/90 % 4;
    	int ansAngle = (int)(90* Math.pow(modAngle, (Math.max(modAngle,2)+1)%2) * (int)Math.pow(-1, modAngle/3));
	    rotate(ansAngle-(int)gyroSamples[0] % 360);
	    Button.LEDPattern(0);
    }
	/**
	 * 現段階では宅配受付所で次の出発に向けて方向転換するための処理を記述する予定である
	 */
	protected void turn() {

		System.out.println("turn()");

		try {
			EV3LargeRegulatedMotor motorA;
			EV3LargeRegulatedMotor motorB;

		    motorA = new EV3LargeRegulatedMotor(MotorPort.A);
		    motorB = new EV3LargeRegulatedMotor(MotorPort.D);


			motorA.setSpeed(100);		// TODO マジックナンバー
			motorB.setSpeed(50);		// TODO マジックナンバー
			gyroMode.fetchSample(gyroSamples, 0);
			float currentAngle = gyroSamples[0];
			while(true){
				gyroMode.fetchSample(gyroSamples, 0);
				if(Math.abs(180) <= Math.abs(gyroSamples[0]-currentAngle)) break;
				motorA.forward();
				motorB.backward();
			}

			stopMotor(motorA,motorB);
			}catch(Exception e){
				Delay.msDelay(100);
				turn();
			}

		//TODO 消す
		System.out.println("Did turn");

		//TODO EV3確かめ用 消すこと
		//writer.write("Did turn");

	}

	protected void setParkingDistance() {
		parkingDistance = 0;
		while(Button.ENTER.isUp()) {
			irMode.fetchSample(irSamples,0);
			parkingDistance = irSamples[0];
			LCD.clear();
			LCD.drawString("parking dist:"+irSamples[0], 0, 2);
			Delay.msDelay(100);
			LCD.clear();
		}
	}

	protected void parking() {
		try {
			EV3LargeRegulatedMotor motorA = new EV3LargeRegulatedMotor(MotorPort.A);
			EV3LargeRegulatedMotor motorB = new EV3LargeRegulatedMotor(MotorPort.D);

			motorA.setSpeed(50);
			motorB.setSpeed(50);
			irMode.fetchSample(irSamples, 0);
			while(irSamples[0] != parkingDistance) {
				irMode.fetchSample(irSamples, 0);
				LCD.clear();
				LCD.drawString("parking:"+parkingDistance, 0, 2);
				LCD.drawString("distance:"+irSamples[0], 0, 3);
				Delay.msDelay(100);
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
		}catch(Exception e) {
			LCD.drawString("park error", 0, 3);
			Delay.msDelay(100);
			LCD.clear();
			parking();
		}
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
