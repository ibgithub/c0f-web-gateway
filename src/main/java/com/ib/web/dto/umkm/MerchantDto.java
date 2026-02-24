package com.ib.web.dto.umkm;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class MerchantDto {
    private Long id;

    @NotBlank(message = "Nama merchant wajib diisi")
    private String name;
    private Integer status;

    @NotNull(message = "Owner wajib dipilih")
    private Long ownerId;
    private String ownerName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
