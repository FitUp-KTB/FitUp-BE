package site.FitUp.main.api.health.controller;

import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.FitUp.main.common.ApiResponse;

import java.util.NoSuchElementException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/unHealth")
public class UnHealthController {
    @GetMapping("")
    public ApiResponse getUnHealthController() {

        throw new NoSuchElementException("요소가 없습니다.");
    }
}


