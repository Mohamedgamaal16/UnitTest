package example.account;


import org.junit.jupiter.api.Assertions;


import org.junit.jupiter.api.Test;


import static org.mockito.Mockito.*;

class AccountManagerImplTest {

    AccountManager accountManager = new AccountManagerImpl();


    @Test
    void givenValidAmount_whenDeposit_thenBalanceShouldIncrease() {
        //arrange
        Customer customer = mock(Customer.class);

        when(customer.getBalance()).thenReturn(100);

        //act
        accountManager.deposit(customer, 50);

        //assert
        verify(customer).setBalance(150);
    }

    @Test
    void givenZeroAmount_whenDeposit_theBalanceShouldStable() {
        //arrange
        Customer customer = mock(Customer.class);
        when(customer.getBalance()).thenReturn(100);

        // Act
        accountManager.deposit(customer, 0);

        // Assert
        verify(customer).setBalance(100);
    }

    @Test
    void givenInvalidAmount_whenDeposit_thenBalanceShouldRemainUnchanged() {
        Customer customer = mock(Customer.class);

        when(customer.getBalance()).thenReturn(100);

        //act
        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> accountManager.deposit(customer, -100)
        );

        Assertions.assertEquals("the Amount is invalid", exception.getMessage());
        verify(customer, never()).setBalance(anyInt());
    }


    @Test
    void givenSufficientBalance_whenWithdraw_thenBalanceShouldDecrease() {
        //arrange
        Customer customer =mock(Customer.class);
        when(customer.getBalance()).thenReturn(100);

        //act
        accountManager.withdraw(customer , 50);

        //assert
        verify(customer).setBalance(50);
    }


    @Test
    void givenInsufficientBalanceWithoutCredit_whenWithdraw_thenWithdrawalShouldFail() {
        //arrange
        Customer customer =mock(Customer.class);
        when(customer.getBalance()).thenReturn(100);
        when(customer.isCreditAllowed()).thenReturn(false);
        when(customer.isVip()).thenReturn(false);

        //act
        accountManager.withdraw(customer , 150);

        //assert
        verify(customer, never()).setBalance(anyInt());
    }

    @Test
    void givenInsufficientBalanceWithCredit_whenWithdraw_thenWithdrawalShouldSuccess() {
        //arrange
        Customer customer =mock(Customer.class);
        when(customer.getBalance()).thenReturn(100);
        when(customer.isCreditAllowed()).thenReturn(true);
        when(customer.isVip()).thenReturn(false);

        //act
        String result = accountManager.withdraw(customer , 150);

        //assert
        Assertions.assertEquals("success" , result);
        verify(customer).setBalance(-50);
    }

    @Test
    void givenInsufficientBalanceWithCreditButExceedLimit_whenWithdraw_thenWithdrawalShouldFail() {
        //arrange
        Customer customer =mock(Customer.class);
        when(customer.getBalance()).thenReturn(100);
        when(customer.isCreditAllowed()).thenReturn(true);
        when(customer.isVip()).thenReturn(false);

        //act
        String result = accountManager.withdraw(customer , 1500);

        //assert
        Assertions.assertEquals("maximum credit exceeded" , result);
        verify(customer, never()).setBalance(anyInt());
    }

    @Test
    void givenInsufficientBalanceWithVip_whenWithdraw_thenWithdrawalShouldSuccess() {
        //arrange
        Customer customer =mock(Customer.class);
        when(customer.getBalance()).thenReturn(100);
        when(customer.isCreditAllowed()).thenReturn(true);
        when(customer.isVip()).thenReturn(true);

        //act
        String result = accountManager.withdraw(customer , 1500);

        //assert
        Assertions.assertEquals("success" , result);
        verify(customer).setBalance(-1400);
    }
}