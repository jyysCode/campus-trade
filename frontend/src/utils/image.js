// 商品图片映射 - 使用picsum固定图片，确保每个商品ID对应固定图片
const productImageMap = {
  // 电子产品
  1: 'https://picsum.photos/seed/iphone400/400/400', // iPhone
  2: 'https://picsum.photos/seed/macbook400/400/400', // MacBook
  3: 'https://picsum.photos/seed/xiaomi400/400/400', // 小米手机
  4: 'https://picsum.photos/seed/keyboard400/400/400', // 机械键盘
  5: 'https://picsum.photos/seed/headphone400/400/400', // 耳机
  6: 'https://picsum.photos/seed/monitor400/400/400', // 显示器
  
  // 图书教材
  7: 'https://picsum.photos/seed/bookcode400/400/400', // 编程书籍
  8: 'https://picsum.photos/seed/textbook400/400/400', // 教材
  9: 'https://picsum.photos/seed/literature400/400/400', // 文学
  10: 'https://picsum.photos/seed/kaoyan400/400/400', // 考研资料
  
  // 生活用品
  11: 'https://picsum.photos/seed/sofa400/400/400', // 沙发
  12: 'https://picsum.photos/seed/lamp400/400/400', // 台灯
  13: 'https://picsum.photos/seed/storage400/400/400', // 收纳盒
  14: 'https://picsum.photos/seed/cup400/400/400', // 水杯
  
  // 服饰鞋包
  15: 'https://picsum.photos/seed/shoes400/400/400', // 运动鞋
  16: 'https://picsum.photos/seed/bag400/400/400', // 背包
  17: 'https://picsum.photos/seed/tshirt400/400/400', // T恤
  18: 'https://picsum.photos/seed/handbag400/400/400', // 包包
  
  // 运动器材
  19: 'https://picsum.photos/seed/basketball400/400/400', // 篮球
  20: 'https://picsum.photos/seed/dumbbell400/400/400', // 哑铃
  21: 'https://picsum.photos/seed/yoga400/400/400', // 瑜伽垫
  22: 'https://picsum.photos/seed/badminton400/400/400', // 羽毛球拍
  
  // 美妆护肤
  23: 'https://picsum.photos/seed/lipstick400/400/400', // 口红
  24: 'https://picsum.photos/seed/skincare400/400/400', // 护肤品
  25: 'https://picsum.photos/seed/perfume400/400/400', // 香水
  
  // 数码配件
  26: 'https://picsum.photos/seed/cable400/400/400', // 数据线
  27: 'https://picsum.photos/seed/powerbank400/400/400', // 充电宝
  28: 'https://picsum.photos/seed/case400/400/400', // 手机壳
  
  // 其他
  29: 'https://picsum.photos/seed/figure400/400/400', // 手办
  30: 'https://picsum.photos/seed/instrument400/400/400', // 乐器
}

// 分类默认图片
const categoryDefaultImages = {
  '电子产品': 'https://picsum.photos/seed/electronics400/400/400',
  '图书教材': 'https://picsum.photos/seed/books400/400/400',
  '生活用品': 'https://picsum.photos/seed/living400/400/400',
  '服饰鞋包': 'https://picsum.photos/seed/clothing400/400/400',
  '运动器材': 'https://picsum.photos/seed/sports400/400/400',
  '美妆护肤': 'https://picsum.photos/seed/beauty400/400/400',
  '数码配件': 'https://picsum.photos/seed/accessories400/400/400',
  '其他': 'https://picsum.photos/seed/others400/400/400',
}

// 获取商品图片 - 确保每个商品ID对应固定图片
export const getProductImage = (product) => {
  if (!product) return 'https://via.placeholder.com/400x400?text=No+Image'
  
  // 如果商品已有图片，使用已有图片
  if (product.image) return product.image
  
  // 根据商品ID获取固定图片
  if (product.id && productImageMap[product.id]) {
    return productImageMap[product.id]
  }
  
  // 根据分类获取默认图片
  if (product.category && categoryDefaultImages[product.category]) {
    // 使用商品ID的hash值来选择图片，确保同一商品总是同一图片
    const hash = product.id ? product.id % 5 : 0
    const baseUrl = categoryDefaultImages[product.category]
    return `${baseUrl}&sig=${hash}`
  }
  
  // 兜底：使用商品ID生成确定性图片
  return `https://images.unsplash.com/photo-1560343090-f0409e92791a?w=400&h=400&fit=crop&sig=${product.id || 0}`
}

// 获取用户头像 - 使用picsum固定头像
export const getUserAvatar = (userId, username) => {
  if (!userId) {
    // 默认头像
    return 'https://picsum.photos/seed/default200/200/200'
  }
  
  // 管理员固定头像
  if (username === 'root' || username === 'admin') {
    return 'https://picsum.photos/seed/admin200/200/200'
  }
  
  // 普通用户根据ID获取固定头像
  return `https://picsum.photos/seed/user${userId}200/200/200`
}

export default {
  getProductImage,
  getUserAvatar,
  productImageMap,
  categoryDefaultImages
}
