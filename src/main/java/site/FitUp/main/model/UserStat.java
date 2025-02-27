package site.FitUp.main.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Entity @Builder
@Table(name = "user_stat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStat extends BaseTime{

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_stat_seq")
        private Long userStatSeq;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @Column(nullable = false)
        private Integer height;

        @Column(nullable = false)
        private Integer weight;

        @Column(nullable = false)
        private Integer fat;

        @Column(nullable = false)
        private Integer muscleMass;

        private Integer pushUps;
        private Integer sitUp;
        private Double runningPace;
        private Integer runningTime;
        private Integer squat;
        private Integer benchPress;
        private Integer deadLift;
}
