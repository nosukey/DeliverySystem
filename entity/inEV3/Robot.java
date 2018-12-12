package entity.inEV3;

public class Robot {

	protected float P_GAIN;

	protected float I_GAIN;

	protected float D_GAIN;

	protected boolean isRightSide;

	protected float BLACK_THRESHOLD;

	protected float WHITE_THRESHOLD;

	protected float GRAY_THRESHOLD;

	/**
	 * ライントレースしながら指定された速度で指定された距離を移動する
	 */
	protected void lineTrace(float distance, int speed) {

	}

	/**
	 * 機体の軸を固定したまま, 指定された角度分回転する
	 */
	protected void rotate(int angle) {

	}

	protected boolean checkCanEntry() {
		return false;
	}

}
