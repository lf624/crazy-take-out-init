package com.crazy.controller.admin;

import com.crazy.constant.JwtClaimsConstant;
import com.crazy.context.BaseContext;
import com.crazy.dto.EmployeeDTO;
import com.crazy.dto.EmployeeLoginDTO;
import com.crazy.dto.EmployeePageQueryDTO;
import com.crazy.entity.Employee;
import com.crazy.properties.JwtProperties;
import com.crazy.result.PageResult;
import com.crazy.result.Result;
import com.crazy.service.EmployeeService;
import com.crazy.utils.JwtUtil;
import com.crazy.vo.EmployeeLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Tag(name = "员工相关接口")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;
    @Autowired
    JwtProperties jwtProperties;

    /**
     * 登陆
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "员工登陆")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("employee login: {}", employeeLoginDTO);
        Employee employee = employeeService.login(employeeLoginDTO);;

        Map<String, Object> claim = new HashMap<>();
        claim.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJwt(jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(), claim);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .username(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "员工退出")
    public Result<String> logout() {
        BaseContext.removeCurrentId();
        return Result.success();
    }

    @PostMapping
    @Operation(summary = "新增员工")
    public Result<String> save(@RequestBody EmployeeDTO employeeDTO) {
        log.info("newly add employee：{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "员工分页查询")
    public Result<PageResult<Employee>> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("employee page query: {}", employeePageQueryDTO);
        PageResult<Employee> result = employeeService.page(employeePageQueryDTO);
        return Result.success(result);
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "启用或禁用员工账号")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        log.info("change employee id={} status={}", id, status);
        employeeService.startOrStop(status, id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询员工信息")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    @PutMapping
    @Operation(summary = "编辑员工信息")
    public Result<String> update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("update employee: {}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
}