package br.com.auster.common.tests.data;


public class DummyCycle {

	private String uid;
	private String cycleCode;
	private String cycleEndDate;
	private String cycleStartDate;
	
	
	
	public final String getCycleCode() {
		return cycleCode;
	}
	public final void setCycleCode(String cycleCode) {
		this.cycleCode = cycleCode;
	}
	public final String getCycleEndDate() {
		return cycleEndDate;
	}
	public final void setCycleEndDate(String cycleEndDate) {
		this.cycleEndDate = cycleEndDate;
	}
	public final String getCycleStartDate() {
		return cycleStartDate;
	}
	public final void setCycleStartDate(String cycleStartDate) {
		this.cycleStartDate = cycleStartDate;
	}
	public final String getUid() {
		return uid;
	}
	public final void setUid(String uid) {
		this.uid = uid;
	}
	
	
	public int hashCode() {
		int code = 37;
		if (this.cycleCode != null ) { code += this.cycleCode.hashCode(); };
		if (this.cycleStartDate != null ) { code += this.cycleStartDate.hashCode(); };
		return code;
	}
	
	public boolean equals(Object arg0) {
		DummyCycle c = (DummyCycle)arg0;
		boolean result = true;
		if (this.cycleCode == null) {
			result &= (c.cycleCode == null);
		} else {
			result &= (this.cycleCode.equals(c.cycleCode));
		}
		if (this.cycleStartDate == null) {
			result &= (c.cycleStartDate == null);
		} else {
			result &= (this.cycleStartDate.equals(c.cycleStartDate));
		}
		return result;
	}
	
}
