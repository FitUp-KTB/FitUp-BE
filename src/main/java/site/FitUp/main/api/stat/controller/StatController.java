package site.FitUp.main.api.stat.controller;

import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.FitUp.main.api.stat.dtos.StatRequest;
import site.FitUp.main.api.stat.dtos.StatResponse;
import site.FitUp.main.api.stat.services.StatService;
import site.FitUp.main.common.ApiResponse;
import site.FitUp.main.common.enums.CharacterType;
import site.FitUp.main.util.JwtUtil;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stats")
public class StatController {
    private final StatService statService;
    @PostMapping("")
    public ApiResponse<StatResponse.CreateStatResponse> CreateStatController(@RequestBody StatRequest.CreateStatRequest request) throws Exception {

//        statService.CreateStatService(reqeust,"123");
//        return new ApiResponse<>(StatResponse.CreateStatResponse.builder()
//                .userStatSeq(123)
//                .strength(85)
//                .endurance(78)
//                .speed(75)
//                .flexibility(65)
//                .stamina(80)
//                .characterType(CharacterType.POWER.toString()).build());
        return new ApiResponse<>(statService.CreateStatService(request,JwtUtil.getAuthenticatedUserId()));
    }
    @GetMapping("")
    public ApiResponse<StatResponse.GetStatsResponse> GetStatsController(){
//        List<StatResponse.GetStatResponse> lists=new ArrayList<>();
//        int count=11;
//        for(int i=0; i<4;i++){
//
//            lists.add(StatResponse.GetStatResponse.builder()
//                    .userStatSeq(count)
//                    .strength(85)
//                    .endurance(78)
//                    .speed(75)
//                    .flexibility(65)
//                    .stamina(80)
//                    .characterType(CharacterType.POWER.toString())
//                    .createdAt("2020-02-21")
//                    .build());
//            count++;
//        }


        return new ApiResponse<>(statService.GetStatsService(JwtUtil.getAuthenticatedUserId()));
    }

}
