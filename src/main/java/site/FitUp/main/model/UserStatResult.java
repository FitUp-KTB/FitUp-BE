package site.FitUp.main.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import site.FitUp.main.common.enums.CharacterType;

@DynamicUpdate
@Entity
@Builder
@Table(name = "user_stat_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_stat_seq")
    private Long userStatSeq;

    @Column(nullable = false)
    private Integer strength;

    @Column(nullable = false)
    private Integer endurance;

    @Column(nullable = false)
    private Integer speed;

    @Column(nullable = false)
    private Integer flexibility;

    @Column(nullable = false)
    private Integer stamina;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CharacterType characterType;


    @OneToOne
    @JoinColumn(name = "user_stat_seq", referencedColumnName = "user_stat_seq", nullable = false)
    private UserStat userStat;



}
