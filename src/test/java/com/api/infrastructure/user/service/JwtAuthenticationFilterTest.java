package com.api.infrastructure.user.service;

import com.api.domain.user.entity.User;
import com.api.infrastructure.user.dao.UserDao;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private static final String USER_ID = "user-id-123";
    private static final String TOKEN = "jwt-token";
    private static final String AUTHORIZATION_HEADER = "Bearer " + TOKEN;

    private final JwtService jwtService = mock(JwtService.class);
    private final UserDao userDao = mock(UserDao.class);
    private final FilterChain filterChain = mock(FilterChain.class);

    private final JwtAuthenticationFilter jwtAuthenticationFilter =
            new JwtAuthenticationFilter(jwtService, userDao);

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve seguir o filtro quando header Authorization não existir")
    void shouldContinueFilterChainWhenAuthorizationHeaderDoesNotExist() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDao);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Deve seguir o filtro quando header Authorization não começar com Bearer")
    void shouldContinueFilterChainWhenAuthorizationHeaderDoesNotStartWithBearer() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", TOKEN);

        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDao);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Deve autenticar usuário quando token for válido")
    void shouldAuthenticateUserWhenTokenIsValid() throws ServletException, IOException {
        User user = User.builder()
                .id(USER_ID)
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", AUTHORIZATION_HEADER);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractSubject(TOKEN))
                .thenReturn(USER_ID);

        when(userDao.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(jwtService.isValid(TOKEN))
                .thenReturn(true);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNotNull();
        assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(authentication.getPrincipal()).isEqualTo(user);
        assertThat(authentication.getCredentials()).isNull();
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");

        verify(jwtService).extractSubject(TOKEN);
        verify(userDao).findById(USER_ID);
        verify(jwtService).isValid(TOKEN);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar usuário quando token for inválido")
    void shouldNotAuthenticateUserWhenTokenIsInvalid() throws ServletException, IOException {
        User user = User.builder()
                .id(USER_ID)
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", AUTHORIZATION_HEADER);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractSubject(TOKEN))
                .thenReturn(USER_ID);

        when(userDao.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(jwtService.isValid(TOKEN))
                .thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtService).extractSubject(TOKEN);
        verify(userDao).findById(USER_ID);
        verify(jwtService).isValid(TOKEN);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve buscar usuário quando subject do token for nulo")
    void shouldNotFindUserWhenTokenSubjectIsNull() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", AUTHORIZATION_HEADER);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractSubject(TOKEN))
                .thenReturn(null);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtService).extractSubject(TOKEN);
        verify(userDao, never()).findById(USER_ID);
        verify(jwtService, never()).isValid(TOKEN);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar novamente quando já existir autenticação no contexto")
    void shouldNotAuthenticateAgainWhenAuthenticationAlreadyExists() throws ServletException, IOException {
        Authentication existingAuthentication =
                new UsernamePasswordAuthenticationToken("principal", null);

        SecurityContextHolder.getContext().setAuthentication(existingAuthentication);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", AUTHORIZATION_HEADER);

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractSubject(TOKEN))
                .thenReturn(USER_ID);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isEqualTo(existingAuthentication);

        verify(jwtService).extractSubject(TOKEN);
        verify(userDao, never()).findById(USER_ID);
        verify(jwtService, never()).isValid(TOKEN);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não for encontrado")
    void shouldThrowExceptionWhenUserIsNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", AUTHORIZATION_HEADER);

        when(jwtService.extractSubject(TOKEN))
                .thenReturn(USER_ID);

        when(userDao.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtService, never()).isValid(TOKEN);
    }
}
