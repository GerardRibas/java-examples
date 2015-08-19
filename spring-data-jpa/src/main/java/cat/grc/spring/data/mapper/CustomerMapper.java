/**
 * 
 */
package cat.grc.spring.data.mapper;

import cat.grc.spring.data.dto.CustomerDto;
import cat.grc.spring.data.entity.Customer;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class CustomerMapper {

	private CustomerMapper() {

	}

	public static final CustomerDto toDto(Customer entity) {
		CustomerDto result;
		if (entity == null) {
			result = null;
		} else {
			result = new CustomerDto(entity.getId(), entity.getFirstName(), entity.getMiddleName(),
					entity.getLastName(), entity.getGender(), entity.getEmail(), entity.getPhoneNumber(),
					entity.getAddress());
		}
		return result;
	}

	public static final Customer toEntity(CustomerDto dto) {
		Customer result;
		if (dto == null) {
			result = null;
		} else {
			result = new Customer(dto.getId(), dto.getFirstName(), dto.getMiddleName(),
					dto.getLastName(), dto.getGender(), dto.getEmail(), dto.getPhoneNumber(),
					dto.getAddress());
		}
		return result;
	}

}
