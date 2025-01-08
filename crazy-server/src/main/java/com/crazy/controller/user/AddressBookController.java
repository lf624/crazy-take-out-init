package com.crazy.controller.user;

import com.crazy.dto.AddressBookDTO;
import com.crazy.entity.AddressBook;
import com.crazy.result.Result;
import com.crazy.service.AddressBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/addressBook")
@Tag(name = "C端地址簿接口")
public class AddressBookController {

    @Autowired
    AddressBookService addressBookService;

    @PostMapping
    @Operation(summary = "新增地址")
    public Result<String> save(@RequestBody AddressBook addressBook) {
        log.info("add new address: {}", addressBook);
        addressBookService.save(addressBook);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "查询当前用户所有地址信息")
    public Result<List<AddressBook>> list() {
        List<AddressBook> addressBooks = addressBookService.list();
        return Result.success(addressBooks);
    }

    @GetMapping("/default")
    @Operation(summary = "查询默认地址")
    public Result<AddressBook> defaultAddress() {
        return Result.success(addressBookService.getDefault());
    }

    @PutMapping
    @Operation(summary = "根据id修改地址")
    public Result<String> updateWithId(@RequestBody AddressBook addressBook) {
        addressBookService.updateWithId(addressBook);
        return Result.success();
    }

    @DeleteMapping("/")
    @Operation(summary = "根据id删除地址")
    public Result<String> deleteWithId(@RequestParam Long id) {
        addressBookService.deleteWithId(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id) {
        return Result.success(addressBookService.getById(id));
    }

    @PutMapping("/default")
    @Operation(summary = "设置默认地址")
    public Result<String> setDefault(@RequestBody AddressBookDTO addressBookDTO) {
        addressBookService.setDefault(addressBookDTO.getId());
        return Result.success();
    }
}
