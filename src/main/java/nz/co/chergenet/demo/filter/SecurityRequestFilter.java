package nz.co.chergenet.demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nz.co.chergenet.demo.util.JwtUtil;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * This is a very simple Filter to check the JWT token from here to check whether
 * it has a valid Role as Admin for product editable api calls.
 */
@Slf4j
@Component
public class SecurityRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public SecurityRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException, ServletException {
        if (request.getRequestURI().startsWith("/shop/v1/admin")) {
            try {
                final String authorizationHeader = request.getHeader("Authorization");
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    String jwt = authorizationHeader.substring(7);
                    if (!jwtUtil.validateToken(jwt)) {
                        log.error("Invalid JWT token.");
                        sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                        return; // Skip the rest of the filter chain
                    }
                    if (!jwtUtil.isUserAuthorized(jwt)) {
                        log.error("User is not authorized.");
                        sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "User is not authorized");
                        return; // Skip the rest of the filter chain
                    }
                }
            } catch (Exception ex) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Failed to validate security");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String errorMessage) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(String.format("{\"error\":\"%s\",\"message\":\"%s\"}", getErrorDescription(status), errorMessage));
        writer.flush();
    }

    private String getErrorDescription(int status) {
        return switch (status) {
            case HttpServletResponse.SC_UNAUTHORIZED -> "Unauthorized";
            case HttpServletResponse.SC_FORBIDDEN -> "Forbidden";
            default -> "Error";
        };
    }
}