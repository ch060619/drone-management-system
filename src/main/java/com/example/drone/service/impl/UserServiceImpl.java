package com.example.drone.service.impl;

import com.example.drone.domain.dto.*;
import com.example.drone.domain.entity.*;
import com.example.drone.exception.DroneBusinessException;
import com.example.drone.repository.*;
import com.example.drone.security.JwtTokenProvider;
import com.example.drone.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final LoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;
    @Autowired(required = false)
    private JavaMailSender mailSender;
    @Value("${spring.mail.username:}")
    private String mailFrom;

    private final ConcurrentHashMap<String, String> memoryStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> memoryExpires = new ConcurrentHashMap<>();

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PermissionRepository permissionRepository, LoginLogRepository loginLogRepository,
                           PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.loginLogRepository = loginLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private boolean hasRedis() { return redisTemplate != null; }

    private String getFromStore(String key) {
        if (hasRedis()) return redisTemplate.opsForValue().get(key);
        Long exp = memoryExpires.get(key);
        if (exp != null && System.currentTimeMillis() > exp) { memoryStore.remove(key); memoryExpires.remove(key); return null; }
        return memoryStore.get(key);
    }

    private void setToStore(String key, String value, long ttl, TimeUnit unit) {
        if (hasRedis()) { redisTemplate.opsForValue().set(key, value, ttl, unit); return; }
        memoryStore.put(key, value);
        memoryExpires.put(key, System.currentTimeMillis() + unit.toMillis(ttl));
    }

    private boolean hasKeyInStore(String key) {
        if (hasRedis()) return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        Long exp = memoryExpires.get(key);
        if (exp != null && System.currentTimeMillis() > exp) { memoryStore.remove(key); memoryExpires.remove(key); return false; }
        return memoryStore.containsKey(key);
    }

    private void deleteFromStore(String key) {
        if (hasRedis()) { redisTemplate.delete(key); return; }
        memoryStore.remove(key);
        memoryExpires.remove(key);
    }

    @Override
    public String sendVerifyCode(String email) {
        String cooldownKey = "verify_code_cooldown:" + email;
        if (hasKeyInStore(cooldownKey)) {
            throw new DroneBusinessException("验证码发送过于频繁，请60秒后再试");
        }
        String codeKey = "verify_code:" + email;
        deleteFromStore(codeKey);
        String code = String.format("%06d", new Random().nextInt(999999));
        setToStore(codeKey, code, 5, TimeUnit.MINUTES);
        setToStore(cooldownKey, "1", 60, TimeUnit.SECONDS);
        if (mailSender != null && mailFrom != null && !mailFrom.isEmpty()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(mailFrom);
                message.setTo(email);
                message.setSubject("无人机管理系统 - 验证码");
                message.setText("您的验证码是：" + code + "，5分钟内有效。");
                mailSender.send(message);
                logger.info("验证码已发送至 {}", email);
                return null;
            } catch (Exception e) {
                logger.warn("邮件发送失败，降级为页面展示: {} = {}", email, code);
                return code;
            }
        }
        logger.info("开发模式验证码: {} = {}", email, code);
        return code;
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        String codeKey = "verify_code:" + request.getEmail();
        String storedCode = getFromStore(codeKey);
        if (storedCode == null) {
            throw new DroneBusinessException("验证码已过期，请重新获取");
        }
        if (!storedCode.equals(request.getVerifyCode())) {
            throw new DroneBusinessException("验证码错误");
        }
        SysUser existing = userRepository.selectByEmail(request.getEmail());
        if (existing != null) {
            throw new DroneBusinessException("该邮箱已被注册");
        }
        String activationToken = UUID.randomUUID().toString().replace("-", "");
        SysUser user = SysUser.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status("ACTIVE")
                .role("user")
                .activationToken(activationToken)
                .build();
        userRepository.insert(user);
        SysRole userRole = roleRepository.selectByCode("user");
        if (userRole != null) {
            userRepository.insertUserRole(user.getId(), userRole.getId());
        }
        deleteFromStore(codeKey);
        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(mailFrom);
                message.setTo(request.getEmail());
                message.setSubject("无人机管理系统 - 账号激活");
                message.setText("请点击以下链接激活您的账号：http://localhost:8080/api/v1/auth/activate?token=" + activationToken);
                mailSender.send(message);
            } catch (Exception e) {
                logger.warn("激活邮件发送失败，激活Token: {}", activationToken);
            }
        }
        logger.info("用户注册成功: {}", request.getEmail());
    }

    @Override
    public LoginResponse login(LoginRequest request, String ip, String device) {
        String failKey = "login_fail:" + request.getAccount();
        String lockKey = "login_lock:" + request.getAccount();
        String ipFailKey = "login_fail_ip:" + ip;
        String ipLockKey = "login_lock_ip:" + ip;
        if (hasKeyInStore(lockKey)) {
            throw new DroneBusinessException("账号已被锁定，请30分钟后再试");
        }
        if (hasKeyInStore(ipLockKey)) {
            throw new DroneBusinessException("当前IP已被临时封禁，请稍后再试");
        }
        SysUser user = userRepository.selectByEmail(request.getAccount());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            String fails = getFromStore(failKey);
            int failCount = fails == null ? 0 : Integer.parseInt(fails);
            failCount++;
            setToStore(failKey, String.valueOf(failCount), 5, TimeUnit.MINUTES);
            if (failCount >= 5) {
                setToStore(lockKey, "1", 30, TimeUnit.MINUTES);
                deleteFromStore(failKey);
            }
            String ipFails = getFromStore(ipFailKey);
            int ipFailCount = ipFails == null ? 0 : Integer.parseInt(ipFails);
            ipFailCount++;
            setToStore(ipFailKey, String.valueOf(ipFailCount), 1, TimeUnit.HOURS);
            if (ipFailCount >= 20) {
                setToStore(ipLockKey, "1", 24, TimeUnit.HOURS);
            }
            recordLoginLog(null, ip, device, "FAIL", "密码错误");
            throw new DroneBusinessException("用户名或密码错误");
        }
        if ("INACTIVE".equals(user.getStatus())) {
            throw new DroneBusinessException("账号未激活，请先查收激活邮件");
        }
        if ("LOCKED".equals(user.getStatus())) {
            throw new DroneBusinessException("账号已被禁用，请联系管理员");
        }
        deleteFromStore(failKey);
        recordLoginLog(user.getId(), ip, device, "SUCCESS", null);
        List<String> roles = Collections.singletonList(user.getRole());
        List<String> permissions = permissionRepository.selectPermCodesByUserId(user.getId());
        if (permissions == null) permissions = Collections.emptyList();
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), roles, permissions);
        boolean rememberMe = Boolean.TRUE.equals(request.getRememberMe());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), rememberMe);
        long refreshTtl = rememberMe ? 30L * 24 * 3600 : 7 * 24 * 3600;
        setToStore("refresh_token:" + user.getId(), refreshToken, refreshTtl, TimeUnit.SECONDS);
        setToStore("user_perms:" + user.getId(), String.join(",", permissions), 2, TimeUnit.HOURS);
        boolean mfaRequired = "admin".equals(user.getRole());
        String mfaToken = null;
        if (mfaRequired) {
            mfaToken = UUID.randomUUID().toString().replace("-", "");
            setToStore("mfa_session:" + mfaToken, String.valueOf(user.getId()), 5, TimeUnit.MINUTES);
        }
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .id(user.getId()).email(user.getEmail()).roles(roles).permissions(permissions)
                .build();
        return LoginResponse.builder()
                .accessToken(mfaRequired ? "" : accessToken)
                .refreshToken(refreshToken)
                .expiresIn(7200L).userInfo(userInfo).mfaRequired(mfaRequired)
                .mfaToken(mfaToken)
                .build();
    }

    @Override
    public LoginResponse verifyMfa(String mfaToken, String code) {
        String mfaKey = "mfa_session:" + mfaToken;
        String userIdStr = getFromStore(mfaKey);
        if (userIdStr == null) {
            throw new DroneBusinessException("MFA会话已过期，请重新登录");
        }
        Long userId = Long.valueOf(userIdStr);
        String codeKey = "verify_code:" + userId;
        String storedCode = getFromStore(codeKey);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new DroneBusinessException("MFA验证码错误或已过期");
        }
        deleteFromStore(mfaKey);
        deleteFromStore(codeKey);
        SysUser user = userRepository.selectById(userId);
        if (user == null) throw new DroneBusinessException("用户不存在");
        List<String> roles = Collections.singletonList(user.getRole());
        List<String> permissions = permissionRepository.selectPermCodesByUserId(userId);
        if (permissions == null) permissions = Collections.emptyList();
        String accessToken = jwtTokenProvider.generateAccessToken(userId, roles, permissions);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId, false);
        setToStore("refresh_token:" + userId, refreshToken, 7, TimeUnit.DAYS);
        setToStore("user_perms:" + userId, String.join(",", permissions), 2, TimeUnit.HOURS);
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .id(user.getId()).email(user.getEmail()).roles(roles).permissions(permissions)
                .build();
        return LoginResponse.builder()
                .accessToken(accessToken).refreshToken(refreshToken)
                .expiresIn(7200L).userInfo(userInfo).mfaRequired(false).build();
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new DroneBusinessException("Refresh Token 无效或已过期，请重新登录");
        }
        Long userId = jwtTokenProvider.getUserIdFromToken(request.getRefreshToken());
        String storedToken = getFromStore("refresh_token:" + userId);
        if (storedToken == null || !storedToken.equals(request.getRefreshToken())) {
            throw new DroneBusinessException("Refresh Token 已失效，请重新登录");
        }
        SysUser user = userRepository.selectById(userId);
        if (user == null) {
            throw new DroneBusinessException("用户不存在");
        }
        List<String> permissions = permissionRepository.selectPermCodesByUserId(userId);
        if (permissions == null) permissions = Collections.emptyList();
        List<String> roles = Collections.singletonList(user.getRole());
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, roles, permissions);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, false);
        setToStore("refresh_token:" + userId, newRefreshToken, 7, TimeUnit.DAYS);
        setToStore("user_perms:" + userId, String.join(",", permissions), 2, TimeUnit.HOURS);
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .id(user.getId()).email(user.getEmail()).roles(roles).permissions(permissions)
                .build();
        return LoginResponse.builder()
                .accessToken(newAccessToken).refreshToken(newRefreshToken)
                .expiresIn(7200L).userInfo(userInfo).mfaRequired(false)
                .build();
    }

    @Override
    public void logout(Long userId) {
        deleteFromStore("refresh_token:" + userId);
        deleteFromStore("user_perms:" + userId);
        logger.info("用户 {} 已登出", userId);
    }

    @Override
    public void activateAccount(String token) {
        SysUser user = userRepository.selectByActivationToken(token);
        if (user == null) {
            throw new DroneBusinessException("激活链接无效或已过期");
        }
        userRepository.updateStatus(user.getId(), "ACTIVE");
        logger.info("用户 {} 已激活", user.getEmail());
    }

    @Override
    public void deleteAccount(Long userId, String code) {
        if (userId == null) {
            throw new DroneBusinessException("用户未登录");
        }
        SysUser user = userRepository.selectById(userId);
        if (user == null) {
            throw new DroneBusinessException("用户不存在");
        }
        String codeKey = "verify_code:" + user.getEmail();
        String stored = getFromStore(codeKey);
        if (!code.equals(stored)) {
            throw new DroneBusinessException("验证码错误或已过期");
        }
        userRepository.deleteUserRoles(userId);
        userRepository.deleteById(userId);
        deleteFromStore(codeKey);
        deleteFromStore("verify_code_cooldown:" + user.getEmail());
        deleteFromStore("refresh_token:" + userId);
        deleteFromStore("user_perms:" + userId);
        logger.info("用户 {} 已注销账号（硬删除）", user.getEmail());
    }

    private void recordLoginLog(Long userId, String ip, String device, String result, String failReason) {
        try {
            SysLoginLog log = SysLoginLog.builder()
                    .userId(userId).ip(ip).device(device)
                    .result(result).failReason(failReason).build();
            loginLogRepository.insert(log);
        } catch (Exception e) {
            logger.warn("登录日志记录失败: {}", e.getMessage());
        }
    }
}
