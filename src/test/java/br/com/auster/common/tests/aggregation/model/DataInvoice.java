/*
 * Copyright (c) 2004-2007 Auster Solutions. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 11/01/2007
 */
package br.com.auster.common.tests.aggregation.model;

import java.util.Date;

/**
 * @author framos
 * @version $Id$
 *
 */
public class DataInvoice {

	private String cycleCode;
	private Date cycleStartDate;
	private Date cycleEndDate;
	private Date dueDate;
	private Date issueDate;
	private double totalAmount;
	
	private DataAccount account;

	
	
	/**
	 * @return the account
	 */
	public final DataAccount getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public final void setAccount(DataAccount account) {
		this.account = account;
	}

	/**
	 * @return the cycleCode
	 */
	public final String getCycleCode() {
		return cycleCode;
	}

	/**
	 * @param cycleCode the cycleCode to set
	 */
	public final void setCycleCode(String cycleCode) {
		this.cycleCode = cycleCode;
	}

	/**
	 * @return the cycleEndDate
	 */
	public final Date getCycleEndDate() {
		return cycleEndDate;
	}

	/**
	 * @param cycleEndDate the cycleEndDate to set
	 */
	public final void setCycleEndDate(Date cycleEndDate) {
		this.cycleEndDate = cycleEndDate;
	}

	/**
	 * @return the cycleStartDate
	 */
	public final Date getCycleStartDate() {
		return cycleStartDate;
	}

	/**
	 * @param cycleStartDate the cycleStartDate to set
	 */
	public final void setCycleStartDate(Date cycleStartDate) {
		this.cycleStartDate = cycleStartDate;
	}

	/**
	 * @return the dueDate
	 */
	public final Date getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate the dueDate to set
	 */
	public final void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * @return the issueDate
	 */
	public final Date getIssueDate() {
		return issueDate;
	}

	/**
	 * @param issueDate the issueDate to set
	 */
	public final void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	/**
	 * @return the totalAmount
	 */
	public final double getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public final void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	
	
}
