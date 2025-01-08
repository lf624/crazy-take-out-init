package com.crazy.dto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "Address Book set default dto")
public class AddressBookDTO implements Serializable {

    private Long id;
}
