package store.juin.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.juin.api.JUINResponse;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Delivery;
import store.juin.api.domain.response.DeliveryResponse;
import store.juin.api.service.query.DeliveryQueryService;
import store.juin.api.service.query.PrincipalQueryService;

import java.security.Principal;

@Api(tags = {"07. Delivery"})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliveries")
public class DeliveryApiController {
    private final DeliveryQueryService deliveryQueryService;
    private final PrincipalQueryService principalQueryService;

    @ApiOperation(value = "배송 상세 조회", notes = "주문에 대한 배송 상세 내용을 조회한다.")
    @GetMapping("/{deliveryId}")
    public JUINResponse<DeliveryResponse.Read> retrieveOne(Principal principal,
                                                           @PathVariable Long deliveryId) {
        log.info("[P9][CON][DLVR][ONE_]: GET /api/deliveries/{deliveryId} deliveryId({})", deliveryId);
        final Account account = principalQueryService.readByPrincipal(principal);
        final Delivery delivery = deliveryQueryService.readById(deliveryId, account.getId());

        final DeliveryResponse.Read response = DeliveryResponse.Read.from(delivery);
        return new JUINResponse<>(HttpStatus.OK, response);
    }
}
