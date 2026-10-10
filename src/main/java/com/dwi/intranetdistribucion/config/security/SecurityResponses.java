package com.dwi.intranetdistribucion.config.security;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
final class SecurityResponses {
    private SecurityResponses() {}
    static void error(HttpServletResponse response, int status, String mensaje) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\":" + status + ",\"mensaje\":\"" + mensaje + "\"}");
    }
}
