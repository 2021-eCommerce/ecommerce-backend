package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.enums.ItemStatus;
import lombok.Data;
import lombok.experimental.Accessors;

public class ItemRequest {
    @Data @Accessors(chain = true)
    public static class Create {
        private Long categoryId;

        private String name;

        private Integer price;          // 제품의 가격

        private Integer quantity;       // 제품의 총 개수

        private String description;

        public Item toItem(Category category) {
            return Item.builder()
                    .category(category)
                    .name(this.name)
                    .price(this.price)
                    .quantity(this.quantity)
                    .description(this.description)
                    .itemStatus(ItemStatus.READY)
                    .soldCount(0)
                    .build();
        }
    }
}