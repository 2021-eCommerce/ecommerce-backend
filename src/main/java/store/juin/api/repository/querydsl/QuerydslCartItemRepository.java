package store.juin.api.repository.querydsl;

import store.juin.api.domain.entity.CartItem;
import store.juin.api.domain.response.CartItemResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslCartItemRepository {
    Optional<List<CartItem>> findByAccountId(Long accountId);

    Optional<List<CartItem>> findByCartId(Long cartId);

    long deleteByCartIdAndItemId(Long cartId, Long itemId);

    Optional<List<CartItem>> findByCartIdAndItemIdIn(Long cartId, List<Long> itemIdList);

    Optional<List<CartItemResponse.Retrieve>>
    findAllByCartIdAndItemIdListAndThumbnail(Long cartId,
                                             List<Long> itemIdList,
                                             boolean thumbnail,
                                             boolean representative);

}