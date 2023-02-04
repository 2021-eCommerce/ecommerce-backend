package store.juin.api.controller;

import store.juin.api.JUINResponse;
import store.juin.api.domain.entity.Item;
import store.juin.api.domain.response.ItemResponse;
import store.juin.api.service.query.ItemQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@Api(tags = {"05. Item"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/items")
public class ItemApiController {
    private final ItemQueryService itemQueryService;

    @ApiOperation(value = "상품 읽기", notes = "상품을 읽는다.")
    @GetMapping("/{itemId}")
    public JUINResponse<ItemResponse.Read> retrieveOne(@PathVariable Long itemId) {
        log.info("[P9][CTRL][ITEM][ONE_]: GET /api/items/{itemId} itemId({})", itemId);

        try {
            final Item item = itemQueryService.readById(itemId);

            var response = ItemResponse.Read.from(item);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P9][CTRL][ITEM][ONE_]: 존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "상품 목록 조회 및 검색", notes = "전체 또는 조건 별로 검색한다.")
    @GetMapping
    public JUINResponse<Page<ItemResponse.Read>> retrieveAll(@PageableDefault(size = 10) Pageable pageable,
                                                             @RequestParam(required = false) Long categoryId,
                                                             @RequestParam(value = "name", required = false) String searchTitle,
                                                             @RequestParam(required = false) String personalColor) {
        log.info("[P9][CTRL][ITEM][ALL_]: GET /api/items pageable({}), categoryId({})", pageable, categoryId);

        try {
            var response = itemQueryService.display(pageable, searchTitle, categoryId, personalColor);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("[P9][CTRL][ITEM][ALL_]: 존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "전체 상품의 개수", notes = "전체 상품의 개수를 반환한다.")
    @GetMapping("/count")
    public long retrieveCount() {
        log.info("[P9][CTRL][ITEM][CNT_]: GET /api/items/count");
        return itemQueryService.total();
    }

    @ApiOperation(value = "검색한 상품의 개수", notes = "검색한 상품의 개수를 반환한다.")
    @GetMapping("/search/count")
    public Long retrieveSearchCount(@RequestParam("name") String searchTitle) {
        log.info("[P9][CTRL][ITEM][SHCT]: GET /api/items/search/count searchTitle({})", searchTitle);

        return itemQueryService.totalByNameContaining(searchTitle);
    }
}

