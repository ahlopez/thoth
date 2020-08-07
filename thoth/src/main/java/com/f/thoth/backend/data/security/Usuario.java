package com.f.thoth.backend.data.security;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.ui.utils.BakeryConst;

/**
 *  Representa un usuario sencillo o compuesto del sistema
 */
@MappedSuperclass
public abstract class Usuario extends BaseEntity
{
	private static final long DEFAULT_TO_DATE = 90L;

	@NotNull     (message= "{evidentia.category.required}")
	@Min(value=0, message= "{evidentia.category.minvalue}")
	@Max(value=5, message= "{evidentia.category.maxvalue}")
	protected Integer       category; // security category

	@NotNull(message = "{evidentia.date.required}")
	@PastOrPresent(message="{evidentia.date.pastorpresent}")
	protected LocalDate     fromDate; // initial date it can be used. default = now

	@NotNull(message = "{evidentia.date.required}")
	protected LocalDate     toDate;   // final date it can be used. default = a year from now

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@OrderColumn
	@JoinColumn
	@BatchSize(size = 10)
	@Valid
	protected Set<Role>       roles; // roles assigned to it

	@NotNull(message  = "{evidentia.name.required}")
	@NotEmpty(message = "{evidentia.name.required}")
	@NotBlank(message = "{evidentia.name.required}")
	@Size(min = 1, max = 255, message="{evidentia.name.min.max.length}")
	protected String firstName;

	protected boolean locked = false;

	// ----------------- Constructor -----------------
	public Usuario()
	{
		super();
		LocalDate now = LocalDate.now();
		LocalDate yearStart =now.minusDays(now.getDayOfYear());
		
		firstName = "";
		category  = BakeryConst.DEFAULT_CATEGORY;
		locked    = false;
		fromDate  = yearStart;
		toDate    = yearStart.plusYears(1);
		roles     = new TreeSet<>();
	}//Usuario

	public void prepareData()
	{
		this.fromDate  =  fromDate  != null ? fromDate : LocalDate.MIN;
		this.toDate    =  toDate    != null ? toDate   : LocalDate.now().plusDays(DEFAULT_TO_DATE);
		this.category  =  category  != null ? category : 0;
		this.firstName =  TextUtil.nameTidy( firstName);
		this.locked    =  isLocked();

	}//prepareData


	// --------------- Getters & Setters -----------------

	public Integer    getCategory() { return category;}
	public void       setCategory(Integer category) { this.category = (category == null? 0: category);}

	public String     getFirstName() { return firstName;}
	public void       setFirstName(String firstName) { this.firstName = firstName;}

	public LocalDate  getFromDate() {	return fromDate;}
	public void       setFromDate(LocalDate fromDate) { this.fromDate = fromDate;}

	public LocalDate  getToDate() { return toDate; }
	public void       setToDate(LocalDate toDate) { this.toDate = toDate; }

	public Set<Role> getRoles() { return roles;}
	public void       setRoles(Set<Role> roles) { this.roles = roles;}

	public boolean    isLocked()
	{
		if (locked)
			return true;

		if (fromDate != null && toDate != null)
		{
			LocalDate now = LocalDate.now();
			return now.compareTo(fromDate) < 0 || now.compareTo(toDate) > 0;
		}
		return false;
	}
	public void       setLocked(boolean locked) { this.locked = locked;}

	// --------------- Object ------------------

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;

		if (o == null || ! (o instanceof Usuario))
			return false;

		Usuario that = (Usuario) o;

		return  isLocked() == that.isLocked()            &&
				Objects.equals(code,      that.code)     &&
				Objects.equals(tenant,    that.tenant)   &&
				Objects.equals(category,  that.category) &&
				Objects.equals(firstName, that.firstName);

	}// equals

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), tenant, code, category, firstName, isLocked());
	}

	@Override
	public String toString() { return " Usuario{" + super.toString()+ " tenant["+ tenant.getName()+ "] category["+ category+ "] locked["+ isLocked()+ "]"+ "] name[" + firstName+ "]}" ; }

	// --------------- function ----------------

	public void addToRole( Role role)
	{
		if ( role != null)
			roles.add(role);
	}//addToRole

	public abstract boolean canAccess( NeedsProtection object);

}//Usuario
