package com.palette.domain.group;

import com.palette.dto.request.BudgetUpdateDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Column
    private long totalBudget;

    //지출 기록
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses = new ArrayList<>();

    @Builder
    public Budget(Group group, Long totalBudget){
        this.group = group;
        this.totalBudget = totalBudget;
    }

    public void update(BudgetUpdateDto dto){
        totalBudget = dto.getBudget();
    }
}
