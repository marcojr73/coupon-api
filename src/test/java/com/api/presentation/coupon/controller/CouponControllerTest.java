package com.api.presentation.coupon.controller;

import com.api.application.coupon.dto.CouponUseCaseOutput;
import com.api.application.coupon.useCase.*;
import com.api.domain.coupon.entity.Coupon;
import com.api.infrastructure.user.dao.UserDao;
import com.api.infrastructure.user.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    private static final String COUPON_ID = "coupon-id-123";
    private static final String CODE = "JEDI48";
    private static final String DESCRIPTION = "Cupom de desconto para sabre de luz";
    private static final BigDecimal DISCOUNT_VALUE = new BigDecimal("10.50");
    private static final LocalDate EXPIRATION_DATE = LocalDate.now().plusDays(10);
    private static final Boolean PUBLISHED = true;

    private static final String UPDATED_CODE = "XYZ789";
    private static final String UPDATED_DESCRIPTION = "Cupom de desconto para droide";
    private static final BigDecimal UPDATED_DISCOUNT_VALUE = new BigDecimal("20.00");
    private static final LocalDate UPDATED_EXPIRATION_DATE = LocalDate.now().plusDays(20);
    private static final Boolean UPDATED_PUBLISHED = false;

    private static final String INVALID_CODE = "";
    private static final String INVALID_DESCRIPTION = "";
    private static final BigDecimal INVALID_DISCOUNT_VALUE = new BigDecimal("0.40");
    private static final LocalDate INVALID_EXPIRATION_DATE = LocalDate.now().minusDays(1);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCouponUseCase createCouponUseCase;

    @MockitoBean
    private ListCouponUseCase listCouponUseCase;

    @MockitoBean
    private ShowCouponUseCase showCouponUseCase;

    @MockitoBean
    private UpdateCouponUseCase updateCouponUseCase;

    @MockitoBean
    private DeleteCouponUseCase deleteCouponUseCase;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDao userDao;

    @Test
    @DisplayName("Deve cadastrar cupom e retornar status 201")
    void shouldCreateCouponAndReturnCreated() throws Exception {
        CouponUseCaseOutput output = couponOutput();

        when(createCouponUseCase.execute(any(Coupon.class)))
                .thenReturn(output);

        String requestBody = couponRequest(
                CODE,
                DESCRIPTION,
                DISCOUNT_VALUE,
                EXPIRATION_DATE,
                PUBLISHED
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(COUPON_ID))
                .andExpect(jsonPath("$.message").value("Created"));

        ArgumentCaptor<Coupon> couponCaptor = ArgumentCaptor.forClass(Coupon.class);

        verify(createCouponUseCase).execute(couponCaptor.capture());

        Coupon coupon = couponCaptor.getValue();

        assertThat(coupon.getCode()).isEqualTo(CODE);
        assertThat(coupon.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(coupon.getDiscountValue()).isEqualByComparingTo(DISCOUNT_VALUE);
        assertThat(coupon.getExpirationDate()).isEqualTo(EXPIRATION_DATE);
        assertThat(coupon.getPublished()).isEqualTo(PUBLISHED);
    }

    @Test
    @DisplayName("Deve listar cupons e retornar status 200")
    void shouldListCouponsAndReturnOk() throws Exception {
        CouponUseCaseOutput output = couponOutput();

        when(listCouponUseCase.execute())
                .thenReturn(List.of(output));

        mockMvc.perform(get("/coupon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(COUPON_ID))
                .andExpect(jsonPath("$[0].code").value(CODE))
                .andExpect(jsonPath("$[0].description").value(DESCRIPTION))
                .andExpect(jsonPath("$[0].discountValue").value(DISCOUNT_VALUE.doubleValue()))
                .andExpect(jsonPath("$[0].expirationDate").value(EXPIRATION_DATE.toString()))
                .andExpect(jsonPath("$[0].published").value(PUBLISHED));

        verify(listCouponUseCase).execute();
    }

    @Test
    @DisplayName("Deve exibir cupom e retornar status 200")
    void shouldShowCouponAndReturnOk() throws Exception {
        CouponUseCaseOutput output = couponOutput();

        when(showCouponUseCase.execute(COUPON_ID))
                .thenReturn(output);

        mockMvc.perform(get("/coupon/{id}", COUPON_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COUPON_ID))
                .andExpect(jsonPath("$.code").value(CODE))
                .andExpect(jsonPath("$.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.discountValue").value(DISCOUNT_VALUE.doubleValue()))
                .andExpect(jsonPath("$.expirationDate").value(EXPIRATION_DATE.toString()))
                .andExpect(jsonPath("$.published").value(PUBLISHED));

        verify(showCouponUseCase).execute(COUPON_ID);
    }

    @Test
    @DisplayName("Deve atualizar cupom e retornar status 200")
    void shouldUpdateCouponAndReturnOk() throws Exception {
        CouponUseCaseOutput output = CouponUseCaseOutput.builder()
                .id(COUPON_ID)
                .code(UPDATED_CODE)
                .description(UPDATED_DESCRIPTION)
                .discountValue(UPDATED_DISCOUNT_VALUE)
                .expirationDate(UPDATED_EXPIRATION_DATE)
                .published(UPDATED_PUBLISHED)
                .build();

        when(updateCouponUseCase.execute(eq(COUPON_ID), any(Coupon.class)))
                .thenReturn(output);

        String requestBody = couponRequest(
                UPDATED_CODE,
                UPDATED_DESCRIPTION,
                UPDATED_DISCOUNT_VALUE,
                UPDATED_EXPIRATION_DATE,
                UPDATED_PUBLISHED
        );

        mockMvc.perform(put("/coupon/{id}", COUPON_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COUPON_ID))
                .andExpect(jsonPath("$.message").value("Ok"));

        ArgumentCaptor<Coupon> couponCaptor = ArgumentCaptor.forClass(Coupon.class);

        verify(updateCouponUseCase).execute(eq(COUPON_ID), couponCaptor.capture());

        Coupon coupon = couponCaptor.getValue();

        assertThat(coupon.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(coupon.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(coupon.getDiscountValue()).isEqualByComparingTo(UPDATED_DISCOUNT_VALUE);
        assertThat(coupon.getExpirationDate()).isEqualTo(UPDATED_EXPIRATION_DATE);
        assertThat(coupon.getPublished()).isEqualTo(UPDATED_PUBLISHED);
    }

    @Test
    @DisplayName("Deve remover cupom e retornar status 201")
    void shouldDeleteCouponAndReturnCreated() throws Exception {
        mockMvc.perform(delete("/coupon/{id}", COUPON_ID))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(COUPON_ID))
                .andExpect(jsonPath("$.message").value("Ok"));

        verify(deleteCouponUseCase).execute(COUPON_ID);
    }

    @Test
    @DisplayName("Não deve cadastrar cupom quando código for inválido")
    void shouldNotCreateCouponWhenCodeIsInvalid() throws Exception {
        String requestBody = couponRequest(
                INVALID_CODE,
                DESCRIPTION,
                DISCOUNT_VALUE,
                EXPIRATION_DATE,
                PUBLISHED
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createCouponUseCase);
    }

    @Test
    @DisplayName("Não deve cadastrar cupom quando descrição for inválida")
    void shouldNotCreateCouponWhenDescriptionIsInvalid() throws Exception {
        String requestBody = couponRequest(
                CODE,
                INVALID_DESCRIPTION,
                DISCOUNT_VALUE,
                EXPIRATION_DATE,
                PUBLISHED
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createCouponUseCase);
    }

    @Test
    @DisplayName("Não deve cadastrar cupom quando valor de desconto for inválido")
    void shouldNotCreateCouponWhenDiscountValueIsInvalid() throws Exception {
        String requestBody = couponRequest(
                CODE,
                DESCRIPTION,
                INVALID_DISCOUNT_VALUE,
                EXPIRATION_DATE,
                PUBLISHED
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createCouponUseCase);
    }

    @Test
    @DisplayName("Não deve cadastrar cupom quando data de expiração for inválida")
    void shouldNotCreateCouponWhenExpirationDateIsInvalid() throws Exception {
        String requestBody = couponRequest(
                CODE,
                DESCRIPTION,
                DISCOUNT_VALUE,
                INVALID_EXPIRATION_DATE,
                PUBLISHED
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createCouponUseCase);
    }

    @Test
    @DisplayName("Não deve atualizar cupom quando valor de desconto for inválido")
    void shouldNotUpdateCouponWhenDiscountValueIsInvalid() throws Exception {
        String requestBody = couponRequest(
                UPDATED_CODE,
                UPDATED_DESCRIPTION,
                INVALID_DISCOUNT_VALUE,
                UPDATED_EXPIRATION_DATE,
                UPDATED_PUBLISHED
        );

        mockMvc.perform(put("/coupon/{id}", COUPON_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(updateCouponUseCase);
    }

    @Test
    @DisplayName("Não deve atualizar cupom quando data de expiração for inválida")
    void shouldNotUpdateCouponWhenExpirationDateIsInvalid() throws Exception {
        String requestBody = couponRequest(
                UPDATED_CODE,
                UPDATED_DESCRIPTION,
                UPDATED_DISCOUNT_VALUE,
                INVALID_EXPIRATION_DATE,
                UPDATED_PUBLISHED
        );

        mockMvc.perform(put("/coupon/{id}", COUPON_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(updateCouponUseCase);
    }

    private CouponUseCaseOutput couponOutput() {
        return CouponUseCaseOutput.builder()
                .id(COUPON_ID)
                .code(CODE)
                .description(DESCRIPTION)
                .discountValue(DISCOUNT_VALUE)
                .expirationDate(EXPIRATION_DATE)
                .published(PUBLISHED)
                .build();
    }

    private String couponRequest(
            String code,
            String description,
            BigDecimal discountValue,
            LocalDate expirationDate,
            Boolean published
    ) {
        return """
        {
            "code": "%s",
            "description": "%s",
            "discountValue": %s,
            "expirationDate": "%s",
            "published": %s
        }
        """.formatted(
                code,
                description,
                discountValue,
                expirationDate,
                published
        );
    }
}