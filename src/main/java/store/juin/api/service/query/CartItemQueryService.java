package store.juin.api.service.query;

import store.juin.api.domain.entity.CartItem;
import store.juin.api.domain.response.CartItemResponse;
import store.juin.api.exception.Msg;
import store.juin.api.repository.jpa.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemQueryService {
    private final CartItemRepository cartItemRepository;

    @Transactional(readOnly = true)
    public List<CartItem> readByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ITEM_NOT_FOUND_IN_CART));
    }

    @Transactional(readOnly = true)
    public CartItem readByCartIdAndItemId(Long cartId, Long itemId) {
        return cartItemRepository.findByCartIdAndItemId(cartId, itemId);
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse.Retrieve>
    readAllByCartIdAndItemIdListAndThumbnail(Long cartId,
                                             List<Long> itemIdList,
                                             boolean thumbnail,
                                             boolean representative) {
        return cartItemRepository.findAllByCartIdAndItemIdListAndThumbnail(cartId, itemIdList, thumbnail, representative)
                .orElse(new ArrayList<>());
    }
}