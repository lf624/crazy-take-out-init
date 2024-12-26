package com.crazy.controller.admin;

import com.crazy.constant.JwtClaimsConstant;
import com.crazy.context.BaseContext;
import com.crazy.dto.EmployeeDTO;
import com.crazy.dto.EmployeeLoginDTO;
import com.crazy.entity.Employee;
import com.crazy.exception.AccountLockedException;
import com.crazy.exception.AccountNotFoundException;
import com.crazy.exception.PasswordErrorException;
import com.crazy.properties.JwtProperties;
import com.crazy.result.Result;
import com.crazy.service.EmployeeService;
import com.crazy.utils.JwtUtil;
import com.crazy.vo.EmployeeLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        Employee employee;
        try {
            employee = employeeService.login(employeeLoginDTO);
        } catch (PasswordErrorException pe) {
            log.warn("try login failed: password error for {}", employeeLoginDTO);
            return Result.error("密码错误");
        } catch (AccountNotFoundException e) {
            log.warn("try login failed: account not found for {}", employeeLoginDTO);
            return Result.error("账户不存在");
        } catch (AccountLockedException e) {
            log.warn("try login failed: account locked for {}", employeeLoginDTO);
            return Result.error("账户被锁定");
        }

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
        log.info("newly add employee：{}",employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }
}
