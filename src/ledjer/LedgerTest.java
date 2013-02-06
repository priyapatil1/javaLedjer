package ledjer;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class LedgerTest {

	private Ledger ledger;
	
	@Before
	public void setup() {
		ledger = new Ledger();
	}
	
	@Test
	public void startsWithZeroBalance() {
		assertEquals(0, ledger.getBalance());
	}
	
	@Test
	public void depositAddsToBalance() {
		Deposit deposit = new Deposit(100);
		ledger.deposit(deposit); // 1 dollar
		assertEquals(100, ledger.getBalance());
	}
	
	@Test
	public void limitNumberOfDepositsToTen() {
		for (int i=0; i<11; i++)
			ledger.deposit(new Deposit(100));
		assertEquals(1000, ledger.getBalance());
	}
	
	@Test
	public void paymentSubtractsFromBalance() {
		Deposit deposit = new Deposit(100);
		Payment payment = new Payment(50, "foo");
		ledger.deposit(deposit);
		ledger.payment(payment);
		assertEquals(50, ledger.getBalance());
	}
	
	@Test
	public void statementIncludesDepositHistory() {
		ledger.deposit(new Deposit(110));
		ledger.deposit(new Deposit(200));
		String expectedStatement = "Deposit $1.10\\nDeposit $2.00\\nTotal $3.10";
		assertEquals(expectedStatement, ledger.statement());
	}
	
	@Test
	public void statementIncludesPaymentsAndDepositsHistory() {
		ledger.deposit(new Deposit(1000));
		ledger.payment(new Payment(500, "foo"));
		ledger.payment(new Payment(100, "bar"));

		String expectedStatement = "Deposit $10.00\\nPayment to foo ($5.00)\\nPayment to bar ($1.00)\\nTotal $4.00";
		assertEquals(expectedStatement, ledger.statement());
	}

}
