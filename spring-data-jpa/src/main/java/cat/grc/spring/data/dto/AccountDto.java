/**
 * 
 */
package cat.grc.spring.data.dto;

import java.io.Serializable;
import java.util.Date;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class AccountDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private Long customer;

	private Date opened;

	private String name;
	
	public AccountDto() {
	
	}

	public AccountDto(Long id, Long customer, Date opened, String name) {
		super();
		this.id = id;
		this.customer = customer;
		this.opened = opened;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCustomer() {
		return customer;
	}

	public void setCustomer(Long customer) {
		this.customer = customer;
	}

	public Date getOpened() {
		return opened;
	}

	public void setOpened(Date opened) {
		this.opened = opened;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public final int hashCode() {
		return Objects.hashCode(id, opened, name);
	}

	@Override
	public final boolean equals(Object object) {
		if (object instanceof AccountDto) {
			AccountDto that = (AccountDto) object;
			return Objects.equal(this.id, that.id) && Objects.equal(this.opened, that.opened)
					&& Objects.equal(this.name, that.name);
		}
		return false;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("opened", opened).add("name", name).toString();
	}

}
