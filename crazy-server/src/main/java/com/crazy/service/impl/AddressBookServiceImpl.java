package com.crazy.service.impl;

import com.crazy.context.BaseContext;
import com.crazy.entity.AddressBook;
import com.crazy.mapper.AddressBookMapper;
import com.crazy.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    AddressBookMapper addressBookMapper;

    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    @Override
    public List<AddressBook> list() {
        AddressBook addressBook = AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        return addressBookMapper.list(addressBook);
    }

    @Override
    public AddressBook getDefault() {
        AddressBook addressBook = AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .isDefault(1)
                .build();
        List<AddressBook> defaultAddressBook = addressBookMapper.list(addressBook);
        if(defaultAddressBook != null && !defaultAddressBook.isEmpty())
            return defaultAddressBook.get(0);
        return null;
    }

    @Override
    public void updateWithId(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }

    @Override
    @Transactional
    public void setDefault(Long id) {
        // 将已设置为默认的地址取消
        AddressBook defaultAddressBook = getDefault();
        if(defaultAddressBook != null) {
            defaultAddressBook.setIsDefault(0);
            addressBookMapper.update(defaultAddressBook);
        }

        // 再将当前地址改为默认地址
        AddressBook addressBook = AddressBook.builder()
                .id(id)
                .isDefault(1)
                .build();
        addressBookMapper.update(addressBook);
    }

    @Override
    public void deleteWithId(Long id) {
        addressBookMapper.delete(id);
    }
}
