package store.juin.api.repository.querydsl.impl;

import store.juin.api.domain.entity.CartItem;
import store.juin.api.domain.response.CartItemResponse;
import store.juin.api.repository.querydsl.QuerydslCartItemRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static store.juin.api.domain.entity.QCartItem.cartItem;
import static store.juin.api.domain.entity.QItem.item;
import static store.juin.api.domain.entity.QItemImage.itemImage;

@RequiredArgsConstructor
public class QuerydslCartItemRepositoryImpl implements QuerydslCartItemRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<List<CartItem>> findByAccountId(Long accountId) {
        return null;
    }

    @Override
    public Optional<List<CartItem>> findByCartId(Long cartId) {
        return Optional.ofNullable(
                queryFactory
                        .select(cartItem)
                        .from(cartItem)
                        .where(cartItem.cart.id.eq(cartId))
                        .fetch()
        );
    }

    @Override
    @Transactional
    public long deleteByCartIdAndItemId(Long cartId, Long itemId) {
        return queryFactory
                .delete(cartItem)
                .where(cartItem.cart.id.eq(cartId).and(cartItem.item.id.eq(itemId)))
                .execute();
    }

    @Override
    public Optional<List<CartItem>> findByCartIdAndItemIdIn(Long cartId, List<Long> itemIdList) {
        return Optional.ofNullable(
                queryFactory
                        .select(cartItem)
                        .from(cartItem)
                        .where(cartItem.cart.id.eq(cartId).and(cartItem.item.id.in(itemIdList)))
                        .fetch()
        );
    }

    @Override
    public Optional<List<CartItemResponse.Retrieve>>
    findAllByCartIdAndItemIdListAndThumbnail(Long cartId, List<Long> itemIdList, boolean isThumbnail) {

        return Optional.ofNullable(
                queryFactory
                        .select(Projections.fields(CartItemResponse.Retrieve.class
                                , item.id.as("itemId")
                                , item.name.as("itemName")
                                , item.price
                                , cartItem.count
                                , item.description
                                , itemImage.name.as("itemImageName")
                                , itemImage.originName.as("originImageName")
                                , itemImage.imageUrl
                                , itemImage.thumbnail))
                        .from(cartItem)
                        .join(item)
                        .on(cartItem.item.id.eq(item.id))
                        .join(itemImage)
                        .on(itemImage.item.id.eq(item.id))
                        .where(cartItem.cart.id.eq(cartId))
                        .where(item.id.in(itemIdList))
                        .where(itemImage.thumbnail.eq(isThumbnail))
                        .fetch()
        );
    }
}