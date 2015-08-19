package cat.grc.spring.data.service;

import java.util.Collection;

import cat.grc.spring.data.dto.AccountDto;
import cat.grc.spring.data.dto.CustomerDto;


/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface CustomerService {

  Collection<CustomerDto> findAllCustomers(int page, int size);

  CustomerDto findCustomerById(Long id);

  CustomerDto addCustomer(CustomerDto customer);

  CustomerDto updateCustomer(CustomerDto customer);

  void deleteCustomer(Long id);

  Collection<AccountDto> findAllAccountsByCustomerId(Long customerId, int page, int size);

  AccountDto findAccountById(Long id);

  AccountDto addAccount(AccountDto account);

  AccountDto updateAccount(AccountDto account);

  void deleteAccount(Long id);

}
