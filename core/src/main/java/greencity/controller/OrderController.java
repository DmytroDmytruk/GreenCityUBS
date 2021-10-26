package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.CurrentUserUuid;
import greencity.constants.HttpStatuses;
import greencity.constants.ValidationConstant;
import greencity.dto.*;
import greencity.service.ubs.UBSClientService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ubs")
@Validated
public class OrderController {
    private final UBSClientService ubsClientService;

    /**
     * Constructor with parameters.
     */
    @Autowired
    public OrderController(UBSClientService ubsClientService) {
        this.ubsClientService = ubsClientService;
    }

    /**
     * Controller returns all available bags and bonus points of current user.
     * {@link greencity.dto.UserVO}.
     *
     * @param userUuid {@link UserVO} id.
     * @return {@link UserPointsAndAllBagsDto}.
     * @author Oleh Bilonizhka
     */
    @ApiOperation(value = "Get current user points and all bags list.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = UserPointsAndAllBagsDto.class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/order-details")
    public ResponseEntity<UserPointsAndAllBagsDto> getCurrentUserPoints(
        @ApiIgnore @CurrentUserUuid String userUuid) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ubsClientService.getFirstPageData(userUuid));
    }

    /**
     * Controller returns entered certificate status if not absent.
     *
     * @param code {@link String} code of certificate.
     * @return {@link CertificateDto}.
     * @author Oleh Bilonizhka
     */
    @ApiOperation(value = "Check if certificate is available.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = CertificateDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/certificate/{code}")
    public ResponseEntity<CertificateDto> checkIfCertificateAvailable(
        @PathVariable @Pattern(regexp = ValidationConstant.SERTIFICATE_CODE_REGEXP,
            message = ValidationConstant.SERTIFICATE_CODE_REGEXP_MESSAGE) String code) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ubsClientService.checkCertificate(code));
    }

    /**
     * Controller returns list of saved {@link UserVO} data.
     *
     * @param userUuid {@link UserVO} id.
     * @return list of {@link PersonalDataDto}.
     * @author Oleh Bilonizhka
     */
    @ApiOperation(value = "Get user's personal data.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = PersonalDataDto[].class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/personal-data")
    public ResponseEntity<PersonalDataDto> getUBSusers(
        @ApiIgnore @CurrentUserUuid String userUuid) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ubsClientService.getSecondPageData(userUuid));
    }

    /**
     * Controller saves all entered by user data to database.
     *
     * @param userUuid {@link UserVO} id.
     * @param dto      {@link OrderResponseDto} order data.
     * @return {@link HttpStatus}.
     * @author Oleh Bilonizhka
     */
    @ApiOperation(value = "Process user order.")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/processOrder")
    public ResponseEntity<String> processOrder(
        @ApiIgnore @CurrentUserUuid String userUuid,
        @Valid @RequestBody OrderResponseDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(ubsClientService.saveFullOrderToDB(dto, userUuid));
    }

    /**
     * Controller checks if received data is valid and stores payment info if is.
     *
     * @param dto {@link PaymentResponseDto} - response order data.
     * @return {@link HttpStatus} - http status.
     */
    @ApiOperation(value = "Receive payment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @PostMapping("/receivePayment")
    public ResponseEntity<HttpStatus> receivePayment(
        @RequestBody @Valid PaymentResponseDto dto) {
        ubsClientService.validatePayment(dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Controller for getting all addresses for current order.
     *
     * @param userUuid {@link UserVO} id.
     * @return {@link HttpStatus} - http status.
     */
    @ApiOperation(value = "Get all addresses for order")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = OrderWithAddressesResponseDto.class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/findAll-order-address")
    public ResponseEntity<OrderWithAddressesResponseDto> getAllAddressesForCurrentUser(
        @ApiIgnore @CurrentUserUuid String userUuid) {
        return ResponseEntity.status(HttpStatus.OK).body(ubsClientService.findAllAddressesForCurrentOrder(userUuid));
    }

    /**
     * Controller save address for current order.
     *
     * @param dtoRequest {@link OrderAddressDtoRequest}.
     * @param uuid       {@link UserVO} id.
     * @return {@link HttpStatus} - http status.
     */
    @ApiOperation(value = "Save order address")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = OrderWithAddressesResponseDto.class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("/save-order-address")
    public ResponseEntity<OrderWithAddressesResponseDto> saveAddressForOrder(
        @Valid @RequestBody OrderAddressDtoRequest dtoRequest,
        @ApiIgnore @CurrentUserUuid String uuid) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ubsClientService.saveCurrentAddressForOrder(dtoRequest, uuid));
    }

    /**
     * Controller delete order address.
     *
     * @param id   {@link Long}.
     * @param uuid {@link UserVO} id.
     * @return {@link HttpStatus} - http status.
     */
    @ApiOperation(value = "Delete order address")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.CREATED, response = OrderWithAddressesResponseDto.class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{id}/delete-order-address")
    public ResponseEntity<OrderWithAddressesResponseDto> deleteOrderAddress(
        @Valid @PathVariable("id") Long id,
        @ApiIgnore @CurrentUserUuid String uuid) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ubsClientService.deleteCurrentAddressForOrder(id, uuid));
    }

    /**
     * Controller gets info about user, ubs_user and user violations by order id.
     *
     * @param id {@link Long}.
     * @return {@link HttpStatus} - http status.
     */
    @ApiOperation(value = "Get user and ubs_user and violations info in order")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = UserInfoDto.class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("/user-info/{orderId}")
    public ResponseEntity<UserInfoDto> getOrderDetailsByOrderId(
        @Valid @PathVariable("orderId") Long id) {
        return ResponseEntity.ok()
            .body(ubsClientService.getUserAndUserUbsAndViolationsInfoByOrderId(id));
    }

    /**
     * Controller gets info about events history from,order bu order id.
     *
     * @param id {@link Long}.
     * @return {@link HttpStatus} - http status.
     * @author Yuriy Bahlay.
     */
    @ApiOperation(value = "Get events history from order by Id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = EventDto.class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("/order_history/{orderId}")
    public ResponseEntity<List<EventDto>> getOderHistoryByOrderId(
        @Valid @PathVariable("orderId") Long id) {
        return ResponseEntity.ok().body(ubsClientService.getAllEventsForOrder(id));
    }

    /**
     * Controller updates info about ubs_user in order .
     *
     * @param dto {@link UbsCustomersDtoUpdate}.
     * @return {@link HttpStatus} - http status.
     */
    @ApiOperation(value = "Update recipient information in order")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = UbsCustomersDto.class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @PutMapping("/update-recipients-data")
    public ResponseEntity<UbsCustomersDto> updateRecipientsInfo(
        @Valid @RequestBody UbsCustomersDtoUpdate dto, @ApiIgnore @CurrentUserUuid String uuid) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ubsClientService.updateUbsUserInfoInOrder(dto, uuid));
    }

    /**
     * Controller updates info about order cancellation reason .
     *
     * @param id  {@link Long}.
     * @param dto {@link OrderCancellationReasonDto}
     * @return {@link HttpStatus} - http status.
     */
    @ApiOperation(value = "updates info about order cancellation reason ")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = OrderCancellationReasonDto.class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @PostMapping("/order/{id}/cancellation/")
    public ResponseEntity<OrderCancellationReasonDto> updateCancellationReason(
        @RequestBody final OrderCancellationReasonDto dto,
        @PathVariable("id") final Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(ubsClientService.updateOrderCancellationReason(id, dto));
    }

    /**
     * Controller gets info about order cancellation reason.
     *
     * @param id {@link Long}.
     * @return {@link HttpStatus} - http status.
     */
    @ApiOperation(value = "gets info about order cancellation reason ")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = OrderCancellationReasonDto.class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/order/{id}/cancellation")
    public ResponseEntity<OrderCancellationReasonDto> getCancellationReason(
        @PathVariable("id") final Long id) {
        return ResponseEntity.ok().body(ubsClientService.getOrderCancellationReason(id));
    }

    /**
     * Controller gets list of locations.
     *
     * @param userUuid {@link UserVO} id.
     * @return {@link HttpStatus} - http status.
     */
    @ApiOperation(value = "gets list of locations")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = LocationResponseDto.class),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/order/get-locations")
    public ResponseEntity<List<LocationResponseDto>> getAllLocationsForPopUp(
        @ApiIgnore @CurrentUserUuid String userUuid) {
        return ResponseEntity.ok(ubsClientService.getAllLocations(userUuid));
    }

    /**
     * Controller sets new last order location.
     *
     * @param locationId {@link LocationIdDto}
     * @param userUuid   {@link UserVO} id.
     * @return {@link HttpStatus} - http status.
     */
    @ApiOperation(value = "sets new last order location")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @PostMapping("/order/get-locations")
    public ResponseEntity<HttpStatus> setNewLastOrderLocationForUser(
        @ApiIgnore @CurrentUserUuid String userUuid,
        @RequestBody LocationIdDto locationId) {
        ubsClientService.setNewLastOrderLocation(userUuid, locationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Controller saves all entered by user data to database from LiqPay.
     *
     * @param userUuid {@link UserVO} id.
     * @param dto      {@link OrderResponseDto} order data.
     * @return {@link HttpStatus}.
     * @author Vadym Makitra
     */
    @ApiOperation(value = "Process user order.")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/processLiqPayOrder")
    public ResponseEntity<String> processLiqPayOrder(
        @ApiIgnore @CurrentUserUuid String userUuid,
        @Valid @RequestBody OrderResponseDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(ubsClientService.saveFullOrderToDBFromLiqPay(dto, userUuid));
    }

    /**
     * Controller checks if received data is valid and stores payment info if is.
     *
     * @param dto {@link PaymentResponseDtoLiqPay} - response order data.
     * @return {@link HttpStatus} - http status.
     */
    @ApiOperation(value = "Receive payment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @PostMapping(value = "/receiveLiqPayPayment")
    public ResponseEntity<HttpStatus> receiveLiqPayPayment(
        PaymentResponseDtoLiqPay dto, HttpServletResponse response) throws IOException {
        ubsClientService.validateLiqPayPayment(dto);
        if (HttpStatus.OK.is2xxSuccessful()) {
            response.sendRedirect("https://ita-social-projects.github.io/GreenCityClient/#/ubs/confirm");
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Controller for getting status about payment from Liq Pay.
     * 
     * @param orderId - current order
     * @return {@link Map}
     */
    @ApiOperation(value = "Get status of Payment from Liq Pay.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping(value = "/getLiqPayStatus/{orderId}")
    public ResponseEntity<Map<String, Object>> getLiqPayStatusPayment(
        @Valid @PathVariable Long orderId) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(ubsClientService.getLiqPayStatus(orderId));
    }
}
