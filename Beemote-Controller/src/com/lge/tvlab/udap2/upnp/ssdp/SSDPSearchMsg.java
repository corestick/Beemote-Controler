/*
 * http://code.google.com/p/android-dlna
 */
package com.lge.tvlab.udap2.upnp.ssdp;

public class SSDPSearchMsg {
	static final String HOST = "Host: " + SSDP.ADDRESS + ":" + SSDP.PORT;
	static final String MAN = "Man: \"ssdp:discover\"";
	static final String AGENT = "USER-AGENT: UDAP/2.0";

	int mMX = 5; /* seconds to delay response */
	String mST; /* Search target */

	public SSDPSearchMsg(String ST) {
		mST = ST;
	}

	public int getmMX() {
		return mMX;
	}

	public void setmMX(int mMX) {
		this.mMX = mMX;
	}

	public String getmST() {
		return mST;
	}

	public void setmST(String mST) {
		this.mST = mST;
	}

	@Override
	public String toString() {
		StringBuilder content = new StringBuilder();

		content.append(SSDP.SL_MSEARCH).append(SSDP.NEWLINE);
		content.append(HOST).append(SSDP.NEWLINE);
		content.append(MAN).append(SSDP.NEWLINE);
		content.append(mST).append(SSDP.NEWLINE);
		content.append("MX: " + mMX).append(SSDP.NEWLINE);
		content.append(AGENT).append(SSDP.NEWLINE);
		content.append(SSDP.NEWLINE);

		return content.toString();
	}
}
