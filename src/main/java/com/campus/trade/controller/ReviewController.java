package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.pojo.Result;
import com.campus.trade.pojo.Review;
import com.campus.trade.service.ReviewService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/add")
    public Result<String> add(@RequestParam Long orderId,
                              @RequestParam Long productId,
                              @RequestParam Integer rating,
                              @RequestParam(required = false) String content,
                              @RequestAttribute("userId") Long userId) {
        reviewService.add(orderId, productId, userId, rating, content);
        return Result.success("评价成功");
    }

    @GetMapping("/product/{productId}")
    public Result<IPage<Review>> getByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<Review> result = reviewService.getByProductId(productId, page, size);
        return Result.success(result);
    }

    @GetMapping("/order/{orderId}")
    public Result<Review> getByOrder(@PathVariable Long orderId) {
        Review review = reviewService.getByOrderId(orderId);
        return Result.success(review);
    }
}
