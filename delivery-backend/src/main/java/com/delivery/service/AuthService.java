package com.delivery.service;

import com.delivery.dto.LoginDto;
import com.delivery.dto.RegisterDto;
import com.delivery.entity.Customer;
import com.delivery.entity.Merchant;
import com.delivery.entity.Rider;
import com.delivery.mapper.CustomerMapper;
import com.delivery.mapper.MerchantMapper;
import com.delivery.mapper.RiderMapper;
import com.delivery.util.SecurityUtil;
import com.delivery.vo.LoginVo;
import com.delivery.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerMapper customerMapper;
    private final MerchantMapper merchantMapper;
    private final RiderMapper riderMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginVo adminLogin(LoginDto dto) {
        SecurityUtil.validateUsername(dto.getUsername());
        SecurityUtil.validatePassword(dto.getPassword());
        return new LoginVo("admin-token", 1L, dto.getUsername(), "ADMIN");
    }

    public LoginVo customerLogin(LoginDto dto) {
        SecurityUtil.validateUsername(dto.getUsername());
        SecurityUtil.validatePassword(dto.getPassword());
        
        Customer customer = customerMapper.selectByUsername(dto.getUsername());
        if (customer == null || !passwordEncoder.matches(dto.getPassword(), customer.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(customer.getId(), customer.getUsername(), "CUSTOMER");
        return new LoginVo(token, customer.getId(), customer.getUsername(), "CUSTOMER");
    }

    public LoginVo merchantLogin(LoginDto dto) {
        SecurityUtil.validateUsername(dto.getUsername());
        SecurityUtil.validatePassword(dto.getPassword());
        
        Merchant merchant = merchantMapper.selectByUsername(dto.getUsername());
        if (merchant == null || !passwordEncoder.matches(dto.getPassword(), merchant.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(merchant.getId(), merchant.getUsername(), "MERCHANT");
        return new LoginVo(token, merchant.getId(), merchant.getUsername(), "MERCHANT");
    }

    public LoginVo riderLogin(LoginDto dto) {
        SecurityUtil.validateUsername(dto.getUsername());
        SecurityUtil.validatePassword(dto.getPassword());
        
        Rider rider = riderMapper.selectByUsername(dto.getUsername());
        if (rider == null || !passwordEncoder.matches(dto.getPassword(), rider.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(rider.getId(), rider.getUsername(), "RIDER");
        return new LoginVo(token, rider.getId(), rider.getUsername(), "RIDER");
    }

    public void customerRegister(RegisterDto dto) {
        SecurityUtil.validateUsername(dto.getUsername());
        SecurityUtil.validatePassword(dto.getPassword());
        SecurityUtil.validatePhone(dto.getPhone());

        Customer exist = customerMapper.selectByUsername(dto.getUsername());
        if (exist != null) {
            throw new RuntimeException("用户名已存在");
        }
        Customer customer = new Customer();
        customer.setUsername(SecurityUtil.sanitizeInput(dto.getUsername()));
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));
        customer.setPhone(dto.getPhone());
        customer.setNickname(dto.getUsername());
        customer.setStatus(1);
        customerMapper.insert(customer);
    }
}
