package example.store;

import example.account.AccountManager;
import example.account.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import static org.mockito.Mockito.*;

class StoreImplTest {

    AccountManager accountManager = mock(AccountManager.class);
    Product product = mock(Product.class);
    Customer customer = mock(Customer.class);


    private final Store store = new StoreImpl(accountManager);
    ;


    @Test
    void givenCustomerWithSufficientBalance_whenBuyingProduct_thenShouldReduceQuantity() {

        when(product.getPrice()).thenReturn(50);
        when(product.getQuantity()).thenReturn(2);
        when(customer.getBalance()).thenReturn(200);
        when(accountManager.withdraw(customer, 50)).thenReturn("success");


        store.buy(product, customer);


        verify(product).setQuantity(1);  // Verify quantity was reduced by 1
    }

    @Test
    void givenCustomerWithInSufficientBalance_whenBuyingProduct_thenShouldReturnException() {

        when(product.getPrice()).thenReturn(1000);
        when(product.getQuantity()).thenReturn(2);
        when(accountManager.withdraw(customer, 1000)).thenReturn("insufficient balance");

        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> store.buy(product, customer)
        );

        Assertions.assertTrue(exception.getMessage().contains("Payment failure: "));
        verify(product , never()).setQuantity(anyInt());

    }

    @Test
    void givenCustomerWithSufficientBalanceButOutOfStock_whenBuyingProduct_thenShouldReturnException() {

        when(product.getPrice()).thenReturn(1000);
        when(product.getQuantity()).thenReturn(0);
        when(accountManager.withdraw(customer, 1000)).thenReturn("success");

        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> store.buy(product, customer)
        );

        Assertions.assertTrue(exception.getMessage().contains("Product out of stock"));
        verify(product , never()).setQuantity(anyInt());

    }
}