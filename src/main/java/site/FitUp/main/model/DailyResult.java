package site.FitUp.main.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import site.FitUp.main.common.enums.QuestStatus;

import java.time.LocalDateTime;

@DynamicUpdate
@Entity
@Builder
@Table(name = "daily_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_result_seq")
    private Long dailyResultSeq;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer questSuccessCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestStatus questStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reqCommend;

    @Column(nullable = false)
    private Integer pointSum;
}
