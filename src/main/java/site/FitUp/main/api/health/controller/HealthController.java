package site.FitUp.main.api.health.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.FitUp.main.common.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/health")
public class HealthController {
    @GetMapping("")
    public ApiResponse getHealthController() {

        return new ApiResponse("Healthy");
    }
}
