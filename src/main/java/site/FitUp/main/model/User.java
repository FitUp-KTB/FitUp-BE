package site.FitUp.main.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import site.FitUp.main.common.enums.Gender;

import java.time.LocalDate;

@DynamicUpdate
@Entity @Builder
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTime{
    @Id
    @Column(name = "user_id", length = 255)
    private String userId;

    @Column(length = 255, nullable = false)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(length = 255, nullable = false)
    private String nickname;

    @Column(length = 255, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(columnDefinition = "TEXT")
    private String goal;

    @Column(columnDefinition = "TEXT")
    private String chronic;

    @Column(nullable = false)
    private Integer targetWeight;

    @Column(nullable = false)
    private LocalDate birthDate;


}
