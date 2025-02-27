package site.FitUp.main.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import site.FitUp.main.common.enums.QuestType;
@DynamicUpdate
@Entity @Builder
@Table(name = "quest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quest extends BaseTime{

    @Id
    @Column(name = "quest_id", length = 255)
    private String questId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestType type;

    private Boolean isSuccess;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;


    @Column(nullable = false)
    private Integer point;


    @ManyToOne
    @JoinColumn(name = "daily_result_seq", nullable = false)
    private DailyResult dailyResult;
}
