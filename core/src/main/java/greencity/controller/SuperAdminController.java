package greencity.controller;

import greencity.annotations.CurrentUserUuid;
import greencity.constants.HttpStatuses;
import greencity.dto.AddNewTariffDto;
import greencity.dto.DetailsOfDeactivateTariffsDto;
import greencity.dto.courier.*;
import greencity.dto.location.LocationCreateDto;
import greencity.dto.location.LocationInfoDto;
import greencity.dto.service.GetServiceDto;
import greencity.dto.service.GetTariffServiceDto;
import greencity.dto.service.ServiceDto;
import greencity.dto.service.TariffServiceDto;
import greencity.dto.tariff.*;
import greencity.entity.order.Courier;
import greencity.enums.LocationStatus;
import greencity.exceptions.BadRequestException;
import greencity.filters.TariffsInfoFilterCriteria;
import greencity.service.SuperAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/ubs/superAdmin")
@RequiredArgsConstructor
class SuperAdminController {
    private final SuperAdminService superAdminService;

    /**
     * Controller for create new tariff service.
     *
     * @param tariffId {@link Long} tariff id.
     * @param dto      {@link TariffServiceDto} dto for tariff service.
     * @param uuid     {@link String} employee uuid.
     * @return {@link GetTariffServiceDto}
     *
     * @author Vadym Makitra.
     * @author Julia Seti
     */
    @Operation(summary = "Create new tariff service")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
            content = @Content(schema = @Schema(implementation = GetTariffServiceDto.class))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('CONTROL_SERVICE', authentication)")
    @PostMapping("/{tariffId}/createTariffService")
    public ResponseEntity<GetTariffServiceDto> createTariffService(
        @Valid @PathVariable long tariffId,
        @RequestBody @Valid TariffServiceDto dto,
        @Parameter(hidden = true) @CurrentUserUuid String uuid) {
        return ResponseEntity.status(HttpStatus.CREATED).body(superAdminService.addTariffService(tariffId, dto, uuid));
    }

    /**
     * Controller for get info about tariff services.
     *
     * @param tariffId {@link Long} tariff id.
     * @return {@link List} of {@link GetTariffServiceDto} list of all tariff
     *         services for tariff with id = tariffId.
     * @author Vadym Makitra
     * @author Julia Seti
     */

    @Operation(summary = "Get info about tariff services")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = GetTariffServiceDto.class)))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('SEE_PRICING_CARD', authentication)")
    @GetMapping("/{tariffId}/getTariffService")
    public ResponseEntity<List<GetTariffServiceDto>> getTariffService(
        @Valid @PathVariable long tariffId) {
        return ResponseEntity.status(HttpStatus.OK).body(superAdminService.getTariffService(tariffId));
    }

    /**
     * Controller for delete tariff service by Id.
     *
     * @param id {@link Integer} tariff service id.
     * @return {@link HttpStatuses}
     * @author Vadym Makitra.
     */

    @Operation(summary = "Delete tariff service by Id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('EDIT_DELETE_DEACTIVATE_PRICING_CARD', authentication)")
    @DeleteMapping("/deleteTariffService/{id}")
    public ResponseEntity<HttpStatus> deleteTariffService(
        @Valid @PathVariable Integer id) {
        superAdminService.deleteTariffService(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Controller for edit tariff service by id.
     *
     * @param dto  {@link TariffServiceDto} dto for tariff service.
     * @param id   {@link Integer} tariff service id.
     * @param uuid {@link String} employee uuid.
     * @return {@link GetTariffServiceDto}
     * @author Vadym Makitra.
     * @author Julia Seti
     */

    @Operation(summary = "Edit tariff service by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = GetTariffServiceDto.class))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('EDIT_DELETE_DEACTIVATE_PRICING_CARD', authentication)")
    @PutMapping("/editTariffService/{id}")
    public ResponseEntity<GetTariffServiceDto> editTariffService(
        @Valid @RequestBody TariffServiceDto dto,
        @Valid @PathVariable Integer id,
        @Parameter(hidden = true) @CurrentUserUuid String uuid) {
        return ResponseEntity.status(HttpStatus.OK).body(superAdminService.editTariffService(dto, id, uuid));
    }

    /**
     * Controller for creating new service for tariff.
     *
     * @param tariffId {@link Long} - tariff id.
     * @param dto      {@link ServiceDto} - new service dto.
     * @param uuid     {@link String} - employee uuid.
     * @return {@link GetServiceDto} - created service dto.
     *
     * @author Vadym Makitra
     * @author Julia Seti.
     */

    @Operation(summary = "Add service for tariff")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
            content = @Content(schema = @Schema(implementation = GetServiceDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('CONTROL_SERVICE', authentication)")
    @PostMapping("/{tariffId}/createService")
    public ResponseEntity<GetServiceDto> createServices(
        @Valid @PathVariable Long tariffId,
        @Valid @RequestBody ServiceDto dto,
        @Parameter(hidden = true) @CurrentUserUuid String uuid) {
        return ResponseEntity.status(HttpStatus.CREATED).body(superAdminService.addService(tariffId, dto, uuid));
    }

    /**
     * Controller for getting info about service by tariff id.
     *
     * @param tariffId {@link Long} - tariff id.
     * @return {@link GetServiceDto} - service dto.
     *
     * @author Vadym Makitra
     * @author Julia Seti
     */

    @Operation(summary = "Get info about service by tariff id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = GetServiceDto.class))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('SEE_TARIFFS', authentication)")
    @GetMapping("/{tariffId}/getService")
    public ResponseEntity<GetServiceDto> getService(
        @Valid @PathVariable Long tariffId) {
        return ResponseEntity.status(HttpStatus.OK).body(superAdminService.getService(tariffId));
    }

    /**
     * Controller for delete service by Id.
     *
     * @param id {@link Long} - service id.
     * @author Vadym Makitra
     */

    @Operation(summary = "Delete service by Id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('CONTROL_SERVICE', authentication)")
    @DeleteMapping("/deleteService/{id}")
    public ResponseEntity<HttpStatus> deleteService(
        @Valid @PathVariable Long id) {
        superAdminService.deleteService(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Controller for edit service by id.
     *
     * @param id   {@link Long} - service id.
     * @param dto  {@link ServiceDto} - service dto.
     * @param uuid {@link String} - employee uuid.
     * @return {@link GetServiceDto} - edited service dto.
     *
     * @author Vadym Makitra
     * @author Julia Seti
     */

    @Operation(summary = "Edit service by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = GetServiceDto.class))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('CONTROL_SERVICE', authentication)")
    @PutMapping("/editService/{id}")
    public ResponseEntity<GetServiceDto> editService(
        @Valid @PathVariable Long id,
        @Valid @RequestBody ServiceDto dto,
        @Parameter(hidden = true) @CurrentUserUuid String uuid) {
        return ResponseEntity.status(HttpStatus.OK).body(superAdminService.editService(id, dto, uuid));
    }

    /**
     * Get all info about locations, and min amount of bag for locations.
     *
     * @return {@link LocationInfoDto}
     * @author Vadym Makitra
     */
    @Operation(summary = "Get info about location and min amount of bag for this location")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LocationInfoDto.class)))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('SEE_TARIFFS', authentication)")
    @GetMapping("/getLocations")
    public ResponseEntity<List<LocationInfoDto>> getLocations() {
        return ResponseEntity.status(HttpStatus.OK).body(superAdminService.getAllLocation());
    }

    /**
     * Get all info about active locations.
     *
     * @return {@link LocationInfoDto}
     * @author Safarov Renat
     */
    @Operation(summary = "Get all active locations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LocationInfoDto.class)))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('SEE_TARIFFS', authentication)")
    @GetMapping("/getActiveLocations")
    public ResponseEntity<List<LocationInfoDto>> getActiveLocations() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(superAdminService.getLocationsByStatus(LocationStatus.ACTIVE));
    }

    /**
     * Get all deactivated locations.
     *
     * @return {@link LocationInfoDto}
     * @author Maksym Lenets
     */
    @Operation(summary = "Get all deactivated locations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LocationInfoDto.class)))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('SEE_TARIFFS', authentication)")
    @GetMapping("/getDeactivatedLocations")
    public ResponseEntity<List<LocationInfoDto>> getDeactivatedLocations() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(superAdminService.getLocationsByStatus(LocationStatus.DEACTIVATED));
    }

    /**
     * Create new Location.
     *
     * @param dto {@link LocationCreateDto}
     * @return {@link LocationInfoDto}
     * @author Vadym Makitra
     */
    @Operation(summary = "Create new location")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK, content = @Content),
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('CREATE_NEW_LOCATION', authentication)")
    @PostMapping("/addLocations")
    public ResponseEntity<HttpStatus> addLocation(
        @Valid @RequestBody List<LocationCreateDto> dto) {
        superAdminService.addLocation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Controller for deleting location.
     *
     * @param id {@link Long} - location id.
     * @author Anton Bondar
     */
    @Operation(summary = "Delete location")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK, content = @Content),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('DELETE_LOCATION', authentication)")
    @DeleteMapping("/deleteLocation/{id}")
    public ResponseEntity<HttpStatus> deleteLocation(@PathVariable Long id) {
        superAdminService.deleteLocation(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Controller for activating location by ID.
     *
     * @param id - id location
     * @return {@link LocationInfoDto}
     * @author Vadym Makitra
     */
    @Operation(summary = "Active location Id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('DELETE_LOCATION', authentication)")
    @PatchMapping("/activeLocations/{id}")
    public ResponseEntity<HttpStatus> activeLocation(
        @PathVariable Long id) {
        superAdminService.activateLocation(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Controller for creating new courier.
     *
     * @param dto {@link CreateCourierDto}
     * @return {@link Courier}
     * @author Vadym Makitra
     */
    @Operation(summary = "Create new Courier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = CreateCourierDto.class))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('CREATE_NEW_COURIER', authentication)")
    @PostMapping("/createCourier")
    public ResponseEntity<CreateCourierDto> addService(
        @Valid @RequestBody CreateCourierDto dto,
        @Parameter(hidden = true) @CurrentUserUuid String uuid) {
        return ResponseEntity.status(HttpStatus.CREATED).body(superAdminService.createCourier(dto, uuid));
    }

    /**
     * Controller updates courier.
     *
     * @return {@link CourierDto}
     */
    @Operation(summary = "Update courier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = CourierDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content),
        @ApiResponse(responseCode = "422", description = HttpStatuses.UNPROCESSABLE_ENTITY, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('EDIT_COURIER', authentication)")
    @PutMapping("/update-courier")
    public ResponseEntity<CourierDto> updateCourier(@RequestBody @Valid CourierUpdateDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(superAdminService.updateCourier(dto));
    }

    /**
     * Controller for get all info about couriers.
     *
     * @return {@link CourierDto}
     * @author Max Bohonko
     */
    @Operation(summary = "Get all info about couriers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CourierDto.class)))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('SEE_TARIFFS', authentication)")
    @GetMapping("/getCouriers")
    public ResponseEntity<List<CourierDto>> getAllCouriers() {
        return ResponseEntity.status(HttpStatus.OK).body(superAdminService.getAllCouriers());
    }

    /**
     * Controller for deactivate courier's.
     *
     * @param id - courier id that will need to be deleted;
     */
    @Operation(summary = "Deactivate courier's by Id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content)
    })
    @PatchMapping("/deactivateCourier/{id}")
    public ResponseEntity<CourierDto> deactivateCourier(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(superAdminService.deactivateCourier(id));
    }

    /**
     * Controller creates employee receiving station.
     *
     * @return {@link ReceivingStationDto}
     */
    @Operation(summary = "Create employee receiving station")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
            content = @Content(schema = @Schema(implementation = ReceivingStationDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "422", description = HttpStatuses.UNPROCESSABLE_ENTITY, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('CREATE_NEW_STATION', authentication)")
    @PostMapping("/create-receiving-station")
    public ResponseEntity<ReceivingStationDto> createReceivingStation(@Valid AddingReceivingStationDto dto,
        @Parameter(hidden = true) @CurrentUserUuid String uuid) {
        return ResponseEntity.status(HttpStatus.CREATED).body(superAdminService.createReceivingStation(dto, uuid));
    }

    /**
     * Controller updates receiving station.
     *
     * @return {@link ReceivingStationDto}
     */
    @Operation(summary = "Update employee receiving station")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = ReceivingStationDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content),
        @ApiResponse(responseCode = "422", description = HttpStatuses.UNPROCESSABLE_ENTITY, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('EDIT_STATION', authentication)")
    @PutMapping("/update-receiving-station")
    public ResponseEntity<ReceivingStationDto> updateReceivingStation(@RequestBody @Valid ReceivingStationDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(superAdminService.updateReceivingStation(dto));
    }

    /**
     * Controller gets all employee receiving stations.
     *
     * @return {@link ReceivingStationDto}
     */
    @Operation(summary = "Get all employee receiving stations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReceivingStationDto.class)))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('SEE_TARIFFS', authentication)")
    @GetMapping("/get-all-receiving-station")
    public ResponseEntity<List<ReceivingStationDto>> getAllReceivingStation() {
        return ResponseEntity.status(HttpStatus.OK).body(superAdminService.getAllReceivingStations());
    }

    /**
     * Controller deletes employee receiving station.
     *
     */
    @Operation(summary = "Deletes employee receiving station")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('EDIT_STATION', authentication)")
    @DeleteMapping("/delete-receiving-station/{id}")
    public ResponseEntity<HttpStatus> deleteReceivingStation(@PathVariable Long id) {
        superAdminService.deleteReceivingStation(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Controller for get all tariff's info.
     *
     * @return {@link GetTariffsInfoDto}
     * @author Bohdan Melnyk
     */
    @Operation(summary = "Get all info about tariffs.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('SEE_PRICING_CARD', authentication)")
    @GetMapping("/tariffs")
    public ResponseEntity<List<GetTariffsInfoDto>> getAllTariffsInfo(TariffsInfoFilterCriteria filterCriteria) {
        return ResponseEntity.status(HttpStatus.OK).body(superAdminService.getAllTariffsInfo(filterCriteria));
    }

    /**
     * Controller for add new tariff info.
     *
     * @return {@link AddNewTariffResponseDto}
     * @author Yurii Fedorko
     */
    @Operation(summary = "Add new tariff")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content),
        @ApiResponse(responseCode = "409", description = HttpStatuses.CONFLICT, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('CONTROL_SERVICE', authentication)")
    @PostMapping("/add-new-tariff")
    public ResponseEntity<AddNewTariffResponseDto> addNewTariff(@RequestBody @Valid AddNewTariffDto addNewTariffDto,
        @Parameter(hidden = true) @CurrentUserUuid String uuid) {
        return ResponseEntity.status(HttpStatus.CREATED).body(superAdminService.addNewTariff(addNewTariffDto, uuid));
    }

    /**
     * Controller for checking if tariff info is already in the database.
     *
     * @author Inna Yashna
     */
    @Operation(summary = "Check if tariff exists")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('CONTROL_SERVICE', authentication)")
    @PostMapping("/check-if-tariff-exists")
    public ResponseEntity<Boolean> checkIfTariffExists(
        @RequestBody @Valid AddNewTariffDto addNewTariffDto) {
        return ResponseEntity.status(HttpStatus.OK).body(superAdminService.checkIfTariffExists(addNewTariffDto));
    }

    /**
     * Controller for editing tariff info.
     *
     * @param id  {@link Long} tariff id.
     * @param dto {@link EditTariffDto} edited tariff dto.
     *
     * @author Julia Seti
     */
    @Operation(summary = "Edit tariff info")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK, content = @Content),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content),
        @ApiResponse(responseCode = "409", description = HttpStatuses.CONFLICT, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('EDIT_DELETE_DEACTIVATE_PRICING_CARD', authentication)")
    @PutMapping("/editTariffInfo/{id}")
    public ResponseEntity<HttpStatus> editTariff(
        @Valid @PathVariable Long id, @Valid @RequestBody EditTariffDto dto) {
        superAdminService.editTariff(id, dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Controller for updating Tariff Limits by sum price of Order or by amount of
     * Bags.
     *
     * @param tariffId {@link Long} TariffsInfo id
     * @param dto      {@link SetTariffLimitsDto} dto
     *
     * @author Julia Seti
     */
    @Operation(summary = "Set tariff limits")
    @PreAuthorize("@preAuthorizer.hasAuthority('EDIT_DELETE_DEACTIVATE_PRICING_CARD', authentication)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK, content = @Content),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PutMapping("/setTariffLimits/{tariffId}")
    public ResponseEntity<HttpStatus> setLimitsForTariff(
        @Valid @PathVariable Long tariffId,
        @Valid @RequestBody SetTariffLimitsDto dto) {
        superAdminService.setTariffLimits(tariffId, dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Controller for get info about tariff limits.
     *
     * @param tariffId {@link Long} - tariff id
     * @return {@link GetTariffLimitsDto} - dto
     *
     * @author Julia Seti
     */
    @Operation(summary = "Get info about tariff limits")
    @PreAuthorize("@preAuthorizer.hasAuthority('SEE_TARIFFS', authentication)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = GetTariffLimitsDto.class))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @GetMapping("/getTariffLimits/{tariffId}")
    public ResponseEntity<GetTariffLimitsDto> getTariffLimits(
        @Valid @PathVariable long tariffId) {
        return ResponseEntity.status(HttpStatus.OK).body(superAdminService.getTariffLimits(tariffId));
    }

    /**
     * Controller for switching tariff activation status.
     *
     * @param tariffId {@link Long} tariff id
     * @param status   {@link String} tariff activation status
     *
     * @author Julia Seti
     */
    @Operation(summary = "Switch tariff activation status by tariff id")
    @PreAuthorize("@preAuthorizer.hasAuthority('EDIT_DELETE_DEACTIVATE_PRICING_CARD', authentication)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK, content = @Content),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PatchMapping("/switchTariffStatus/{tariffId}")
    public ResponseEntity<HttpStatus> switchTariffStatus(
        @PathVariable @Parameter(name = "tariffId", required = true, description = "tariff id") Long tariffId,
        @Valid @RequestParam @Parameter(name = "status", required = true, description = "status",
            schema = @Schema(type = "string", allowableValues = {"Active", "Deactivated"})) String status) {
        superAdminService.switchTariffStatus(tariffId, status);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Controller for activation or deactivation Locations in Tariff depends
     * on @RequestParam.
     *
     * @author Yurii Fedorko
     */
    @Operation(summary = "Change Tariff Location status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK, content = @Content),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('EDIT_LOCATION', authentication)")
    @PutMapping("tariffs/{id}/locations/change-status")
    public ResponseEntity<HttpStatus> changeLocationsInTariffStatus(@PathVariable Long id,
        @Valid @RequestBody ChangeTariffLocationStatusDto dto, @RequestParam String status) {
        superAdminService.changeTariffLocationsStatus(id, dto, status);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Controller to switching activation statuses by chosen parameters.
     *
     * @param regionsIds  - list of regions ids.
     * @param citiesIds   - list of cities ids.
     * @param stationsIds - list of receiving stations ids.
     * @param courierId   - courier id.
     * @author Nikita Korzh, Julia Seti.
     */
    @Operation(summary = "Switch activation status by chosen parameters. "
        + "If the deactivation status is selected, the tariff will be deactivated")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK, content = @Content),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @PreAuthorize("@preAuthorizer.hasAuthority('EDIT_DELETE_DEACTIVATE_PRICING_CARD', authentication)")
    @PostMapping("/deactivate")
    public ResponseEntity<HttpStatus> switchActivationStatusByChosenParams(
        @RequestParam(name = "regionsIds", required = false) Optional<List<Long>> regionsIds,
        @RequestParam(name = "citiesIds", required = false) Optional<List<Long>> citiesIds,
        @RequestParam(name = "stationsIds", required = false) Optional<List<Long>> stationsIds,
        @RequestParam(name = "courierId", required = false) Optional<Long> courierId,
        @Valid @RequestParam @Parameter(name = "status", required = true, description = "status",
            schema = @Schema(type = "string", allowableValues = {"Active", "Deactivated"})) String status) {
        if (regionsIds.isPresent() || citiesIds.isPresent() || stationsIds.isPresent() || courierId.isPresent()) {
            superAdminService.switchActivationStatusByChosenParams(DetailsOfDeactivateTariffsDto.builder()
                .regionsIds(regionsIds)
                .citiesIds(citiesIds)
                .stationsIds(stationsIds)
                .courierId(courierId)
                .activationStatus(status)
                .build());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            throw new BadRequestException("You should enter at least one parameter");
        }
    }
}
