package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.CartProduct;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.entity.ProductImage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

public class CartProductResponse {
    @Data @Accessors(chain = true)
    public static class Read {
        private Long productId;

        private String productName;             // 제품의 이름

        private Integer price;                  // 제품의 가격

        private Integer count;                  // 카트 안에 들어 있는 제품의 총 개수

        private String description;             // 제품 설명

        private String imageName;               // 이미지 파일명

        private String originImageName;         // 원본 이미지 파일명

        private String imageUrl;                // 이미지 조회 경로

        private Boolean thumbnail;              // 썸네일 여부

    }

    @Data @Accessors(chain = true)
    public static class Create {
        private Long productId;

        private int count;

        public static Create of(CartProduct cartProduct) {
            return new Create()
                    .setProductId(cartProduct.getProduct().getId())
                    .setCount(cartProduct.getCount());
        }
    }

    @Data @Accessors(chain = true)
    public static class Delete {
        private Long productId;

        private int count;

        public static Create from(CartProduct cartProduct) {
            return new Create()
                    .setProductId(cartProduct.getProduct().getId())
                    .setCount(cartProduct.getCount());
        }
    }

    @Data @Accessors(chain = true)
    public static class Buy {
        private Long productId;

        private String productName;             // 제품의 이름

        private Integer price;                  // 제품의 가격

        private Integer count;                  // 제품의 총 개수

        private String description;             // 제품 설명

        private List<ProductImageResponse.Read> ProductImageList = new ArrayList<>();

        public static CartProductResponse.Buy from(Product product, ProductImage productImage, int count) {
            return new CartProductResponse.Buy()
                    .setProductId(product.getId())
                    .setProductName(product.getProductName())
                    .setPrice(product.getPrice())
                    .setCount(count)
                    .setDescription(product.getDescription())
                    .setProductImageList(List.of(ProductImageResponse.Read.of(productImage)));
        }
    }
}
