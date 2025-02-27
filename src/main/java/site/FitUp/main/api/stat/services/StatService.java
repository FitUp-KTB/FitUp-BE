package site.FitUp.main.api.stat.services;

import site.FitUp.main.api.stat.dtos.StatRequest;
import site.FitUp.main.api.stat.dtos.StatResponse;

public interface StatService {
    StatResponse.CreateStatResponse CreateStatService(StatRequest.CreateStatRequest request,String userId) throws Exception;
    StatResponse.GetStatsResponse GetStatsService(String userId);
}
