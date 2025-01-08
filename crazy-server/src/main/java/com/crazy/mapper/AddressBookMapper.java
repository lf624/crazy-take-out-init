package com.crazy.mapper;

import com.crazy.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    @Insert("INSERT INTO address_book (user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, " +
            "district_code, district_name, detail, label, is_default) VALUES " +
            "(#{userId}, #{consignee}, #{sex}, #{phone}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, " +
            "#{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(AddressBook addressBook);

    List<AddressBook> list(AddressBook addressBook);

    void update(AddressBook addressBook);

    @Delete("DELETE FROM address_book WHERE id = #{id}")
    void delete(Long id);

    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);
}
