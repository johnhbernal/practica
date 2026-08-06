package co.com.practica.fact.filter;

import co.com.practica.fact.constantes.Constantes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimitFilter")
class RateLimitFilterTest {

    @InjectMocks
    private RateLimitFilter rateLimitFilter;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.setRemoteAddr("192.168.1.10");
    }

    @Test
    void getRequest_passesThrough() throws Exception {
        request.setMethod("GET");
        rateLimitFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void optionsRequest_passesThroughWithoutLimit() throws Exception {
        request.setMethod("OPTIONS");
        rateLimitFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void postWithinLimit_passesThrough() throws Exception {
        request.setMethod("POST");
        rateLimitFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void exceedingWriteLimit_returns429() throws Exception {
        request.setMethod("POST");
        FilterChain chain = mock(FilterChain.class);
        for (int i = 0; i < 31; i++) {
            MockHttpServletResponse resp = new MockHttpServletResponse();
            rateLimitFilter.doFilterInternal(request, resp, chain);
            if (i == 30) {
                assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), resp.getStatus());
                assertTrue(resp.getContentAsString().contains(Constantes.MSG_FAIL));
            }
        }
        verify(chain, times(30)).doFilter(eq(request), any());
    }

    @Test
    void xForwardedFor_usesClientIp() throws Exception {
        request.setMethod("GET");
        request.addHeader("X-Forwarded-For", "10.0.0.5, 10.0.0.1");
        rateLimitFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void invalidForwardedHeader_fallsBackToRemoteAddr() throws Exception {
        request.setMethod("GET");
        request.addHeader("X-Forwarded-For", "not-an-ip");
        rateLimitFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }
}
