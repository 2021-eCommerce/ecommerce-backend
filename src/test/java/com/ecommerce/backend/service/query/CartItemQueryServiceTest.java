package com.ecommerce.backend.service.query;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.domain.entity.CartItem;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.enums.ItemStatus;
import com.ecommerce.backend.domain.response.CartItemResponse;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.CartItemRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class CartItemQueryServiceTest {
    @InjectMocks
    private CartItemQueryService sut;

    @Mock
    CartItemRepository cartItemRepository;

    @Nested
    @DisplayName("readByCartId 테스트")
    class ReadByCartIdTest {
        @Test
        @DisplayName("정상적으로 불러온 케이스")
        void readByCartIdTest01() {
            // given
            var account = makeAccount();
            var cart = makeCart(account);
            var cartItem1 = makeCartItem(cart, makeItem(), 1);
            var cartItem2 = makeCartItem(cart, makeItem(), 2);
            var expected = List.of(cartItem1, cartItem2);

            given(cartItemRepository.findByCartId(anyLong())).willReturn(Optional.of(expected));

            // when
            final List<CartItem> actual = sut.readByCartId(cart.getId());

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("아무 것도 못 불러온 케이스")
        void readByCartIdTest02() {
            // given
            var account = makeAccount();
            var cart = makeCart(account);

            given(cartItemRepository.findByCartId(anyLong())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readByCartId(cart.getId()));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ITEM_NOT_FOUND_IN_CART);
        }
    }

    @Nested
    @DisplayName("readByCartIdAndItemId 테스트")
    class ReadByCartIdAndItemIdTest {
        @Test
        @DisplayName("정상적으로 불러옴")
        void readByCartIdAndItemIdTest01() {
            // given
            var account = makeAccount();
            var cart = makeCart(account);
            var expected = makeCartItem(cart, makeItem(), 1);

            given(cartItemRepository.findByCartIdAndItemId(anyLong(), anyLong()))
                    .willReturn(expected);

            // when
            final CartItem actual = sut.readByCartIdAndItemId(1L, 1L);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("데이터가 없어서 못 불러옴")
        void readByCartIdAndItemIdTest02() {
            // when
            final CartItem actual = sut.readByCartIdAndItemId(1L, 1L);

            // then
            assertNull(actual);
        }
    }

    @Nested
    @DisplayName("readByCartIdAndItemId 테스트")
    class ReadAllByCartIdAndItemIdListAndThumbnailTest {
        @Test
        @DisplayName("정상적으로 불러옴")
        void readAllByCartIdAndItemIdListAndThumbnailTest01() {
            // given
            var response1 = makeReadResponse(1L, "firstName", 100);
            var response2 = makeReadResponse(2L, "firstName", 200);
            var expected = List.of(response1, response2);

            given(cartItemRepository.findAllByCartIdAndItemIdListAndThumbnail(anyLong(), anyList(), anyBoolean()))
                    .willReturn(Optional.of(expected));

            // when
            final List<CartItemResponse.Read> actual =
                    sut.readAllByCartIdAndItemIdListAndThumbnail(1L, List.of(1L, 2L), true);

            // then
            assertIterableEquals(expected, actual);
        }

        @Test
        @DisplayName("데이터가 없어서 못 불러옴")
        void readAllByCartIdAndItemIdListAndThumbnailTest02() {
            // given
            var expected = new ArrayList<>();
            given(cartItemRepository.findAllByCartIdAndItemIdListAndThumbnail(anyLong(), anyList(), anyBoolean()))
                    .willReturn(Optional.empty());
            // when
            final List<CartItemResponse.Read> actual =
                    sut.readAllByCartIdAndItemIdListAndThumbnail(1L, List.of(1L, 2L), true);

            // then
            assertIterableEquals(expected, actual);
        }
    }

    private CartItemResponse.Read makeReadResponse(Long itemId, String itemName, Integer price) {
        var response = new CartItemResponse.Read();

        return response.setItemId(itemId)
                .setItemName(itemName)
                .setPrice(price)
                .setCount(1)
                .setDescription("상품 설명")
                .setItemImageName("copy-이미지 이름")
                .setOriginImageName("이미지 이름")
                .setImageUrl("/thumbnail/");
    }


    private CartItem makeCartItem(Cart cart, Item item, int count) {
        return CartItem.builder()
                .cart(cart)
                .item(item)
                .count(count)
                .build();
    }

    private Account makeAccount() {
        return Account.builder()
                .id(1L)
                .build();
    }

    private Cart makeCart(Account account) {
        return Cart.builder()
                .id(1L)
                .account(account)
                .build();
    }

    private Item makeItem() {
        return Item.builder()
                .id(1L)
                .name("상품1")
                .price(1000)
                .quantity(100)
                .soldCount(2)
                .description("상품1 설명")
                .itemStatus(ItemStatus.SOLD_OUT)
                .category(null)
                .build();
    }
}