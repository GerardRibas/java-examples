package cat.grc.spring.data.mapper;

import cat.grc.spring.data.dto.AccountDto;
import cat.grc.spring.data.entity.Account;
import cat.grc.spring.data.entity.Customer;

public class AccountMapper {
	
	private AccountMapper() {

	}

	public static final AccountDto toDto(Account entity) {
		AccountDto result;
		if (entity == null) {
			result = null;
		} else {
			result = new AccountDto(entity.getId(), entity.getCustomer().getId(), entity.getOpened(), entity.getName());
		}
		return result;
	}

	public static final Account toEntity(AccountDto dto) {
		Account result;
		if (dto == null) {
			result = null;
		} else {
			result = new Account(dto.getId(), new Customer(dto.getCustomer()), dto.getOpened(), dto.getName());
		}
		return result;
	}

}
