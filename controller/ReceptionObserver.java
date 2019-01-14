package controller;

import entity.inPC.Reception;

/**
 * 宅配受付所の状態管理クラスです。
 * 宅配受付所の荷物の個数と収集担当ロボットの在否を把握しています。
 * @author 澤田 悠暉
 * @version 1.0 (2019/01/14)
*/
public class ReceptionObserver {

	private Reception reception;

	private int numOfStocks;

	private boolean hasCollector;

	private static final int THRESHOLD_AMOUNT = 3;

	private static final int WAIT_TIME = 90000;

	/**
	 * 宅配受付所オブザーバを生成します。
	 * @param parent 宅配受付所
	*/
	public ReceptionObserver(Reception parent) {
		this.reception    = parent;
		this.numOfStocks  = 0;
		this.hasCollector = true;
	}

	/**
	 * オブザーバの管理情報を初期化します。
	 * @param numOfStocks 宅配受付所が保持している荷物の個数
	*/
	public void init(int numOfStocks) {
		this.numOfStocks  = numOfStocks;
		this.hasCollector = false;
	}

	/**
	 * オブザーバの管理情報を更新します。
 	 * @param numOfStocks 宅配受付所が保持している荷物の個数
	*/
	public void update(int numOfStocks) {
		this.numOfStocks = numOfStocks;

		if(canStartTransport()) {
			reception.promptToTransport();
		}
	}

	/**
	 * オブザーバの管理情報を更新します。
 	 * @param hasCollector 宅配受付所に収集担当ロボットがいるか
	*/
	public void update(boolean hasCollector) {
		this.hasCollector = hasCollector;

		if(!canStartTransport()) {
			try {
				Thread.sleep(WAIT_TIME);
			} catch(InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		if(!reception.isEmpty()) {
			reception.promptToTransport();
		}
	}

	/**
	 * オブザーバの管理情報を更新します。
	 * @param numOfStocks 宅配受付所が保持している荷物の個数
	 * @param hasCollector 宅配受付所に収集担当ロボットがいるか
	*/
	public void update(int numOfStocks, boolean hasCollector) {
		this.numOfStocks = numOfStocks;
		update(hasCollector);
	}

	/**
	 * 宅配受付所に収集担当ロボットがいる場合はtrueを返します。
	 * @return 宅配受付所に収集担当ロボットがいる場合はtrue
	*/
	public boolean hasCollector() {
		return hasCollector;
	}

	private boolean canStartTransport() {
		return numOfStocks >= THRESHOLD_AMOUNT && hasCollector;
	}

}
