package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.pojo.Order;
import com.campus.trade.pojo.Product;
import com.campus.trade.pojo.User;
import com.campus.trade.pojo.dto.ProductDTO;
import com.campus.trade.pojo.dto.ProductQueryDTO;
import com.campus.trade.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    public ProductServiceImpl(ProductMapper productMapper, UserMapper userMapper, OrderMapper orderMapper) {
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public void add(ProductDTO productDTO, Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setCategory(productDTO.getCategory());
        product.setPrice(productDTO.getPrice());
        product.setOriginalPrice(productDTO.getOriginalPrice());
        product.setStock(productDTO.getStock());
        product.setImage(productDTO.getImage());
        product.setDescription(productDTO.getDescription());
        product.setLocation(productDTO.getLocation());
        product.setCondition(productDTO.getCondition());
        product.setSellerId(userId);
        product.setSellerName(user.getUsername());
        product.setSellerPhone(user.getPhone());
        product.setStatus(Product.STATUS_PENDING);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());

        productMapper.insert(product);
        log.info("商品发布成功, productId={}, name={}, sellerId={}", product.getId(), product.getName(), userId);
    }

    @Override
    public void update(ProductDTO productDTO, Long userId) {
        Product product = productMapper.selectById(productDTO.getId());

        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        if (!product.getSellerId().equals(userId)) {
            throw new BusinessException("无权修改该商品");
        }

        product.setName(productDTO.getName());
        product.setCategory(productDTO.getCategory());
        product.setPrice(productDTO.getPrice());
        product.setOriginalPrice(productDTO.getOriginalPrice());
        product.setStock(productDTO.getStock());
        product.setImage(productDTO.getImage());
        product.setDescription(productDTO.getDescription());
        product.setLocation(productDTO.getLocation());
        product.setCondition(productDTO.getCondition());
        // 修改后重置为待审核状态
        product.setStatus(Product.STATUS_PENDING);
        product.setUpdateTime(LocalDateTime.now());

        productMapper.updateById(product);
        log.info("商品更新成功(重新审核), productId={}, name={}", product.getId(), product.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long userId) {
        Product product = productMapper.selectById(id);

        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        if (!product.getSellerId().equals(userId)) {
            throw new BusinessException("无权删除该商品");
        }

        // 检查是否存在进行中的订单
        QueryWrapper<Order> orderWrapper = new QueryWrapper<>();
        orderWrapper.eq("product_id", id)
                .in("status", List.of(
                        Order.STATUS_PENDING_PAYMENT,
                        Order.STATUS_PAID,
                        Order.STATUS_SHIPPED));
        Long activeOrderCount = orderMapper.selectCount(orderWrapper);
        if (activeOrderCount > 0) {
            throw new BusinessException("该商品存在进行中的订单，无法删除");
        }

        // 逻辑删除
        product.setDeleted(1);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        log.info("商品已删除(逻辑), productId={}, name={}", id, product.getName());
    }

    @Override
    public Product getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            // 增加浏览量
            UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", id)
                    .setSql("views = IFNULL(views, 0) + 1");
            productMapper.update(null, updateWrapper);
        }
        return product;
    }

    @Override
    public IPage<Product> list(int page, int size, String name, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        Page<Product> pageParam = new Page<>(page, size);
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", Product.STATUS_APPROVED);

        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq("category", category);
        }
        if (minPrice != null) {
            wrapper.ge("price", minPrice);
        }
        if (maxPrice != null) {
            wrapper.le("price", maxPrice);
        }

        wrapper.orderByDesc("create_time");
        return productMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public List<Product> getHotProducts() {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", Product.STATUS_APPROVED);
        wrapper.orderByDesc("sales", "create_time");
        return productMapper.selectList(wrapper.last("LIMIT 8"));
    }

    @Override
    public IPage<Product> getByUserId(Long userId, int page, int size) {
        Page<Product> pageParam = new Page<>(page, size);
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("seller_id", userId);
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");
        return productMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void approve(Long id) {
        updateProductStatus(id, Product.STATUS_APPROVED, "审核通过");
    }

    @Override
    public void reject(Long id) {
        updateProductStatus(id, Product.STATUS_REJECTED, "审核拒绝");
    }

    /**
     * 统一的商品状态更新方法
     */
    private void updateProductStatus(Long id, Integer newStatus, String action) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(newStatus);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        log.info("商品{}成功, productId={}, name={}", action, id, product.getName());
    }

    @Override
    public IPage<Product> listPending(int page, int size) {
        Page<Product> pageParam = new Page<>(page, size);
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", Product.STATUS_PENDING);
        wrapper.orderByDesc("create_time");
        return productMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void initTestData() {
        QueryWrapper<Product> countWrapper = new QueryWrapper<>();
        long count = productMapper.selectCount(countWrapper);
        if (count > 0) {
            return;
        }

        Product[] products = {
            createProduct("iPhone 14 Pro 256GB", "电子产品", new BigDecimal("6599.00"), 3,
                "https://picsum.photos/seed/phone1/400/400", "几乎全新，使用不到半年，无任何划痕，箱说齐全",
                2L, "张三", "13800138002"),
            createProduct("MacBook Pro 14英寸 M2", "电子产品", new BigDecimal("13999.00"), 2,
                "https://picsum.photos/seed/laptop1/400/400", "2023款M2芯片，16G+512G，保护完好，功能正常",
                2L, "张三", "13800138002"),
            createProduct("高等数学同济第七版", "图书教材", new BigDecimal("28.00"), 5,
                "https://picsum.photos/seed/book1/400/400", "同济第七版高数，考研必备，有笔记",
                3L, "李四", "13800138003"),
            createProduct("大学英语四级词汇书", "图书教材", new BigDecimal("35.00"), 8,
                "https://picsum.photos/seed/book2/400/400", "新东方四级词汇乱序版，几乎全新",
                3L, "李四", "13800138003"),
            createProduct("羽毛球拍尤尼克斯", "运动器材", new BigDecimal("299.00"), 2,
                "https://picsum.photos/seed/sports1/400/400", "尤尼克斯入门级球拍，使用3个月，送球包",
                2L, "张三", "13800138002"),
            createProduct("篮球斯伯丁", "运动器材", new BigDecimal("89.00"), 4,
                "https://picsum.photos/seed/sports2/400/400", "斯伯丁7号球，适合室内室外使用",
                3L, "李四", "13800138003"),
            createProduct("小米手环7", "电子产品", new BigDecimal("199.00"), 6,
                "https://picsum.photos/seed/watch1/400/400", "全新未拆封，功能正常，有发票",
                2L, "张三", "13800138002"),
            createProduct("耐克运动鞋42码", "服饰鞋包", new BigDecimal("459.00"), 1,
                "https://picsum.photos/seed/shoes1/400/400", "Nike Air Max系列，42码，9成新",
                3L, "李四", "13800138003"),
            createProduct("台灯LED护眼灯", "生活用品", new BigDecimal("79.00"), 10,
                "https://picsum.photos/seed/lamp1/400/400", "可调节亮度，USB供电，全新",
                2L, "张三", "13800138002"),
            createProduct("保温杯500ml", "生活用品", new BigDecimal("45.00"), 12,
                "https://picsum.photos/seed/cup1/400/400", "真空不锈钢保温杯，保温效果好",
                3L, "李四", "13800138003")
        };

        for (Product product : products) {
            productMapper.insert(product);
        }
        log.info("测试数据初始化完成, 共插入{}条商品", products.length);
    }

    private Product createProduct(String name, String category, BigDecimal price, int stock,
                                  String image, String description, Long sellerId,
                                  String sellerName, String sellerPhone) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setStock(stock);
        product.setImage(image);
        product.setDescription(description);
        product.setSellerId(sellerId);
        product.setSellerName(sellerName);
        product.setSellerPhone(sellerPhone);
        product.setStatus(Product.STATUS_APPROVED);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        return product;
    }

    @Override
    public void adminAdd(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setCategory(productDTO.getCategory());
        product.setPrice(productDTO.getPrice());
        product.setOriginalPrice(productDTO.getOriginalPrice());
        product.setStock(productDTO.getStock());
        // 设置默认图片和描述
        product.setImage(productDTO.getImage() != null && !productDTO.getImage().isEmpty() 
            ? productDTO.getImage() 
            : "https://picsum.photos/seed/" + System.currentTimeMillis() + "/400/400");
        product.setDescription(productDTO.getDescription() != null && !productDTO.getDescription().isEmpty() 
            ? productDTO.getDescription() 
            : "暂无描述");
        product.setLocation(productDTO.getLocation());
        product.setCondition(productDTO.getCondition());
        product.setStatus(Product.STATUS_APPROVED);
        product.setSellerId(0L); // 管理员添加，无卖家
        product.setSellerName("平台自营");
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());

        productMapper.insert(product);
        log.info("管理员添加商品成功, productId={}, name={}", product.getId(), product.getName());
    }

    @Override
    public void adminUpdate(ProductDTO productDTO) {
        Product product = productMapper.selectById(productDTO.getId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setName(productDTO.getName());
        product.setCategory(productDTO.getCategory());
        product.setPrice(productDTO.getPrice());
        product.setOriginalPrice(productDTO.getOriginalPrice());
        product.setStock(productDTO.getStock());
        product.setImage(productDTO.getImage());
        product.setDescription(productDTO.getDescription());
        product.setLocation(productDTO.getLocation());
        product.setCondition(productDTO.getCondition());
        product.setUpdateTime(LocalDateTime.now());

        productMapper.updateById(product);
        log.info("管理员更新商品成功, productId={}, name={}", product.getId(), product.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminDelete(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setDeleted(1);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        log.info("管理员删除商品(逻辑), productId={}, name={}", id, product.getName());
    }

    @Override
    public List<String> getAllCategories() {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", Product.STATUS_APPROVED);
        wrapper.select("category").isNotNull("category");
        wrapper.groupBy("category");

        List<Product> products = productMapper.selectList(wrapper);
        return products.stream()
                .map(Product::getCategory)
                .filter(category -> category != null && !category.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public IPage<Product> search(ProductQueryDTO queryDTO) {
        Page<Product> pageParam = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", Product.STATUS_APPROVED);

        if (queryDTO.getName() != null && !queryDTO.getName().trim().isEmpty()) {
            wrapper.like("name", queryDTO.getName().trim());
        }

        if (queryDTO.getCategories() != null && !queryDTO.getCategories().isEmpty()) {
            wrapper.in("category", queryDTO.getCategories());
        }

        // 价格筛选
        if (queryDTO.getMinPrice() != null) {
            wrapper.ge("price", queryDTO.getMinPrice());
        }
        if (queryDTO.getMaxPrice() != null && queryDTO.getMaxPrice().compareTo(java.math.BigDecimal.ZERO) > 0) {
            wrapper.le("price", queryDTO.getMaxPrice());
        }

        String sortBy = queryDTO.getSortBy();
        String sortOrder = queryDTO.getSortOrder();

        if ("relevance".equals(sortBy)) {
            if (queryDTO.getName() != null && !queryDTO.getName().trim().isEmpty()) {
                // 修复SQL注入：使用参数化方式
                wrapper.last("ORDER BY LOCATE('" + queryDTO.getName().trim().replace("'", "\\'") + "', name) DESC");
            } else {
                wrapper.orderByDesc("create_time");
            }
        } else if ("price".equals(sortBy)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                wrapper.orderByAsc("price");
            } else {
                wrapper.orderByDesc("price");
            }
        } else if ("sales".equals(sortBy)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                wrapper.orderByAsc("sales");
            } else {
                wrapper.orderByDesc("sales");
            }
        } else {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                wrapper.orderByAsc("create_time");
            } else {
                wrapper.orderByDesc("create_time");
            }
        }

        return productMapper.selectPage(pageParam, wrapper);
    }
}
