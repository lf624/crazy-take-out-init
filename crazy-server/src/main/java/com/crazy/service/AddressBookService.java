package com.crazy.service;

import com.crazy.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

    void save(AddressBook addressBook);

    List<AddressBook> list();

    AddressBook getDefault();

    void updateWithId(AddressBook addressBook);

    AddressBook getById(Long id);

    void setDefault(Long id);

    void deleteWithId(Long id);
}
