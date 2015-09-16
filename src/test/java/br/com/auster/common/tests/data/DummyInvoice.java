package br.com.auster.common.tests.data;

public class DummyInvoice {

	private double disputeAmount;
	private double adjustmentsAmount;
	private double paymentsAmount;
	private double penaltiesAmount;
	private double totalAmount;
	private DummyCycle cycle;
	private String invoiceId;
	
	
	
	public final double getAdjustmentsAmount() {
		return adjustmentsAmount;
	}
	public final void setAdjustmentsAmount(double adjustmentsAmount) {
		this.adjustmentsAmount = adjustmentsAmount;
	}
	public final DummyCycle getCycle() {
		return cycle;
	}
	public final void setCycle(DummyCycle cycle) {
		this.cycle = cycle;
	}
	public final double getDisputeAmount() {
		return disputeAmount;
	}
	public final void setDisputeAmount(double disputeAmount) {
		this.disputeAmount = disputeAmount;
	}
	public final double getPaymentsAmount() {
		return paymentsAmount;
	}
	public final void setPaymentsAmount(double paymentsAmount) {
		this.paymentsAmount = paymentsAmount;
	}
	public final double getPenaltiesAmount() {
		return penaltiesAmount;
	}
	public final void setPenaltiesAmount(double penaltiesAmount) {
		this.penaltiesAmount = penaltiesAmount;
	}
	public final double getTotalAmount() {
		return totalAmount;
	}
	public final void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	public final String getInvoiceId() {
		return invoiceId;
	}
	public final void setInvoiceId(String _id) {
		this.invoiceId = _id;
	}
	
	public int hashCode() {
		int hash = 1;
		if (this.cycle != null) { hash *= this.cycle.hashCode(); }
		if (this.invoiceId != null) { hash *= this.invoiceId.hashCode(); }
		return hash;
	}
	
	public boolean equals(Object arg0) {
		DummyInvoice i = (DummyInvoice)arg0;
		return (this.cycle.equals(i.getCycle()) && this.invoiceId.equals(i.getInvoiceId()));
	}
	
}
