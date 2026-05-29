package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.mapper.CartMapper;
import com.campus.trade.mapper.FavoriteMapper;
import com.campus.trade.mapper.MessageMapper;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.mapper.ReviewMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.pojo.User;
import com.campus.trade.pojo.dto.AdminAddUserDTO;
import com.campus.trade.pojo.dto.LoginDTO;
import com.campus.trade.pojo.dto.RegisterDTO;
import com.campus.trade.service.UserService;
import com.campus.trade.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ProductMapper productMapper;
    private final CartMapper cartMapper;
    private final FavoriteMapper favoriteMapper;
    private final MessageMapper messageMapper;
    private final OrderMapper orderMapper;
    private final ReviewMapper reviewMapper;

    public UserServiceImpl(UserMapper userMapper, JwtUtil jwtUtil, PasswordEncoder passwordEncoder,
                           ProductMapper productMapper, CartMapper cartMapper, FavoriteMapper favoriteMapper,
                           MessageMapper messageMapper, OrderMapper orderMapper, ReviewMapper reviewMapper) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.productMapper = productMapper;
        this.cartMapper = cartMapper;
        this.favoriteMapper = favoriteMapper;
        this.messageMapper = messageMapper;
        this.orderMapper = orderMapper;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public String login(LoginDTO loginDTO) {
        User user = authenticate(loginDTO.getUsername(), loginDTO.getPassword(), User.TYPE_USER, "手机号或密码错误");
        log.info("用户登录成功, userId={}, username={}", user.getId(), user.getUsername());
        return jwtUtil.generateToken(user.getId());
    }

    @Override
    public String adminLogin(LoginDTO loginDTO) {
        User user = authenticate(loginDTO.getUsername(), loginDTO.getPassword(), User.TYPE_ADMIN, "管理员用户名或密码错误");
        log.info("管理员登录成功, userId={}, username={}", user.getId(), user.getUsername());
        return jwtUtil.generateToken(user.getId());
    }

    /**
     * 统一认证方法，支持用户名/手机号登录
     * 分步校验：账号格式 → 账号存在 → 账号状态 → 密码正确
     */
    private User authenticate(String username, String rawPassword, Integer userType, String accountType) {
        // 1. 校验账号格式
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("账号不能为空");
        }
        boolean isPhone = username.matches("^1[3-9]\\d{9}$");
        if (!isPhone && (username.length() < 3 || username.length() > 20)) {
            throw new BusinessException("用户名长度应在3-20位之间");
        }

        // 2. 校验密码格式
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        if (rawPassword.length() < 6 || rawPassword.length() > 20) {
            throw new BusinessException("密码长度应在6-20位之间");
        }

        // 3. 检查账号是否存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (isPhone) {
            wrapper.eq("phone", username);
        } else {
            wrapper.eq("username", username);
        }
        wrapper.eq("user_type", userType);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            if (isPhone) {
                throw new BusinessException("该手机号未注册" + (userType == User.TYPE_ADMIN ? "管理员账号" : ""));
            } else {
                throw new BusinessException("用户名不存在");
            }
        }

        // 4. 检查账号状态
        if (User.STATUS_DISABLED == user.getStatus()) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 5. 校验密码是否正确
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        return user;
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        if (!registerDTO.getPassword().matches("^(?=.*[a-zA-Z])(?=.*\\d).{6,20}$")) {
            throw new BusinessException("密码必须包含字母和数字，长度6-20位");
        }

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", registerDTO.getPhone());
        User existingUser = userMapper.selectOne(wrapper);
        if (existingUser != null) {
            throw new BusinessException("该手机号已注册");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhone(registerDTO.getPhone());
        user.setUserType(User.TYPE_USER);
        user.setStatus(User.STATUS_NORMAL);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
        log.info("用户注册成功, username={}, phone={}", user.getUsername(), user.getPhone());
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public void update(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("用户信息更新, userId={}", user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 1. 先获取该用户的所有商品ID
        QueryWrapper<com.campus.trade.pojo.Product> productWrapper = new QueryWrapper<>();
        productWrapper.eq("seller_id", id);
        List<com.campus.trade.pojo.Product> userProducts = productMapper.selectList(productWrapper);
        List<Long> productIds = userProducts.stream().map(com.campus.trade.pojo.Product::getId).collect(Collectors.toList());
        int productCount = productIds.size();
        
        // 2. 删除这些商品被其他用户收藏的记录（外键约束）
        if (!productIds.isEmpty()) {
            QueryWrapper<com.campus.trade.pojo.Favorite> favByProductWrapper = new QueryWrapper<>();
            favByProductWrapper.in("product_id", productIds);
            int favByProductCount = favoriteMapper.selectCount(favByProductWrapper).intValue();
            if (favByProductCount > 0) {
                favoriteMapper.delete(favByProductWrapper);
                log.info("已删除用户{}商品的{}条被收藏记录", id, favByProductCount);
            }
        }
        
        // 3. 删除这些商品被其他用户加入购物车的记录（外键约束）
        if (!productIds.isEmpty()) {
            QueryWrapper<com.campus.trade.pojo.Cart> cartByProductWrapper = new QueryWrapper<>();
            cartByProductWrapper.in("product_id", productIds);
            int cartByProductCount = cartMapper.selectCount(cartByProductWrapper).intValue();
            if (cartByProductCount > 0) {
                cartMapper.delete(cartByProductWrapper);
                log.info("已删除用户{}商品的{}条购物车记录", id, cartByProductCount);
            }
        }
        
        // 4. 删除用户发布的商品
        if (productCount > 0) {
            productMapper.delete(productWrapper);
            log.info("已删除用户{}的{}个商品", id, productCount);
        }

        // 5. 删除用户的购物车记录
        QueryWrapper<com.campus.trade.pojo.Cart> cartWrapper = new QueryWrapper<>();
        cartWrapper.eq("user_id", id);
        int cartCount = cartMapper.selectCount(cartWrapper).intValue();
        if (cartCount > 0) {
            cartMapper.delete(cartWrapper);
            log.info("已删除用户{}的{}条购物车记录", id, cartCount);
        }

        // 6. 删除用户的收藏记录
        QueryWrapper<com.campus.trade.pojo.Favorite> favWrapper = new QueryWrapper<>();
        favWrapper.eq("user_id", id);
        int favCount = favoriteMapper.selectCount(favWrapper).intValue();
        if (favCount > 0) {
            favoriteMapper.delete(favWrapper);
            log.info("已删除用户{}的{}条收藏记录", id, favCount);
        }

        // 4. 删除用户的消息记录（发送的和接收的）
        QueryWrapper<com.campus.trade.pojo.Message> msgSendWrapper = new QueryWrapper<>();
        msgSendWrapper.eq("from_user_id", id);
        int msgSendCount = messageMapper.selectCount(msgSendWrapper).intValue();
        if (msgSendCount > 0) {
            messageMapper.delete(msgSendWrapper);
        }
        QueryWrapper<com.campus.trade.pojo.Message> msgRecvWrapper = new QueryWrapper<>();
        msgRecvWrapper.eq("to_user_id", id);
        int msgRecvCount = messageMapper.selectCount(msgRecvWrapper).intValue();
        if (msgRecvCount > 0) {
            messageMapper.delete(msgRecvWrapper);
        }
        log.info("已删除用户{}的{}条消息记录", id, msgSendCount + msgRecvCount);

        // 5. 删除用户的订单（作为买家或卖家的）
        QueryWrapper<com.campus.trade.pojo.Order> orderBuyWrapper = new QueryWrapper<>();
        orderBuyWrapper.eq("buyer_id", id);
        int orderBuyCount = orderMapper.selectCount(orderBuyWrapper).intValue();
        if (orderBuyCount > 0) {
            orderMapper.delete(orderBuyWrapper);
        }
        QueryWrapper<com.campus.trade.pojo.Order> orderSellWrapper = new QueryWrapper<>();
        orderSellWrapper.eq("seller_id", id);
        int orderSellCount = orderMapper.selectCount(orderSellWrapper).intValue();
        if (orderSellCount > 0) {
            orderMapper.delete(orderSellWrapper);
        }
        log.info("已删除用户{}的{}条订单记录", id, orderBuyCount + orderSellCount);

        // 6. 删除用户的评价记录
        QueryWrapper<com.campus.trade.pojo.Review> reviewWrapper = new QueryWrapper<>();
        reviewWrapper.eq("user_id", id);
        int reviewCount = reviewMapper.selectCount(reviewWrapper).intValue();
        if (reviewCount > 0) {
            reviewMapper.delete(reviewWrapper);
            log.info("已删除用户{}的{}条评价记录", id, reviewCount);
        }

        // 7. 最后删除用户本身
        userMapper.deleteById(id);
        log.info("用户已删除(级联), userId={}, 商品={}, 购物车={}, 收藏={}, 消息={}, 订单={}, 评价={}",
                id, productCount, cartCount, favCount, msgSendCount + msgRecvCount,
                orderBuyCount + orderSellCount, reviewCount);
    }

    @Override
    public List<User> search(String keyword) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        // 不过滤deleted字段，显示所有用户（包括已删除的）
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("username", keyword).or().like("phone", keyword));
        }
        wrapper.orderByDesc("deleted"); // 未删除的排在前面
        return userMapper.selectList(wrapper);
    }

    @Override
    public void adminAddUser(AdminAddUserDTO adminAddUserDTO) {
        // 检查用户名是否已存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", adminAddUserDTO.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已存在
        wrapper = new QueryWrapper<>();
        wrapper.eq("phone", adminAddUserDTO.getPhone());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("手机号已被注册");
        }

        User user = new User();
        user.setUsername(adminAddUserDTO.getUsername());
        user.setPassword(passwordEncoder.encode(adminAddUserDTO.getPassword()));
        user.setPhone(adminAddUserDTO.getPhone());
        user.setUserType(adminAddUserDTO.getUserType() != null ? adminAddUserDTO.getUserType() : User.TYPE_USER);
        user.setNickname(adminAddUserDTO.getUsername()); // 默认昵称为用户名
        user.setStatus(0); // 正常状态
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
        log.info("管理员添加用户成功, userId={}, username={}", user.getId(), user.getUsername());
    }

    @Override
    public void resetPassword(String phone, String newPassword) {
        if (!newPassword.matches("^(?=.*[a-zA-Z])(?=.*\\d).{6,20}$")) {
            throw new BusinessException("密码必须包含字母和数字，长度6-20位");
        }

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("该手机号未注册");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("密码重置成功, phone={}", phone);
    }
}
