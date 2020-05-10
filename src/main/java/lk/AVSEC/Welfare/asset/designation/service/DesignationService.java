package lk.AVSEC.Welfare.asset.designation.service;

import lk.AVSEC.Welfare.asset.designation.dao.DesignationDao;
import lk.AVSEC.Welfare.asset.designation.entity.Designation;
import lk.AVSEC.Welfare.asset.grievances.dao.GrievancesDao;
import lk.AVSEC.Welfare.asset.grievances.entity.Grievances;
import lk.AVSEC.Welfare.util.interfaces.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// spring transactional annotation need to tell spring to this method work through the project
@CacheConfig(cacheNames = "designation")
public class DesignationService implements AbstractService<Designation, Integer> {

    private final DesignationDao designationDao;

    @Autowired
    public DesignationService(DesignationDao designationDao) {

        this.designationDao = designationDao;
    }

    public List<Designation> findAll() {
        return designationDao.findAll();
    }

    public Designation findById(Integer id) {
        return designationDao.getOne(id);
    }

    public Designation persist(Designation designation) {
        return designationDao.save(designation);
    }

    public boolean delete(Integer id) {
        designationDao.deleteById(id);
        return false;
    }

    public List<Designation> search(Designation designation) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Designation> designationExample = Example.of(designation, matcher);
        return designationDao.findAll(designationExample);
    }

}
