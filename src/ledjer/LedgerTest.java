package ledjer;

import static org.junit.Assert.*;
import java.io.File;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

public class LedgerTest {

	private Ledger ledger;
	private Calendar cal;
	private Date today;
	private SimpleDateFormat format;
	
	@Before
	public void setup() {
		ledger = new Ledger();
		Transaction.resetNextNumber();
		cal = new GregorianCalendar();
		today = cal.getTime();
		format = new SimpleDateFormat("yyyy/MM/dd");
	}
	
	private String todaysDate() {
		return format.format(today);
	}
	
	@Test
	public void startsWithZeroBalance() {
		assertEquals(0, ledger.getBalance());
	}
	
	@Test
	public void depositAddsToBalance() {
		ledger.deposit(new Deposit(100));
		assertEquals(100, ledger.getBalance());
	}
	
	@Test
	public void paymentSubtractsFromBalance() {
		Transaction deposit = new Deposit(100);
		Payment payment = new Payment(50, "foo");
		ledger.deposit(deposit);
		ledger.pay(payment);
		assertEquals(50, ledger.getBalance());
	}
	
	@Test
	public void statementIncludesPaymentsAndDepositsHistory() {
		ledger.deposit(new Deposit(1000));
		ledger.pay(new Payment(500, "foo"));
		ledger.pay(new Payment(100, "bar"));

		String expectedStatement = todaysDate() + " 1. Deposit: $10.00" + Transaction.newLine() + 
								   todaysDate() + " 2. Payment to foo: ($5.00)" + Transaction.newLine() +
								   todaysDate() + " 3. Payment to bar: ($1.00)" + Transaction.newLine() +
								   "Total: $4.00";
		assertEquals(expectedStatement, ledger.statement());
	}
	
	@Test(expected = NegativeBalanceException.class)
	public void doesNotAllowNegativeBalance() {
		ledger.pay(new Payment(1, "payee"));
	}
	
    @Test
    public void emptyLedgersAreEqual() {
        assertEquals(ledger, new Ledger());
    }
    
    @Test
    public void equalityBasedOnInstanceOfTarget() {
        assertFalse(ledger.equals(new Object()));
    }
    
  @Test
  public void ledgersNotEqualIfNumberOfTransactionsDifferent() {
      Ledger ledgerTwo = new Ledger();
      ledger.deposit(new Deposit(100));
      assertFalse(ledger.equals(ledgerTwo));
  }
 
  @Test
  public void ledgersNotEqualIfTransactionsAreNotEqual() {
	  Deposit deposit = new Deposit(200);
      Payment paymentOne = new Payment(100, "foo");
      Payment paymentTwo = new Payment(100, "bar");
	  Ledger ledgerTwo = new Ledger();
      ledger.deposit(deposit);
      ledgerTwo.deposit(deposit);
      ledger.pay(paymentOne);
      ledgerTwo.pay(paymentTwo);
      
      assertFalse(ledger.equals(ledgerTwo));
  }
  
  @Test
  public void ledgersAreEqualIfTransactionHistoryIsEqual() {
	  Deposit deposit = new Deposit(400);
      Payment paymentOne = new Payment(100, "foo");
      Payment paymentTwo = new Payment(100, "bar");
	  Ledger ledgerTwo = new Ledger();
      ledger.deposit(deposit);
      ledgerTwo.deposit(deposit);
      ledger.pay(paymentOne);
      ledger.pay(paymentTwo);
      ledgerTwo.pay(paymentOne);
      ledgerTwo.pay(paymentTwo);
      
      assertEquals(ledger, ledgerTwo);
  }
  
  @Test
  public void transactionsAndBalanceClonedWhenCloningLedgers() throws CloneNotSupportedException {
	  Deposit deposit = new Deposit(400);
      Payment paymentOne = new Payment(100, "foo");
      Payment paymentTwo = new Payment(100, "bar");
      ledger.deposit(deposit);
      ledger.pay(paymentOne);
      ledger.pay(paymentTwo);
      
      assertEquals(ledger.getBalance(), ledger.clone().getBalance());
      assertEquals(ledger, ledger.clone());
      assertNotSame(ledger, ledger.clone());
  }
  
  @Test
  public void savesToFile() {
	  ledger.deposit(new Deposit(1000));
	  ledger.pay(new Payment(500, "Joe"));
	  ledger.save();
	  assertTrue(new File("ledger.dump").exists());
  }

  @Test
  public void loads() {
	  ledger.deposit(new Deposit(1000));
	  ledger.pay(new Payment(500, "Joe"));
	  String statement = ledger.statement();
	  String ledgerString = ledger.toString();
	  ledger.save();
	  ledger = null;
	  System.gc();
	  
	  ledger = Ledger.load();
	  assertEquals(statement, ledger.statement());
	  assertFalse(ledgerString.equals(ledger.toString()));
  }
}
