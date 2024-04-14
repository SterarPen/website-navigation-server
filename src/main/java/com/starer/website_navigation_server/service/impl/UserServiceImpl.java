package com.starer.website_navigation_server.service.impl;

import com.starer.website_navigation_server.constrant.IdentifyType;
import com.starer.website_navigation_server.dao.IUserDao;
import com.starer.website_navigation_server.pojo.Role;
import com.starer.website_navigation_server.pojo.User;
import com.starer.website_navigation_server.pojo.dto.CacheInformation;
import com.starer.website_navigation_server.pojo.dto.LoginInformation;
import com.starer.website_navigation_server.pojo.dto.RegisterInformation;
import com.starer.website_navigation_server.pojo.dto.TokenInformation;
import com.starer.website_navigation_server.service.ICacheService;
import com.starer.website_navigation_server.service.ISendCodeService;
import com.starer.website_navigation_server.service.IUserService;
import com.starer.website_navigation_server.util.JWTUtil;
import com.starer.website_navigation_server.util.RandomUtil;
import com.starer.website_navigation_server.util.RedisUtil;
import com.starer.website_navigation_server.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserDao userDao;
    private final Properties properties;
    private final RedisUtil redisUtil;
    private final ICacheService cacheService;
    private final ISendCodeService sendEmailCodeService;
    private final ISendCodeService sendPhoneCodeService;

    @Autowired
    public UserServiceImpl(IUserDao userDao, Properties properties,
                           RedisUtil redisUtil, ICacheService cacheService,
                           @Qualifier("emailSendCodeServiceImpl") ISendCodeService sendEmailCodeService,
                           @Qualifier("phoneSendCodeServiceImpl") ISendCodeService sendPhoneCodeService) {
        this.userDao = userDao;
        this.properties = properties;
        this.redisUtil = redisUtil;
        this.cacheService = cacheService;
        this.sendEmailCodeService = sendEmailCodeService;
        this.sendPhoneCodeService = sendPhoneCodeService;
    }

    @Override
    public ServiceResult<TokenInformation> login(LoginInformation loginInformation, String ip, String device) {
        ServiceResult<TokenInformation> serviceResult;

        if(loginInformation == null) {
            serviceResult = ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.LOGIN")),
                    properties.getProperty("FAILURE.LOGIN.ERROR_INFORMATION")
            );
            return serviceResult;
        }

        boolean loginSuccess = false;
        User user = new User();
        switch (loginInformation.getLoginType()) {
            case LoginInformation.ID_AND_PASSWORD:
                user.setUserId(loginInformation.getAccount());
                user.setPassword(loginInformation.getToken());
                break;
            case LoginInformation.PHONE_AND_PASSWORD:
                user.setPhone(loginInformation.getAccount());
                user.setPassword(loginInformation.getToken());
                break;
            case LoginInformation.EMAIL_AND_PASSWORD:
                user.setEmail(loginInformation.getAccount());
                user.setPassword(loginInformation.getToken());
                break;
            case LoginInformation.PHONE_AND_CODE:
                String string = redisUtil.getString(loginInformation.getAccount());
                if(string != null && string.equals(loginInformation.getToken())) {
                    loginSuccess = true;
                    user.setPhone(loginInformation.getAccount());
                }
                break;
            case LoginInformation.EMAIL_AND_CODE:
                String str2 = redisUtil.getString(loginInformation.getAccount());
                if(str2 != null && str2.equals(loginInformation.getToken())) {
                    loginSuccess = true;
                    user.setEmail(loginInformation.getAccount());
                }
                break;
            default:
                serviceResult = ServiceResult.createFactory(
                        Integer.parseInt(properties.getProperty("FAILURE.LOGIN")),
                        properties.getProperty("FAILURE.LOGIN.TYPE")
                );
                return serviceResult;
        }

        User[] select = userDao.select(user);
        if (select.length == 1) {
            Date date = new Date();
            String s = JWTUtil.genAccessToken(select[0].getUserId(), ip,
                    device, date, 3600L);

            redisUtil.setString(select[0].getUserId(), s);
            redisUtil.expire(select[0].getUserId(), 3600);

            serviceResult = ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("SUCCESS.LOGIN")),
                    properties.getProperty("SUCCESS.LOGIN.ALL"),
                    new TokenInformation(select[0].getUserId(), s, ip, device, date, 3600L)
            );
            return serviceResult;
        } else if(select.length == 0) {
            serviceResult = ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.LOGIN")),
                    properties.getProperty("FAILURE.LOGIN.ID_OR_PASSWORD")
            );
            return serviceResult;
        } else {
            throw new RuntimeException("存在重复属性");
        }
    }

    public ServiceResult<User> registerByEmail(RegisterInformation registerInformation) {
        String prefix = "verification:email:register:";
        ServiceResult<CacheInformation> cacheData =
                cacheService.getString(prefix + registerInformation.getAccount());

        if(cacheData.getData() == null || !registerInformation.getCode().equals(cacheData.getData().getValue())) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.REGISTER")),
                    properties.getProperty("FAILURE.REGISTER.CODE_ERROR")
            );
        }

        User user = new User();
        user.setUserId(RandomUtil.generateId());
        user.setUserName(registerInformation.getUserName());
        user.setPassword(registerInformation.getPassword());
        user.setRole(new Role("111111111110", null, null));
        user.setEmail(registerInformation.getAccount());

        int insert = userDao.insert(user);
        if(insert == 1) {
           return ServiceResult.createFactory(
                   Integer.parseInt(properties.getProperty("SUCCESS.REGISTER")),
                   properties.getProperty("SUCCESS.REGISTER.ALL"),
                   user
           );
        } else {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.REGISTER")),
                    properties.getProperty("FAILURE.REGISTER.ALL")
            );
        }
    }

    public ServiceResult<User> registerByPhone(RegisterInformation registerInformation) {
        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("FAILURE.REGISTER")),
                "暂未开通手机号注册方式"
        );
    }

    public ServiceResult<?> sendRegisterEmailCode(String email) {

        // 判断邮箱是否已被注册
        User user = new User();
        user.setEmail(email);
        User[] select = userDao.select(user);
        if(select.length != 0) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.REGISTER")),
                    properties.getProperty("FAILURE.REGISTER.REPETITION_EMAIL")
            );
        }

        // 随机生成6位验证码
        String code = RandomUtil.randomCode(6);

        // 向缓存中保存注册验证码
        CacheInformation cacheInformation =
                new CacheInformation("verification:email:register:" + email,
                        code, String.class, 300L, TimeUnit.SECONDS);
        ServiceResult<CacheInformation> cacheInformationServiceResult =
                cacheService.setStringAndExpire(cacheInformation);
        if(!cacheInformationServiceResult.isSuccess()) { // 当向缓存中添加验证码失败时的操作
            return ServiceResult.createFactory(
                    cacheInformationServiceResult.getCode(),
                    cacheInformationServiceResult.getMessage()
            );
        }

        // 发送验证码邮箱
        return sendEmailCodeService.sendCode(email, code);
    }

    public ServiceResult<?> sendRegisterPhoneCode(String phone) {
        // 判断邮箱是否已被注册
        User user = new User();
        user.setEmail(phone);
        User[] select = userDao.select(user);
        if(select.length != 0) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.REGISTER")),
                    properties.getProperty("FAILURE.REGISTER.REPETITION_EMAIL")
            );
        }

        // 随机生成6位验证码
        String code = RandomUtil.randomCode(6);

        // 向缓存中保存注册验证码
        CacheInformation cacheInformation =
                new CacheInformation("verification:phone:register:" + phone,
                        code, String.class, 300L, TimeUnit.SECONDS);
        ServiceResult<CacheInformation> cacheInformationServiceResult =
                cacheService.setStringAndExpire(cacheInformation);
        if(!cacheInformationServiceResult.isSuccess()) { // 当向缓存中添加验证码失败时的操作
            return ServiceResult.createFactory(
                    cacheInformationServiceResult.getCode(),
                    cacheInformationServiceResult.getMessage()
            );
        }

        // 发送验证码邮箱
        return sendPhoneCodeService.sendCode(phone, code);
    }

    public ServiceResult<Boolean> verifyCode(String key, String code) {

        // 获取缓存中对应key的值
        ServiceResult<CacheInformation> cacheCode = cacheService.getString(key);

        // 如果查询失败，则直接返回失败信息
        if(!cacheCode.isSuccess()) {
            return ServiceResult.createFactory(
                    cacheCode.getCode(),
                    cacheCode.getMessage()
            );
        }

        // 若缓存中不存在验证码，直接返回更新失败信息
        if(cacheCode.getData() == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.USER")),
                    properties.getProperty("FAILURE.USER.NOT_SEND")
            );
        }

        // 判断缓存中对应key的值是否等于code
        return code.equals(cacheCode.getData().getValue()) ?
                ServiceResult.createFactory(cacheCode.getCode(), cacheCode.getMessage(), true)
                : ServiceResult.createFactory(cacheCode.getCode(), cacheCode.getMessage());
    }
    @Override
    public ServiceResult<User> register(RegisterInformation registerInformation) {
        if(registerInformation == null ||
            registerInformation.getPlatform() == null ||
            registerInformation.getAccount() == null ||
            registerInformation.getPassword() == null ||
            registerInformation.getCode() == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.REGISTER")),
                    properties.getProperty("FAILURE.REGISTER.METHOD_PROPERTIES_IS_NULL")
            );
        }

        User user = null;
        switch (registerInformation.getPlatform()) {
            case "email":
                ServiceResult<CacheInformation> cacheData =
                        cacheService.getString("verification:email:register:" + registerInformation.getAccount());
                boolean equals = registerInformation.getCode().equals(cacheData.getData().getValue());
                if(equals) {
                    user = new User();
                    user.setEmail(registerInformation.getAccount());
                }
                break;
            case "phone":
                ServiceResult<CacheInformation> cacheDataPhone =
                        cacheService.getString("verification:phone:register:" + registerInformation.getAccount());
                boolean equals1 = registerInformation.getCode().equals(cacheDataPhone.getData().getValue());
                if(equals1) {
                    user = new User();
                    user.setPhone(registerInformation.getAccount());
                }
                break;
        }

        if(user == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.REGISTER")),
                    properties.getProperty("FAILURE.REGISTER.ALL")
            );
        }

        user.setUserId("1" + RandomUtil.generateId());
        user.setUserName(registerInformation.getUserName());
        user.setRole(new Role("111111111110", "普通用户", "00000001"));
        user.setPassword(registerInformation.getPassword());

        int insert = userDao.insert(user);
        if(insert != 1) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.REGISTER")),
                    properties.getProperty("FAILURE.REGISTER.ALL")
            );
        }

        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("SUCCESS.REGISTER")),
                properties.getProperty("SUCCESS.REGISTER.ALL"),
                user
        );
    }

    @Override
    public ServiceResult<User> getInformation(String userId) {
        User user = userDao.selectById(userId);
        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("SUCCESS.USER")),
                properties.getProperty("SUCCESS.USER.QUERY_INFORMATION"),
                user
        );
    }

    @Override
    public ServiceResult<User> updateInformation(User user) {
        int update = userDao.update(user);
        if(update == 1) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("SUCCESS.USER")),
                    properties.getProperty("SUCCESS.USER.UPDATE"),
                    user
            );
        } else {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("SUCCESS.USER")),
                    properties.getProperty("SUCCESS.USER.UPDATE")
            );
        }
    }

    /**
     * 为用户发送验证码
     * @param account 邮箱或手机号
     * @param platform 发送验证码的方式（邮箱-email，手机号-phone）
     * @param function 验证码用途
     * @return 添加缓存的错误信息，或发送消息的结果信息
     */
    public ServiceResult<?> sendCode(String account, String platform, String function) {
        // 根据参数组合成key
        String key = "verification:" + platform + ":" + function + ":" + account;

        // 随机生成6位数字，作为value
        String value = RandomUtil.randomCode(6);

        // 根据key和value创建缓存信息对象
        CacheInformation cacheInformation = new CacheInformation("verification:" + platform + ":" + function + ":" + account,
                RandomUtil.randomCode(6), String.class, 300L, TimeUnit.SECONDS);

        // 向缓存中添加元素
        ServiceResult<CacheInformation> cacheInformationServiceResult = cacheService.setString(cacheInformation);
        if(!cacheInformationServiceResult.isSuccess()) { // 如果添加失败，直接返回添加缓存的错误信息
            return cacheInformationServiceResult;
        }

        // 发送消息
        ServiceResult<HashMap<String, String>> resultData = null;
        switch (platform) {
            case "email":
                resultData = sendEmailCodeService.sendCode(account, value);
                break;
            case "phone":
                resultData = sendPhoneCodeService.sendCode(account, value);
                break;
            default:
                return ServiceResult.createFactory(
                        Integer.parseInt(properties.getProperty("FAILURE.CODE")),
                        properties.getProperty("FAILURE.CODE.PLATFORM_ERROR")
                );
        }

        return resultData;
    }

    @Override
    public ServiceResult<User> updatePassword(String account, String token, String newPassword,
                                              IdentifyType identifyType) {
        switch (identifyType) {
            case ID_AND_PASSWORD:
                return updateInformationByIdAndPassword(account, token, newPassword);
            case EMAIL_AND_PASSWORD:
                return updateInformationByEmailAndCode(account, token, newPassword);
            case PHONE_AND_PASSWORD:
                return updateInformationByPhoneAndCode(account, token, newPassword);
            default:
                return ServiceResult.createFactory(
                        Integer.parseInt(properties.getProperty("FAILURE.CODE")),
                        properties.getProperty("FAILURE.CODE.PLATFORM_ERROR")
                );
        }

    }


    @Override
    public ServiceResult<User> updatePhone(String userId, String code, String newPhone, String newCode) {

        // 根据userId获取对应用户信息
        User user = userDao.selectById(userId);

        // 如果用户不存在，则直接返回更新失败信息
        if(user == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.USER")),
                    properties.getProperty("FAILURE.USER.UPDATE")
            );
        }

        if(user.getPhone() != null) {
            // 查询缓存中对应验证码信息
            String key = "verification:phone:update-phone:" + user.getPhone();
            ServiceResult<CacheInformation> cacheCode = cacheService.getString(key);

            // 如果查询失败，则直接返回失败信息
            if (!cacheCode.isSuccess()) {
                return ServiceResult.createFactory(
                        cacheCode.getCode(),
                        cacheCode.getMessage()
                );
            }

            // 若缓存中不存在验证码，直接返回更新失败信息
            if (cacheCode.getData() == null) {
                return ServiceResult.createFactory(
                        Integer.parseInt(properties.getProperty("FAILURE.USER")),
                        properties.getProperty("FAILURE.USER.NOT_SEND")
                );
            }

            if(!code.equals(cacheCode.getData().getValue())) {
                return ServiceResult.createFactory(
                        Integer.parseInt(properties.getProperty("FAILURE.USER")),
                        properties.getProperty("FAILURE.USER.UPDATE")
                );
            }
        }

        // 查询缓存中对应验证码信息
        String key = "verification:phone:update-phone:" + newPhone;
        ServiceResult<CacheInformation> cacheCode = cacheService.getString(key);

        // 如果查询失败，则直接返回失败信息
        if (!cacheCode.isSuccess()) {
            return ServiceResult.createFactory(
                    cacheCode.getCode(),
                    cacheCode.getMessage()
            );
        }

        // 若缓存中不存在验证码，直接返回更新失败信息
        if (cacheCode.getData() == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.USER")),
                    properties.getProperty("FAILURE.USER.NOT_SEND")
            );
        }

        // 判断验证码是否正确，正确则执行更新操作
        if(code.equals(cacheCode.getData().getValue())) {
            User user1 = new User();
            user1.setUserId(userId);
            user1.setPhone(newPhone);
            int update = userDao.update(user1);
            if (update == 1) {
                user.setPhone(newPhone);
                return ServiceResult.createFactory(
                        Integer.parseInt(properties.getProperty("SUCCESS.USER")),
                        properties.getProperty("SUCCESS.USER.UPDATE"),
                        user
                );
            }
        }

        // 验证码错误，返回更新失败信息
        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("FAILURE.USER")),
                properties.getProperty("FAILURE.USER.UPDATE")
        );
    }

    public ServiceResult<User> updateEmail(String userId, String code, String newEmail, String newCode) {
        // 查询userId对应的用户信息
        User user = userDao.selectById(userId);

        // 如果用户不存在，则直接返回更新失败信息
        if(user == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.USER")),
                    properties.getProperty("FAILURE.USER.UPDATE")
            );
        }

        // 判断新邮箱是否已被绑定
        User queryUser = new User();
        queryUser.setEmail(newEmail);
        User[] select = userDao.select(queryUser);
        if(select.length != 0) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.USER")),
                    properties.getProperty("FAILURE.USER.REPETITION_EMAIL")
            );
        }

        if(user.getEmail() != null) {
            // 查询缓存中对应验证码信息
            String key = "verification:email:update-email:" + user.getEmail();
            ServiceResult<CacheInformation> cacheCode = cacheService.getString(key);

            // 如果查询失败，则直接返回失败信息
            if (!cacheCode.isSuccess()) {
                return ServiceResult.createFactory(
                        cacheCode.getCode(),
                        cacheCode.getMessage()
                );
            }

            // 若缓存中不存在验证码，直接返回更新失败信息
            if (cacheCode.getData() == null) {
                return ServiceResult.createFactory(
                        Integer.parseInt(properties.getProperty("FAILURE.USER")),
                        properties.getProperty("FAILURE.USER.NOT_SEND")
                );
            }

            // 验证码错误，返回更新失败信息
            if (!code.equals(cacheCode.getData().getValue())) {
                return ServiceResult.createFactory(
                        Integer.parseInt(properties.getProperty("FAILURE.USER")),
                        properties.getProperty("FAILURE.USER.UPDATE")
                );
            }
        }

        String key1 = "verification:email:update-email:" + newEmail;
        ServiceResult<CacheInformation> cacheCode1 = cacheService.getString(key1);

        // 如果查询失败，则直接返回失败信息
        if (!cacheCode1.isSuccess()) {
            return ServiceResult.createFactory(
                    cacheCode1.getCode(),
                    cacheCode1.getMessage()
            );
        }

        // 若缓存中不存在验证码，直接返回更新失败信息
        if (cacheCode1.getData() == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.USER")),
                    properties.getProperty("FAILURE.USER.NOT_SEND")
            );
        }

        // 判断验证码是否正确，正确则执行更新操作
        if(newCode.equals(cacheCode1.getData().getValue())) {
            User user1 = new User();
            user1.setUserId(user.getUserId());
            user1.setEmail(newEmail);
            int update = userDao.update(user1);
            if(update == 1) {
                user.setEmail(newEmail);
                return ServiceResult.createFactory(
                        Integer.parseInt(properties.getProperty("SUCCESS.USER")),
                        properties.getProperty("SUCCESS.USER.UPDATE"),
                        user
                );
            }
        }

        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("FAILURE.USER")),
                properties.getProperty("FAILURE.USER.CODE_ERROR")
        );

    }

    /**
     * 判断邮箱或手机号是否未被绑定
     * @param email
     * @param phone
     * @return 未绑定-true，已绑定-false
     */
    @Override
    public ServiceResult<Boolean> emptyUser(String email, String phone) {
        boolean emailEmpty = true;
        if(email != null) {
            User user = new User();
            user.setEmail(email);
            User[] select = userDao.select(user);
            emailEmpty = (select.length == 0);
        }

        boolean phoneEmpty = true;
        if(phone != null) {
            User user1 = new User();
            user1.setPhone(phone);
            User[] select1 = userDao.select(user1);
            phoneEmpty = (select1.length == 0);
        }

        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("SUCCESS.USER")),
                properties.getProperty("SUCCESS.USER.QUERY_INFORMATION"),
                emailEmpty && phoneEmpty
        );

    }

    private ServiceResult<User> updateInformationByIdAndPassword(String userId, String password, String newPassword) {

        // 获取userId对应的用户信息
        User user = userDao.selectById(userId);

        // 若用户不存在，直接返回更新失败信息
        if(user == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.USER")),
                    properties.getProperty("FAILURE.USER.UPDATE")
            );
        }

        // 判断密码是否正确，正确则执行更新操作
        if(password.equals(user.getPassword())) {
            User updateUser = new User();
            updateUser.setUserId(userId);
            updateUser.setPassword(newPassword);

            int updateResult = userDao.update(updateUser);
            if(updateResult == 1) {
                user.setPassword(newPassword);
                return ServiceResult.createFactory(
                        Integer.parseInt(properties.getProperty("SUCCESS.USER")),
                        properties.getProperty("SUCCESS.USER.UPDATE"),
                        user
                );
            }
        }
        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("FAILURE.USER")),
                properties.getProperty("FAILURE.USER.UPDATE")
        );
    }

    private ServiceResult<User> updateInformationByEmailAndCode(String email, String code, String newPassword) {

        // 查询email对应的用户信息
        User user = new User();
        user.setEmail(email);
        User[] select = userDao.select(user);

        // 如果用户不存在，则直接返回更新失败信息
        if(select.length != 1) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.USER")),
                    properties.getProperty("FAILURE.USER.UPDATE")
            );
        }

        // 查询缓存中对应验证码信息
        String key = "verification:email:update-information:" + email;
        ServiceResult<CacheInformation> cacheCode = cacheService.getString(key);

        // 如果查询失败，则直接返回失败信息
        if(!cacheCode.isSuccess()) {
            return ServiceResult.createFactory(
                    cacheCode.getCode(),
                    cacheCode.getMessage()
            );
        }

        // 若缓存中不存在验证码，直接返回更新失败信息
        if(cacheCode.getData() == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.USER")),
                    properties.getProperty("FAILURE.USER.NOT_SEND")
            );
        }

        // 判断验证码是否正确，正确则执行更新操作
        if(code.equals(cacheCode.getData().getValue())) {
            user.setUserId(select[0].getUserId());
            user.setPassword(newPassword);
            user.setEmail(null);
            int update = userDao.update(user);
            if(update == 1) {
                select[0].setPassword(newPassword);
                return ServiceResult.createFactory(
                        Integer.parseInt(properties.getProperty("SUCCESS.USER")),
                        properties.getProperty("SUCCESS.USER.UPDATE"),
                        user
                );
            }
        }

        // 验证码错误，返回更新失败信息
        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("FAILURE.USER")),
                properties.getProperty("FAILURE.USER.UPDATE")
        );
    }

    private ServiceResult<User> updateInformationByPhoneAndCode(String phone, String code, String newPassword) {

        // 查询email对应的用户信息
        User user = new User();
        user.setPhone(phone);
        User[] select = userDao.select(user);

        // 如果用户不存在，则直接返回更新失败信息
        if(select.length != 1) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.USER")),
                    properties.getProperty("FAILURE.USER.UPDATE")
            );
        }

        // 查询缓存中对应验证码信息
        String key = "verification:phone:update-information:" + phone;
        ServiceResult<CacheInformation> cacheCode = cacheService.getString(key);

        // 如果查询失败，则直接返回失败信息
        if(!cacheCode.isSuccess()) {
            return ServiceResult.createFactory(
                    cacheCode.getCode(),
                    cacheCode.getMessage()
            );
        }

        // 若缓存中不存在验证码，直接返回更新失败信息
        if(cacheCode.getData() == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.USER")),
                    properties.getProperty("FAILURE.USER.NOT_SEND")
            );
        }

        // 判断验证码是否正确，正确则执行更新操作
        if(code.equals(cacheCode.getData().getValue())) {
            user.setUserId(select[0].getUserId());
            user.setPassword(newPassword);
            user.setPhone(null);
            int update = userDao.update(user);
            if(update == 1) {
                select[0].setPassword(newPassword);
                return ServiceResult.createFactory(
                        Integer.parseInt(properties.getProperty("SUCCESS.USER")),
                        properties.getProperty("SUCCESS.USER.UPDATE"),
                        user
                );
            }
        }

        // 验证码错误，返回更新失败信息
        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("FAILURE.USER")),
                properties.getProperty("FAILURE.USER.UPDATE")
        );
    }



}
