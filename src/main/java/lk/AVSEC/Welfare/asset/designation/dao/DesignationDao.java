package lk.AVSEC.Welfare.asset.designation.dao;

import lk.AVSEC.Welfare.asset.designation.entity.Designation;
import lk.AVSEC.Welfare.asset.grievances.entity.Grievances;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignationDao extends JpaRepository<Designation, Integer> {

/*//select * from district where province = ?1
    List<Qualification> findByProvince(Province province);*/

}
