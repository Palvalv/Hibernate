package com.company;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "CustomerDemographics", schema = "Northwind", catalog = "")
public class CustomerDemographicsEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "CustomerTypeID")
    private String customerTypeId;
    @Basic
    @Column(name = "CustomerDesc")
    private String customerDesc;
    @OneToMany(mappedBy = "customerDemographicsByCustomerTypeId")
    private Collection<CustomerCustomerDemoEntity> customerCustomerDemosByCustomerTypeId;

    public String getCustomerTypeId() {
        return customerTypeId;
    }

    public void setCustomerTypeId(String customerTypeId) {
        this.customerTypeId = customerTypeId;
    }

    public String getCustomerDesc() {
        return customerDesc;
    }

    public void setCustomerDesc(String customerDesc) {
        this.customerDesc = customerDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerDemographicsEntity that = (CustomerDemographicsEntity) o;
        return Objects.equals(customerTypeId, that.customerTypeId) && Objects.equals(customerDesc, that.customerDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerTypeId, customerDesc);
    }

    public Collection<CustomerCustomerDemoEntity> getCustomerCustomerDemosByCustomerTypeId() {
        return customerCustomerDemosByCustomerTypeId;
    }

    public void setCustomerCustomerDemosByCustomerTypeId(Collection<CustomerCustomerDemoEntity> customerCustomerDemosByCustomerTypeId) {
        this.customerCustomerDemosByCustomerTypeId = customerCustomerDemosByCustomerTypeId;
    }
}
