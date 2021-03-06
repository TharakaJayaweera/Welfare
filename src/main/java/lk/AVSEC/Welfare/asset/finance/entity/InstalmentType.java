package lk.AVSEC.Welfare.asset.finance.entity;

import lk.AVSEC.Welfare.asset.finance.entity.Enum.CollectionType;
import lk.AVSEC.Welfare.asset.finance.entity.Enum.ExpenseOrReceived;
import lk.AVSEC.Welfare.util.audit.AuditEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InstalmentType extends AuditEntity {

    private String name;

    private String year;

    @Enumerated(EnumType.STRING)
    private ExpenseOrReceived expenseOrReceived;

    @Enumerated(EnumType.STRING)
    private CollectionType collectionType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @OneToMany(mappedBy = "instalmentType")
    private List<Instalment> instalments;

}
