package com.crazy.service;

import com.crazy.dto.EmployeeDTO;
import com.crazy.dto.EmployeeLoginDTO;
import com.crazy.entity.Employee;


public interface EmployeeService {

    /**
     * 员工登陆
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void save(EmployeeDTO employeeDTO);
}
